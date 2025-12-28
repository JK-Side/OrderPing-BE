package com.orderping.api.menu.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.orderping.api.menu.dto.CategoryCreateRequest;
import com.orderping.api.menu.dto.CategoryResponse;
import com.orderping.api.menu.service.CategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController implements CategoryApi {

    private final CategoryService categoryService;

    @PostMapping
    @Override
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody CategoryCreateRequest request) {
        CategoryResponse response = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<CategoryResponse> getCategory(@PathVariable Long id) {
        CategoryResponse response = categoryService.getCategory(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Override
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> responses = categoryService.getAllCategories();
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
