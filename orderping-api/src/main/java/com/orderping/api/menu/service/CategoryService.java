package com.orderping.api.menu.service;

import com.orderping.api.menu.dto.CategoryCreateRequest;
import com.orderping.api.menu.dto.CategoryResponse;
import com.orderping.domain.menu.Category;
import com.orderping.domain.menu.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public CategoryResponse createCategory(CategoryCreateRequest request) {
        Category category = Category.builder()
                .storeId(request.storeId())
                .name(request.name())
                .build();

        Category saved = categoryRepository.save(category);
        return CategoryResponse.from(saved);
    }

    public CategoryResponse getCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + id));
        return CategoryResponse.from(category);
    }

    public List<CategoryResponse> getCategoriesByStoreId(Long storeId) {
        return categoryRepository.findByStoreId(storeId).stream()
                .map(CategoryResponse::from)
                .toList();
    }

    @Transactional
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}
