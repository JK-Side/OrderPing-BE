package com.orderping.api.menu.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.orderping.api.menu.dto.CustomerMenuDetailResponse;
import com.orderping.api.menu.service.CustomerMenuService;
import com.orderping.api.qr.dto.TableQrInfoResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/customer/menus")
@RequiredArgsConstructor
public class CustomerMenuController implements CustomerMenuApi {

    private final CustomerMenuService customerMenuService;

    @GetMapping("/tables/{tableId}")
    @Override
    public ResponseEntity<TableQrInfoResponse> getMenusByTable(
        @PathVariable Long tableId
    ) {
        TableQrInfoResponse response = customerMenuService.getMenusByTable(tableId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/details/{menuId}")
    @Override
    public ResponseEntity<CustomerMenuDetailResponse> getMenuDetail(
        @PathVariable Long menuId
    ) {
        CustomerMenuDetailResponse response = customerMenuService.getMenuDetail(menuId);
        return ResponseEntity.ok(response);
    }
}
