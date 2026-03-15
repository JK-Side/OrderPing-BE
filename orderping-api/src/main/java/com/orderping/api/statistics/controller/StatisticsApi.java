package com.orderping.api.statistics.controller;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import com.orderping.api.statistics.dto.MenuStatisticsResponse;
import com.orderping.api.statistics.dto.StatisticsResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Statistics", description = "통계 API")
public interface StatisticsApi {

    @Operation(summary = "통계 조회", description = "기간별 매출 요약 및 전체 주문 목록 조회")
    ResponseEntity<StatisticsResponse> getStatistics(
        @Parameter(hidden = true) Long userId,
        @RequestParam Long storeId,
        @RequestParam LocalDate from,
        @RequestParam LocalDate to
    );

    @Operation(summary = "메뉴별 통계 조회", description = "기간별 메뉴별 재고량 및 판매량 조회")
    ResponseEntity<MenuStatisticsResponse> getMenuStatistics(
        @Parameter(hidden = true) Long userId,
        @RequestParam Long storeId,
        @RequestParam LocalDate from,
        @RequestParam LocalDate to
    );
}
