package com.orderping.api.menu.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.orderping.api.menu.dto.MenuCreateRequest;
import com.orderping.api.menu.dto.MenuResponse;
import com.orderping.api.menu.dto.MenuUpdateRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Menu", description = "메뉴 관리 API")
public interface MenuApi {

    @Operation(summary = "메뉴 생성", description = "새로운 메뉴를 생성합니다 (본인 매장만 가능)")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "메뉴 생성 성공"),
        @ApiResponse(responseCode = "401", description = "인증 필요"),
        @ApiResponse(responseCode = "403", description = "본인 매장이 아님"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    ResponseEntity<MenuResponse> createMenu(
        @Parameter(hidden = true) Long userId,
        MenuCreateRequest request
    );

    @Operation(summary = "메뉴 조회", description = "ID로 메뉴를 조회합니다")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "메뉴를 찾을 수 없음")
    })
    ResponseEntity<MenuResponse> getMenu(
        @Parameter(description = "메뉴 ID", required = true) Long id
    );

    @Operation(summary = "메뉴 목록 조회", description = "storeId, categoryId로 메뉴 목록을 조회합니다. 둘 중 하나는 필수이며, 둘 다 입력하면 해당 매장의 해당 카테고리 메뉴만 조회됩니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "400", description = "storeId 또는 categoryId 중 하나는 필수")
    })
    ResponseEntity<List<MenuResponse>> getMenus(
        @Parameter(description = "매장 ID (categoryId와 함께 사용 가능)") Long storeId,
        @Parameter(description = "카테고리 ID (storeId와 함께 사용 가능)") Long categoryId
    );

    @Operation(summary = "판매 가능한 메뉴 목록", description = "매장의 판매 가능한 메뉴 목록을 조회합니다 (GET /api/menus/available/{storeId})")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    ResponseEntity<List<MenuResponse>> getAvailableMenusByStoreId(
        @Parameter(description = "매장 ID", required = true) Long storeId
    );

    @Operation(summary = "메뉴 삭제", description = "ID로 메뉴를 삭제합니다 (본인 매장만 가능)")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "삭제 성공"),
        @ApiResponse(responseCode = "401", description = "인증 필요"),
        @ApiResponse(responseCode = "403", description = "본인 매장이 아님"),
        @ApiResponse(responseCode = "404", description = "메뉴를 찾을 수 없음")
    })
    ResponseEntity<Void> deleteMenu(
        @Parameter(hidden = true) Long userId,
        @Parameter(description = "메뉴 ID", required = true) Long id
    );

    @Operation(summary = "메뉴 수정", description = "메뉴 정보를 수정합니다 (본인 매장만 가능)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "수정 성공"),
        @ApiResponse(responseCode = "401", description = "인증 필요"),
        @ApiResponse(responseCode = "403", description = "본인 매장이 아님"),
        @ApiResponse(responseCode = "404", description = "메뉴를 찾을 수 없음")
    })
    ResponseEntity<MenuResponse> updateMenu(
        @Parameter(hidden = true) Long userId,
        @Parameter(description = "메뉴 ID", required = true) Long id,
        MenuUpdateRequest request
    );
}
