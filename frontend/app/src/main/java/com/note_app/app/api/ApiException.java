package com.note_app.app.api;

import org.json.JSONObject;

public class ApiException extends RuntimeException {

    private final int statusCode;

    public ApiException(int statusCode, String body) {
        super(parseMessage(body, statusCode));
        this.statusCode = statusCode;
    }

    public int getStatusCode() { return statusCode; }

    private static String parseMessage(String body, int code) {
        if (body == null || body.isEmpty()) return "HTTP " + code;
        try {
            JSONObject obj = new JSONObject(body);
            if (obj.has("message")) return obj.getString("message");
            if (obj.has("error")) return obj.getString("error");
        } catch (Exception ignored) {}
        return "HTTP " + code + ": " + body;
    }
}
