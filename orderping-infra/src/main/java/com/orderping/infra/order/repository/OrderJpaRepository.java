package com.orderping.infra.order.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderping.domain.enums.OrderStatus;
import com.orderping.infra.order.entity.OrderEntity;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, Long> {

    List<OrderEntity> findByStoreId(Long storeId);

    List<OrderEntity> findByTableId(Long tableId);

    List<OrderEntity> findBySessionId(String sessionId);

    List<OrderEntity> findByStoreIdAndStatus(Long storeId, OrderStatus status);
}
