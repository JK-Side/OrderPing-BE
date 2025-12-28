package com.orderping.infra.menu.repository;

import com.orderping.domain.menu.Category;
import com.orderping.domain.menu.repository.CategoryRepository;
import com.orderping.infra.menu.entity.CategoryEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {

    private final CategoryJpaRepository jpaRepository;

    @Override
    public Category save(Category category) {
        CategoryEntity entity = CategoryEntity.from(category);
        CategoryEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<Category> findById(Long id) {
        return jpaRepository.findById(id)
                .map(CategoryEntity::toDomain);
    }

    @Override
    public List<Category> findAll() {
        return jpaRepository.findAll().stream()
                .map(CategoryEntity::toDomain)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
