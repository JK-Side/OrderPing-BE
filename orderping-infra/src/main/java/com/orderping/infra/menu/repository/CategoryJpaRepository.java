package com.orderping.infra.menu.repository;

import com.orderping.infra.menu.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryJpaRepository extends JpaRepository<CategoryEntity, Long> {

    List<CategoryEntity> findByStoreId(Long storeId);
}
