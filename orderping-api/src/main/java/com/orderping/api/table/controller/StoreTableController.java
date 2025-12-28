package com.orderping.api.table.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.orderping.api.table.dto.StoreTableCreateRequest;
import com.orderping.api.table.dto.StoreTableResponse;
import com.orderping.api.table.dto.StoreTableStatusUpdateRequest;
import com.orderping.api.table.service.StoreTableService;
import com.orderping.domain.enums.TableStatus;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tables")
@RequiredArgsConstructor
public class StoreTableController implements StoreTableApi {

    private final StoreTableService storeTableService;

    @PostMapping
    @Override
    public ResponseEntity<StoreTableResponse> createStoreTable(@RequestBody StoreTableCreateRequest request) {
        StoreTableResponse response = storeTableService.createStoreTable(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<StoreTableResponse> getStoreTable(@PathVariable Long id) {
        StoreTableResponse response = storeTableService.getStoreTable(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping(params = "storeId")
    @Override
    public ResponseEntity<List<StoreTableResponse>> getStoreTablesByStoreId(@RequestParam Long storeId) {
        List<StoreTableResponse> responses = storeTableService.getStoreTablesByStoreId(storeId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping(params = {"storeId", "status"})
    @Override
    public ResponseEntity<List<StoreTableResponse>> getStoreTablesByStoreIdAndStatus(
        @RequestParam Long storeId,
        @RequestParam TableStatus status) {
        List<StoreTableResponse> responses = storeTableService.getStoreTablesByStoreIdAndStatus(storeId, status);
        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/{id}/status")
    @Override
    public ResponseEntity<StoreTableResponse> updateStoreTableStatus(
        @PathVariable Long id,
        @RequestBody StoreTableStatusUpdateRequest request) {
        StoreTableResponse response = storeTableService.updateStoreTableStatus(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> deleteStoreTable(@PathVariable Long id) {
        storeTableService.deleteStoreTable(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/clear")
    @Override
    public ResponseEntity<StoreTableResponse> clearTable(@PathVariable Long id) {
        StoreTableResponse response = storeTableService.clearTable(id);
        return ResponseEntity.ok(response);
    }
}
