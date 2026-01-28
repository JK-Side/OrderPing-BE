package com.orderping.api.order.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.orderping.api.order.dto.OrderCreateRequest;
import com.orderping.api.order.dto.OrderDetailResponse;
import com.orderping.api.order.dto.OrderResponse;
import com.orderping.api.order.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Customer Order", description = "고객용 주문 API")
@RestController
@RequestMapping("/api/customer/orders")
@RequiredArgsConstructor
public class CustomerOrderController {

    private final OrderService orderService;

    @Operation(summary = "주문 생성", description = "새로운 주문을 생성합니다 (인증 불필요)")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "주문 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "409", description = "재고 부족")
    })
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderCreateRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "테이블 주문 내역 조회", description = "해당 테이블의 모든 주문 내역을 메뉴 상세와 함께 조회합니다 (인증 불필요)")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/table/{tableId}")
    public ResponseEntity<List<OrderDetailResponse>> getOrdersByTable(
        @Parameter(description = "테이블 ID", required = true) @PathVariable Long tableId
    ) {
        List<OrderDetailResponse> responses = orderService.getOrdersWithMenusByTableId(tableId);
        return ResponseEntity.ok(responses);
    }
}
