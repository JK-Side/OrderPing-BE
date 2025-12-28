package com.orderping.infra.store.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderping.infra.store.entity.StoreEntity;

public interface StoreJpaRepository extends JpaRepository<StoreEntity, Long> {

    List<StoreEntity> findByUserId(Long userId);
}
