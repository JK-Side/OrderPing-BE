package com.orderping.infra.payment.repository;

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
import com.orderping.domain.enums.PaymentMethod;
import com.orderping.domain.enums.PaymentStatus;
import com.orderping.domain.enums.Role;
import com.orderping.domain.enums.TableStatus;
import com.orderping.domain.order.Order;
import com.orderping.domain.payment.Payment;
import com.orderping.domain.store.Store;
import com.orderping.domain.store.StoreTable;
import com.orderping.domain.user.User;
import com.orderping.infra.config.TestConfig;
import com.orderping.infra.order.repository.OrderRepositoryImpl;
import com.orderping.infra.store.repository.StoreRepositoryImpl;
import com.orderping.infra.store.repository.StoreTableRepositoryImpl;
import com.orderping.infra.user.repository.UserRepositoryImpl;

@SpringBootTest(classes = TestConfig.class)
@Transactional
class PaymentRepositoryImplTest {

    @Autowired
    private PaymentRepositoryImpl paymentRepository;

    @Autowired
    private OrderRepositoryImpl orderRepository;

    @Autowired
    private StoreTableRepositoryImpl storeTableRepository;

    @Autowired
    private StoreRepositoryImpl storeRepository;

    @Autowired
    private UserRepositoryImpl userRepository;

    private Order savedOrder;

    @BeforeEach
    void setUp() {
        User savedUser = userRepository.save(User.builder()
            .role(Role.OWNER)
            .nickname("사장님")
            .build());

        Store savedStore = storeRepository.save(Store.builder()
            .userId(savedUser.getId())
            .name("테스트 가게")
            .isOpen(true)
            .build());

        StoreTable savedTable = storeTableRepository.save(StoreTable.builder()
            .storeId(savedStore.getId())
            .tableNum(1)
            .status(TableStatus.OCCUPIED)
            .build());

        savedOrder = orderRepository.save(Order.builder()
            .tableId(savedTable.getId())
            .tableNum(savedTable.getTableNum())
            .storeId(savedStore.getId())
            .sessionId("session-123")
            .status(OrderStatus.PENDING)
            .totalPrice(25000L)
            .build());
    }

    @Test
    @DisplayName("결제 저장 및 조회 테스트")
    void saveAndFindPayment() {
        // given
        Payment payment = Payment.builder()
            .orderId(savedOrder.getId())
            .method(PaymentMethod.CASH)
            .amount(25000L)
            .status(PaymentStatus.COMPLETED)
            .build();

        // when
        Payment savedPayment = paymentRepository.save(payment);

        // then
        assertNotNull(savedPayment.getId());
        assertEquals(PaymentMethod.CASH, savedPayment.getMethod());
        assertEquals(25000L, savedPayment.getAmount());
        assertEquals(PaymentStatus.COMPLETED, savedPayment.getStatus());
    }

    @Test
    @DisplayName("부분 결제 테스트 - 쿠폰 + 현금")
    void partialPayment() {
        // given
        paymentRepository.save(Payment.builder()
            .orderId(savedOrder.getId())
            .method(PaymentMethod.COUPON)
            .amount(5000L)
            .status(PaymentStatus.COMPLETED)
            .build());

        paymentRepository.save(Payment.builder()
            .orderId(savedOrder.getId())
            .method(PaymentMethod.CASH)
            .amount(20000L)
            .status(PaymentStatus.COMPLETED)
            .build());

        // when
        List<Payment> payments = paymentRepository.findByOrderId(savedOrder.getId());

        // then
        assertEquals(2, payments.size());

        long totalPaid = payments.stream()
            .mapToLong(Payment::getAmount)
            .sum();
        assertEquals(25000L, totalPaid);
    }

    @Test
    @DisplayName("주문 ID와 상태로 결제 조회")
    void findByOrderIdAndStatus() {
        // given
        paymentRepository.save(Payment.builder()
            .orderId(savedOrder.getId())
            .method(PaymentMethod.COUPON)
            .amount(5000L)
            .status(PaymentStatus.COMPLETED)
            .build());

        paymentRepository.save(Payment.builder()
            .orderId(savedOrder.getId())
            .method(PaymentMethod.CASH)
            .amount(20000L)
            .status(PaymentStatus.PENDING)
            .build());

        // when
        List<Payment> completedPayments = paymentRepository.findByOrderIdAndStatus(
            savedOrder.getId(), PaymentStatus.COMPLETED);

        // then
        assertEquals(1, completedPayments.size());
        assertEquals(PaymentMethod.COUPON, completedPayments.get(0).getMethod());
    }
}
