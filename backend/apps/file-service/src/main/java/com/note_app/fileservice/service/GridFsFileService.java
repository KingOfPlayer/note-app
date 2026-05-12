package com.note_app.fileservice.service;

import com.mongodb.client.gridfs.model.GridFSFile;
import com.note_app.commonutils.exception.BadRequestException;
import com.note_app.commonutils.exception.ForbiddenException;
import com.note_app.commonutils.exception.NotFoundException;
import com.note_app.fileservice.dto.FileMetadataResponse;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class GridFsFileService implements IFileService {

    private final GridFsTemplate gridFsTemplate;

    public GridFsFileService(GridFsTemplate gridFsTemplate) {
        this.gridFsTemplate = gridFsTemplate;
    }

    @Override
    public FileMetadataResponse upload(String userId, Long noteId, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Dosya bos olamaz");
        }
        Document meta = new Document();
        meta.put("userId", userId);
        if (noteId != null) meta.put("noteId", noteId);
        meta.put("uploadedAt", LocalDateTime.now().toString());

        ObjectId id = gridFsTemplate.store(
                file.getInputStream(),
                file.getOriginalFilename(),
                file.getContentType(),
                meta
        );
        GridFSFile stored = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(id)));
        return toResponse(stored);
    }

    @Override
    public InputStream download(String userId, String fileId) throws IOException {
        GridFSFile gridFile = findOwnedFile(userId, fileId);
        GridFsResource resource = gridFsTemplate.getResource(gridFile);
        return resource.getInputStream();
    }

    @Override
    public FileMetadataResponse getMetadata(String userId, String fileId) {
        GridFSFile gridFile = findOwnedFile(userId, fileId);
        return toResponse(gridFile);
    }

    @Override
    public List<FileMetadataResponse> listForUser(String userId) {
        Query query = new Query(Criteria.where("metadata.userId").is(userId));
        return toResponseList(gridFsTemplate.find(query));
    }

    @Override
    public List<FileMetadataResponse> listForNote(String userId, Long noteId) {
        Query query = new Query(Criteria.where("metadata.userId").is(userId)
                .and("metadata.noteId").is(noteId));
        return toResponseList(gridFsTemplate.find(query));
    }

    @Override
    public void delete(String userId, String fileId) {
        GridFSFile gridFile = findOwnedFile(userId, fileId);
        gridFsTemplate.delete(new Query(Criteria.where("_id").is(gridFile.getObjectId())));
    }

    private GridFSFile findOwnedFile(String userId, String fileId) {
        ObjectId oid;
        try {
            oid = new ObjectId(fileId);
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Gecersiz dosya kimligi");
        }
        GridFSFile gridFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(oid)));
        if (gridFile == null) {
            throw new NotFoundException("Dosya bulunamadi: " + fileId);
        }
        Document md = gridFile.getMetadata();
        Object owner = md != null ? md.get("userId") : null;
        if (owner == null || !owner.equals(userId)) {
            throw new ForbiddenException("Bu dosyaya erisme yetkiniz yok");
        }
        return gridFile;
    }

    private List<FileMetadataResponse> toResponseList(Iterable<GridFSFile> files) {
        List<FileMetadataResponse> out = new ArrayList<>();
        for (GridFSFile f : files) {
            out.add(toResponse(f));
        }
        return out;
    }

    private FileMetadataResponse toResponse(GridFSFile f) {
        Document md = f.getMetadata();
        String userId = md != null ? (String) md.get("userId") : null;
        Long noteId = null;
        if (md != null && md.get("noteId") != null) {
            Object n = md.get("noteId");
            if (n instanceof Number num) noteId = num.longValue();
        }
        LocalDateTime uploadedAt = f.getUploadDate() != null
                ? f.getUploadDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
                : null;
        return new FileMetadataResponse(
                f.getObjectId().toHexString(),
                f.getFilename(),
                md != null ? (String) md.get("_contentType") : null,
                f.getLength(),
                userId,
                noteId,
                uploadedAt
        );
    }
}
