package com.orderping.domain.order;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.orderping.domain.enums.OrderStatus;

class OrderTest {

    @Test
    @DisplayName("Order 객체 생성 테스트")
    void createOrder() {
        // given
        LocalDateTime now = LocalDateTime.now();
        String sessionId = "abc123-def456";

        // when
        Order order = Order.builder()
            .id(1L)
            .tableId(1L)
            .storeId(1L)
            .sessionId(sessionId)
            .status(OrderStatus.PENDING)
            .totalPrice(25000L)
            .createdAt(now)
            .build();

        // then
        assertNotNull(order);
        assertEquals(1L, order.getId());
        assertEquals(1L, order.getTableId());
        assertEquals(1L, order.getStoreId());
        assertEquals(sessionId, order.getSessionId());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(25000L, order.getTotalPrice());
    }

    @Test
    @DisplayName("주문 상태 흐름 테스트")
    void orderStatusFlow() {
        // PENDING -> COOKING -> COMPLETE
        String sessionId = "session-001";

        Order pendingOrder = Order.builder()
            .id(1L)
            .tableId(1L)
            .storeId(1L)
            .sessionId(sessionId)
            .status(OrderStatus.PENDING)
            .totalPrice(10000L)
            .build();

        Order cookingOrder = Order.builder()
            .id(2L)
            .tableId(1L)
            .storeId(1L)
            .sessionId(sessionId)
            .status(OrderStatus.COOKING)
            .totalPrice(10000L)
            .build();

        Order completeOrder = Order.builder()
            .id(3L)
            .tableId(1L)
            .storeId(1L)
            .sessionId(sessionId)
            .status(OrderStatus.COMPLETE)
            .totalPrice(10000L)
            .build();

        assertEquals(OrderStatus.PENDING, pendingOrder.getStatus());
        assertEquals(OrderStatus.COOKING, cookingOrder.getStatus());
        assertEquals(OrderStatus.COMPLETE, completeOrder.getStatus());
    }

    @Test
    @DisplayName("같은 테이블 다른 손님 구분 테스트")
    void differentSessionSameTable() {
        // given
        String sessionA = "customer-a-session";
        String sessionB = "customer-b-session";

        // when
        Order orderA = Order.builder()
            .id(1L)
            .tableId(1L)
            .storeId(1L)
            .sessionId(sessionA)
            .status(OrderStatus.COMPLETE)
            .totalPrice(20000L)
            .build();

        Order orderB = Order.builder()
            .id(2L)
            .tableId(1L)
            .storeId(1L)
            .sessionId(sessionB)
            .status(OrderStatus.PENDING)
            .totalPrice(15000L)
            .build();

        // then
        assertEquals(orderA.getTableId(), orderB.getTableId());
        assertNotEquals(orderA.getSessionId(), orderB.getSessionId());
    }
}
