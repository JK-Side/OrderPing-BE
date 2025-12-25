package com.orderping.api.payment.service;

import com.orderping.api.payment.dto.PaymentCreateRequest;
import com.orderping.api.payment.dto.PaymentResponse;
import com.orderping.domain.enums.PaymentStatus;
import com.orderping.domain.payment.Payment;
import com.orderping.domain.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;

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

    public PaymentResponse getPayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + id));
        return PaymentResponse.from(payment);
    }

    public List<PaymentResponse> getPaymentsByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId).stream()
                .map(PaymentResponse::from)
                .toList();
    }

    @Transactional
    public PaymentResponse completePayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + id));

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
    public PaymentResponse failPayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + id));

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
    public void deletePayment(Long id) {
        paymentRepository.deleteById(id);
    }
}
