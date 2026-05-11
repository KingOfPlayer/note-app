package com.note_app.noteservice.service.search;

import com.note_app.noteservice.entity.Note;
import com.note_app.noteservice.repository.INoteRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TitleSearchStrategy implements SearchStrategy {

    private final INoteRepository repository;

    public TitleSearchStrategy(INoteRepository repository) {
        this.repository = repository;
    }

    @Override
    public String getType() {
        return "title";
    }

    @Override
    public List<Note> search(String userId, String keyword) {
        return repository.searchByTitle(userId, keyword);
    }
}
