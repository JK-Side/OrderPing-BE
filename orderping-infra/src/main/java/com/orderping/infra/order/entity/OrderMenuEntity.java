package com.orderping.infra.order.entity;

import com.orderping.domain.order.OrderMenu;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_menus")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderMenuEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_menu_id")
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "menu_id", nullable = false)
    private Long menuId;

    @Column(nullable = false)
    private Long quantity;

    @Column(nullable = false)
    private Long price;

    @Builder
    public OrderMenuEntity(Long id, Long orderId, Long menuId, Long quantity, Long price) {
        this.id = id;
        this.orderId = orderId;
        this.menuId = menuId;
        this.quantity = quantity;
        this.price = price;
    }

    // Domain -> Entity
    public static OrderMenuEntity from(OrderMenu orderMenu) {
        return OrderMenuEntity.builder()
                .id(orderMenu.getId())
                .orderId(orderMenu.getOrderId())
                .menuId(orderMenu.getMenuId())
                .quantity(orderMenu.getQuantity())
                .price(orderMenu.getPrice())
                .build();
    }

    // Entity -> Domain
    public OrderMenu toDomain() {
        return OrderMenu.builder()
                .id(this.id)
                .orderId(this.orderId)
                .menuId(this.menuId)
                .quantity(this.quantity)
                .price(this.price)
                .build();
    }
}
