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

import com.orderping.api.auth.security.CurrentUser;
import com.orderping.api.table.dto.StoreTableBulkCreateRequest;
import com.orderping.api.table.dto.StoreTableBulkDeleteRequest;
import com.orderping.api.table.dto.StoreTableBulkQrUpdateRequest;
import com.orderping.api.table.dto.StoreTableCreateRequest;
import com.orderping.api.table.dto.StoreTableDetailResponse;
import com.orderping.api.table.dto.StoreTableResponse;
import com.orderping.api.table.dto.StoreTableStatusUpdateRequest;
import com.orderping.api.table.dto.StoreTableUpdateRequest;
import com.orderping.api.table.dto.TableQrUrlsResponse;
import com.orderping.api.table.service.StoreTableService;
import com.orderping.domain.enums.TableStatus;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tables")
@RequiredArgsConstructor
public class StoreTableController implements StoreTableApi {

    private final StoreTableService storeTableService;

    @PostMapping
    @Override
    public ResponseEntity<StoreTableResponse> createStoreTable(
        @CurrentUser Long userId,
        @Valid @RequestBody StoreTableCreateRequest request
    ) {
        StoreTableResponse response = storeTableService.createStoreTable(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<StoreTableResponse> getStoreTable(@PathVariable Long id) {
        StoreTableResponse response = storeTableService.getStoreTable(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Override
    public ResponseEntity<List<StoreTableDetailResponse>> getStoreTables(
        @CurrentUser Long userId,
        @RequestParam Long storeId,
        @RequestParam(required = false) TableStatus status
    ) {
        List<StoreTableDetailResponse> responses = storeTableService.getStoreTables(userId, storeId, status);
        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/{id}/status")
    @Override
    public ResponseEntity<StoreTableResponse> updateStoreTableStatus(
        @CurrentUser Long userId,
        @PathVariable Long id,
        @RequestBody StoreTableStatusUpdateRequest request
    ) {
        StoreTableResponse response = storeTableService.updateStoreTableStatus(userId, id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> deleteStoreTable(@CurrentUser Long userId, @PathVariable Long id) {
        storeTableService.deleteStoreTable(userId, id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    @Override
    public ResponseEntity<StoreTableResponse> updateStoreTable(
        @CurrentUser Long userId,
        @PathVariable Long id,
        @RequestBody StoreTableUpdateRequest request
    ) {
        StoreTableResponse response = storeTableService.updateStoreTable(userId, id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/clear")
    @Override
    public ResponseEntity<StoreTableResponse> clearTable(@CurrentUser Long userId, @PathVariable Long id) {
        StoreTableResponse response = storeTableService.clearTable(userId, id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/bulk")
    @Override
    public ResponseEntity<List<StoreTableResponse>> createStoreTablesBulk(
        @CurrentUser Long userId,
        @RequestBody StoreTableBulkCreateRequest request
    ) {
        List<StoreTableResponse> responses = storeTableService.createStoreTablesBulk(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    @DeleteMapping("/bulk")
    @Override
    public ResponseEntity<Void> deleteStoreTablesBulk(
        @CurrentUser Long userId,
        @Valid @RequestBody StoreTableBulkDeleteRequest request
    ) {
        storeTableService.deleteStoreTablesByTableNums(userId, request.storeId(), request.tableNums());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/qr")
    @Override
    public ResponseEntity<TableQrUrlsResponse> getQrImageUrls(
        @CurrentUser Long userId,
        @RequestParam Long storeId
    ) {
        return ResponseEntity.ok(storeTableService.getQrImageUrls(userId, storeId));
    }

    @PatchMapping("/bulk/{storeId}")
    @Override
    public ResponseEntity<List<StoreTableResponse>> updateStoreTableQrBulk(
        @CurrentUser Long userId,
        @PathVariable Long storeId,
        @Valid @RequestBody StoreTableBulkQrUpdateRequest request
    ) {
        List<StoreTableResponse> responses = storeTableService.updateStoreTableQrBulk(userId, storeId, request);
        return ResponseEntity.ok(responses);
    }
}
