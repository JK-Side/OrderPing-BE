package com.orderping.infra.menu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.orderping.infra.menu.entity.MenuEntity;

public interface MenuJpaRepository extends JpaRepository<MenuEntity, Long> {

    List<MenuEntity> findByStoreId(Long storeId);

    List<MenuEntity> findByCategoryId(Long categoryId);

    List<MenuEntity> findByStoreIdAndIsSoldOutFalse(Long storeId);

    @Modifying
    @Query("UPDATE MenuEntity m SET m.stock = m.stock - :quantity WHERE m.id = :id AND m.stock >= :quantity")
    int decreaseStock(@Param("id") Long id, @Param("quantity") Long quantity);
}
