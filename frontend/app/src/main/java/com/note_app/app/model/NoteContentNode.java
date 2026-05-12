package com.note_app.app.model;

public abstract class NoteContentNode {

    public abstract String getType();

    public abstract String preview();

    public static class TextNode extends NoteContentNode {
        private String text;

        public TextNode() {}
        public TextNode(String text) { this.text = text; }

        @Override public String getType() { return "text"; }
        @Override public String preview() { return text == null ? "" : text; }

        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
    }

    public static class ChecklistNode extends NoteContentNode {
        private String text;
        private boolean done;

        public ChecklistNode() {}
        public ChecklistNode(String text, boolean done) {
            this.text = text;
            this.done = done;
        }

        @Override public String getType() { return "checklist"; }
        @Override public String preview() {
            return (done ? "[x] " : "[ ] ") + (text == null ? "" : text);
        }

        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
        public boolean isDone() { return done; }
        public void setDone(boolean done) { this.done = done; }
    }

    public static class ImageNode extends NoteContentNode {
        private String fileId;
        private String caption;

        public ImageNode() {}
        public ImageNode(String fileId, String caption) {
            this.fileId = fileId;
            this.caption = caption;
        }

        @Override public String getType() { return "image"; }
        @Override public String preview() {
            String c = (caption != null && !caption.isEmpty()) ? " (" + caption + ")" : "";
            return "[Resim" + c + "]";
        }

        public String getFileId() { return fileId; }
        public void setFileId(String fileId) { this.fileId = fileId; }
        public String getCaption() { return caption; }
        public void setCaption(String caption) { this.caption = caption; }
    }
}
