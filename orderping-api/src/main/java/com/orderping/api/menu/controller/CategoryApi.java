package com.orderping.api.menu.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.orderping.api.menu.dto.CategoryCreateRequest;
import com.orderping.api.menu.dto.CategoryResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Category", description = "카테고리 관리 API")
public interface CategoryApi {

    @Operation(summary = "카테고리 생성", description = "새로운 카테고리를 생성합니다")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "카테고리 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    ResponseEntity<CategoryResponse> createCategory(CategoryCreateRequest request);

    @Operation(summary = "카테고리 조회", description = "ID로 카테고리를 조회합니다")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "카테고리를 찾을 수 없음")
    })
    ResponseEntity<CategoryResponse> getCategory(
        @Parameter(description = "카테고리 ID", required = true) Long id
    );

    @Operation(summary = "전체 카테고리 목록", description = "모든 카테고리를 조회합니다")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    ResponseEntity<List<CategoryResponse>> getAllCategories();

    @Operation(summary = "카테고리 삭제", description = "ID로 카테고리를 삭제합니다")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "삭제 성공"),
        @ApiResponse(responseCode = "404", description = "카테고리를 찾을 수 없음")
    })
    ResponseEntity<Void> deleteCategory(
        @Parameter(description = "카테고리 ID", required = true) Long id
    );
}
