package com.orderping.domain.store.repository;

import java.util.List;
import java.util.Optional;

import com.orderping.domain.store.Store;

public interface StoreRepository {

    Store save(Store store);

    Optional<Store> findById(Long id);

    List<Store> findByUserId(Long userId);

    void deleteById(Long id);
}
