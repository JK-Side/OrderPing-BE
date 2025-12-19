package com.orderping.infra.menu.repository;

import com.orderping.infra.menu.entity.MenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuJpaRepository extends JpaRepository<MenuEntity, Long> {

    List<MenuEntity> findByStoreId(Long storeId);

    List<MenuEntity> findByCategoryId(Long categoryId);

    List<MenuEntity> findByStoreIdAndIsSoldOutFalse(Long storeId);
}
