package mg.framework;

import jakarta.servlet.ServletException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "FrontServlet", urlPatterns = {"/*"}, loadOnStartup = 1)
public class FrontServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        service(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        service(request, response);
    }
    
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        
        System.out.println("Requête reçue : " + requestURI);
        System.out.println("Contexte : " + contextPath);
        
        try {
            // Obtenir le chemin relatif à partir de l'URL demandée
            String path = requestURI.substring(contextPath.length());
            System.out.println("Chemin relatif : " + path);
            
            // Si c'est la racine, afficher la page d'accueil
            if (path.equals("/") || path.isEmpty()) {
                System.out.println("Affichage de la page d'accueil");
                showWelcomePage(request, response);
                return;
            }
            
            // Si la requête est pour une ressource statique, laisser le conteneur la gérer
            if (isStaticResource(path)) {
                System.out.println("Ressource statique détectée : " + path);
                RequestDispatcher defaultServlet = getServletContext().getNamedDispatcher("default");
                if (defaultServlet != null) {
                    defaultServlet.forward(request, response);
                    return;
                }
            }
            
            // Pour toutes les autres requêtes, afficher la page d'erreur personnalisée
            System.out.println("Affichage de la page d'erreur personnalisée pour : " + requestURI);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            showFrameworkPage(request, response, requestURI);
            
        } catch (Exception e) {
            System.err.println("Erreur lors du traitement de la requête : " + e.getMessage());
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur interne du serveur");
        }
    }
    
    /**
     * Vérifie si l'URL correspond à une ressource statique
     */
    private boolean isStaticResource(String requestURI) {
        // Supprimer les paramètres de requête s'il y en a
        String path = requestURI.split("\\?")[0].toLowerCase();
        
        // Vérifier les extensions de fichiers statiques
        return path.endsWith(".css") || 
               path.endsWith(".js") ||
               path.endsWith(".jpg") ||
               path.endsWith(".jpeg") ||
               path.endsWith(".png") ||
               path.endsWith(".gif") ||
               path.endsWith(".ico") ||
               path.endsWith(".svg") ||
               path.endsWith(".woff") ||
               path.endsWith(".woff2") ||
               path.endsWith(".ttf") ||
               path.endsWith(".eot") ||
               path.endsWith(".html") ||
               path.endsWith(".jsp");
    }
    
    protected void showWelcomePage(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html lang='fr'>");
            out.println("<head>");
            out.println("    <meta charset='UTF-8'>");
            out.println("    <meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            out.println("    <title>Bienvenue - Framework Java</title>");
            out.println("    <style>");
            out.println("        body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f5f5f5; }");
            out.println("        .container { max-width: 800px; margin: 0 auto; padding: 2rem; }");
            out.println("        .card { background: white; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); padding: 2rem; margin-top: 2rem; }");
            out.println("        h1 { color: #2563eb; margin-bottom: 1.5rem; }");
            out.println("        p { color: #4b5563; line-height: 1.6; }");
            out.println("        .btn { display: inline-block; background: #2563eb; color: white; text-decoration: none; padding: 0.75rem 1.5rem; border-radius: 4px; margin-top: 1rem; }");
            out.println("        .btn:hover { background: #1d4ed8; }");
            out.println("    </style>");
            out.println("</head>");
            out.println("<body>");
            out.println("    <div class='container'>");
            out.println("        <div class='card'>");
            out.println("            <h1>🌟 Bienvenue sur le Framework Java</h1>");
            out.println("            <p>Le framework est correctement configuré et fonctionne !</p>");
            out.println("            <p>Essayez d'accéder à une URL qui n'existe pas pour voir la page d'erreur personnalisée.</p>");
            out.println("            <a href='./une-url-qui-nexiste-pas' class='btn'>Tester une URL inexistante</a>");
            out.println("        </div>");
            out.println("    </div>");
            out.println("</body>");
            out.println("</html>");
        }
    }
    
    protected void showErrorPage(HttpServletRequest request, HttpServletResponse response, 
                               String title, String message, int statusCode) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("text/html;charset=UTF-8");
        
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html lang='fr'>");
            out.println("<head>");
            out.println("    <meta charset='UTF-8'>");
            out.println("    <meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            out.println("    <title>" + statusCode + " - " + title + "</title>");
            out.println("    <style>");
            out.println("        :root { --primary: #4f46e5; --danger: #dc2626; }");
            out.println("        body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif; margin: 0; padding: 0; background-color: #f9fafb; color: #1f2937; }");
            out.println("        .container { max-width: 800px; margin: 0 auto; padding: 2rem; }");
            out.println("        .card { background: white; border-radius: 0.5rem; box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1); overflow: hidden; }");
            out.println("        .card-header { background: linear-gradient(135deg, var(--primary), #6366f1); color: white; padding: 1.5rem 2rem; }");
            out.println("        .card-body { padding: 2rem; }");
            out.println("        h1 { margin: 0 0 0.5rem; font-size: 2rem; font-weight: 700; }");
            out.println("        .error-code { font-size: 5rem; font-weight: 800; color: var(--danger); text-align: center; margin: 1rem 0; }");
            out.println("        .message { background: #fef2f2; color: #991b1b; padding: 1rem; border-radius: 0.375rem; border-left: 4px solid var(--danger); margin: 1.5rem 0; }");
            out.println("        .btn { display: inline-block; background: var(--primary); color: white; text-decoration: none; padding: 0.75rem 1.5rem; border-radius: 0.375rem; font-weight: 500; transition: background-color 0.2s; }");
            out.println("        .btn:hover { background: #4338ca; }");
            out.println("        .btn-secondary { background: #e5e7eb; color: #1f2937; margin-left: 0.75rem; }");
            out.println("        .btn-secondary:hover { background: #d1d5db; }");
            out.println("        .btn-group { margin-top: 1.5rem; }");
            out.println("        .path { font-family: 'Courier New', monospace; background: #f3f4f6; padding: 0.5rem; border-radius: 0.25rem; display: inline-block; margin-top: 0.5rem; }");
            out.println("    </style>");
            out.println("</head>");
            out.println("<body>");
            out.println("    <div class='container'>");
            out.println("        <div class='card'>");
            out.println("            <div class='card-header'>");
            out.println("                <h1>" + title + "</h1>");
            out.println("            </div>");
            out.println("            <div class='card-body'>");
            out.println("                <div class=\"error-code\">" + statusCode + "</div>");
            out.println("                <div class=\"message\">" + message + "</div>");
            out.println("                <p>URL demandée : <span class='path'>" + request.getRequestURI() + "</span></p>");
            out.println("                <div class='btn-group'>");
            out.println("                    <a href='./' class='btn'>Retour à l'accueil</a>");
            out.println("                    <a href='javascript:history.back()' class='btn btn-secondary'>Revenir en arrière</a>");
            out.println("                </div>");
            out.println("            </div>");
            out.println("        </div>");
            out.println("    </div>");
            out.println("</body>");
            out.println("</html>");
        }
    }
    
    private void showFrameworkPage(HttpServletRequest request, HttpServletResponse response, String requestedPath) 
            throws IOException {
        
        // Définir le code d'état HTTP 404
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        response.setContentType("text/html;charset=UTF-8");
        
        // Désactiver la mise en cache pour cette page d'erreur
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        
        // Utiliser try-with-resources pour s'assurer que le PrintWriter est correctement fermé
        try (PrintWriter out = response.getWriter()) {
        
        out.println("<!DOCTYPE html>");
        out.println("<html lang='fr'>");
        out.println("<head>");
        out.println("    <meta charset='UTF-8'>");
        out.println("    <meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("    <title>404 - Ressource non trouvée</title>");
        out.println("    <link href='https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap' rel='stylesheet'>");
        out.println("    <style>");
        out.println("        :root {");
        out.println("            --primary-color: #4f46e5;");
        out.println("            --secondary-color: #6366f1;");
        out.println("            --success-color: #10b981;");
        out.println("            --danger-color: #ef4444;");
        out.println("            --dark-color: #1f2937;");
        out.println("            --light-color: #f9fafb;");
        out.println("            --gray-100: #f3f4f6;");
        out.println("            --gray-200: #e5e7eb;");
        out.println("            --gray-300: #d1d5db;");
        out.println("            --shadow-sm: 0 1px 2px 0 rgba(0, 0, 0, 0.05);");
        out.println("            --shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);");
        out.println("            --shadow-lg: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);");
        out.println("        }");
        out.println("        * { margin: 0; padding: 0; box-sizing: border-box; }");
        out.println("        body { font-family: 'Poppins', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif; background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%); color: var(--dark-color); min-height: 100vh; line-height: 1.5; }");
        out.println("        .container { max-width: 680px; margin: 0 auto; padding: 2rem 1rem; }");
        out.println("        .card { background: white; border-radius: 1rem; box-shadow: var(--shadow-lg); overflow: hidden; transition: transform 0.3s ease, box-shadow 0.3s ease; }");
        out.println("        .card:hover { transform: translateY(-5px); box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04); }");
        out.println("        .card-header { background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)); color: white; padding: 2rem; text-align: center; }");
        out.println("        .card-body { padding: 2.5rem; }");
        out.println("        h1 { font-size: 2.5rem; font-weight: 700; margin-bottom: 0.5rem; }");
        out.println("        .subtitle { font-size: 1.125rem; opacity: 0.9; margin-bottom: 2rem; }");
        out.println("        .error-code { font-size: 4rem; font-weight: 800; color: var(--primary-color); text-align: center; margin: 1.5rem 0; }");
        out.println("        .message { background: var(--gray-100); padding: 1.5rem; border-radius: 0.75rem; border-left: 4px solid var(--primary-color); margin: 1.5rem 0; }");
        out.println("        .path { font-family: 'Fira Code', 'Courier New', monospace; background: var(--dark-color); color: #f0f0f0; padding: 0.75rem 1rem; border-radius: 0.5rem; display: block; margin: 1rem 0; font-size: 0.9rem; overflow-x: auto; white-space: nowrap; }");
        out.println("        .btn { display: inline-block; background: var(--primary-color); color: white; text-decoration: none; padding: 0.75rem 1.5rem; border-radius: 0.5rem; font-weight: 500; transition: all 0.2s ease; border: none; cursor: pointer; }");
        out.println("        .btn:hover { background: #4338ca; transform: translateY(-1px); }");
        out.println("        .btn-secondary { background: var(--gray-200); color: var(--dark-color); }");
        out.println("        .btn-secondary:hover { background: var(--gray-300); }");
        out.println("        .btn-group { display: flex; gap: 1rem; margin-top: 2rem; flex-wrap: wrap; }");
        out.println("        .footer { margin-top: 3rem; text-align: center; color: #6b7280; font-size: 0.875rem; }");
        out.println("        @media (max-width: 640px) { .btn-group { flex-direction: column; } .btn { width: 100%; text-align: center; } }");
        out.println("        .error-icon { font-size: 5rem; text-align: center; margin: 1rem 0; color: var(--danger-color); }");
        out.println("    </style>");
        out.println("</head>");
        out.println("<body>");
        out.println("    <div class='container'>");
        out.println("        <div class='card'>");
        out.println("            <div class='card-header'>");
        out.println("                <h1>Oups !</h1>");
        out.println("                <p class='subtitle'>La page que vous recherchez est introuvable</p>");
        out.println("            </div>");
        out.println("                <div class='error-icon'>⚠️</div>");
        out.println("                <div class='message'>");
        out.println("                    <h3>Ressource non trouvée</h3>");
        out.println("                    <p>L'URL suivante n'a pas pu être trouvée sur ce serveur :</p>");
        out.println("                    <div class='path'><code>" + requestedPath + "</code></div>");
        out.println("                    <p>Veuillez vérifier l'orthographe ou utiliser les liens ci-dessous pour naviguer.</p>");
        out.println("                </div>");
        
        // Boutons d'action
        out.println("                <div class='btn-group'>");
        out.println("                    <a href='/' class='btn'>Page d'accueil</a>");
        out.println("                    <a href='javascript:history.back()' class='btn btn-secondary'>Page précédente</a>");
        out.println("                </div>");
        
        // Pied de page
        out.println("                <div class='footer'>");
        out.println("                    <p>Framework Java &copy; " + java.time.Year.now().getValue() + " - Tous droits réservés</p>");
        out.println("                </div>");
        
        out.println("            </div>"); // Fin de card-body
        out.println("        </div>"); // Fin de card
        out.println("    </div>"); // Fin de container
        out.println("</body>");
            out.println("</html>");
        } // Le PrintWriter est automatiquement fermé ici
    }
}