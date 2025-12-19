package com.orderping.domain.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderMenuTest {

    @Test
    @DisplayName("OrderMenu 객체 생성 테스트")
    void createOrderMenu() {
        // when
        OrderMenu orderMenu = OrderMenu.builder()
                .id(1L)
                .orderId(1L)
                .menuId(1L)
                .quantity(2L)
                .price(10000L)
                .build();

        // then
        assertNotNull(orderMenu);
        assertEquals(1L, orderMenu.getId());
        assertEquals(1L, orderMenu.getOrderId());
        assertEquals(1L, orderMenu.getMenuId());
        assertEquals(2L, orderMenu.getQuantity());
        assertEquals(10000L, orderMenu.getPrice());
    }

    @Test
    @DisplayName("주문 메뉴의 총 금액 계산")
    void calculateTotalPrice() {
        // given
        Long quantity = 3L;
        Long unitPrice = 8000L;

        // when
        OrderMenu orderMenu = OrderMenu.builder()
                .id(1L)
                .orderId(1L)
                .menuId(1L)
                .quantity(quantity)
                .price(unitPrice * quantity)
                .build();

        // then
        assertEquals(24000L, orderMenu.getPrice());
    }
}
