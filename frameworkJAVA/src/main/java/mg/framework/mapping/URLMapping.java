package mg.framework.mapping;

import java.lang.reflect.Method;

/**
 * Représente le mapping entre une URL et une méthode de contrôleur.
 */
public class URLMapping {
    private final Object controller;
    private final Method method;
    private final String httpMethod;
    private final String urlPattern;

    public URLMapping(Object controller, Method method, String httpMethod, String urlPattern) {
        this.controller = controller;
        this.method = method;
        this.httpMethod = httpMethod != null ? httpMethod.toUpperCase() : "GET";
        this.urlPattern = urlPattern;
    }

    public Object getController() {
        return controller;
    }

    public Method getMethod() {
        return method;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getUrlPattern() {
        return urlPattern;
    }

    /**
     * Vérifie si ce mapping correspond à la requête donnée.
     */
    public boolean matches(String requestMethod, String requestPath) {
        // Pour l'instant, une simple égalité, mais pourrait être étendu pour gérer les paramètres
        return this.httpMethod.equalsIgnoreCase(requestMethod) && 
               this.urlPattern.equals(requestPath);
    }

    @Override
    public String toString() {
        return String.format("%s %s -> %s.%s()", 
            httpMethod, 
            urlPattern, 
            controller.getClass().getSimpleName(), 
            method.getName());
    }
}
