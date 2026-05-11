package com.note_app.app.api;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class AuthApi {

    private final ApiClient client;

    public AuthApi(ApiClient client) {
        this.client = client;
    }

    public AuthResult register(String name, String email, String password) throws IOException, JSONException {
        JSONObject body = new JSONObject();
        body.put("name", name);
        body.put("email", email);
        body.put("password", password);
        String response = client.post("/api/auth/register", body.toString(), false);
        return parse(response);
    }

    public AuthResult login(String email, String password) throws IOException, JSONException {
        JSONObject body = new JSONObject();
        body.put("email", email);
        body.put("password", password);
        String response = client.post("/api/auth/login", body.toString(), false);
        return parse(response);
    }

    private AuthResult parse(String response) throws JSONException {
        JSONObject root = new JSONObject(response);
        JSONObject data = root.getJSONObject("data");
        JSONObject user = data.getJSONObject("user");
        return new AuthResult(
                user.getString("id"),
                user.getString("name"),
                user.optString("email"),
                user.optString("role", "USER"),
                data.optString("token")
        );
    }

    public static class AuthResult {
        public final String userId;
        public final String name;
        public final String email;
        public final String role;
        public final String token;

        public AuthResult(String userId, String name, String email, String role, String token) {
            this.userId = userId;
            this.name = name;
            this.email = email;
            this.role = role;
            this.token = token;
        }
    }
}
