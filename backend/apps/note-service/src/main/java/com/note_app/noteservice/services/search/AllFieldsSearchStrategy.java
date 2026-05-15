package com.note_app.noteservice.services.search;

import com.note_app.noteservice.entities.Note;
import com.note_app.noteservice.repository.INoteRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AllFieldsSearchStrategy implements SearchStrategy {

    private final INoteRepository repository;

    public AllFieldsSearchStrategy(INoteRepository repository) {
        this.repository = repository;
    }

    @Override
    public String getType() {
        return "all";
    }

    @Override
    public List<Note> search(String userId, String keyword) {
        return repository.searchByTitleOrContent(userId, keyword);
    }
}
