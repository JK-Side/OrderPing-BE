package com.orderping.infra.store.repository;

import com.orderping.domain.store.Store;
import com.orderping.domain.store.repository.StoreRepository;
import com.orderping.infra.store.entity.StoreEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class StoreRepositoryImpl implements StoreRepository {

    private final StoreJpaRepository jpaRepository;

    @Override
    public Store save(Store store) {
        StoreEntity entity = StoreEntity.from(store);
        StoreEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<Store> findById(Long id) {
        return jpaRepository.findById(id)
                .map(StoreEntity::toDomain);
    }

    @Override
    public List<Store> findByUserId(Long userId) {
        return jpaRepository.findByUserId(userId).stream()
                .map(StoreEntity::toDomain)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
