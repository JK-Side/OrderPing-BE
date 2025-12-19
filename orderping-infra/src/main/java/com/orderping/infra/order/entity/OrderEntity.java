package com.orderping.infra.order.entity;

import com.orderping.domain.enums.OrderStatus;
import com.orderping.domain.order.Order;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @Column(name = "table_id", nullable = false)
    private Long tableId;

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @Column(name = "session_id", nullable = false, length = 36)
    private String sessionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    @Column(name = "total_price", nullable = false)
    private Long totalPrice;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public OrderEntity(Long id, Long tableId, Long storeId, String sessionId, OrderStatus status, Long totalPrice, LocalDateTime createdAt) {
        this.id = id;
        this.tableId = tableId;
        this.storeId = storeId;
        this.sessionId = sessionId;
        this.status = status;
        this.totalPrice = totalPrice;
        this.createdAt = createdAt;
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
    }

    // Domain -> Entity
    public static OrderEntity from(Order order) {
        return OrderEntity.builder()
                .id(order.getId())
                .tableId(order.getTableId())
                .storeId(order.getStoreId())
                .sessionId(order.getSessionId())
                .status(order.getStatus())
                .totalPrice(order.getTotalPrice())
                .createdAt(order.getCreatedAt())
                .build();
    }

    // Entity -> Domain
    public Order toDomain() {
        return Order.builder()
                .id(this.id)
                .tableId(this.tableId)
                .storeId(this.storeId)
                .sessionId(this.sessionId)
                .status(this.status)
                .totalPrice(this.totalPrice)
                .createdAt(this.createdAt)
                .build();
    }
}
