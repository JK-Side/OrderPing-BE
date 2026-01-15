package com.orderping.api.payment.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.orderping.api.payment.dto.DeeplinkResponse;
import com.orderping.api.payment.dto.PaymentCreateRequest;
import com.orderping.api.payment.dto.PaymentResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Payment", description = "결제 관리 API")
public interface PaymentApi {

    @Operation(summary = "결제 생성", description = "새로운 결제를 생성합니다")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "결제 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    ResponseEntity<PaymentResponse> createPayment(PaymentCreateRequest request);

    @Operation(summary = "결제 조회", description = "ID로 결제를 조회합니다")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "결제를 찾을 수 없음")
    })
    ResponseEntity<PaymentResponse> getPayment(
        @Parameter(description = "결제 ID", required = true) Long id
    );

    @Operation(summary = "주문별 결제 목록", description = "주문 ID로 결제 목록을 조회합니다")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    ResponseEntity<List<PaymentResponse>> getPaymentsByOrderId(
        @Parameter(description = "주문 ID", required = true) Long orderId
    );

    @Operation(summary = "결제 완료", description = "결제를 완료 처리합니다")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "결제 완료 성공"),
        @ApiResponse(responseCode = "404", description = "결제를 찾을 수 없음")
    })
    ResponseEntity<PaymentResponse> completePayment(
        @Parameter(description = "결제 ID", required = true) Long id
    );

    @Operation(summary = "결제 실패", description = "결제를 실패 처리합니다")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "결제 실패 처리 성공"),
        @ApiResponse(responseCode = "404", description = "결제를 찾을 수 없음")
    })
    ResponseEntity<PaymentResponse> failPayment(
        @Parameter(description = "결제 ID", required = true) Long id
    );

    @Operation(summary = "결제 삭제", description = "ID로 결제를 삭제합니다")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "삭제 성공"),
        @ApiResponse(responseCode = "404", description = "결제를 찾을 수 없음")
    })
    ResponseEntity<Void> deletePayment(
        @Parameter(description = "결제 ID", required = true) Long id
    );

    @Operation(summary = "토스 송금 딥링크 조회", description = "토스 송금용 딥링크와 계좌 정보를 조회합니다 (인증 불필요)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "계좌를 찾을 수 없음")
    })
    ResponseEntity<DeeplinkResponse> getDeeplink(
        @Parameter(description = "주점 ID", required = true) Long storeId,
        @Parameter(description = "송금 금액", required = true) Long amount
    );
}
