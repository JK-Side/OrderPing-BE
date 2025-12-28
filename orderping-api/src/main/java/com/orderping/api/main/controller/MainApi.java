package com.orderping.api.main.controller;

import org.springframework.http.ResponseEntity;

import com.orderping.api.main.dto.MainResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Main", description = "메인 페이지 API")
public interface MainApi {

    @Operation(summary = "메인 페이지 정보 조회", description = "로그인 후 메인 페이지에 필요한 정보를 조회합니다 (사용자명, 보유 주점 목록)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    ResponseEntity<MainResponse> getMainInfo(
        @Parameter(description = "사용자 ID (JWT 도입 후 제거 예정)", required = true) Long userId
    );
}
