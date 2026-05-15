package com.note_app.noteservice.content;

import java.util.ArrayList;
import java.util.List;

public class NoteContent {

    private List<NoteContentNode> nodes = new ArrayList<>();

    public NoteContent() {
    }

    public NoteContent(List<NoteContentNode> nodes) {
        this.nodes = nodes == null ? new ArrayList<>() : nodes;
    }

    public List<NoteContentNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<NoteContentNode> nodes) {
        this.nodes = nodes;
    }

    public String plainPreview(int limitChars) {
        StringBuilder sb = new StringBuilder();
        for (NoteContentNode node : nodes) {
            if (sb.length() > 0) sb.append(" · ");
            sb.append(node.preview());
            if (sb.length() >= limitChars) break;
        }
        return sb.length() > limitChars ? sb.substring(0, limitChars) + "..." : sb.toString();
    }
}
