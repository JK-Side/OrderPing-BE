package com.orderping.api.bank.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.orderping.api.bank.dto.BankResponse;
import com.orderping.api.bank.service.BankService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/banks")
@RequiredArgsConstructor
public class BankController implements BankApi {

    private final BankService bankService;

    @GetMapping
    @Override
    public ResponseEntity<List<BankResponse>> getAllBanks() {
        List<BankResponse> response = bankService.getAllBanks();
        return ResponseEntity.ok(response);
    }
}
