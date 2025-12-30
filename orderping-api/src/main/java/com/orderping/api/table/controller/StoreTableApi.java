package com.orderping.api.table.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.orderping.api.table.dto.StoreTableCreateRequest;
import com.orderping.api.table.dto.StoreTableResponse;
import com.orderping.api.table.dto.StoreTableStatusUpdateRequest;
import com.orderping.domain.enums.TableStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "StoreTable", description = "테이블 관리 API")
public interface StoreTableApi {

    @Operation(summary = "테이블 생성", description = "새로운 테이블을 생성합니다 (본인 매장만 가능)")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "테이블 생성 성공"),
        @ApiResponse(responseCode = "401", description = "인증 필요"),
        @ApiResponse(responseCode = "403", description = "본인 매장이 아님"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    ResponseEntity<StoreTableResponse> createStoreTable(
        @Parameter(hidden = true) Long userId,
        StoreTableCreateRequest request
    );

    @Operation(summary = "테이블 조회", description = "ID로 테이블을 조회합니다")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "테이블을 찾을 수 없음")
    })
    ResponseEntity<StoreTableResponse> getStoreTable(
        @Parameter(description = "테이블 ID", required = true) Long id
    );

    @Operation(summary = "매장별 테이블 목록", description = "매장 ID로 테이블 목록을 조회합니다 (본인 매장만 가능)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 필요"),
        @ApiResponse(responseCode = "403", description = "본인 매장이 아님")
    })
    ResponseEntity<List<StoreTableResponse>> getStoreTablesByStoreId(
        @Parameter(hidden = true) Long userId,
        @Parameter(description = "매장 ID", required = true) Long storeId
    );

    @Operation(summary = "매장별 상태별 테이블 목록", description = "매장 ID와 상태로 테이블 목록을 조회합니다 (본인 매장만 가능)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 필요"),
        @ApiResponse(responseCode = "403", description = "본인 매장이 아님")
    })
    ResponseEntity<List<StoreTableResponse>> getStoreTablesByStoreIdAndStatus(
        @Parameter(hidden = true) Long userId,
        @Parameter(description = "매장 ID", required = true) Long storeId,
        @Parameter(description = "테이블 상태", required = true) TableStatus status
    );

    @Operation(summary = "테이블 상태 변경", description = "테이블 상태를 변경합니다 (본인 매장만 가능)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "상태 변경 성공"),
        @ApiResponse(responseCode = "401", description = "인증 필요"),
        @ApiResponse(responseCode = "403", description = "본인 매장이 아님"),
        @ApiResponse(responseCode = "404", description = "테이블을 찾을 수 없음")
    })
    ResponseEntity<StoreTableResponse> updateStoreTableStatus(
        @Parameter(hidden = true) Long userId,
        @Parameter(description = "테이블 ID", required = true) Long id,
        StoreTableStatusUpdateRequest request
    );

    @Operation(summary = "테이블 삭제", description = "ID로 테이블을 삭제합니다 (본인 매장만 가능)")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "삭제 성공"),
        @ApiResponse(responseCode = "401", description = "인증 필요"),
        @ApiResponse(responseCode = "403", description = "본인 매장이 아님"),
        @ApiResponse(responseCode = "404", description = "테이블을 찾을 수 없음")
    })
    ResponseEntity<Void> deleteStoreTable(
        @Parameter(hidden = true) Long userId,
        @Parameter(description = "테이블 ID", required = true) Long id
    );

    @Operation(summary = "테이블 비우기", description = "현재 테이블을 종료하고 같은 번호의 새 테이블을 생성합니다 (본인 매장만 가능)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "테이블 비우기 성공"),
        @ApiResponse(responseCode = "401", description = "인증 필요"),
        @ApiResponse(responseCode = "403", description = "본인 매장이 아님"),
        @ApiResponse(responseCode = "404", description = "테이블을 찾을 수 없음")
    })
    ResponseEntity<StoreTableResponse> clearTable(
        @Parameter(hidden = true) Long userId,
        @Parameter(description = "테이블 ID", required = true) Long id
    );
}
