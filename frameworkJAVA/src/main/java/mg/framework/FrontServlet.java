package mg.framework;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import mg.framework.mapping.URLMapping;
import mg.framework.scanner.URLMappingScanner;

@WebServlet(name = "FrontServlet", urlPatterns = {"/*"}, loadOnStartup = 1)
public class FrontServlet extends HttpServlet {
    
    private static final String BASE_PACKAGE = "mg.teste";
    private final Map<String, URLMapping> urlMappings = new ConcurrentHashMap<>();
    
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        System.out.println("\n=== INITIALISATION DU FRONT SERVLET ===");
        System.out.println("Recherche des contrôleurs dans le package: " + BASE_PACKAGE);
        
        // Scanner les contrôleurs au démarrage
        URLMappingScanner.registerMappings(getServletContext(), BASE_PACKAGE);
        
        // Charger les mappings depuis le contexte
        @SuppressWarnings("unchecked")
        List<URLMapping> mappings = (List<URLMapping>) getServletContext().getAttribute("urlMappings");
        
        if (mappings != null) {
            System.out.println("\n" + mappings.size() + " mappings trouvés :");
            for (URLMapping mapping : mappings) {
                String key = mapping.getHttpMethod() + ":" + mapping.getUrlPattern();
                urlMappings.put(key, mapping);
                System.out.println(" - " + key + " -> " + 
                                 mapping.getController().getClass().getSimpleName() + "." + 
                                 mapping.getMethod().getName() + "()");
            }
        } else {
            System.out.println("Aucun mapping trouvé dans le contexte de l'application.");
        }
        
