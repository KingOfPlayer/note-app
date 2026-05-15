package com.note_app.noteservice.services;

import com.note_app.commonutils.generic.GenericService;
import com.note_app.commonutils.generic.PageResponse;
import com.note_app.noteservice.entities.Note;

import java.util.List;

public interface INoteService extends GenericService<Note, Long> {

    PageResponse<Note> getUserNotes(String userId, int page, int size);

    List<Note> getUserNotesByCategory(String userId, Long categoryId);

    List<Note> getPinnedNotes(String userId);

    List<Note> search(String userId, String type, String keyword);

    Note togglePin(String userId, Long noteId);
}
