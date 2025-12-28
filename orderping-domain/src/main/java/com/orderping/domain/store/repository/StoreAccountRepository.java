package com.orderping.domain.store.repository;

import java.util.List;
import java.util.Optional;

import com.orderping.domain.store.StoreAccount;

public interface StoreAccountRepository {

    StoreAccount save(StoreAccount storeAccount);

    Optional<StoreAccount> findById(Long id);

    List<StoreAccount> findByStoreId(Long storeId);

    List<StoreAccount> findActiveByStoreId(Long storeId);

    void deleteById(Long id);
}
