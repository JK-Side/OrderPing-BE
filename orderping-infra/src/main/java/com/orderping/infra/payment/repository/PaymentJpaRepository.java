package com.orderping.infra.payment.repository;

import com.orderping.domain.enums.PaymentStatus;
import com.orderping.infra.payment.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentJpaRepository extends JpaRepository<PaymentEntity, Long> {

    List<PaymentEntity> findByOrderId(Long orderId);

    List<PaymentEntity> findByOrderIdAndStatus(Long orderId, PaymentStatus status);
}
