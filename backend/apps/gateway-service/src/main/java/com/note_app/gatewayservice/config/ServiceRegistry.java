package com.note_app.gatewayservice.config;

import java.util.LinkedHashMap;
import java.util.Map;

public class ServiceRegistry {

    private final Map<String, String> routes = new LinkedHashMap<>();

    public void register(String prefix, String targetUrl) {
        routes.put(prefix, targetUrl);
    }

    public String resolve(String path) {
        for (Map.Entry<String, String> e : routes.entrySet()) {
            if (path.startsWith(e.getKey())) {
                return e.getValue();
            }
        }
        return null;
    }

    public Map<String, String> all() {
        return Map.copyOf(routes);
    }
}
