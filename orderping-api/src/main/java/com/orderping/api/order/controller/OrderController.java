package com.orderping.api.order.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.orderping.api.order.dto.OrderCreateRequest;
import com.orderping.api.order.dto.OrderResponse;
import com.orderping.api.order.dto.OrderStatusUpdateRequest;
import com.orderping.api.order.service.OrderService;
import com.orderping.domain.enums.OrderStatus;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController implements OrderApi {

    private final OrderService orderService;

    @PostMapping
    @Override
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderCreateRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id) {
        OrderResponse response = orderService.getOrder(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping(params = "storeId")
    @Override
    public ResponseEntity<List<OrderResponse>> getOrdersByStoreId(@RequestParam Long storeId) {
        List<OrderResponse> responses = orderService.getOrdersByStoreId(storeId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping(params = {"storeId", "status"})
    @Override
    public ResponseEntity<List<OrderResponse>> getOrdersByStoreIdAndStatus(
        @RequestParam Long storeId,
        @RequestParam OrderStatus status) {
        List<OrderResponse> responses = orderService.getOrdersByStoreIdAndStatus(storeId, status);
        return ResponseEntity.ok(responses);
    }

    @GetMapping(params = "tableId")
    @Override
    public ResponseEntity<List<OrderResponse>> getOrdersByTableId(@RequestParam Long tableId) {
        List<OrderResponse> responses = orderService.getOrdersByTableId(tableId);
        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/{id}/status")
    @Override
    public ResponseEntity<OrderResponse> updateOrderStatus(
        @PathVariable Long id,
        @RequestBody OrderStatusUpdateRequest request) {
        OrderResponse response = orderService.updateOrderStatus(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}
