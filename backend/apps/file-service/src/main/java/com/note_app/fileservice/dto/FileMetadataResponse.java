package com.note_app.fileservice.dto;

import java.time.LocalDateTime;

public class FileMetadataResponse {

    private final String id;
    private final String filename;
    private final String contentType;
    private final long size;
    private final String userId;
    private final Long noteId;
    private final LocalDateTime uploadedAt;

    public FileMetadataResponse(String id, String filename, String contentType, long size,
                                String userId, Long noteId, LocalDateTime uploadedAt) {
        this.id = id;
        this.filename = filename;
        this.contentType = contentType;
        this.size = size;
        this.userId = userId;
        this.noteId = noteId;
        this.uploadedAt = uploadedAt;
    }

    public String getId() { return id; }
    public String getFilename() { return filename; }
    public String getContentType() { return contentType; }
    public long getSize() { return size; }
    public String getUserId() { return userId; }
    public Long getNoteId() { return noteId; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }
}
