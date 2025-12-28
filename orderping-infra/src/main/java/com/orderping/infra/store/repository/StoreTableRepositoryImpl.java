package com.orderping.infra.store.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.orderping.domain.enums.TableStatus;
import com.orderping.domain.store.StoreTable;
import com.orderping.domain.store.repository.StoreTableRepository;
import com.orderping.infra.store.entity.StoreTableEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class StoreTableRepositoryImpl implements StoreTableRepository {

    private final StoreTableJpaRepository jpaRepository;

    @Override
    public StoreTable save(StoreTable storeTable) {
        StoreTableEntity entity = StoreTableEntity.from(storeTable);
        StoreTableEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<StoreTable> findById(Long id) {
        return jpaRepository.findById(id)
            .map(StoreTableEntity::toDomain);
    }

    @Override
    public List<StoreTable> findByStoreId(Long storeId) {
        return jpaRepository.findByStoreId(storeId).stream()
            .map(StoreTableEntity::toDomain)
            .toList();
    }

    @Override
    public List<StoreTable> findByStoreIdAndStatus(Long storeId, TableStatus status) {
        return jpaRepository.findByStoreIdAndStatus(storeId, status).stream()
            .map(StoreTableEntity::toDomain)
            .toList();
    }

    @Override
    public Optional<StoreTable> findByStoreIdAndTableNum(Long storeId, Integer tableNum) {
        return jpaRepository.findByStoreIdAndTableNum(storeId, tableNum)
            .map(StoreTableEntity::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
