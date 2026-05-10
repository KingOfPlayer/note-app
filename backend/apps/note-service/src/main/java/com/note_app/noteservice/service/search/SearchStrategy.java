package com.note_app.noteservice.service.search;

import com.note_app.noteservice.entity.Note;

import java.util.List;

public interface SearchStrategy {

    String getType();

    List<Note> search(String userId, String keyword);
}
