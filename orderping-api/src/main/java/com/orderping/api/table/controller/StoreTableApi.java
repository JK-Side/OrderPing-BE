package com.orderping.api.table.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.orderping.api.table.dto.StoreTableBulkCreateRequest;
import com.orderping.api.table.dto.StoreTableBulkDeleteRequest;
import com.orderping.api.table.dto.StoreTableBulkQrUpdateRequest;
import com.orderping.api.table.dto.StoreTableCreateRequest;
import com.orderping.api.table.dto.StoreTableDetailResponse;
import com.orderping.api.table.dto.StoreTableResponse;
import com.orderping.api.table.dto.StoreTableStatusUpdateRequest;
import com.orderping.api.table.dto.StoreTableUpdateRequest;
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

    @Operation(summary = "매장별 테이블 목록", description = "매장 ID로 테이블 목록을 조회합니다. 각 테이블의 주문 메뉴와 총 주문 금액도 함께 반환합니다. status를 지정하면 해당 상태의 테이블만 조회합니다. (본인 매장만 가능)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 필요"),
        @ApiResponse(responseCode = "403", description = "본인 매장이 아님")
    })
    ResponseEntity<List<StoreTableDetailResponse>> getStoreTables(
        @Parameter(hidden = true) Long userId,
        @Parameter(description = "매장 ID", required = true) Long storeId,
        @Parameter(description = "테이블 상태 (선택)") TableStatus status
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

    @Operation(summary = "테이블 수정", description = "테이블 정보를 수정합니다 (QR 이미지 URL 등)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "수정 성공"),
        @ApiResponse(responseCode = "401", description = "인증 필요"),
        @ApiResponse(responseCode = "403", description = "본인 매장이 아님"),
        @ApiResponse(responseCode = "404", description = "테이블을 찾을 수 없음")
    })
    ResponseEntity<StoreTableResponse> updateStoreTable(
        @Parameter(hidden = true) Long userId,
        @Parameter(description = "테이블 ID", required = true) Long id,
        StoreTableUpdateRequest request
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

    @Operation(summary = "테이블 일괄 생성", description = "1번부터 지정한 개수만큼 테이블을 일괄 생성합니다 (본인 매장만 가능)")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "테이블 일괄 생성 성공"),
        @ApiResponse(responseCode = "401", description = "인증 필요"),
        @ApiResponse(responseCode = "403", description = "본인 매장이 아님"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    ResponseEntity<List<StoreTableResponse>> createStoreTablesBulk(
        @Parameter(hidden = true) Long userId,
        StoreTableBulkCreateRequest request
    );

    @Operation(summary = "테이블 일괄 삭제", description = "지정한 테이블 번호들을 일괄 삭제합니다. 주문이 있는 테이블은 삭제할 수 없습니다. (본인 매장만 가능)")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "테이블 일괄 삭제 성공"),
        @ApiResponse(responseCode = "401", description = "인증 필요"),
        @ApiResponse(responseCode = "403", description = "본인 매장이 아님"),
        @ApiResponse(responseCode = "400", description = "주문이 존재하는 테이블이 포함됨"),
        @ApiResponse(responseCode = "404", description = "삭제할 테이블을 찾을 수 없음")
    })
    ResponseEntity<Void> deleteStoreTablesBulk(
        @Parameter(hidden = true) Long userId,
        StoreTableBulkDeleteRequest request
    );

    @Operation(summary = "테이블 QR 일괄 업데이트", description = "여러 테이블의 QR 이미지 URL을 일괄 업데이트합니다 (본인 매장만 가능)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "QR 일괄 업데이트 성공"),
        @ApiResponse(responseCode = "401", description = "인증 필요"),
        @ApiResponse(responseCode = "403", description = "본인 매장이 아님"),
        @ApiResponse(responseCode = "404", description = "테이블을 찾을 수 없음")
    })
    ResponseEntity<List<StoreTableResponse>> updateStoreTableQrBulk(
        @Parameter(hidden = true) Long userId,
        @Parameter(description = "매장 ID", required = true) Long storeId,
        StoreTableBulkQrUpdateRequest request
    );
}