        System.out.println("=== FIN DE L'INITIALISATION ===\n");
    }
    
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        String requestMethod = request.getMethod().toUpperCase();
        
        // Extraire le chemin de la requête de manière plus robuste
        String pathInfo;
        try {
            // S'assurer que le contextPath est bien retiré
            if (requestURI.startsWith(contextPath)) {
                pathInfo = requestURI.substring(contextPath.length());
                // Gérer le cas où pathInfo est vide (requête sur la racine)
                if (pathInfo.isEmpty()) {
                    pathInfo = "/";
                }
            } else {
                pathInfo = requestURI;
            }
        } catch (Exception e) {
            pathInfo = requestURI; // En cas d'erreur, on utilise l'URI complète
        }
        
        // Journalisation pour le débogage
        System.out.println("\n=== NOUVELLE REQUÊTE ===");
        System.out.println("[FrontServlet] URI complète: " + requestURI);
        System.out.println("[FrontServlet] Contexte application: " + contextPath);
        System.out.println("[FrontServlet] Chemin demandé: " + pathInfo);
        System.out.println("[FrontServlet] Méthode HTTP: " + requestMethod);
        
        // Afficher tous les mappings disponibles
        System.out.println("\nMappings disponibles:");
        urlMappings.forEach((key, value) -> {
            System.out.println(" - " + key + " -> " + value);
        });
        
        // Essayer de trouver un mapping correspondant
        String mappingKey = requestMethod + ":" + pathInfo;
        System.out.println("\nRecherche du mapping pour: " + mappingKey);
        
        URLMapping mapping = urlMappings.get(mappingKey);
        
        if (mapping != null) {
            System.out.println("Mapping trouvé: " + mapping);
        } else {
            System.out.println("Aucun mapping trouvé pour: " + mappingKey);
        }
        
        if (mapping != null) {
            try {
                // Appeler la méthode du contrôleur
                Object result = mapping.getMethod().invoke(
                    mapping.getController(), 
                    request, 
                    response
                );
                
                // Gérer le retour de la méthode
                if (result != null) {
                    if (result instanceof String) {
                        // Si la méthode retourne une chaîne, l'écrire directement dans la réponse
                        response.setContentType("text/plain;charset=UTF-8");
                        response.setCharacterEncoding("UTF-8");
                        response.getWriter().write((String) result);
                    } else if (result instanceof mg.framework.mvc.ModelView) {
                        // Si la méthode retourne un ModelView, forwarder vers la vue
                        mg.framework.mvc.ModelView mv = (mg.framework.mvc.ModelView) result;
                        
                        // Ajouter tous les attributs du ModelView à la requête
                        for (Map.Entry<String, Object> entry : mv.getData().entrySet()) {
                            request.setAttribute(entry.getKey(), entry.getValue());
                        }
                        
                        // Forwarder vers la vue
                        String viewPath = "/WEB-INF/views/" + mv.getView() + ".jsp";
                        request.getRequestDispatcher(viewPath).forward(request, response);
                        return; // Important pour arrêter l'exécution après le forward
                    } else {
                        // Pour les autres types d'objets, les sérialiser en JSON
                        response.setContentType("application/json;charset=UTF-8");
                        response.getWriter().write(result.toString());
                    }
                }
                // Si la méthode retourne null, on suppose qu'elle a déjà géré la réponse
            } catch (Exception e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setContentType("text/plain;charset=UTF-8");
                response.getWriter().write("Erreur lors du traitement de la requête: " + e.getMessage());
            }
        }
        
        // Aucun mapping trouvé, essayer de servir une ressource statique
        try {
            if (getServletContext().getResource(pathInfo) != null) {
                getServletContext().getNamedDispatcher("default").forward(request, response);
                return;
            }
        } catch (Exception e) {
            // Continuer et afficher la page d'erreur personnalisée
        }
        
        // Définir le code de statut HTTP 404 avant d'afficher la page d'erreur
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        
        // Afficher la page du framework avec les URLs disponibles
        showFrameworkPage(request, response, pathInfo);
    }
    
    private void showFrameworkPage(HttpServletRequest request, HttpServletResponse response, String requestedPath) 
            throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        out.println("<!DOCTYPE html>");
        out.println("<html lang='fr'>");
        out.println("<head>");
        out.println("    <meta charset='UTF-8'>");
        out.println("    <meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("    <title>Framework Java - Page non trouvée</title>");
        out.println("    <style>");
        out.println("        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 0; padding: 40px; background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%); color: #212529; min-height: 100vh; }");
        out.println("        .container { max-width: 800px; margin: 0 auto; background: white; padding: 40px; border-radius: 15px; box-shadow: 0 10px 30px rgba(0,0,0,0.1); border: 1px solid #e9ecef; }");
        out.println("        h1 { color: #212529; text-align: center; margin-bottom: 30px; padding-bottom: 15px; border-bottom: 2px solid #333; font-weight: 300; font-size: 2.5em; }");
        out.println("        .message { background: #f8f9fa; padding: 25px; border-radius: 12px; border-left: 4px solid #333; margin: 25px 0; box-shadow: 0 2px 10px rgba(0,0,0,0.05); }");
        out.println("        .path { font-family: 'Courier New', monospace; background: #e9ecef; padding: 12px 16px; border-radius: 8px; display: inline-block; margin: 15px 0; font-weight: bold; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }");
        out.println("        .footer { margin-top: 30px; text-align: center; color: #6c757d; font-size: 14px; padding-top: 20px; border-top: 1px solid #dee2e6; }");
        out.println("        p { line-height: 1.6; margin-bottom: 15px; color: #495057; }");
        out.println("        h3 { color: #333; margin-bottom: 15px; font-weight: 500; }");
        out.println("        .url-list { margin-top: 30px; }");
        out.println("        .url-item { background: #f8f9fa; margin: 10px 0; padding: 15px; border-radius: 8px; border-left: 4px solid #007bff; }");
        out.println("        .url-method { display: inline-block; background: #007bff; color: white; padding: 3px 8px; border-radius: 4px; font-size: 0.9em; margin-right: 10px; }");
        out.println("        .url-path { font-family: 'Courier New', monospace; font-weight: bold; }");
        out.println("    </style>");
        out.println("</head>");
        out.println("<body>");
        out.println("    <div class='container'>");
        out.println("        <h1>FRAMEWORK JAVA</h1>");
        
        out.println("        <div class='message'>");
        out.println("            <h3>Ressource non trouvée</h3>");
        out.println("            <p>Voici l'URL demandée :</p>");
        out.println("            <div class='path'><strong>" + requestedPath + "</strong></div>");
        out.println("        </div>");
        
        // Afficher la liste des URLs disponibles
        if (!urlMappings.isEmpty()) {
            out.println("        <div class='url-list'>");
            out.println("            <h3>URLs disponibles :</h3>");
            
            urlMappings.forEach((key, mapping) -> {
                String[] parts = key.split(":", 2);
                String method = parts[0];
                String path = parts.length > 1 ? parts[1] : "";
                
                out.println("            <div class='url-item'>");
                out.println("                <span class='url-method'>" + method + "</span>");
                out.println("                <span class='url-path'>" + path + "</span>");
                out.println("                <div class='url-handler' style='margin-top: 5px; color: #6c757d; font-size: 0.9em;'>");
                out.println("                    Handler: " + mapping.getController().getClass().getSimpleName() + "." + mapping.getMethod().getName() + "()");
                out.println("                </div>");
                out.println("            </div>");
            });
            
            out.println("        </div>");
        }
        
        out.println("        <div class='footer'>");
        out.println("            <p>Framework Java - &copy; " + java.time.Year.now().getValue() + "</p>");
        out.println("        </div>");
        out.println("    </div>");
        out.println("</body>");
        out.println("</html>");
    }
}