package com.orderping.infra.store.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderping.infra.store.entity.StoreAccountEntity;

public interface StoreAccountJpaRepository extends JpaRepository<StoreAccountEntity, Long> {

    List<StoreAccountEntity> findByStoreId(Long storeId);

    List<StoreAccountEntity> findByStoreIdAndIsActiveTrue(Long storeId);
}
