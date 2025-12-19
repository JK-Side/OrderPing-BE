package com.orderping.infra.store.repository;

import com.orderping.domain.store.StoreAccount;
import com.orderping.domain.store.repository.StoreAccountRepository;
import com.orderping.infra.store.entity.StoreAccountEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class StoreAccountRepositoryImpl implements StoreAccountRepository {

    private final StoreAccountJpaRepository jpaRepository;

    @Override
    public StoreAccount save(StoreAccount storeAccount) {
        StoreAccountEntity entity = StoreAccountEntity.from(storeAccount);
        StoreAccountEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<StoreAccount> findById(Long id) {
        return jpaRepository.findById(id)
                .map(StoreAccountEntity::toDomain);
    }

    @Override
    public List<StoreAccount> findByStoreId(Long storeId) {
        return jpaRepository.findByStoreId(storeId).stream()
                .map(StoreAccountEntity::toDomain)
                .toList();
    }

    @Override
    public List<StoreAccount> findActiveByStoreId(Long storeId) {
        return jpaRepository.findByStoreIdAndIsActiveTrue(storeId).stream()
                .map(StoreAccountEntity::toDomain)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
