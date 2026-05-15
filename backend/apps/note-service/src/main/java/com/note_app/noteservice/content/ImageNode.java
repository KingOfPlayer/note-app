package com.note_app.noteservice.content;

public class ImageNode extends NoteContentNode {

    private String fileId;
    private String caption;

    public ImageNode() {
    }

    public ImageNode(String fileId, String caption) {
        this.fileId = fileId;
        this.caption = caption;
    }

    @Override
    public String getType() {
        return "image";
    }

    @Override
    public String preview() {
        String c = caption != null && !caption.isBlank() ? " (" + caption + ")" : "";
        return "[Resim" + c + "]";
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }
}
