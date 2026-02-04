package com.orderping.api.order.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.orderping.api.order.dto.OrderResponse;
import com.orderping.api.order.dto.ServiceOrderCreateRequest;
import com.orderping.api.order.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Service Order", description = "서비스(무료) 주문 API")
@RestController
@RequestMapping("/api/customer/order")
@RequiredArgsConstructor
public class ServiceOrderController {

    private final OrderService orderService;

    @Operation(summary = "서비스 주문 생성", description = "서비스(무료) 주문을 생성합니다. 가격은 0원으로 처리됩니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "서비스 주문 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "409", description = "재고 부족")
    })
    @PostMapping
    public ResponseEntity<OrderResponse> createServiceOrder(@RequestBody ServiceOrderCreateRequest request) {
        OrderResponse response = orderService.createServiceOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
