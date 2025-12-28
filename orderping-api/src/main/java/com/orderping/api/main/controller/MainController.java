package com.orderping.api.main.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.orderping.api.main.dto.MainResponse;
import com.orderping.api.main.service.MainService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/main")
@RequiredArgsConstructor
public class MainController implements MainApi {

    private final MainService mainService;

    @GetMapping
    @Override
    public ResponseEntity<MainResponse> getMainInfo(@RequestParam Long userId) {
        MainResponse response = mainService.getMainInfo(userId);
        return ResponseEntity.ok(response);
    }
}
