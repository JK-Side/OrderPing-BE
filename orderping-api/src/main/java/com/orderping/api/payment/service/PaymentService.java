package com.orderping.api.payment.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderping.api.payment.dto.PaymentCreateRequest;
import com.orderping.api.payment.dto.PaymentResponse;
import com.orderping.domain.enums.PaymentStatus;
import com.orderping.domain.exception.ForbiddenException;
import com.orderping.domain.exception.NotFoundException;
import com.orderping.domain.order.Order;
import com.orderping.domain.order.repository.OrderRepository;
import com.orderping.domain.payment.Payment;
import com.orderping.domain.payment.repository.PaymentRepository;
import com.orderping.domain.store.Store;
import com.orderping.domain.store.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final StoreRepository storeRepository;

    @Transactional
    public PaymentResponse createPayment(PaymentCreateRequest request) {
        Payment payment = Payment.builder()
            .orderId(request.orderId())
            .method(request.method())
            .amount(request.amount())
            .status(PaymentStatus.PENDING)
            .build();

        Payment saved = paymentRepository.save(payment);
        return PaymentResponse.from(saved);
    }

    public PaymentResponse getPayment(Long userId, Long id) {
        Payment payment = paymentRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("결제 정보를 찾을 수 없습니다."));
        validatePaymentOwner(userId, payment);
        return PaymentResponse.from(payment);
    }

    public List<PaymentResponse> getPaymentsByOrderId(Long userId, Long orderId) {
        validateOrderOwner(userId, orderId);
        return paymentRepository.findByOrderId(orderId).stream()
            .map(PaymentResponse::from)
            .toList();
    }

    @Transactional
    public PaymentResponse completePayment(Long userId, Long id) {
        Payment payment = paymentRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("결제 정보를 찾을 수 없습니다."));
        validatePaymentOwner(userId, payment);

        Payment updated = Payment.builder()
            .id(payment.getId())
            .orderId(payment.getOrderId())
            .method(payment.getMethod())
            .amount(payment.getAmount())
            .status(PaymentStatus.COMPLETED)
            .createdAt(payment.getCreatedAt())
            .build();

        Payment saved = paymentRepository.save(updated);
        return PaymentResponse.from(saved);
    }

    @Transactional
    public PaymentResponse failPayment(Long userId, Long id) {
        Payment payment = paymentRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("결제 정보를 찾을 수 없습니다."));
        validatePaymentOwner(userId, payment);

        Payment updated = Payment.builder()
            .id(payment.getId())
            .orderId(payment.getOrderId())
            .method(payment.getMethod())
            .amount(payment.getAmount())
            .status(PaymentStatus.FAILED)
            .createdAt(payment.getCreatedAt())
            .build();

        Payment saved = paymentRepository.save(updated);
        return PaymentResponse.from(saved);
    }

    @Transactional
    public void deletePayment(Long userId, Long id) {
        Payment payment = paymentRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("결제 정보를 찾을 수 없습니다."));
        validatePaymentOwner(userId, payment);
        paymentRepository.deleteById(id);
    }

    private void validatePaymentOwner(Long userId, Payment payment) {
        validateOrderOwner(userId, payment.getOrderId());
    }

    private void validateOrderOwner(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new NotFoundException("주문을 찾을 수 없습니다."));
        Store store = storeRepository.findById(order.getStoreId())
            .orElseThrow(() -> new NotFoundException("매장을 찾을 수 없습니다."));
        if (!store.getUserId().equals(userId)) {
            throw new ForbiddenException("본인 매장의 결제만 관리할 수 있습니다.");
        }
    }
}
