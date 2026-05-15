package com.note_app.noteservice.services.search;

import java.util.List;

import com.note_app.noteservice.entities.Note;

public interface SearchStrategy {

    String getType();

    List<Note> search(String userId, String keyword);
}
