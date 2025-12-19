package com.orderping.infra.payment.entity;

import com.orderping.domain.enums.PaymentMethod;
import com.orderping.domain.enums.PaymentStatus;
import com.orderping.domain.payment.Payment;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentMethod method;

    @Column(nullable = false)
    private Long amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public PaymentEntity(Long id, Long orderId, PaymentMethod method, Long amount, PaymentStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.orderId = orderId;
        this.method = method;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = PaymentStatus.PENDING;
        }
    }

    // Domain -> Entity
    public static PaymentEntity from(Payment payment) {
        return PaymentEntity.builder()
                .id(payment.getId())
                .orderId(payment.getOrderId())
                .method(payment.getMethod())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .createdAt(payment.getCreatedAt())
                .build();
    }

    // Entity -> Domain
    public Payment toDomain() {
        return Payment.builder()
                .id(this.id)
                .orderId(this.orderId)
                .method(this.method)
                .amount(this.amount)
                .status(this.status)
                .createdAt(this.createdAt)
                .build();
    }
}
