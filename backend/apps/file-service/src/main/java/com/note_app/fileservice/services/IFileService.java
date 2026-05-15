package com.note_app.fileservice.services;

import com.note_app.fileservice.dto.FileMetadataResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface IFileService {

    FileMetadataResponse upload(String userId, Long noteId, MultipartFile file) throws IOException;

    InputStream download(String userId, String fileId) throws IOException;

    FileMetadataResponse getMetadata(String userId, String fileId);

    List<FileMetadataResponse> listForUser(String userId);

    List<FileMetadataResponse> listForNote(String userId, Long noteId);

    void delete(String userId, String fileId);
}
