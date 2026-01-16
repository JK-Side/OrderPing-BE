package com.orderping.domain.menu.repository;

import java.util.List;
import java.util.Optional;

import com.orderping.domain.menu.Menu;

public interface MenuRepository {

    Menu save(Menu menu);

    Optional<Menu> findById(Long id);

    List<Menu> findByStoreId(Long storeId);

    List<Menu> findByCategoryId(Long categoryId);

    List<Menu> findByStoreIdAndCategoryId(Long storeId, Long categoryId);

    List<Menu> findAvailableByStoreId(Long storeId);

    void deleteById(Long id);

    int decreaseStock(Long id, Long quantity);

    Optional<Menu> findByIdWithLock(Long id);
}
