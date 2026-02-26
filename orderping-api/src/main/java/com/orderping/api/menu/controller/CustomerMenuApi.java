package com.orderping.api.menu.controller;

import org.springframework.http.ResponseEntity;

import com.orderping.api.menu.dto.CustomerMenuDetailResponse;
import com.orderping.api.qr.dto.TableQrInfoResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Customer Menu", description = "고객용 메뉴 조회 API")
public interface CustomerMenuApi {

    @Operation(summary = "테이블 메뉴 화면 조회", description = "storeId + tableNum으로 고객용 메뉴 화면 정보를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "테이블 또는 매장을 찾을 수 없음")
    })
    ResponseEntity<TableQrInfoResponse> getMenusByStoreAndTableNum(
        @Parameter(description = "주점 ID", required = true) Long storeId,
        @Parameter(description = "테이블 번호", required = true) Integer tableNum
    );

    @Operation(summary = "메뉴 상세 조회", description = "메뉴 ID로 고객용 상세 정보를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "메뉴를 찾을 수 없음")
    })
    ResponseEntity<CustomerMenuDetailResponse> getMenuDetail(
        @Parameter(description = "메뉴 ID", required = true) Long menuId
    );
}

