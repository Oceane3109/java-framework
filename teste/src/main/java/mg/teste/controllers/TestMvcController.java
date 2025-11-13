package mg.teste.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.framework.annotations.HandleURL;
import mg.framework.mvc.ModelView;

public class TestMvcController {
    
    @HandleURL("/mvc/accueil")
    public ModelView accueil() {
        ModelView mv = new ModelView("accueil");
        mv.addObject("titre", "Page d'accueil");
        mv.addObject("message", "Bienvenue sur notre application MVC !");
        return mv;
    }
    
    @HandleURL("/mvc/profil")
    public ModelView afficherProfil() {
        ModelView mv = new ModelView("profil");
        mv.addObject("utilisateur", "Jean Dupont");
        mv.addObject("email", "jean.dupont@example.com");
        mv.addObject("role", "Administrateur");
        return mv;
    }
    
    @HandleURL("/mvc/bonjour")
    public String direBonjour() {
        return "Bonjour depuis le contrôleur MVC !";
    }
    
    @HandleURL("/mvc/api/statistiques")
    public String getStatistiques() {
        // Simuler des données JSON
        return "{\"utilisateurs\": 42, \"articles\": 123, \"commentaires\": 456}";
    }
}
