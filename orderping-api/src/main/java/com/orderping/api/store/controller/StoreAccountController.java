package com.orderping.api.store.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.orderping.api.auth.security.CurrentUser;
import com.orderping.api.store.dto.StoreAccountCreateRequest;
import com.orderping.api.store.dto.StoreAccountResponse;
import com.orderping.api.store.dto.StoreAccountUpdateRequest;
import com.orderping.api.store.service.StoreAccountService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/store-accounts")
@RequiredArgsConstructor
public class StoreAccountController implements StoreAccountApi {

    private final StoreAccountService storeAccountService;

    @PostMapping
    @Override
    public ResponseEntity<StoreAccountResponse> createStoreAccount(
        @CurrentUser Long userId,
        @RequestBody StoreAccountCreateRequest request
    ) {
        StoreAccountResponse response = storeAccountService.createStoreAccount(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<StoreAccountResponse> getStoreAccount(@PathVariable Long id) {
        StoreAccountResponse response = storeAccountService.getStoreAccount(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping(params = "storeId")
    @Override
    public ResponseEntity<List<StoreAccountResponse>> getStoreAccountsByStoreId(
        @CurrentUser Long userId,
        @RequestParam Long storeId
    ) {
        List<StoreAccountResponse> responses = storeAccountService.getStoreAccountsByStoreId(userId, storeId);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    @Override
    public ResponseEntity<StoreAccountResponse> updateStoreAccount(
        @CurrentUser Long userId,
        @PathVariable Long id,
        @RequestBody StoreAccountUpdateRequest request
    ) {
        StoreAccountResponse response = storeAccountService.updateStoreAccount(userId, id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> deleteStoreAccount(@CurrentUser Long userId, @PathVariable Long id) {
        storeAccountService.deleteStoreAccount(userId, id);
        return ResponseEntity.noContent().build();
    }
}
