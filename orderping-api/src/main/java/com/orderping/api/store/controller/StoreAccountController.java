package com.orderping.api.store.controller;

import com.orderping.api.store.dto.StoreAccountCreateRequest;
import com.orderping.api.store.dto.StoreAccountResponse;
import com.orderping.api.store.dto.StoreAccountUpdateRequest;
import com.orderping.api.store.service.StoreAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/store-accounts")
@RequiredArgsConstructor
public class StoreAccountController implements StoreAccountApi {

    private final StoreAccountService storeAccountService;

    @PostMapping
    @Override
    public ResponseEntity<StoreAccountResponse> createStoreAccount(@RequestBody StoreAccountCreateRequest request) {
        StoreAccountResponse response = storeAccountService.createStoreAccount(request);
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
    public ResponseEntity<List<StoreAccountResponse>> getStoreAccountsByStoreId(@RequestParam Long storeId) {
        List<StoreAccountResponse> responses = storeAccountService.getStoreAccountsByStoreId(storeId);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    @Override
    public ResponseEntity<StoreAccountResponse> updateStoreAccount(@PathVariable Long id, @RequestBody StoreAccountUpdateRequest request) {
        StoreAccountResponse response = storeAccountService.updateStoreAccount(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> deleteStoreAccount(@PathVariable Long id) {
        storeAccountService.deleteStoreAccount(id);
        return ResponseEntity.noContent().build();
    }
}
