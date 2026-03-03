package com.orderping.api.store.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.orderping.api.store.dto.CustomerStoreAccountResponse;
import com.orderping.api.store.service.CustomerStoreAccountService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Customer Store Account", description = "고객용 주점 계좌 조회 API")
@RestController
@RequestMapping("/api/customer/stores")
@RequiredArgsConstructor
public class CustomerStoreAccountController {

    private final CustomerStoreAccountService customerStoreAccountService;

    @Operation(summary = "주점 계좌 조회", description = "storeId 기준으로 주점 운영자의 계좌 정보를 조회합니다 (인증 불필요)")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/{storeId}/account")
    public ResponseEntity<CustomerStoreAccountResponse> getStoreAccount(@PathVariable Long storeId) {
        CustomerStoreAccountResponse response = customerStoreAccountService.getAccountByStoreId(storeId);
        return ResponseEntity.ok(response);
    }
}
