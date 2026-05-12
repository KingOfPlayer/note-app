package com.note_app.app.api;

import com.note_app.app.model.Category;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CategoryApi {

    private final ApiClient client;

    public CategoryApi(ApiClient client) {
        this.client = client;
    }

    public List<Category> list() throws IOException, JSONException {
        String response = client.get("/api/categories");
        JSONObject root = new JSONObject(response);
        JSONArray items = root.getJSONArray("data");
        return parseList(items);
    }

    public Category create(String name, String color) throws IOException, JSONException {
        JSONObject body = new JSONObject();
        body.put("name", name);
        if (color != null) body.put("color", color);
        String response = client.post("/api/categories", body.toString(), true);
        return parseSingle(response);
    }

    public Category update(long id, String name, String color) throws IOException, JSONException {
        JSONObject body = new JSONObject();
        body.put("name", name);
        if (color != null) body.put("color", color);
        String response = client.put("/api/categories/" + id, body.toString());
        return parseSingle(response);
    }

    public void delete(long id) throws IOException {
        client.delete("/api/categories/" + id);
    }

    private Category parseSingle(String response) throws JSONException {
        JSONObject root = new JSONObject(response);
        return fromJson(root.getJSONObject("data"));
    }

    private List<Category> parseList(JSONArray items) throws JSONException {
        List<Category> result = new ArrayList<>();
        for (int i = 0; i < items.length(); i++) {
            result.add(fromJson(items.getJSONObject(i)));
        }
        return result;
    }

    private Category fromJson(JSONObject json) throws JSONException {
        Category c = new Category();
        c.setId(json.getLong("id"));
        c.setUserId(json.optString("userId"));
        c.setName(json.getString("name"));
        c.setColor(json.optString("color", null));
        c.setCreatedAt(json.optString("createdAt"));
        c.setUpdatedAt(json.optString("updatedAt"));
        return c;
    }
}
