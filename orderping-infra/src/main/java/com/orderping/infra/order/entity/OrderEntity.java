package com.orderping.infra.order.entity;

import java.time.LocalDateTime;

import com.orderping.domain.enums.OrderStatus;
import com.orderping.domain.order.Order;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders", indexes = {
    @Index(name = "idx_orders_table_id", columnList = "table_id"),
    @Index(name = "idx_orders_store_id_status", columnList = "store_id, status"),
    @Index(name = "idx_orders_store_id_created_at", columnList = "store_id, created_at")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @Column(name = "table_id", nullable = false)
    private Long tableId;

    @Column(name = "table_num", nullable = false)
    private Integer tableNum;

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @Column(name = "depositor_name", length = 50)
    private String depositorName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    @Column(name = "total_price", nullable = false)
    private Long totalPrice;

    @Column(name = "coupon_amount", nullable = false)
    private Long couponAmount;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "idempotency_key", unique = true, length = 100)
    private String idempotencyKey;

    @Builder
    public OrderEntity(Long id, Long tableId, Integer tableNum, Long storeId, String depositorName,
        OrderStatus status,
        Long totalPrice, Long couponAmount, LocalDateTime createdAt, String idempotencyKey) {
        this.id = id;
        this.tableId = tableId;
        this.tableNum = tableNum;
        this.storeId = storeId;
        this.depositorName = depositorName;
        this.status = status;
        this.totalPrice = totalPrice;
        this.couponAmount = couponAmount;
        this.createdAt = createdAt;
        this.idempotencyKey = idempotencyKey;
    }

    // Domain -> Entity
    public static OrderEntity from(Order order) {
        return OrderEntity.builder()
            .id(order.getId())
            .tableId(order.getTableId())
            .tableNum(order.getTableNum())
            .storeId(order.getStoreId())
            .depositorName(order.getDepositorName())
            .status(order.getStatus())
            .totalPrice(order.getTotalPrice())
            .couponAmount(order.getCouponAmount())
            .createdAt(order.getCreatedAt())
            .idempotencyKey(order.getIdempotencyKey())
            .build();
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = OrderStatus.PENDING;
        }
        if (this.totalPrice == null) {
            this.totalPrice = 0L;
        }
        if (this.couponAmount == null) {
            this.couponAmount = 0L;
        }
    }

    // Entity -> Domain
    public Order toDomain() {
        return Order.builder()
            .id(this.id)
            .tableId(this.tableId)
            .tableNum(this.tableNum)
            .storeId(this.storeId)
            .depositorName(this.depositorName)
            .status(this.status)
            .totalPrice(this.totalPrice)
            .couponAmount(this.couponAmount)
            .createdAt(this.createdAt)
            .idempotencyKey(this.idempotencyKey)
            .build();
    }
}
