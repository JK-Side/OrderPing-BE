package com.orderping.api.order.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.orderping.api.order.dto.OrderCreateRequest;
import com.orderping.api.order.dto.OrderResponse;
import com.orderping.api.order.dto.OrderStatusUpdateRequest;
import com.orderping.domain.enums.OrderStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Order", description = "주문 관리 API")
public interface OrderApi {

    @Operation(summary = "주문 생성", description = "새로운 주문을 생성합니다")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "주문 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    ResponseEntity<OrderResponse> createOrder(OrderCreateRequest request);

    @Operation(summary = "주문 조회", description = "ID로 주문을 조회합니다")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음")
    })
    ResponseEntity<OrderResponse> getOrder(
        @Parameter(description = "주문 ID", required = true) Long id
    );

    @Operation(summary = "매장별 주문 목록", description = "매장 ID로 주문 목록을 조회합니다. status를 지정하면 해당 상태의 주문만 조회합니다. (본인 매장만 가능)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 필요"),
        @ApiResponse(responseCode = "403", description = "본인 매장이 아님")
    })
    ResponseEntity<List<OrderResponse>> getOrdersByStore(
        @Parameter(hidden = true) Long userId,
        @Parameter(description = "매장 ID", required = true) Long storeId,
        @Parameter(description = "주문 상태 (선택, 미지정 시 전체 조회)") OrderStatus status
    );

    @Operation(summary = "테이블별 주문 목록", description = "테이블 ID로 주문 목록을 조회합니다")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    ResponseEntity<List<OrderResponse>> getOrdersByTableId(
        @Parameter(description = "테이블 ID", required = true) Long tableId
    );

    @Operation(summary = "주문 상태 변경", description = "주문 상태를 변경합니다 (본인 매장만 가능)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "상태 변경 성공"),
        @ApiResponse(responseCode = "401", description = "인증 필요"),
        @ApiResponse(responseCode = "403", description = "본인 매장이 아님"),
        @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음")
    })
    ResponseEntity<OrderResponse> updateOrderStatus(
        @Parameter(hidden = true) Long userId,
        @Parameter(description = "주문 ID", required = true) Long id,
        OrderStatusUpdateRequest request
    );

    @Operation(summary = "주문 삭제", description = "ID로 주문을 삭제합니다 (본인 매장만 가능)")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "삭제 성공"),
        @ApiResponse(responseCode = "401", description = "인증 필요"),
        @ApiResponse(responseCode = "403", description = "본인 매장이 아님"),
        @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음")
    })
    ResponseEntity<Void> deleteOrder(
        @Parameter(hidden = true) Long userId,
        @Parameter(description = "주문 ID", required = true) Long id
    );
}
