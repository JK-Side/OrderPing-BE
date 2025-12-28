package com.orderping.api.menu.controller;

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

import com.orderping.api.menu.dto.MenuCreateRequest;
import com.orderping.api.menu.dto.MenuResponse;
import com.orderping.api.menu.dto.MenuUpdateRequest;
import com.orderping.api.menu.service.MenuService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class MenuController implements MenuApi {

    private final MenuService menuService;

    @PostMapping
    @Override
    public ResponseEntity<MenuResponse> createMenu(@RequestBody MenuCreateRequest request) {
        MenuResponse response = menuService.createMenu(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<MenuResponse> getMenu(@PathVariable Long id) {
        MenuResponse response = menuService.getMenu(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping(params = "storeId")
    @Override
    public ResponseEntity<List<MenuResponse>> getMenusByStoreId(@RequestParam Long storeId) {
        List<MenuResponse> responses = menuService.getMenusByStoreId(storeId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping(params = "categoryId")
    @Override
    public ResponseEntity<List<MenuResponse>> getMenusByCategoryId(@RequestParam Long categoryId) {
        List<MenuResponse> responses = menuService.getMenusByCategoryId(categoryId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping(path = "/available", params = "storeId")
    @Override
    public ResponseEntity<List<MenuResponse>> getAvailableMenusByStoreId(@RequestParam Long storeId) {
        List<MenuResponse> responses = menuService.getAvailableMenusByStoreId(storeId);
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> deleteMenu(@PathVariable Long id) {
        menuService.deleteMenu(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @Override
    public ResponseEntity<MenuResponse> updateMenu(@PathVariable Long id, @RequestBody MenuUpdateRequest request) {
        MenuResponse response = menuService.updateMenu(id, request);
        return ResponseEntity.ok(response);
    }
}
