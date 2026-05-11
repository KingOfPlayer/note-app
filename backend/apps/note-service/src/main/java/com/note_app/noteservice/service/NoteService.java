package com.note_app.noteservice.service;

import com.note_app.commonutils.exception.BadRequestException;
import com.note_app.commonutils.exception.ForbiddenException;
import com.note_app.commonutils.generic.AbstractCrudService;
import com.note_app.commonutils.generic.PageResponse;
import com.note_app.noteservice.entity.Note;
import com.note_app.noteservice.repository.INoteRepository;
import com.note_app.noteservice.service.search.SearchStrategy;
import com.note_app.noteservice.service.search.SearchStrategyFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteService extends AbstractCrudService<Note, Long> implements INoteService {

    private final INoteRepository noteRepository;
    private final SearchStrategyFactory searchFactory;

    public NoteService(INoteRepository noteRepository, SearchStrategyFactory searchFactory) {
        super(noteRepository);
        this.noteRepository = noteRepository;
        this.searchFactory = searchFactory;
    }

    @Override
    protected String entityName() {
        return "Not";
    }

    @Override
    protected void beforeCreate(Note entity) {
        if (entity.getTitle() == null || entity.getTitle().isBlank()) {
            throw new BadRequestException("Notun basligi bos olamaz");
        }
        if (entity.getUserId() == null || entity.getUserId().isBlank()) {
            throw new BadRequestException("Kullanici bilgisi eksik");
        }
        if (entity.getTitle().length() > 255) {
            throw new BadRequestException("Baslik en fazla 255 karakter olabilir");
        }
    }

    @Override
    protected void beforeUpdate(Note existing, Note incoming) {
        if (incoming.getTitle() == null || incoming.getTitle().isBlank()) {
            throw new BadRequestException("Notun basligi bos olamaz");
        }
        if (!existing.getUserId().equals(incoming.getUserId())) {
            throw new ForbiddenException("Baska bir kullanicinin notunu degistiremezsiniz");
        }
        incoming.setCreatedAt(existing.getCreatedAt());
    }

    @Override
    public PageResponse<Note> getUserNotes(String userId, int page, int size) {
        if (page < 0) page = 0;
        if (size <= 0 || size > 100) size = 20;
        List<Note> items = noteRepository.findByUserId(userId, page, size);
        long total = noteRepository.countByUserId(userId);
        return new PageResponse<>(items, page, size, total);
    }

    @Override
    public List<Note> getUserNotesByCategory(String userId, Long categoryId) {
        return noteRepository.findByUserIdAndCategoryId(userId, categoryId);
    }

    @Override
    public List<Note> getPinnedNotes(String userId) {
        return noteRepository.findByUserIdAndPinned(userId, true);
    }

    @Override
    public List<Note> search(String userId, String type, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            throw new BadRequestException("Arama kelimesi bos olamaz");
        }
        SearchStrategy strategy = searchFactory.resolve(type);
        return strategy.search(userId, keyword.trim());
    }

    @Override
    public Note togglePin(String userId, Long noteId) {
        Note note = getById(noteId);
        if (!note.getUserId().equals(userId)) {
            throw new ForbiddenException("Baska bir kullanicinin notunu degistiremezsiniz");
        }
        note.setPinned(!note.isPinned());
        return noteRepository.save(note);
    }
}
