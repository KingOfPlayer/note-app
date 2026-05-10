package com.note_app.noteservice.controller;

import com.note_app.commonutils.exception.BadRequestException;
import com.note_app.commonutils.exception.ForbiddenException;
import com.note_app.commonutils.generic.ApiResponse;
import com.note_app.noteservice.dto.CategoryRequest;
import com.note_app.noteservice.entity.Category;
import com.note_app.noteservice.service.ICategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final ICategoryService categoryService;

    public CategoryController(ICategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Category>>> list(@RequestHeader("X-User-Id") String userId) {
        requireUser(userId);
        return ResponseEntity.ok(ApiResponse.ok(categoryService.getUserCategories(userId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Category>> create(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody CategoryRequest request) {
        requireUser(userId);
        Category cat = new Category();
        cat.setName(request.getName());
        cat.setColor(request.getColor());
        Category created = categoryService.createForUser(userId, cat);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(created, "Kategori olusturuldu"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Category>> update(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {
        requireUser(userId);
        Category existing = categoryService.getById(id);
        if (!existing.getUserId().equals(userId)) {
            throw new ForbiddenException("Baska bir kullanicinin kategorisini degistiremezsiniz");
        }
        existing.setName(request.getName());
        existing.setColor(request.getColor());
        return ResponseEntity.ok(ApiResponse.ok(categoryService.update(id, existing), "Kategori guncellendi"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable Long id) {
        requireUser(userId);
        Category existing = categoryService.getById(id);
        if (!existing.getUserId().equals(userId)) {
            throw new ForbiddenException("Baska bir kullanicinin kategorisini silemezsiniz");
        }
        categoryService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Kategori silindi"));
    }

    private void requireUser(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new BadRequestException("Kullanici basligi (X-User-Id) eksik");
        }
    }
}
