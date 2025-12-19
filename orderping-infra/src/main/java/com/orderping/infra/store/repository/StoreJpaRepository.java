package com.orderping.infra.store.repository;

import com.orderping.infra.store.entity.StoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreJpaRepository extends JpaRepository<StoreEntity, Long> {

    List<StoreEntity> findByUserId(Long userId);
}
