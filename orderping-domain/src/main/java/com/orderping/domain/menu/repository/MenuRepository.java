package com.orderping.domain.menu.repository;

import com.orderping.domain.menu.Menu;

import java.util.List;
import java.util.Optional;

public interface MenuRepository {

    Menu save(Menu menu);

    Optional<Menu> findById(Long id);

    List<Menu> findByStoreId(Long storeId);

    List<Menu> findByCategoryId(Long categoryId);

    List<Menu> findAvailableByStoreId(Long storeId);

    void deleteById(Long id);
}
