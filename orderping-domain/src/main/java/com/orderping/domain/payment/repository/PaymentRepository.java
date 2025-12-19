package com.orderping.domain.payment.repository;

import com.orderping.domain.enums.PaymentStatus;
import com.orderping.domain.payment.Payment;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository {

    Payment save(Payment payment);

    Optional<Payment> findById(Long id);

    List<Payment> findByOrderId(Long orderId);

    List<Payment> findByOrderIdAndStatus(Long orderId, PaymentStatus status);

    void deleteById(Long id);
}
