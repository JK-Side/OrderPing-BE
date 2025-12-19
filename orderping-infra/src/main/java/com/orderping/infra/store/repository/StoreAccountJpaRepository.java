package com.orderping.infra.store.repository;

import com.orderping.infra.store.entity.StoreAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreAccountJpaRepository extends JpaRepository<StoreAccountEntity, Long> {

    List<StoreAccountEntity> findByStoreId(Long storeId);

    List<StoreAccountEntity> findByStoreIdAndIsActiveTrue(Long storeId);
}
