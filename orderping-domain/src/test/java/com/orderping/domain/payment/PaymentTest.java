package com.orderping.domain.payment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.orderping.domain.enums.PaymentMethod;
import com.orderping.domain.enums.PaymentStatus;

class PaymentTest {

    @Test
    @DisplayName("Payment 객체 생성 테스트 - 현금 결제")
    void createCashPayment() {
        // given
        LocalDateTime now = LocalDateTime.now();

        // when
        Payment payment = Payment.builder()
            .id(1L)
            .orderId(1L)
            .method(PaymentMethod.CASH)
            .amount(25000L)
            .status(PaymentStatus.COMPLETED)
            .createdAt(now)
            .build();

        // then
        assertNotNull(payment);
        assertEquals(1L, payment.getId());
        assertEquals(1L, payment.getOrderId());
        assertEquals(PaymentMethod.CASH, payment.getMethod());
        assertEquals(25000L, payment.getAmount());
        assertEquals(PaymentStatus.COMPLETED, payment.getStatus());
    }

    @Test
    @DisplayName("Payment 객체 생성 테스트 - 쿠폰 결제")
    void createCouponPayment() {
        // when
        Payment payment = Payment.builder()
            .id(2L)
            .orderId(1L)
            .method(PaymentMethod.COUPON)
            .amount(5000L)
            .status(PaymentStatus.COMPLETED)
            .build();

        // then
        assertEquals(PaymentMethod.COUPON, payment.getMethod());
        assertEquals(5000L, payment.getAmount());
    }

    @Test
    @DisplayName("부분 결제 테스트 - 쿠폰 + 현금")
    void partialPaymentTest() {
        // given
        Long totalOrderPrice = 25000L;
        Long couponAmount = 5000L;
        Long cashAmount = totalOrderPrice - couponAmount;

        // when
        Payment couponPayment = Payment.builder()
            .id(1L)
            .orderId(1L)
            .method(PaymentMethod.COUPON)
            .amount(couponAmount)
            .status(PaymentStatus.COMPLETED)
            .build();

        Payment cashPayment = Payment.builder()
            .id(2L)
            .orderId(1L)
            .method(PaymentMethod.CASH)
            .amount(cashAmount)
            .status(PaymentStatus.COMPLETED)
            .build();

        // then
        assertEquals(5000L, couponPayment.getAmount());
        assertEquals(20000L, cashPayment.getAmount());
        assertEquals(totalOrderPrice, couponPayment.getAmount() + cashPayment.getAmount());
    }
}
