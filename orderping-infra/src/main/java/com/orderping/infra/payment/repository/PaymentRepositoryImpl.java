package com.orderping.infra.payment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.orderping.domain.enums.PaymentStatus;
import com.orderping.domain.payment.Payment;
import com.orderping.domain.payment.repository.PaymentRepository;
import com.orderping.infra.payment.entity.PaymentEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository jpaRepository;

    @Override
    public Payment save(Payment payment) {
        PaymentEntity entity = PaymentEntity.from(payment);
        PaymentEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<Payment> findById(Long id) {
        return jpaRepository.findById(id)
            .map(PaymentEntity::toDomain);
    }

    @Override
    public List<Payment> findByOrderId(Long orderId) {
        return jpaRepository.findByOrderId(orderId).stream()
            .map(PaymentEntity::toDomain)
            .toList();
    }

    @Override
    public List<Payment> findByOrderIdAndStatus(Long orderId, PaymentStatus status) {
        return jpaRepository.findByOrderIdAndStatus(orderId, status).stream()
            .map(PaymentEntity::toDomain)
            .toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
