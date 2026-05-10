package com.note_app.noteservice.controller;

import com.note_app.commonutils.exception.BadRequestException;
import com.note_app.commonutils.exception.ForbiddenException;
import com.note_app.commonutils.generic.ApiResponse;
import com.note_app.commonutils.generic.PageResponse;
import com.note_app.noteservice.dto.NoteRequest;
import com.note_app.noteservice.entity.Note;
import com.note_app.noteservice.service.INoteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final INoteService noteService;

    public NoteController(INoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<Note>>> list(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        requireUser(userId);
        return ResponseEntity.ok(ApiResponse.ok(noteService.getUserNotes(userId, page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Note>> get(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable Long id) {
        requireUser(userId);
        Note note = noteService.getById(id);
        if (!note.getUserId().equals(userId)) {
            throw new ForbiddenException("Baska bir kullanicinin notuna erisemezsiniz");
        }
        return ResponseEntity.ok(ApiResponse.ok(note));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Note>> create(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody NoteRequest request) {
        requireUser(userId);
        Note note = toEntity(request);
        note.setUserId(userId);
        Note created = noteService.create(note);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(created, "Not olusturuldu"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Note>> update(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable Long id,
            @Valid @RequestBody NoteRequest request) {
        requireUser(userId);
        Note existing = noteService.getById(id);
        if (!existing.getUserId().equals(userId)) {
            throw new ForbiddenException("Baska bir kullanicinin notunu degistiremezsiniz");
        }
        Note updated = toEntity(request);
        updated.setUserId(userId);
        return ResponseEntity.ok(ApiResponse.ok(noteService.update(id, updated), "Not guncellendi"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable Long id) {
        requireUser(userId);
        Note existing = noteService.getById(id);
        if (!existing.getUserId().equals(userId)) {
            throw new ForbiddenException("Baska bir kullanicinin notunu silemezsiniz");
        }
        noteService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Not silindi"));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Note>>> search(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(required = false, defaultValue = "all") String type,
            @RequestParam String q) {
        requireUser(userId);
        return ResponseEntity.ok(ApiResponse.ok(noteService.search(userId, type, q)));
    }

    @GetMapping("/pinned")
    public ResponseEntity<ApiResponse<List<Note>>> pinned(@RequestHeader("X-User-Id") String userId) {
        requireUser(userId);
        return ResponseEntity.ok(ApiResponse.ok(noteService.getPinnedNotes(userId)));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<Note>>> byCategory(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable Long categoryId) {
        requireUser(userId);
        return ResponseEntity.ok(ApiResponse.ok(noteService.getUserNotesByCategory(userId, categoryId)));
    }

    @PostMapping("/{id}/toggle-pin")
    public ResponseEntity<ApiResponse<Note>> togglePin(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable Long id) {
        requireUser(userId);
        return ResponseEntity.ok(ApiResponse.ok(noteService.togglePin(userId, id), "Sabitleme durumu degisti"));
    }

    private void requireUser(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new BadRequestException("Kullanici basligi (X-User-Id) eksik");
        }
    }

    private Note toEntity(NoteRequest req) {
        Note n = new Note();
        n.setTitle(req.getTitle());
        n.setContent(req.getContent());
        n.setCategoryId(req.getCategoryId());
        n.setColor(req.getColor());
        n.setPinned(req.isPinned());
        return n;
    }
}
