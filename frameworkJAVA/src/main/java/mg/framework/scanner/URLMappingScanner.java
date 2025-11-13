package mg.framework.scanner;

import mg.framework.annotations.HandleURL;
import mg.framework.mapping.URLMapping;

import jakarta.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

/**
 * Scanne le classpath pour trouver les méthodes annotées avec @HandleURL.
 */
public class URLMappingScanner {
    private static final String CLASS_EXTENSION = ".class";
    private static final String PACKAGE_SEPARATOR = ".";
    private static final String FILE_SEPARATOR = "/";
    
    private final String basePackage;
    private final ClassLoader classLoader;
    
    public URLMappingScanner(String basePackage) {
        this.basePackage = basePackage;
        this.classLoader = Thread.currentThread().getContextClassLoader();
    }
    
    /**
     * Scanne le package de base pour trouver les contrôleurs avec des méthodes @HandleURL.
     */
    public List<URLMapping> findMappings() {
        List<URLMapping> mappings = new ArrayList<>();
        
        try {
            System.out.println("\n=== DÉBUT DU SCAN DES CONTRÔLEURS ===");
            System.out.println("Package de base à scanner: " + basePackage);
            
            // Convertir le nom du package en chemin de répertoire
            String packagePath = basePackage.replace(PACKAGE_SEPARATOR, FILE_SEPARATOR);
            System.out.println("Chemin du package: " + packagePath);
            
            Enumeration<URL> resources = classLoader.getResources(packagePath);
            
            if (!resources.hasMoreElements()) {
                System.out.println("Aucune ressource trouvée pour le chemin: " + packagePath);
            }
            
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                System.out.println("Ressource trouvée: " + resource);
                
                // Essayer de gérer les URL encodées (pour les espaces, etc.)
                String filePath = resource.getFile();
                try {
                    filePath = java.net.URLDecoder.decode(filePath, "UTF-8");
                } catch (Exception e) {
                    System.err.println("Erreur lors du décodage de l'URL: " + e.getMessage());
                }
                
                File directory = new File(filePath);
                System.out.println("Dossier à scanner: " + directory.getAbsolutePath());
                System.out.println("Le dossier existe: " + directory.exists());
                System.out.println("Est un dossier: " + directory.isDirectory());
                
                if (directory.exists() && directory.isDirectory()) {
                    scanDirectory(basePackage, directory, mappings);
                } else {
                    System.out.println("Le chemin n'est pas un dossier valide ou n'existe pas.");
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Erreur lors du scan des contrôleurs", e);
        }
        
        return mappings;
    }
    
    /**
     * Scanne un répertoire à la recherche de classes.
     */
    private void scanDirectory(String packageName, File directory, List<URLMapping> mappings) 
            throws ClassNotFoundException {
        System.out.println("\nScan du répertoire: " + directory.getAbsolutePath());
        System.out.println("Package: " + packageName);
        
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }
        
        for (File file : files) {
            if (file.isDirectory()) {
                // Récursivement scanner les sous-répertoires
                scanDirectory(packageName + PACKAGE_SEPARATOR + file.getName(), file, mappings);
            } else if (file.getName().endsWith(CLASS_EXTENSION)) {
                // Charger la classe et vérifier les annotations
                String className = packageName + PACKAGE_SEPARATOR + 
                                 file.getName().substring(0, file.getName().length() - CLASS_EXTENSION.length());
                
                processClass(Class.forName(className), mappings);
            }
        }
    }
    
    /**
     * Traite une classe pour trouver les méthodes annotées avec @HandleURL.
     */
    private void processClass(Class<?> clazz, List<URLMapping> mappings) {
        System.out.println("\nTraitement de la classe: " + clazz.getName());
        
        // Ignorer les interfaces et les classes abstraites
        if (clazz.isInterface() || clazz.isAnnotation() || clazz.isEnum() || 
            clazz.isAnonymousClass() || clazz.isPrimitive() || 
            clazz.isMemberClass() || clazz.isArray() || 
            clazz.isLocalClass() || clazz.isSynthetic()) {
            System.out.println("Classe ignorée (interface, annotation, enum, etc.)");
            return;
        }
        
        try {
            // Créer une instance du contrôleur
            Object controller = clazz.getDeclaredConstructor().newInstance();
            
            // Parcourir toutes les méthodes de la classe
            for (Method method : clazz.getDeclaredMethods()) {
                HandleURL annotation = method.getAnnotation(HandleURL.class);
                if (annotation != null) {
                    System.out.println("Méthode annotée trouvée: " + method.getName());
                    String urlPattern = annotation.value();
                    if (urlPattern == null || urlPattern.isEmpty()) {
                        urlPattern = "/" + method.getName();
                    }
                    
                    // Pour simplifier, on suppose que toutes les méthodes sont en GET par défaut
                    // Dans une version plus avancée, on pourrait ajouter un paramètre 'method' à l'annotation
                    URLMapping mapping = new URLMapping(controller, method, "GET", urlPattern);
                    mappings.add(mapping);
                    
                    System.out.println("[URL Mapping] " + mapping);
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du traitement de la classe " + clazz.getName() + ": " + e.getMessage());
        }
    }
    
    /**
     * Enregistre les mappings dans le contexte de la servlet.
     */
    public static void registerMappings(ServletContext context, String basePackage) {
        URLMappingScanner scanner = new URLMappingScanner(basePackage);
        List<URLMapping> mappings = scanner.findMappings();
        
        // Stocker les mappings dans le contexte de la servlet
        context.setAttribute("urlMappings", mappings);
    }
}
