package com.orderping.api.store.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.orderping.api.store.dto.StoreCreateRequest;
import com.orderping.api.store.dto.StoreDetailResponse;
import com.orderping.api.store.dto.StoreResponse;
import com.orderping.api.store.dto.StoreUpdateRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Store", description = "매장 관리 API")
public interface StoreApi {

    @Operation(summary = "매장 생성", description = "새로운 매장을 생성합니다")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "매장 생성 성공"),
        @ApiResponse(responseCode = "401", description = "인증 필요"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    ResponseEntity<StoreResponse> createStore(
        @Parameter(hidden = true) Long userId,
        StoreCreateRequest request
    );

    @Operation(summary = "매장 조회", description = "ID로 매장을 조회합니다")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "매장을 찾을 수 없음")
    })
    ResponseEntity<StoreResponse> getStore(
        @Parameter(description = "매장 ID", required = true) Long id
    );

    @Operation(summary = "내 매장 목록", description = "로그인한 사용자의 매장 목록을 조회합니다")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    ResponseEntity<List<StoreResponse>> getStoresByUserId(
        @Parameter(hidden = true) Long userId
    );

    @Operation(summary = "매장 삭제", description = "ID로 매장을 삭제합니다 (본인 매장만 가능)")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "삭제 성공"),
        @ApiResponse(responseCode = "401", description = "인증 필요"),
        @ApiResponse(responseCode = "403", description = "본인 매장이 아님"),
        @ApiResponse(responseCode = "404", description = "매장을 찾을 수 없음")
    })
    ResponseEntity<Void> deleteStore(
        @Parameter(hidden = true) Long userId,
        @Parameter(description = "매장 ID", required = true) Long id
    );

    @Operation(summary = "매장 수정", description = "매장 정보를 수정합니다 (본인 매장만 가능)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "수정 성공"),
        @ApiResponse(responseCode = "401", description = "인증 필요"),
        @ApiResponse(responseCode = "403", description = "본인 매장이 아님"),
        @ApiResponse(responseCode = "404", description = "매장을 찾을 수 없음")
    })
    ResponseEntity<StoreResponse> updateStore(
        @Parameter(hidden = true) Long userId,
        @Parameter(description = "매장 ID", required = true) Long id,
        StoreUpdateRequest request
    );

    @Operation(summary = "매장 상세 조회 (운영자용)", description = "운영자용 매장 상세 정보를 조회합니다. 카테고리별 메뉴 포함, 재고/품절 정보 포함")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 필요"),
        @ApiResponse(responseCode = "403", description = "본인 가게가 아님"),
        @ApiResponse(responseCode = "404", description = "매장을 찾을 수 없음")
    })
    ResponseEntity<StoreDetailResponse> getStoreForManage(
        @Parameter(hidden = true) Long userId,
        @Parameter(description = "매장 ID", required = true) Long id
    );

    @Operation(summary = "매장 상세 조회 (주문용)", description = "손님용 매장 상세 정보를 조회합니다. 카테고리별 메뉴 포함, 품절 여부만 포함")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "매장을 찾을 수 없음")
    })
    ResponseEntity<StoreDetailResponse> getStoreForOrder(
        @Parameter(description = "매장 ID", required = true) Long id
    );
}
