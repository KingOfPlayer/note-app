package com.note_app.app.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NoteContent {

    private final List<NoteContentNode> nodes;

    public NoteContent(List<NoteContentNode> nodes) {
        this.nodes = nodes;
    }

    public List<NoteContentNode> getNodes() {
        return nodes;
    }

    public String summary(int limitChars) {
        StringBuilder sb = new StringBuilder();
        for (NoteContentNode node : nodes) {
            if (sb.length() > 0) sb.append(" · ");
            sb.append(node.preview());
            if (sb.length() >= limitChars) break;
        }
        if (sb.length() > limitChars) return sb.substring(0, limitChars) + "...";
        return sb.toString();
    }

    public static NoteContent parse(String raw) {
        List<NoteContentNode> nodes = new ArrayList<>();
        if (raw == null || raw.trim().isEmpty()) {
            return new NoteContent(nodes);
        }
        String trimmed = raw.trim();
        if (trimmed.startsWith("[")) {
            try {
                JSONArray arr = new JSONArray(trimmed);
                for (int i = 0; i < arr.length(); i++) {
                    NoteContentNode node = parseNode(arr.getJSONObject(i));
                    if (node != null) nodes.add(node);
                }
                return new NoteContent(nodes);
            } catch (JSONException ignored) {
            }
        }
        return parseMarkup(raw);
    }

    public static String serialize(NoteContent content) {
        JSONArray arr = new JSONArray();
        try {
            for (NoteContentNode node : content.getNodes()) {
                arr.put(toJson(node));
            }
        } catch (JSONException ex) {
            return "[]";
        }
        return arr.toString();
    }

    private static NoteContentNode parseNode(JSONObject obj) throws JSONException {
        String type = obj.optString("type");
        switch (type) {
            case "text":
                return new NoteContentNode.TextNode(obj.optString("text"));
            case "checklist":
                return new NoteContentNode.ChecklistNode(
                        obj.optString("text"), obj.optBoolean("done", false));
            case "image":
                return new NoteContentNode.ImageNode(
                        obj.optString("fileId"),
                        obj.has("caption") ? obj.optString("caption") : null);
            default:
                return null;
        }
    }

    private static JSONObject toJson(NoteContentNode node) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("type", node.getType());
        if (node instanceof NoteContentNode.TextNode t) {
            json.put("text", t.getText() == null ? "" : t.getText());
        } else if (node instanceof NoteContentNode.ChecklistNode c) {
            json.put("text", c.getText() == null ? "" : c.getText());
            json.put("done", c.isDone());
        } else if (node instanceof NoteContentNode.ImageNode i) {
            json.put("fileId", i.getFileId() == null ? "" : i.getFileId());
            if (i.getCaption() != null) json.put("caption", i.getCaption());
        }
        return json;
    }

    private static NoteContent parseMarkup(String raw) {
        List<NoteContentNode> nodes = new ArrayList<>();
        StringBuilder textBuffer = new StringBuilder();
        for (String line : raw.split("\\r?\\n", -1)) {
            String t = line.trim();
            if (t.startsWith("[ ]") || t.startsWith("[x]") || t.startsWith("[X]")) {
                flush(textBuffer, nodes);
                boolean done = !t.startsWith("[ ]");
                nodes.add(new NoteContentNode.ChecklistNode(t.substring(3).trim(), done));
            } else if (t.startsWith("![file:") && t.endsWith("]")) {
                flush(textBuffer, nodes);
                String fid = t.substring("![file:".length(), t.length() - 1);
                nodes.add(new NoteContentNode.ImageNode(fid, null));
            } else {
                if (textBuffer.length() > 0) textBuffer.append("\n");
                textBuffer.append(line);
            }
        }
        flush(textBuffer, nodes);
        if (nodes.isEmpty()) {
            nodes.add(new NoteContentNode.TextNode(raw));
        }
        return new NoteContent(nodes);
    }

    private static void flush(StringBuilder buf, List<NoteContentNode> out) {
        if (buf.length() == 0) return;
        String text = buf.toString();
        if (!text.trim().isEmpty()) {
            out.add(new NoteContentNode.TextNode(text.trim()));
        }
        buf.setLength(0);
    }
}
