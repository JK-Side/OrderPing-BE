package com.orderping.api.qr.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.orderping.api.qr.dto.TableQrInfoResponse;
import com.orderping.api.qr.service.TableQrService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/customer/qr")
@RequiredArgsConstructor
@Tag(name = "Customer QR", description = "고객용 QR 코드 API")
public class QrController {

    private final TableQrService tableQrService;

    @GetMapping("/tables/{token}")
    @Operation(summary = "QR 토큰으로 테이블 정보 조회", description = "QR 코드 스캔 후 테이블 및 주점 정보를 조회합니다 (인증 불필요)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "유효하지 않은 QR 코드 또는 테이블을 찾을 수 없음")
    })
    public ResponseEntity<TableQrInfoResponse> getTableInfoByToken(
        @Parameter(description = "QR 토큰", required = true) @PathVariable String token
    ) {
        TableQrInfoResponse response = tableQrService.getTableInfoByToken(token);
        return ResponseEntity.ok(response);
    }
}
