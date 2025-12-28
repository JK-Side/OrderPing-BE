package com.orderping.infra.menu.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderping.infra.menu.entity.CategoryEntity;

public interface CategoryJpaRepository extends JpaRepository<CategoryEntity, Long> {
}
