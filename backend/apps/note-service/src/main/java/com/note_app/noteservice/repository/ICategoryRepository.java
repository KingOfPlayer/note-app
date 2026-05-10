package com.note_app.noteservice.repository;

import com.note_app.commonutils.generic.GenericRepository;
import com.note_app.noteservice.entity.Category;

import java.util.List;
import java.util.Optional;

public interface ICategoryRepository extends GenericRepository<Category, Long> {

    List<Category> findByUserId(String userId);

    Optional<Category> findByUserIdAndName(String userId, String name);
}
