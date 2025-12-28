package com.orderping.infra.payment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderping.domain.enums.PaymentStatus;
import com.orderping.infra.payment.entity.PaymentEntity;

public interface PaymentJpaRepository extends JpaRepository<PaymentEntity, Long> {

    List<PaymentEntity> findByOrderId(Long orderId);

    List<PaymentEntity> findByOrderIdAndStatus(Long orderId, PaymentStatus status);
}
