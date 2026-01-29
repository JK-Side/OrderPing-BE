package com.orderping.infra.order.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.orderping.domain.enums.OrderStatus;
import com.orderping.domain.enums.Role;
import com.orderping.domain.enums.TableStatus;
import com.orderping.domain.order.Order;
import com.orderping.domain.store.Store;
import com.orderping.domain.store.StoreTable;
import com.orderping.domain.user.User;
import com.orderping.infra.config.TestConfig;
import com.orderping.infra.store.repository.StoreRepositoryImpl;
import com.orderping.infra.store.repository.StoreTableRepositoryImpl;
import com.orderping.infra.user.repository.UserRepositoryImpl;

@SpringBootTest(classes = TestConfig.class)
@Transactional
class OrderRepositoryImplTest {

    @Autowired
    private OrderRepositoryImpl orderRepository;

    @Autowired
    private StoreTableRepositoryImpl storeTableRepository;

    @Autowired
    private StoreRepositoryImpl storeRepository;

    @Autowired
    private UserRepositoryImpl userRepository;

    private Store savedStore;
    private StoreTable savedTable;

    @BeforeEach
    void setUp() {
        User savedUser = userRepository.save(User.builder()
            .role(Role.OWNER)
            .nickname("사장님")
            .build());

        savedStore = storeRepository.save(Store.builder()
            .userId(savedUser.getId())
            .name("테스트 가게")
            .isOpen(true)
            .build());

        savedTable = storeTableRepository.save(StoreTable.builder()
            .storeId(savedStore.getId())
            .tableNum(1)
            .status(TableStatus.OCCUPIED)
            .build());
    }

    @Test
    @DisplayName("주문 저장 및 조회 테스트")
    void saveAndFindOrder() {
        // given
        Order order = Order.builder()
            .tableId(savedTable.getId())
            .tableNum(savedTable.getTableNum())
            .storeId(savedStore.getId())
            .sessionId("session-123")
            .status(OrderStatus.PENDING)
            .totalPrice(25000L)
            .build();

        // when
        Order savedOrder = orderRepository.save(order);

        // then
        assertNotNull(savedOrder.getId());
        assertEquals(OrderStatus.PENDING, savedOrder.getStatus());
        assertEquals(25000L, savedOrder.getTotalPrice());
        assertEquals("session-123", savedOrder.getSessionId());
    }

    @Test
    @DisplayName("세션 ID로 주문 조회")
    void findBySessionId() {
        // given
        String sessionId = "customer-session-abc";

        orderRepository.save(Order.builder()
            .tableId(savedTable.getId())
            .tableNum(savedTable.getTableNum())
            .storeId(savedStore.getId())
            .sessionId(sessionId)
            .status(OrderStatus.PENDING)
            .totalPrice(10000L)
            .build());

        orderRepository.save(Order.builder()
            .tableId(savedTable.getId())
            .tableNum(savedTable.getTableNum())
            .storeId(savedStore.getId())
            .sessionId(sessionId)
            .status(OrderStatus.COOKING)
            .totalPrice(15000L)
            .build());

        // when
        List<Order> orders = orderRepository.findBySessionId(sessionId);

        // then
        assertEquals(2, orders.size());
    }

    @Test
    @DisplayName("가게 ID와 상태로 주문 조회")
    void findByStoreIdAndStatus() {
        // given
        orderRepository.save(Order.builder()
            .tableId(savedTable.getId())
            .tableNum(savedTable.getTableNum())
            .storeId(savedStore.getId())
            .sessionId("session-1")
            .status(OrderStatus.PENDING)
            .totalPrice(10000L)
            .build());

        orderRepository.save(Order.builder()
            .tableId(savedTable.getId())
            .tableNum(savedTable.getTableNum())
            .storeId(savedStore.getId())
            .sessionId("session-2")
            .status(OrderStatus.COOKING)
            .totalPrice(20000L)
            .build());

        orderRepository.save(Order.builder()
            .tableId(savedTable.getId())
            .tableNum(savedTable.getTableNum())
            .storeId(savedStore.getId())
            .sessionId("session-3")
            .status(OrderStatus.PENDING)
            .totalPrice(30000L)
            .build());

        // when
        List<Order> pendingOrders = orderRepository.findByStoreIdAndStatus(
            savedStore.getId(), OrderStatus.PENDING);

        // then
        assertEquals(2, pendingOrders.size());
    }
}
