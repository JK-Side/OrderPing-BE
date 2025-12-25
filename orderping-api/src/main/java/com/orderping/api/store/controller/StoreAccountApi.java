package com.orderping.api.store.controller;

import com.orderping.api.store.dto.StoreAccountCreateRequest;
import com.orderping.api.store.dto.StoreAccountResponse;
import com.orderping.api.store.dto.StoreAccountUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "StoreAccount", description = "매장 계좌 관리 API")
public interface StoreAccountApi {

    @Operation(summary = "계좌 등록", description = "매장에 새로운 계좌를 등록합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "계좌 등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    ResponseEntity<StoreAccountResponse> createStoreAccount(StoreAccountCreateRequest request);

    @Operation(summary = "계좌 조회", description = "ID로 계좌를 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "계좌를 찾을 수 없음")
    })
    ResponseEntity<StoreAccountResponse> getStoreAccount(
            @Parameter(description = "계좌 ID", required = true) Long id
    );

    @Operation(summary = "매장별 계좌 목록", description = "매장 ID로 계좌 목록을 조회합니다")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    ResponseEntity<List<StoreAccountResponse>> getStoreAccountsByStoreId(
            @Parameter(description = "매장 ID", required = true) Long storeId
    );

    @Operation(summary = "계좌 수정", description = "계좌 정보를 수정합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "404", description = "계좌를 찾을 수 없음")
    })
    ResponseEntity<StoreAccountResponse> updateStoreAccount(
            @Parameter(description = "계좌 ID", required = true) Long id,
            StoreAccountUpdateRequest request
    );

    @Operation(summary = "계좌 삭제", description = "ID로 계좌를 삭제합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "계좌를 찾을 수 없음")
    })
    ResponseEntity<Void> deleteStoreAccount(
            @Parameter(description = "계좌 ID", required = true) Long id
    );
}
