package com.orderping.infra.menu.repository;

import com.orderping.infra.menu.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryJpaRepository extends JpaRepository<CategoryEntity, Long> {
}
