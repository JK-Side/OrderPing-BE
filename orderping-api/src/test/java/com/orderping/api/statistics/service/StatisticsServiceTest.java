package com.orderping.api.statistics.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.orderping.api.statistics.dto.MenuStatisticsResponse;
import com.orderping.api.statistics.dto.StatisticsResponse;
import com.orderping.domain.enums.OrderStatus;
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

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {

    private final Long userId = 1L;
    private final Long storeId = 10L;
    private final Long otherUserId = 99L;

    @Mock private OrderRepository orderRepository;
    @Mock private OrderMenuRepository orderMenuRepository;
    @Mock private MenuRepository menuRepository;
    @Mock private StoreRepository storeRepository;

    @InjectMocks
    private StatisticsService statisticsService;

    private Store store;
    private final LocalDate from = LocalDate.of(2026, 1, 23);
    private final LocalDate to = LocalDate.of(2026, 1, 24);

    @BeforeEach
    void setUp() {
        store = Store.builder()
            .id(storeId)
            .userId(userId)
            .name("테스트 주점")
            .build();
    }

    private Order order(Long id, long totalPrice, long couponAmount) {
        return Order.builder()
            .id(id)
            .storeId(storeId)
            .tableNum(1)
            .tableId(100L)
            .depositorName("홍길동")
            .status(OrderStatus.COMPLETE)
            .totalPrice(totalPrice)
            .couponAmount(couponAmount)
            .createdAt(LocalDateTime.of(2026, 1, 23, 18, 0))
            .build();
    }

    private OrderMenu orderMenu(Long orderId, Long menuId, Long quantity, Long price, boolean isService) {
        return OrderMenu.builder()
            .orderId(orderId)
            .menuId(menuId)
            .quantity(quantity)
            .price(price)
            .isService(isService)
            .build();
    }

    private Menu menu(Long menuId, String name, Long stock) {
        return Menu.builder()
            .id(menuId)
            .storeId(storeId)
            .name(name)
            .price(5000L)
            .stock(stock)
            .isSoldOut(false)
            .build();
    }

    @Nested
    @DisplayName("통계 조회 (getStatistics)")
    class GetStatistics {

        @Test
        @DisplayName("총 매출, 입금 매출, 쿠폰 매출을 올바르게 계산한다")
        void getStatistics_RevenueCalculatedCorrectly() {
            Order o1 = order(1L, 30000L, 5000L);  // transfer: 25000, coupon: 5000
            Order o2 = order(2L, 20000L, 0L);     // transfer: 20000, coupon: 0

            given(storeRepository.findById(storeId)).willReturn(Optional.of(store));
            given(orderRepository.findByStoreIdAndCreatedAtBetween(eq(storeId), any(), any()))
                .willReturn(List.of(o1, o2));
            given(orderRepository.findByStoreId(storeId)).willReturn(List.of(o1, o2));
            given(orderMenuRepository.findByOrderIds(List.of(1L, 2L))).willReturn(List.of());
            given(menuRepository.findAllByIds(List.of())).willReturn(List.of());

            StatisticsResponse result = statisticsService.getStatistics(userId, storeId, from, to);

            assertEquals(50000L, result.totalRevenue());     // 30000 + 20000
            assertEquals(45000L, result.transferRevenue());  // 50000 - 5000
            assertEquals(5000L, result.couponRevenue());     // 5000 + 0
        }

        @Test
        @DisplayName("총 주문 수를 올바르게 반환한다")
        void getStatistics_OrderCountCorrect() {
            Order o1 = order(1L, 10000L, 0L);
            Order o2 = order(2L, 20000L, 0L);
            Order o3 = order(3L, 5000L, 0L);

            given(storeRepository.findById(storeId)).willReturn(Optional.of(store));
            given(orderRepository.findByStoreIdAndCreatedAtBetween(eq(storeId), any(), any()))
                .willReturn(List.of(o1, o2, o3));
            given(orderRepository.findByStoreId(storeId)).willReturn(List.of(o1, o2, o3));
            given(orderMenuRepository.findByOrderIds(any())).willReturn(List.of());
            given(menuRepository.findAllByIds(any())).willReturn(List.of());

            StatisticsResponse result = statisticsService.getStatistics(userId, storeId, from, to);

            assertEquals(3, result.orderCount());
        }

        @Test
        @DisplayName("주문 목록에 메뉴 상세 정보가 포함된다")
        void getStatistics_OrdersIncludeMenuDetails() {
            Order o1 = order(1L, 10000L, 0L);
            OrderMenu om = orderMenu(1L, 100L, 2L, 5000L, false);
            Menu m = menu(100L, "소주", 50L);

            given(storeRepository.findById(storeId)).willReturn(Optional.of(store));
            given(orderRepository.findByStoreIdAndCreatedAtBetween(eq(storeId), any(), any()))
                .willReturn(List.of(o1));
            given(orderRepository.findByStoreId(storeId)).willReturn(List.of(o1));
            given(orderMenuRepository.findByOrderIds(List.of(1L))).willReturn(List.of(om));
            given(menuRepository.findAllByIds(List.of(100L))).willReturn(List.of(m));

            StatisticsResponse result = statisticsService.getStatistics(userId, storeId, from, to);

            assertEquals(1, result.orders().size());
            StatisticsResponse.OrderSummary summary = result.orders().get(0);
            assertEquals(1, summary.orderNumber());
            assertEquals(1, summary.tableNum());
            assertEquals(1, summary.menus().size());

            StatisticsResponse.MenuDetail detail = summary.menus().get(0);
            assertEquals("소주", detail.menuName());
            assertEquals(2L, detail.quantity());
            assertEquals(5000L, detail.price());
            assertEquals(false, detail.isService());
        }

        @Test
        @DisplayName("서비스 주문(isService=true)도 포함된다")
        void getStatistics_ServiceOrderIncluded() {
            Order o1 = order(1L, 0L, 0L);
            OrderMenu serviceOm = orderMenu(1L, 100L, 1L, 0L, true);
            Menu m = menu(100L, "서비스 안주", 50L);

            given(storeRepository.findById(storeId)).willReturn(Optional.of(store));
            given(orderRepository.findByStoreIdAndCreatedAtBetween(eq(storeId), any(), any()))
                .willReturn(List.of(o1));
            given(orderRepository.findByStoreId(storeId)).willReturn(List.of(o1));
            given(orderMenuRepository.findByOrderIds(List.of(1L))).willReturn(List.of(serviceOm));
            given(menuRepository.findAllByIds(List.of(100L))).willReturn(List.of(m));

            StatisticsResponse result = statisticsService.getStatistics(userId, storeId, from, to);

            StatisticsResponse.MenuDetail detail = result.orders().get(0).menus().get(0);
            assertEquals(true, detail.isService());
        }

        @Test
        @DisplayName("삭제된 메뉴는 '삭제된 메뉴'로 표시된다")
        void getStatistics_DeletedMenuShowsPlaceholder() {
            Order o1 = order(1L, 10000L, 0L);
            OrderMenu om = orderMenu(1L, 999L, 2L, 5000L, false); // 존재하지 않는 메뉴

            given(storeRepository.findById(storeId)).willReturn(Optional.of(store));
            given(orderRepository.findByStoreIdAndCreatedAtBetween(eq(storeId), any(), any()))
                .willReturn(List.of(o1));
            given(orderRepository.findByStoreId(storeId)).willReturn(List.of(o1));
            given(orderMenuRepository.findByOrderIds(List.of(1L))).willReturn(List.of(om));
            given(menuRepository.findAllByIds(List.of(999L))).willReturn(List.of());

            StatisticsResponse result = statisticsService.getStatistics(userId, storeId, from, to);

            assertEquals("삭제된 메뉴", result.orders().get(0).menus().get(0).menuName());
        }

        @Test
        @DisplayName("기간 내 주문이 없으면 매출 0, 빈 리스트 반환")
        void getStatistics_NoOrders_ReturnsZeroAndEmpty() {
            given(storeRepository.findById(storeId)).willReturn(Optional.of(store));
            given(orderRepository.findByStoreIdAndCreatedAtBetween(eq(storeId), any(), any()))
                .willReturn(List.of());
            given(orderRepository.findByStoreId(storeId)).willReturn(List.of());

            StatisticsResponse result = statisticsService.getStatistics(userId, storeId, from, to);

            assertEquals(0L, result.totalRevenue());
            assertEquals(0L, result.transferRevenue());
            assertEquals(0L, result.couponRevenue());
            assertEquals(0, result.orderCount());
            assertTrue(result.orders().isEmpty());
        }

        @Test
        @DisplayName("orderNumber는 주점 전체 주문 기준 순번이다")
        void getStatistics_OrderNumber_IsStoreWideSequence() {
            // 주점 전체 주문: 1~5번 존재, 기간 내에는 3번, 5번만 조회됨
            Order o3 = order(3L, 10000L, 0L);
            Order o5 = order(5L, 20000L, 0L);
            List<Order> allOrders = List.of(
                order(1L, 0L, 0L), order(2L, 0L, 0L), o3, order(4L, 0L, 0L), o5
            );

            given(storeRepository.findById(storeId)).willReturn(Optional.of(store));
            given(orderRepository.findByStoreIdAndCreatedAtBetween(eq(storeId), any(), any()))
                .willReturn(List.of(o3, o5));
            given(orderRepository.findByStoreId(storeId)).willReturn(allOrders);
            given(orderMenuRepository.findByOrderIds(List.of(3L, 5L))).willReturn(List.of());
            given(menuRepository.findAllByIds(List.of())).willReturn(List.of());

            StatisticsResponse result = statisticsService.getStatistics(userId, storeId, from, to);

            assertEquals(3, result.orders().get(0).orderNumber()); // 3번째 주문
            assertEquals(5, result.orders().get(1).orderNumber()); // 5번째 주문
        }

        @Test
        @DisplayName("본인 매장이 아니면 ForbiddenException 발생")
        void getStatistics_NotOwner_ThrowsForbiddenException() {
            given(storeRepository.findById(storeId)).willReturn(Optional.of(store));

            assertThrows(ForbiddenException.class,
                () -> statisticsService.getStatistics(otherUserId, storeId, from, to));
        }

        @Test
        @DisplayName("존재하지 않는 매장이면 NotFoundException 발생")
        void getStatistics_StoreNotFound_ThrowsNotFoundException() {
            given(storeRepository.findById(storeId)).willReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                () -> statisticsService.getStatistics(userId, storeId, from, to));
        }
    }

    @Nested
    @DisplayName("메뉴별 통계 조회 (getMenuStatistics)")
    class GetMenuStatistics {

        @Test
        @DisplayName("기간 내 메뉴별 판매량을 올바르게 집계한다")
        void getMenuStatistics_SoldQuantityAggregatedCorrectly() {
            Order o1 = order(1L, 10000L, 0L);
            Order o2 = order(2L, 15000L, 0L);
            OrderMenu om1 = orderMenu(1L, 100L, 2L, 5000L, false);
            OrderMenu om2 = orderMenu(2L, 100L, 3L, 5000L, false);
            Menu m = menu(100L, "소주", 45L);

            given(storeRepository.findById(storeId)).willReturn(Optional.of(store));
            given(orderRepository.findByStoreIdAndCreatedAtBetween(eq(storeId), any(), any()))
                .willReturn(List.of(o1, o2));
            given(orderMenuRepository.findByOrderIds(List.of(1L, 2L))).willReturn(List.of(om1, om2));
            given(menuRepository.findByStoreId(storeId)).willReturn(List.of(m));

            MenuStatisticsResponse result = statisticsService.getMenuStatistics(userId, storeId, from, to);

            assertEquals(1, result.menus().size());
            MenuStatisticsResponse.MenuStat stat = result.menus().get(0);
            assertEquals(100L, stat.menuId());
            assertEquals("소주", stat.menuName());
            assertEquals(45L, stat.stock());
            assertEquals(5L, stat.soldQuantity()); // 2 + 3
        }

        @Test
        @DisplayName("기간 내 판매되지 않은 메뉴는 soldQuantity가 0이다")
        void getMenuStatistics_UnsoldMenu_SoldQuantityIsZero() {
            Menu m = menu(200L, "맥주", 100L);

            given(storeRepository.findById(storeId)).willReturn(Optional.of(store));
            given(orderRepository.findByStoreIdAndCreatedAtBetween(eq(storeId), any(), any()))
                .willReturn(List.of());
            given(menuRepository.findByStoreId(storeId)).willReturn(List.of(m));

            MenuStatisticsResponse result = statisticsService.getMenuStatistics(userId, storeId, from, to);

            assertEquals(1, result.menus().size());
            assertEquals(0L, result.menus().get(0).soldQuantity());
        }

        @Test
        @DisplayName("서비스 주문의 수량도 판매량에 포함된다")
        void getMenuStatistics_ServiceOrderIncludedInSoldQuantity() {
            Order o1 = order(1L, 0L, 0L);
            OrderMenu serviceOm = orderMenu(1L, 100L, 2L, 0L, true);
            Menu m = menu(100L, "소주", 98L);

            given(storeRepository.findById(storeId)).willReturn(Optional.of(store));
            given(orderRepository.findByStoreIdAndCreatedAtBetween(eq(storeId), any(), any()))
                .willReturn(List.of(o1));
            given(orderMenuRepository.findByOrderIds(List.of(1L))).willReturn(List.of(serviceOm));
            given(menuRepository.findByStoreId(storeId)).willReturn(List.of(m));

            MenuStatisticsResponse result = statisticsService.getMenuStatistics(userId, storeId, from, to);

            assertEquals(2L, result.menus().get(0).soldQuantity());
        }

        @Test
        @DisplayName("여러 메뉴가 각각 독립적으로 집계된다")
        void getMenuStatistics_MultipleMenusAggregatedSeparately() {
            Order o1 = order(1L, 15000L, 0L);
            OrderMenu om1 = orderMenu(1L, 100L, 3L, 5000L, false); // 소주 3
            OrderMenu om2 = orderMenu(1L, 200L, 1L, 8000L, false); // 안주 1
            Menu m1 = menu(100L, "소주", 47L);
            Menu m2 = menu(200L, "안주", 9L);

            given(storeRepository.findById(storeId)).willReturn(Optional.of(store));
            given(orderRepository.findByStoreIdAndCreatedAtBetween(eq(storeId), any(), any()))
                .willReturn(List.of(o1));
            given(orderMenuRepository.findByOrderIds(List.of(1L))).willReturn(List.of(om1, om2));
            given(menuRepository.findByStoreId(storeId)).willReturn(List.of(m1, m2));

            MenuStatisticsResponse result = statisticsService.getMenuStatistics(userId, storeId, from, to);

            assertEquals(2, result.menus().size());
            MenuStatisticsResponse.MenuStat soju = result.menus().stream()
                .filter(s -> s.menuId().equals(100L)).findFirst().orElseThrow();
            MenuStatisticsResponse.MenuStat food = result.menus().stream()
                .filter(s -> s.menuId().equals(200L)).findFirst().orElseThrow();

            assertEquals(3L, soju.soldQuantity());
            assertEquals(1L, food.soldQuantity());
        }

        @Test
        @DisplayName("본인 매장이 아니면 ForbiddenException 발생")
        void getMenuStatistics_NotOwner_ThrowsForbiddenException() {
            given(storeRepository.findById(storeId)).willReturn(Optional.of(store));

            assertThrows(ForbiddenException.class,
                () -> statisticsService.getMenuStatistics(otherUserId, storeId, from, to));
        }

        @Test
        @DisplayName("존재하지 않는 매장이면 NotFoundException 발생")
        void getMenuStatistics_StoreNotFound_ThrowsNotFoundException() {
            given(storeRepository.findById(storeId)).willReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                () -> statisticsService.getMenuStatistics(userId, storeId, from, to));
        }
    }
}
