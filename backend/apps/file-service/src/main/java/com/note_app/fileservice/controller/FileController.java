package com.note_app.fileservice.controller;

import com.note_app.commonutils.authguard.AuthGuard;
import com.note_app.commonutils.authguard.UserRoles;
import com.note_app.commonutils.exception.ErrorMessages;
import com.note_app.commonutils.exception.InternalServerException;
import com.note_app.commonutils.generic.ApiResponse;
import com.note_app.fileservice.dto.FileMetadataResponse;
import com.note_app.fileservice.service.IFileService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final IFileService fileService;

    public FileController(IFileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @AuthGuard(UserRoles.USER)
    public ResponseEntity<ApiResponse<FileMetadataResponse>> upload(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(name = "noteId", required = false) Long noteId,
            @RequestPart("file") MultipartFile file) {
        try {
            FileMetadataResponse meta = fileService.upload(userId, noteId, file);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.ok(meta, "Dosya yuklendi"));
        } catch (IOException e) {
            throw new InternalServerException(ErrorMessages.FILE_UPLOAD_FAILED);
        }
    }

    @GetMapping("/{id}")
    @AuthGuard(UserRoles.USER)
    public ResponseEntity<ApiResponse<FileMetadataResponse>> getMeta(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable("id") String id) {
        return ResponseEntity.ok(ApiResponse.ok(fileService.getMetadata(userId, id)));
    }

    @GetMapping("/{id}/download")
    @AuthGuard(UserRoles.USER)
    public ResponseEntity<InputStreamResource> download(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable("id") String id) {
        try {
            FileMetadataResponse meta = fileService.getMetadata(userId, id);
            InputStream is = fileService.download(userId, id);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + meta.getFilename() + "\"");
            MediaType type = meta.getContentType() != null
                    ? MediaType.parseMediaType(meta.getContentType())
                    : MediaType.APPLICATION_OCTET_STREAM;
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(meta.getSize())
                    .contentType(type)
                    .body(new InputStreamResource(is));
        } catch (IOException e) {
            throw new InternalServerException(ErrorMessages.FILE_DOWNLOAD_FAILED);
        }
    }

    @GetMapping
    @AuthGuard(UserRoles.USER)
    public ResponseEntity<ApiResponse<List<FileMetadataResponse>>> list(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(name = "noteId", required = false) Long noteId) {
        if (noteId != null) {
            return ResponseEntity.ok(ApiResponse.ok(fileService.listForNote(userId, noteId)));
        }
        return ResponseEntity.ok(ApiResponse.ok(fileService.listForUser(userId)));
    }

    @DeleteMapping("/{id}")
    @AuthGuard(UserRoles.USER)
    public ResponseEntity<ApiResponse<Void>> delete(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable("id") String id) {
        fileService.delete(userId, id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Dosya silindi"));
    }
}
