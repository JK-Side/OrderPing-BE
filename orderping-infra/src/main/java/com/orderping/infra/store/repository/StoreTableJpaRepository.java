package com.orderping.infra.store.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderping.domain.enums.TableStatus;
import com.orderping.infra.store.entity.StoreTableEntity;

public interface StoreTableJpaRepository extends JpaRepository<StoreTableEntity, Long> {

    List<StoreTableEntity> findByStoreId(Long storeId);

    List<StoreTableEntity> findByStoreIdAndStatus(Long storeId, TableStatus status);

    List<StoreTableEntity> findByStoreIdAndStatusNot(Long storeId, TableStatus status);

    Optional<StoreTableEntity> findByStoreIdAndTableNum(Long storeId, Integer tableNum);

    Optional<StoreTableEntity> findByStoreIdAndTableNumAndStatusNot(Long storeId, Integer tableNum, TableStatus status);
}
