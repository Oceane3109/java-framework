package mg.framework.mvc;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe utilitaire pour gérer les vues et leurs données associées.
 */
public class ModelView {
    private String view;
    private Map<String, Object> data;
    
    public ModelView(String view) {
        this.view = view;
        this.data = new HashMap<>();
    }
    
    public String getView() {
        return view;
    }
    
    public void setView(String view) {
        this.view = view;
    }
    
    public Map<String, Object> getData() {
        return data;
    }
    
    public void addObject(String key, Object value) {
        this.data.put(key, value);
    }
    
    public Object getObject(String key) {
        return this.data.get(key);
    }
}
