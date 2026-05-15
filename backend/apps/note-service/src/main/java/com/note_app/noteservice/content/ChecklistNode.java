package com.note_app.noteservice.content;

public class ChecklistNode extends NoteContentNode {

    private String text;
    private boolean done;

    public ChecklistNode() {
    }

    public ChecklistNode(String text, boolean done) {
        this.text = text;
        this.done = done;
    }

    @Override
    public String getType() {
        return "checklist";
    }

    @Override
    public String preview() {
        String mark = done ? "[x]" : "[ ]";
        return mark + " " + (text == null ? "" : text);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
