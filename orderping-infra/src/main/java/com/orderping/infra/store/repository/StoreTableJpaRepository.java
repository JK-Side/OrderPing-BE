package com.orderping.infra.store.repository;

import com.orderping.domain.enums.TableStatus;
import com.orderping.infra.store.entity.StoreTableEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StoreTableJpaRepository extends JpaRepository<StoreTableEntity, Long> {

    List<StoreTableEntity> findByStoreId(Long storeId);

    List<StoreTableEntity> findByStoreIdAndStatus(Long storeId, TableStatus status);

    Optional<StoreTableEntity> findByStoreIdAndTableNum(Long storeId, Integer tableNum);
}
