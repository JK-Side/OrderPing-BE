package com.orderping.infra.order.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.orderping.domain.enums.OrderStatus;
import com.orderping.domain.order.Order;
import com.orderping.domain.order.repository.OrderRepository;
import com.orderping.infra.order.entity.OrderEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository jpaRepository;

    @Override
    public Order save(Order order) {
        OrderEntity entity = OrderEntity.from(order);
        OrderEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<Order> findById(Long id) {
        return jpaRepository.findById(id)
            .map(OrderEntity::toDomain);
    }

    @Override
    public List<Order> findByStoreId(Long storeId) {
        return jpaRepository.findByStoreId(storeId).stream()
            .map(OrderEntity::toDomain)
            .toList();
    }

    @Override
    public List<Order> findByTableId(Long tableId) {
        return jpaRepository.findByTableId(tableId).stream()
            .map(OrderEntity::toDomain)
            .toList();
    }

    @Override
    public List<Order> findByStoreIdAndStatus(Long storeId, OrderStatus status) {
        return jpaRepository.findByStoreIdAndStatus(storeId, status).stream()
            .map(OrderEntity::toDomain)
            .toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
