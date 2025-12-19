package com.orderping.domain.store.repository;

import com.orderping.domain.store.Store;

import java.util.List;
import java.util.Optional;

public interface StoreRepository {

    Store save(Store store);

    Optional<Store> findById(Long id);

    List<Store> findByUserId(Long userId);

    void deleteById(Long id);
}
