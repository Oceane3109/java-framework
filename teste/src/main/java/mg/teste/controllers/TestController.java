package mg.teste.controllers;

import jakarta.servlet.http.HttpServletResponse;
import mg.framework.annotations.HandleURL;

public class TestController {
    
    // Retourne une chaîne qui sera envoyée directement dans la réponse
    @HandleURL("/bonjour")
    public String direBonjour() {
        return "Bonjour depuis le contrôleur avec retour direct de chaîne !";
    }
    
    // Retourne du JSON sous forme de chaîne
    @HandleURL("/api/info")
    public String getInfo() {
        return "{\"status\":\"ok\", \"message\":\"API fonctionnelle\"}";
    }
    
    // Retourne du HTML directement
    @HandleURL("/page-simple")
    public String pageSimple() {
        return "<html><body><h1>Page simple</h1><p>Ceci est une page HTML générée directement depuis le contrôleur.</p></body></html>";
    }
    
    // Méthode qui gère elle-même la réponse
    @HandleURL("/gestion-manuelle")
    public void gestionManuelle(HttpServletResponse response) throws java.io.IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("Cette méthode gère directement la réponse HTTP");
    }
    
    // Retourne un objet qui sera converti en JSON
    @HandleURL("/api/utilisateur")
    public Utilisateur getUtilisateur() {
        Utilisateur user = new Utilisateur();
        user.setId(1);
        user.setNom("Dupont");
        user.setPrenom("Jean");
        user.setEmail("jean.dupont@example.com");
        return user;
    }
    
    // Classe interne pour l'exemple
    public static class Utilisateur {
        private int id;
        private String nom;
        private String prenom;
        private String email;
        
        // Getters et setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getNom() { return nom; }
        public void setNom(String nom) { this.nom = nom; }
        public String getPrenom() { return prenom; }
        public void setPrenom(String prenom) { this.prenom = prenom; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        @Override
        public String toString() {
            return String.format(
                "{\"id\":%d, \"nom\":\"%s\", \"prenom\":\"%s\", \"email\":\"%s\"}",
                id, nom, prenom, email
            );
        }
    }
}
