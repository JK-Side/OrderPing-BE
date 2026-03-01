package com.orderping.infra.menu.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.orderping.infra.menu.entity.MenuEntity;

import jakarta.persistence.LockModeType;

public interface MenuJpaRepository extends JpaRepository<MenuEntity, Long> {

    List<MenuEntity> findByStoreId(Long storeId);

    List<MenuEntity> findByCategoryId(Long categoryId);

    List<MenuEntity> findByStoreIdAndCategoryId(Long storeId, Long categoryId);

    List<MenuEntity> findByStoreIdAndIsSoldOutFalse(Long storeId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT m FROM MenuEntity m WHERE m.id = :id")
    Optional<MenuEntity> findByIdWithLock(@Param("id") Long id);

    @Modifying
    @Query("UPDATE MenuEntity m SET m.stock = m.stock - :quantity WHERE m.id = :id AND m.stock >= :quantity")
    int decreaseStock(@Param("id") Long id, @Param("quantity") Long quantity);

    @Modifying
    @Query("UPDATE MenuEntity m SET m.stock = m.stock + :quantity, m.isSoldOut = false WHERE m.id = :id")
    int increaseStock(@Param("id") Long id, @Param("quantity") Long quantity);
}
