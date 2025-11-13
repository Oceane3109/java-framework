package mg.teste;

import mg.framework.annotations.HandleURL;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Teste {
    @HandleURL("/hello")
    public String hello() {
        return "Bonjour depuis la méthode hello()!";
    }

    @HandleURL("/teste")
    public void about(HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("Ceci est la page de test!");
    }
}