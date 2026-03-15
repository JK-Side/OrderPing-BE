package com.orderping.api.statistics.controller;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.orderping.api.auth.security.CurrentUser;
import com.orderping.api.statistics.dto.MenuStatisticsResponse;
import com.orderping.api.statistics.dto.StatisticsResponse;
import com.orderping.api.statistics.service.StatisticsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController implements StatisticsApi {

    private final StatisticsService statisticsService;

    @GetMapping
    public ResponseEntity<StatisticsResponse> getStatistics(
        @CurrentUser Long userId,
        @RequestParam Long storeId,
        @RequestParam LocalDate from,
        @RequestParam LocalDate to
    ) {
        return ResponseEntity.ok(statisticsService.getStatistics(userId, storeId, from, to));
    }

    @GetMapping("/menus")
    public ResponseEntity<MenuStatisticsResponse> getMenuStatistics(
        @CurrentUser Long userId,
        @RequestParam Long storeId,
        @RequestParam LocalDate from,
        @RequestParam LocalDate to
    ) {
        return ResponseEntity.ok(statisticsService.getMenuStatistics(userId, storeId, from, to));
    }
}
