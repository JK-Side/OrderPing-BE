package com.orderping.api.user.controller;

import com.orderping.api.user.dto.UserCreateRequest;
import com.orderping.api.user.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "User", description = "사용자 관리 API")
public interface UserApi {

    @Operation(summary = "사용자 생성", description = "새로운 사용자를 생성합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "사용자 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    ResponseEntity<UserResponse> createUser(UserCreateRequest request);

    @Operation(summary = "사용자 조회", description = "ID로 사용자를 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    ResponseEntity<UserResponse> getUser(
            @Parameter(description = "사용자 ID", required = true) Long id
    );

    @Operation(summary = "사용자 삭제", description = "ID로 사용자를 삭제합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    ResponseEntity<Void> deleteUser(
            @Parameter(description = "사용자 ID", required = true) Long id
    );
}
