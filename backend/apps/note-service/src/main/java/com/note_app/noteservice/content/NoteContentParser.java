package com.note_app.noteservice.content;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class NoteContentParser {

    private final ObjectMapper mapper;

    public NoteContentParser(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public NoteContent parse(String raw) {
        if (raw == null || raw.isBlank()) {
            return new NoteContent(new ArrayList<>());
        }
        String trimmed = raw.trim();
        if (trimmed.startsWith("[")) {
            try {
                JsonNode root = mapper.readTree(trimmed);
                if (root.isArray()) {
                    List<NoteContentNode> nodes = mapper.convertValue(
                            root, new TypeReference<List<NoteContentNode>>() {});
                    return new NoteContent(nodes);
                }
            } catch (Exception ignored) {
            }
        }
        return parseMarkup(raw);
    }

    public String serialize(NoteContent content) {
        try {
            return mapper.writeValueAsString(content.getNodes());
        } catch (Exception ex) {
            return "[]";
        }
    }

    private NoteContent parseMarkup(String raw) {
        List<NoteContentNode> nodes = new ArrayList<>();
        StringBuilder textBuffer = new StringBuilder();
        for (String line : raw.split("\\r?\\n", -1)) {
            String trimmed = line.trim();
            if (trimmed.startsWith("[ ]") || trimmed.startsWith("[x]") || trimmed.startsWith("[X]")) {
                flushText(textBuffer, nodes);
                boolean done = !trimmed.startsWith("[ ]");
                String text = trimmed.substring(3).trim();
                nodes.add(new ChecklistNode(text, done));
            } else if (trimmed.startsWith("![file:") && trimmed.endsWith("]")) {
                flushText(textBuffer, nodes);
                String fileId = trimmed.substring("![file:".length(), trimmed.length() - 1);
                nodes.add(new ImageNode(fileId, null));
            } else {
                if (textBuffer.length() > 0) textBuffer.append("\n");
                textBuffer.append(line);
            }
        }
        flushText(textBuffer, nodes);
        if (nodes.isEmpty()) {
            nodes.add(new TextNode(raw));
        }
        return new NoteContent(nodes);
    }

    private void flushText(StringBuilder buf, List<NoteContentNode> nodes) {
        if (buf.length() == 0) return;
        String text = buf.toString();
        if (!text.isBlank()) {
            nodes.add(new TextNode(text.strip()));
        }
        buf.setLength(0);
    }
}
