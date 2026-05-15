package com.note_app.gatewayservice.middlewares;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.*;

public class MutableHttpServletRequest extends HttpServletRequestWrapper {

    private final Map<String, List<String>> customHeaders = new HashMap<>();
    private final Set<String> removedHeaders = new HashSet<>();

    public MutableHttpServletRequest(HttpServletRequest request) {
        super(request);
    }

    public void removeHeader(String name) {
        if (name == null) {
            return;
        }
        String key = name.toLowerCase(Locale.ROOT);
        removedHeaders.add(key);
        customHeaders.remove(key);
    }

    public void putHeader(String name, String value) {
        if (name == null) {
            return;
        }
        String key = name.toLowerCase(Locale.ROOT);
        removedHeaders.remove(key);
        customHeaders.put(key, value == null ? List.of() : List.of(value));
    }

    @Override
    public String getHeader(String name) {
        if (name == null) {
            return null;
        }

        String key = name.toLowerCase(Locale.ROOT);
        if (removedHeaders.contains(key)) {
            return null;
        }

        List<String> values = customHeaders.get(key);
        if (values != null) {
            return values.isEmpty() ? null : values.get(0);
        }

        return super.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        if (name == null) {
            return Collections.emptyEnumeration();
        }

        String key = name.toLowerCase(Locale.ROOT);
        if (removedHeaders.contains(key)) {
            return Collections.emptyEnumeration();
        }

        List<String> values = customHeaders.get(key);
        if (values != null) {
            return Collections.enumeration(values);
        }

        return super.getHeaders(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        Set<String> headerNames = new LinkedHashSet<>();

        Enumeration<String> originalHeaderNames = super.getHeaderNames();
        while (originalHeaderNames.hasMoreElements()) {
            String name = originalHeaderNames.nextElement();
            if (name == null) {
                continue;
            }
            if (!removedHeaders.contains(name.toLowerCase(Locale.ROOT))) {
                headerNames.add(name);
            }
        }

        for (String customHeader : customHeaders.keySet()) {
            if (!removedHeaders.contains(customHeader)) {
                headerNames.add(customHeader);
            }
        }

        return Collections.enumeration(headerNames);
    }
}
