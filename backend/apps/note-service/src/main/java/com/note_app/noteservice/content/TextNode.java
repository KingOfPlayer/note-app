package com.note_app.noteservice.content;

public class TextNode extends NoteContentNode {

    private String text;

    public TextNode() {
    }

    public TextNode(String text) {
        this.text = text;
    }

    @Override
    public String getType() {
        return "text";
    }

    @Override
    public String preview() {
        return text == null ? "" : text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
