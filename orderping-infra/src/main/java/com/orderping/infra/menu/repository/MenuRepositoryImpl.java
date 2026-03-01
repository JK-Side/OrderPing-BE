package com.orderping.infra.menu.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.orderping.domain.menu.Menu;
import com.orderping.domain.menu.repository.MenuRepository;
import com.orderping.infra.menu.entity.MenuEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MenuRepositoryImpl implements MenuRepository {

    private final MenuJpaRepository jpaRepository;

    @Override
    public Menu save(Menu menu) {
        MenuEntity entity = MenuEntity.from(menu);
        MenuEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<Menu> findById(Long id) {
        return jpaRepository.findById(id)
            .map(MenuEntity::toDomain);
    }

    @Override
    public List<Menu> findAllByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return jpaRepository.findAllById(ids).stream()
            .map(MenuEntity::toDomain)
            .toList();
    }

    @Override
    public List<Menu> findByStoreId(Long storeId) {
        return jpaRepository.findByStoreId(storeId).stream()
            .map(MenuEntity::toDomain)
            .toList();
    }

    @Override
    public List<Menu> findByCategoryId(Long categoryId) {
        return jpaRepository.findByCategoryId(categoryId).stream()
            .map(MenuEntity::toDomain)
            .toList();
    }

    @Override
    public List<Menu> findByStoreIdAndCategoryId(Long storeId, Long categoryId) {
        return jpaRepository.findByStoreIdAndCategoryId(storeId, categoryId).stream()
            .map(MenuEntity::toDomain)
            .toList();
    }

    @Override
    public List<Menu> findAvailableByStoreId(Long storeId) {
        return jpaRepository.findByStoreIdAndIsSoldOutFalse(storeId).stream()
            .map(MenuEntity::toDomain)
            .toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public int decreaseStock(Long id, Long quantity) {
        return jpaRepository.decreaseStock(id, quantity);
    }

    @Override
    public int increaseStock(Long id, Long quantity) {
        return jpaRepository.increaseStock(id, quantity);
    }

    @Override
    public Optional<Menu> findByIdWithLock(Long id) {
        return jpaRepository.findByIdWithLock(id)
            .map(MenuEntity::toDomain);
    }
}
