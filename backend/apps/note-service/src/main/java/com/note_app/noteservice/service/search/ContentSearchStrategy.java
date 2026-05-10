package com.note_app.noteservice.service.search;

import com.note_app.noteservice.entity.Note;
import com.note_app.noteservice.repository.INoteRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ContentSearchStrategy implements SearchStrategy {

    private final INoteRepository repository;

    public ContentSearchStrategy(INoteRepository repository) {
        this.repository = repository;
    }

    @Override
    public String getType() {
        return "content";
    }

    @Override
    public List<Note> search(String userId, String keyword) {
        return repository.searchByContent(userId, keyword);
    }
}
