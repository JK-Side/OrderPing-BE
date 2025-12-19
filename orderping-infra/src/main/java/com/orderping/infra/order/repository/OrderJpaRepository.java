package com.orderping.infra.order.repository;

import com.orderping.domain.enums.OrderStatus;
import com.orderping.infra.order.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, Long> {

    List<OrderEntity> findByStoreId(Long storeId);

    List<OrderEntity> findByTableId(Long tableId);

    List<OrderEntity> findBySessionId(String sessionId);

    List<OrderEntity> findByStoreIdAndStatus(Long storeId, OrderStatus status);
}
