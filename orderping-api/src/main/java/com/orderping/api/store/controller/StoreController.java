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

import com.orderping.api.store.dto.StoreCreateRequest;
import com.orderping.api.store.dto.StoreDetailResponse;
import com.orderping.api.store.dto.StoreResponse;
import com.orderping.api.store.dto.StoreUpdateRequest;
import com.orderping.api.store.service.StoreService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController implements StoreApi {

    private final StoreService storeService;

    @PostMapping
    @Override
    public ResponseEntity<StoreResponse> createStore(@RequestBody StoreCreateRequest request) {
        StoreResponse response = storeService.createStore(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<StoreResponse> getStore(@PathVariable Long id) {
        StoreResponse response = storeService.getStore(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Override
    public ResponseEntity<List<StoreResponse>> getStoresByUserId(@RequestParam Long userId) {
        List<StoreResponse> responses = storeService.getStoresByUserId(userId);
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> deleteStore(@PathVariable Long id) {
        storeService.deleteStore(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @Override
    public ResponseEntity<StoreResponse> updateStore(@PathVariable Long id, @RequestBody StoreUpdateRequest request) {
        StoreResponse response = storeService.updateStore(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/manage")
    @Override
    public ResponseEntity<StoreDetailResponse> getStoreForManage(@PathVariable Long id, @RequestParam Long userId) {
        StoreDetailResponse response = storeService.getStoreForManage(id, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/order")
    @Override
    public ResponseEntity<StoreDetailResponse> getStoreForOrder(@PathVariable Long id) {
        StoreDetailResponse response = storeService.getStoreForOrder(id);
        return ResponseEntity.ok(response);
    }
}
