package com.orderping.infra.order.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderping.infra.order.entity.OrderMenuEntity;

public interface OrderMenuJpaRepository extends JpaRepository<OrderMenuEntity, Long> {

    List<OrderMenuEntity> findByOrderId(Long orderId);
}
