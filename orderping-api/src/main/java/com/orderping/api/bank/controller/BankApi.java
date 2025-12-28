package com.orderping.api.bank.controller;

import com.orderping.api.bank.dto.BankResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Bank", description = "은행 코드 API")
public interface BankApi {

    @Operation(summary = "은행 목록 조회", description = "활성화된 은행 목록을 조회합니다 (드롭다운용)")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    ResponseEntity<List<BankResponse>> getAllBanks();
}
