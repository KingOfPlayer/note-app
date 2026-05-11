package com.note_app.app.api;

import com.note_app.app.model.Note;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NoteApi {

    private final ApiClient client;

    public NoteApi(ApiClient client) {
        this.client = client;
    }

    public List<Note> list(int page, int size) throws IOException, JSONException {
        String response = client.get("/api/notes?page=" + page + "&size=" + size);
        JSONObject root = new JSONObject(response);
        JSONObject data = root.getJSONObject("data");
        JSONArray items = data.getJSONArray("items");
        return parseList(items);
    }

    public List<Note> search(String type, String keyword) throws IOException, JSONException {
        String response = client.get("/api/notes/search?type=" + type + "&q=" + keyword);
        JSONObject root = new JSONObject(response);
        return parseList(root.getJSONArray("data"));
    }

    public Note create(Note note) throws IOException, JSONException {
        JSONObject body = toJson(note);
        String response = client.post("/api/notes", body.toString(), true);
        return parseSingle(response);
    }

    public Note update(long id, Note note) throws IOException, JSONException {
        JSONObject body = toJson(note);
        String response = client.put("/api/notes/" + id, body.toString());
        return parseSingle(response);
    }

    public void delete(long id) throws IOException {
        client.delete("/api/notes/" + id);
    }

    public Note togglePin(long id) throws IOException, JSONException {
        String response = client.post("/api/notes/" + id + "/toggle-pin", "{}", true);
        return parseSingle(response);
    }

    private JSONObject toJson(Note note) throws JSONException {
        JSONObject body = new JSONObject();
        body.put("title", note.getTitle());
        body.put("content", note.getContent() != null ? note.getContent() : "");
        if (note.getCategoryId() != null) body.put("categoryId", note.getCategoryId());
        if (note.getColor() != null) body.put("color", note.getColor());
        body.put("pinned", note.isPinned());
        return body;
    }

    private Note parseSingle(String response) throws JSONException {
        JSONObject root = new JSONObject(response);
        return fromJson(root.getJSONObject("data"));
    }

    private List<Note> parseList(JSONArray items) throws JSONException {
        List<Note> result = new ArrayList<>();
        for (int i = 0; i < items.length(); i++) {
            result.add(fromJson(items.getJSONObject(i)));
        }
        return result;
    }

    private Note fromJson(JSONObject json) throws JSONException {
        Note n = new Note();
        n.setId(json.getLong("id"));
        n.setUserId(json.optString("userId"));
        if (!json.isNull("categoryId")) n.setCategoryId(json.getLong("categoryId"));
        n.setTitle(json.getString("title"));
        n.setContent(json.optString("content", ""));
        n.setColor(json.optString("color", null));
        n.setPinned(json.optBoolean("pinned", false));
        n.setCreatedAt(json.optString("createdAt"));
        n.setUpdatedAt(json.optString("updatedAt"));
        return n;
    }
}
