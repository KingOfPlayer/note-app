package com.note_app.noteservice.service;

import com.note_app.commonutils.exception.BadRequestException;
import com.note_app.commonutils.generic.AbstractCrudService;
import com.note_app.noteservice.entity.Category;
import com.note_app.noteservice.repository.ICategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService extends AbstractCrudService<Category, Long> implements ICategoryService {

    private final ICategoryRepository categoryRepository;

    public CategoryService(ICategoryRepository categoryRepository) {
        super(categoryRepository);
        this.categoryRepository = categoryRepository;
    }

    @Override
    protected String entityName() {
        return "Kategori";
    }

    @Override
    protected void beforeCreate(Category entity) {
        if (entity.getName() == null || entity.getName().isBlank()) {
            throw new BadRequestException("Kategori adi bos olamaz");
        }
        if (entity.getName().length() > 100) {
            throw new BadRequestException("Kategori adi en fazla 100 karakter olabilir");
        }
    }

    @Override
    public List<Category> getUserCategories(String userId) {
        return categoryRepository.findByUserId(userId);
    }

    @Override
    public Category createForUser(String userId, Category category) {
        category.setUserId(userId);
        if (categoryRepository.findByUserIdAndName(userId, category.getName()).isPresent()) {
            throw new BadRequestException("Bu isimde bir kategoriniz zaten var");
        }
        return create(category);
    }
}
