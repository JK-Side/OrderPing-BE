package com.orderping.api.statistics.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderping.api.statistics.dto.MenuStatisticsResponse;
import com.orderping.api.statistics.dto.StatisticsResponse;
import com.orderping.domain.exception.ForbiddenException;
import com.orderping.domain.exception.NotFoundException;
import com.orderping.domain.menu.Menu;
import com.orderping.domain.menu.repository.MenuRepository;
import com.orderping.domain.order.Order;
import com.orderping.domain.order.OrderMenu;
import com.orderping.domain.order.repository.OrderMenuRepository;
import com.orderping.domain.order.repository.OrderRepository;
import com.orderping.domain.store.Store;
import com.orderping.domain.store.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsService {

    private final OrderRepository orderRepository;
    private final OrderMenuRepository orderMenuRepository;
    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;

    public StatisticsResponse getStatistics(Long userId, Long storeId, LocalDate from, LocalDate to) {
        validateStoreOwner(storeId, userId);

        LocalDateTime fromDateTime = from.atStartOfDay();
        LocalDateTime toDateTime = to.atTime(LocalTime.MAX);

        List<Order> orders = orderRepository.findByStoreIdAndCreatedAtBetween(storeId, fromDateTime, toDateTime);

        long totalRevenue = orders.stream().mapToLong(Order::getTotalPrice).sum();
        long couponRevenue = orders.stream().mapToLong(Order::getCouponAmount).sum();
        long transferRevenue = totalRevenue - couponRevenue;

        List<Long> orderIds = orders.stream().map(Order::getId).toList();
        List<OrderMenu> allOrderMenus = orderIds.isEmpty() ? List.of() : orderMenuRepository.findByOrderIds(orderIds);

        List<Long> menuIds = allOrderMenus.stream().map(OrderMenu::getMenuId).distinct().toList();
        Map<Long, String> menuNameMap = menuRepository.findAllByIds(menuIds).stream()
            .collect(Collectors.toMap(Menu::getId, Menu::getName));

        Map<Long, List<OrderMenu>> orderMenuMap = allOrderMenus.stream()
            .collect(Collectors.groupingBy(OrderMenu::getOrderId));

        List<StatisticsResponse.OrderSummary> orderSummaries = orders.stream()
            .map(order -> {
                List<StatisticsResponse.MenuDetail> menuDetails = orderMenuMap
                    .getOrDefault(order.getId(), List.of()).stream()
                    .map(om -> new StatisticsResponse.MenuDetail(
                        menuNameMap.getOrDefault(om.getMenuId(), "삭제된 메뉴"),
                        om.getQuantity(),
                        om.getPrice(),
                        om.getIsService()
                    ))
                    .toList();
                return new StatisticsResponse.OrderSummary(
                    order.getId(),
                    order.getTableNum(),
                    order.getCreatedAt(),
                    menuDetails,
                    order.getTotalPrice(),
                    order.getDepositorName()
                );
            })
            .toList();

        return new StatisticsResponse(totalRevenue, transferRevenue, couponRevenue, orders.size(), orderSummaries);
    }

    public MenuStatisticsResponse getMenuStatistics(Long userId, Long storeId, LocalDate from, LocalDate to) {
        validateStoreOwner(storeId, userId);

        LocalDateTime fromDateTime = from.atStartOfDay();
        LocalDateTime toDateTime = to.atTime(LocalTime.MAX);

        List<Order> orders = orderRepository.findByStoreIdAndCreatedAtBetween(storeId, fromDateTime, toDateTime);

        List<Long> orderIds = orders.stream().map(Order::getId).toList();
        List<OrderMenu> allOrderMenus = orderIds.isEmpty() ? List.of() : orderMenuRepository.findByOrderIds(orderIds);

        Map<Long, Long> soldMap = allOrderMenus.stream()
            .collect(Collectors.groupingBy(OrderMenu::getMenuId, Collectors.summingLong(OrderMenu::getQuantity)));

        List<Menu> menus = menuRepository.findByStoreId(storeId);
        List<MenuStatisticsResponse.MenuStat> menuStats = menus.stream()
            .map(menu -> new MenuStatisticsResponse.MenuStat(
                menu.getId(),
                menu.getName(),
                menu.getStock(),
                soldMap.getOrDefault(menu.getId(), 0L)
            ))
            .toList();

        return new MenuStatisticsResponse(menuStats);
    }

    private void validateStoreOwner(Long storeId, Long userId) {
        Store store = storeRepository.findById(storeId)
            .orElseThrow(() -> new NotFoundException("매장을 찾을 수 없습니다."));
        if (!store.getUserId().equals(userId)) {
            throw new ForbiddenException("본인 매장의 통계만 조회할 수 있습니다.");
        }
    }
}
