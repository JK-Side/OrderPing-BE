package com.orderping.domain.store.repository;

import java.util.List;
import java.util.Optional;

import com.orderping.domain.enums.TableStatus;
import com.orderping.domain.store.StoreTable;

public interface StoreTableRepository {

    StoreTable save(StoreTable storeTable);

    Optional<StoreTable> findById(Long id);

    List<StoreTable> findByStoreId(Long storeId);

    List<StoreTable> findByStoreIdAndStatus(Long storeId, TableStatus status);

    List<StoreTable> findByStoreIdAndStatusNot(Long storeId, TableStatus status);

    Optional<StoreTable> findByStoreIdAndTableNum(Long storeId, Integer tableNum);

    Optional<StoreTable> findActiveByStoreIdAndTableNum(Long storeId, Integer tableNum);

    List<StoreTable> findAllActiveByStoreIdAndTableNum(Long storeId, Integer tableNum);

    List<StoreTable> findAllByIds(List<Long> ids);

    void deleteById(Long id);
}
