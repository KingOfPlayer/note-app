package com.note_app.noteservice.repository;

import com.note_app.commonutils.generic.GenericRepository;
import com.note_app.noteservice.entities.Note;

import java.util.List;

public interface INoteRepository extends GenericRepository<Note, Long> {

    List<Note> findByUserId(String userId, int page, int size);

    long countByUserId(String userId);

    List<Note> findByUserIdAndCategoryId(String userId, Long categoryId);

    List<Note> findByUserIdAndPinned(String userId, boolean pinned);

    List<Note> searchByTitle(String userId, String keyword);

    List<Note> searchByContent(String userId, String keyword);

    List<Note> searchByTitleOrContent(String userId, String keyword);
}
