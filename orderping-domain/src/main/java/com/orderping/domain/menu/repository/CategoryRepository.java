package com.orderping.domain.menu.repository;

import java.util.List;
import java.util.Optional;

import com.orderping.domain.menu.Category;

public interface CategoryRepository {

    Category save(Category category);

    Optional<Category> findById(Long id);

    List<Category> findAll();

    void deleteById(Long id);
}
