package com.orderping.api.payment.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.orderping.api.payment.dto.DeeplinkResponse;
import com.orderping.api.payment.dto.PaymentCreateRequest;
import com.orderping.api.payment.dto.PaymentResponse;
import com.orderping.api.payment.service.DeeplinkService;
import com.orderping.api.payment.service.PaymentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController implements PaymentApi {

    private final PaymentService paymentService;
    private final DeeplinkService deeplinkService;

    @PostMapping
    @Override
    public ResponseEntity<PaymentResponse> createPayment(@RequestBody PaymentCreateRequest request) {
        PaymentResponse response = paymentService.createPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable Long id) {
        PaymentResponse response = paymentService.getPayment(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping(params = "orderId")
    @Override
    public ResponseEntity<List<PaymentResponse>> getPaymentsByOrderId(@RequestParam Long orderId) {
        List<PaymentResponse> responses = paymentService.getPaymentsByOrderId(orderId);
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{id}/complete")
    @Override
    public ResponseEntity<PaymentResponse> completePayment(@PathVariable Long id) {
        PaymentResponse response = paymentService.completePayment(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/fail")
    @Override
    public ResponseEntity<PaymentResponse> failPayment(@PathVariable Long id) {
        PaymentResponse response = paymentService.failPayment(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/deeplink")
    @Override
    public ResponseEntity<DeeplinkResponse> getDeeplink(
        @RequestParam Long storeId,
        @RequestParam Long amount
    ) {
        DeeplinkResponse response = deeplinkService.getDeeplink(storeId, amount);
        return ResponseEntity.ok(response);
    }
}
