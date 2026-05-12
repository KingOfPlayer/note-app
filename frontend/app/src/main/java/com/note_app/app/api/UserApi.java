package com.note_app.app.api;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class UserApi {

    private final ApiClient client;

    public UserApi(ApiClient client) {
        this.client = client;
    }

    public UserInfo me() throws IOException, JSONException {
        String response = client.get("/api/users/me");
        JSONObject root = new JSONObject(response);
        return fromJson(root.getJSONObject("data"));
    }

    public UserInfo update(String userId, String name, String email) throws IOException, JSONException {
        JSONObject body = new JSONObject();
        if (name != null) body.put("name", name);
        if (email != null) body.put("email", email);
        String response = client.put("/api/users/" + userId, body.toString());
        JSONObject root = new JSONObject(response);
        return fromJson(root.getJSONObject("data"));
    }

    public void delete(String userId) throws IOException {
        client.delete("/api/users/" + userId);
    }

    private UserInfo fromJson(JSONObject json) throws JSONException {
        return new UserInfo(
                json.getString("id"),
                json.optString("name"),
                json.optString("email"),
                json.optString("role", "USER")
        );
    }

    public static class UserInfo {
        public final String id;
        public final String name;
        public final String email;
        public final String role;

        public UserInfo(String id, String name, String email, String role) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.role = role;
        }
    }
}
