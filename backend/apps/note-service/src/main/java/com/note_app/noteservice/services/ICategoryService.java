package com.note_app.noteservice.services;

import com.note_app.commonutils.generic.GenericService;
import com.note_app.noteservice.entities.Category;

import java.util.List;

public interface ICategoryService extends GenericService<Category, Long> {

    List<Category> getUserCategories(String userId);

    Category createForUser(String userId, Category category);
}
