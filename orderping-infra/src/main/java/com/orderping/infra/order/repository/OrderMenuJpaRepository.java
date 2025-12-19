package com.orderping.infra.order.repository;

import com.orderping.infra.order.entity.OrderMenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderMenuJpaRepository extends JpaRepository<OrderMenuEntity, Long> {

    List<OrderMenuEntity> findByOrderId(Long orderId);
}
