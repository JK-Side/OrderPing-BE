package com.orderping.api.table.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.Collections;
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

import com.orderping.api.qr.service.QrTokenProvider;
import com.orderping.api.table.dto.StoreTableBulkCreateRequest;
import com.orderping.api.table.dto.StoreTableDetailResponse;
import com.orderping.api.table.dto.StoreTableResponse;
import com.orderping.domain.enums.OrderStatus;
import com.orderping.domain.enums.TableStatus;
import com.orderping.domain.exception.ForbiddenException;
import com.orderping.domain.exception.NotFoundException;
import com.orderping.domain.menu.Menu;
import com.orderping.domain.menu.repository.MenuRepository;
import com.orderping.domain.order.Order;
import com.orderping.domain.order.OrderMenu;
import com.orderping.domain.order.repository.OrderMenuRepository;
import com.orderping.domain.order.repository.OrderRepository;
import com.orderping.domain.store.Store;
import com.orderping.domain.store.StoreTable;
import com.orderping.domain.store.repository.StoreRepository;
import com.orderping.domain.store.repository.StoreTableRepository;

@ExtendWith(MockitoExtension.class)
class StoreTableServiceTest {

    @Mock
    private StoreTableRepository storeTableRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMenuRepository orderMenuRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private QrTokenProvider qrTokenProvider;

    @InjectMocks
    private StoreTableService storeTableService;

    private Store testStore;
    private Long userId = 1L;
    private Long storeId = 1L;

    @BeforeEach
    void setUp() {
        testStore = Store.builder()
            .id(storeId)
            .userId(userId)
            .name("테스트 주점")
            .isOpen(true)
            .build();
    }

    @Nested
    @DisplayName("테이블 일괄 생성 테스트")
    class CreateStoreTablesBulkTest {

        @Test
        @DisplayName("테이블 일괄 생성 - 성공")
        void createStoreTablesBulk_Success() {
            // given
            int count = 5;
            StoreTableBulkCreateRequest request = new StoreTableBulkCreateRequest(storeId, count);

            given(storeRepository.findById(storeId)).willReturn(Optional.of(testStore));
            given(storeTableRepository.save(any(StoreTable.class))).willAnswer(invocation -> {
                StoreTable table = invocation.getArgument(0);
                return StoreTable.builder()
                    .id((long) table.getTableNum())
                    .storeId(table.getStoreId())
                    .tableNum(table.getTableNum())
                    .status(table.getStatus())
                    .build();
            });

            // when
            List<StoreTableResponse> responses = storeTableService.createStoreTablesBulk(userId, request);

            // then
            assertEquals(count, responses.size());
            for (int i = 0; i < count; i++) {
                assertEquals(i + 1, responses.get(i).tableNum());
                assertEquals(TableStatus.EMPTY, responses.get(i).status());
            }
        }

        @Test
        @DisplayName("테이블 일괄 생성 - 매장 없음 예외")
        void createStoreTablesBulk_StoreNotFound() {
            // given
            StoreTableBulkCreateRequest request = new StoreTableBulkCreateRequest(storeId, 5);
            given(storeRepository.findById(storeId)).willReturn(Optional.empty());

            // when & then
            assertThrows(NotFoundException.class, () ->
                storeTableService.createStoreTablesBulk(userId, request));
        }

        @Test
        @DisplayName("테이블 일괄 생성 - 본인 매장 아님 예외")
        void createStoreTablesBulk_NotOwner() {
            // given
            Long otherUserId = 999L;
            StoreTableBulkCreateRequest request = new StoreTableBulkCreateRequest(storeId, 5);
            given(storeRepository.findById(storeId)).willReturn(Optional.of(testStore));

            // when & then
            assertThrows(ForbiddenException.class, () ->
                storeTableService.createStoreTablesBulk(otherUserId, request));
        }

        @Test
        @DisplayName("테이블 일괄 생성 - 1개만 생성")
        void createStoreTablesBulk_SingleTable() {
            // given
            int count = 1;
            StoreTableBulkCreateRequest request = new StoreTableBulkCreateRequest(storeId, count);

            given(storeRepository.findById(storeId)).willReturn(Optional.of(testStore));
            given(storeTableRepository.save(any(StoreTable.class))).willAnswer(invocation -> {
                StoreTable table = invocation.getArgument(0);
                return StoreTable.builder()
                    .id(1L)
                    .storeId(table.getStoreId())
                    .tableNum(table.getTableNum())
                    .status(table.getStatus())
                    .build();
            });

            // when
            List<StoreTableResponse> responses = storeTableService.createStoreTablesBulk(userId, request);

            // then
            assertEquals(1, responses.size());
            assertEquals(1, responses.get(0).tableNum());
        }
    }

    @Nested
    @DisplayName("테이블 상세 조회 테스트 - 주문 상태 우선순위")
    class GetStoreTablesWithOrderStatusTest {

        private StoreTable testTable;
        private Menu testMenu;

        @BeforeEach
        void setUp() {
            testTable = StoreTable.builder()
                .id(1L)
                .storeId(storeId)
                .tableNum(1)
                .status(TableStatus.OCCUPIED)
                .build();

            testMenu = Menu.builder()
                .id(1L)
                .storeId(storeId)
                .name("소주")
                .price(5000L)
                .build();
        }

        @Test
        @DisplayName("주문이 없는 테이블 - orderStatus가 null")
        void getStoreTables_NoOrders_OrderStatusNull() {
            // given
            given(storeRepository.findById(storeId)).willReturn(Optional.of(testStore));
            given(storeTableRepository.findByStoreIdAndStatusNot(storeId, TableStatus.CLOSED))
                .willReturn(List.of(testTable));
            given(orderRepository.findByTableId(testTable.getId()))
                .willReturn(Collections.emptyList());

            // when
            List<StoreTableDetailResponse> responses = storeTableService.getStoreTables(userId, storeId, null);

            // then
            assertEquals(1, responses.size());
            assertNull(responses.get(0).orderStatus());
            assertEquals(0L, responses.get(0).totalOrderAmount());
            assertTrue(responses.get(0).orderMenus().isEmpty());
        }

        @Test
        @DisplayName("PENDING 상태 주문만 있는 테이블 - orderStatus가 PENDING")
        void getStoreTables_OnlyPendingOrders() {
            // given
            Order pendingOrder = createOrder(1L, OrderStatus.PENDING);
            OrderMenu orderMenu = createOrderMenu(1L, 1L, 2L, 5000L);

            given(storeRepository.findById(storeId)).willReturn(Optional.of(testStore));
            given(storeTableRepository.findByStoreIdAndStatusNot(storeId, TableStatus.CLOSED))
                .willReturn(List.of(testTable));
            given(orderRepository.findByTableId(testTable.getId()))
                .willReturn(List.of(pendingOrder));
            given(orderMenuRepository.findByOrderId(1L))
                .willReturn(List.of(orderMenu));
            given(menuRepository.findById(1L))
                .willReturn(Optional.of(testMenu));

            // when
            List<StoreTableDetailResponse> responses = storeTableService.getStoreTables(userId, storeId, null);

            // then
            assertEquals(1, responses.size());
            assertEquals(OrderStatus.PENDING, responses.get(0).orderStatus());
            assertEquals(10000L, responses.get(0).totalOrderAmount());
        }

        @Test
        @DisplayName("COOKING 상태 주문만 있는 테이블 - orderStatus가 COOKING")
        void getStoreTables_OnlyCookingOrders() {
            // given
            Order cookingOrder = createOrder(1L, OrderStatus.COOKING);
            OrderMenu orderMenu = createOrderMenu(1L, 1L, 1L, 5000L);

            given(storeRepository.findById(storeId)).willReturn(Optional.of(testStore));
            given(storeTableRepository.findByStoreIdAndStatusNot(storeId, TableStatus.CLOSED))
                .willReturn(List.of(testTable));
            given(orderRepository.findByTableId(testTable.getId()))
                .willReturn(List.of(cookingOrder));
            given(orderMenuRepository.findByOrderId(1L))
                .willReturn(List.of(orderMenu));
            given(menuRepository.findById(1L))
                .willReturn(Optional.of(testMenu));

            // when
            List<StoreTableDetailResponse> responses = storeTableService.getStoreTables(userId, storeId, null);

            // then
            assertEquals(1, responses.size());
            assertEquals(OrderStatus.COOKING, responses.get(0).orderStatus());
        }

        @Test
        @DisplayName("COMPLETE 상태 주문만 있는 테이블 - orderStatus가 COMPLETE")
        void getStoreTables_OnlyCompleteOrders() {
            // given
            Order completeOrder = createOrder(1L, OrderStatus.COMPLETE);
            OrderMenu orderMenu = createOrderMenu(1L, 1L, 1L, 5000L);

            given(storeRepository.findById(storeId)).willReturn(Optional.of(testStore));
            given(storeTableRepository.findByStoreIdAndStatusNot(storeId, TableStatus.CLOSED))
                .willReturn(List.of(testTable));
            given(orderRepository.findByTableId(testTable.getId()))
                .willReturn(List.of(completeOrder));
            given(orderMenuRepository.findByOrderId(1L))
                .willReturn(List.of(orderMenu));
            given(menuRepository.findById(1L))
                .willReturn(Optional.of(testMenu));

            // when
            List<StoreTableDetailResponse> responses = storeTableService.getStoreTables(userId, storeId, null);

            // then
            assertEquals(1, responses.size());
            assertEquals(OrderStatus.COMPLETE, responses.get(0).orderStatus());
        }

        @Test
        @DisplayName("PENDING + COOKING 주문이 있는 테이블 - PENDING이 우선")
        void getStoreTables_PendingAndCooking_PendingWins() {
            // given
            Order cookingOrder = createOrder(1L, OrderStatus.COOKING);
            Order pendingOrder = createOrder(2L, OrderStatus.PENDING);
            OrderMenu orderMenu1 = createOrderMenu(1L, 1L, 1L, 5000L);
            OrderMenu orderMenu2 = createOrderMenu(2L, 1L, 1L, 5000L);

            given(storeRepository.findById(storeId)).willReturn(Optional.of(testStore));
            given(storeTableRepository.findByStoreIdAndStatusNot(storeId, TableStatus.CLOSED))
                .willReturn(List.of(testTable));
            given(orderRepository.findByTableId(testTable.getId()))
                .willReturn(List.of(cookingOrder, pendingOrder));
            given(orderMenuRepository.findByOrderId(1L))
                .willReturn(List.of(orderMenu1));
            given(orderMenuRepository.findByOrderId(2L))
                .willReturn(List.of(orderMenu2));
            given(menuRepository.findById(1L))
                .willReturn(Optional.of(testMenu));

            // when
            List<StoreTableDetailResponse> responses = storeTableService.getStoreTables(userId, storeId, null);

            // then
            assertEquals(1, responses.size());
            assertEquals(OrderStatus.PENDING, responses.get(0).orderStatus());
        }

        @Test
        @DisplayName("PENDING + COMPLETE 주문이 있는 테이블 - PENDING이 우선")
        void getStoreTables_PendingAndComplete_PendingWins() {
            // given
            Order completeOrder = createOrder(1L, OrderStatus.COMPLETE);
            Order pendingOrder = createOrder(2L, OrderStatus.PENDING);
            OrderMenu orderMenu1 = createOrderMenu(1L, 1L, 1L, 5000L);
            OrderMenu orderMenu2 = createOrderMenu(2L, 1L, 1L, 5000L);

            given(storeRepository.findById(storeId)).willReturn(Optional.of(testStore));
            given(storeTableRepository.findByStoreIdAndStatusNot(storeId, TableStatus.CLOSED))
                .willReturn(List.of(testTable));
            given(orderRepository.findByTableId(testTable.getId()))
                .willReturn(List.of(completeOrder, pendingOrder));
            given(orderMenuRepository.findByOrderId(1L))
                .willReturn(List.of(orderMenu1));
            given(orderMenuRepository.findByOrderId(2L))
                .willReturn(List.of(orderMenu2));
            given(menuRepository.findById(1L))
                .willReturn(Optional.of(testMenu));

            // when
            List<StoreTableDetailResponse> responses = storeTableService.getStoreTables(userId, storeId, null);

            // then
            assertEquals(1, responses.size());
            assertEquals(OrderStatus.PENDING, responses.get(0).orderStatus());
        }

        @Test
        @DisplayName("COOKING + COMPLETE 주문이 있는 테이블 - COOKING이 우선")
        void getStoreTables_CookingAndComplete_CookingWins() {
            // given
            Order completeOrder = createOrder(1L, OrderStatus.COMPLETE);
            Order cookingOrder = createOrder(2L, OrderStatus.COOKING);
            OrderMenu orderMenu1 = createOrderMenu(1L, 1L, 1L, 5000L);
            OrderMenu orderMenu2 = createOrderMenu(2L, 1L, 1L, 5000L);

            given(storeRepository.findById(storeId)).willReturn(Optional.of(testStore));
            given(storeTableRepository.findByStoreIdAndStatusNot(storeId, TableStatus.CLOSED))
                .willReturn(List.of(testTable));
            given(orderRepository.findByTableId(testTable.getId()))
                .willReturn(List.of(completeOrder, cookingOrder));
            given(orderMenuRepository.findByOrderId(1L))
                .willReturn(List.of(orderMenu1));
            given(orderMenuRepository.findByOrderId(2L))
                .willReturn(List.of(orderMenu2));
            given(menuRepository.findById(1L))
                .willReturn(Optional.of(testMenu));

            // when
            List<StoreTableDetailResponse> responses = storeTableService.getStoreTables(userId, storeId, null);

            // then
            assertEquals(1, responses.size());
            assertEquals(OrderStatus.COOKING, responses.get(0).orderStatus());
        }

        @Test
        @DisplayName("세 가지 상태 모두 있는 테이블 - PENDING이 우선")
        void getStoreTables_AllStatuses_PendingWins() {
            // given
            Order completeOrder = createOrder(1L, OrderStatus.COMPLETE);
            Order cookingOrder = createOrder(2L, OrderStatus.COOKING);
            Order pendingOrder = createOrder(3L, OrderStatus.PENDING);
            OrderMenu orderMenu1 = createOrderMenu(1L, 1L, 1L, 5000L);
            OrderMenu orderMenu2 = createOrderMenu(2L, 1L, 2L, 5000L);
            OrderMenu orderMenu3 = createOrderMenu(3L, 1L, 3L, 5000L);

            given(storeRepository.findById(storeId)).willReturn(Optional.of(testStore));
            given(storeTableRepository.findByStoreIdAndStatusNot(storeId, TableStatus.CLOSED))
                .willReturn(List.of(testTable));
            given(orderRepository.findByTableId(testTable.getId()))
                .willReturn(List.of(completeOrder, cookingOrder, pendingOrder));
            given(orderMenuRepository.findByOrderId(1L))
                .willReturn(List.of(orderMenu1));
            given(orderMenuRepository.findByOrderId(2L))
                .willReturn(List.of(orderMenu2));
            given(orderMenuRepository.findByOrderId(3L))
                .willReturn(List.of(orderMenu3));
            given(menuRepository.findById(1L))
                .willReturn(Optional.of(testMenu));

            // when
            List<StoreTableDetailResponse> responses = storeTableService.getStoreTables(userId, storeId, null);

            // then
            assertEquals(1, responses.size());
            assertEquals(OrderStatus.PENDING, responses.get(0).orderStatus());
            assertEquals(30000L, responses.get(0).totalOrderAmount()); // 5000 * (1+2+3)
            assertEquals(3, responses.get(0).orderMenus().size());
        }

        @Test
        @DisplayName("여러 테이블 조회 - 각 테이블별로 우선순위 계산")
        void getStoreTables_MultipleTables() {
            // given
            StoreTable table1 = StoreTable.builder()
                .id(1L).storeId(storeId).tableNum(1).status(TableStatus.OCCUPIED).build();
            StoreTable table2 = StoreTable.builder()
                .id(2L).storeId(storeId).tableNum(2).status(TableStatus.OCCUPIED).build();
            StoreTable table3 = StoreTable.builder()
                .id(3L).storeId(storeId).tableNum(3).status(TableStatus.EMPTY).build();

            Order pendingOrder = createOrder(1L, OrderStatus.PENDING);
            Order cookingOrder = createOrder(2L, OrderStatus.COOKING);
            OrderMenu orderMenu1 = createOrderMenu(1L, 1L, 1L, 5000L);
            OrderMenu orderMenu2 = createOrderMenu(2L, 1L, 1L, 5000L);

            given(storeRepository.findById(storeId)).willReturn(Optional.of(testStore));
            given(storeTableRepository.findByStoreIdAndStatusNot(storeId, TableStatus.CLOSED))
                .willReturn(List.of(table1, table2, table3));
            given(orderRepository.findByTableId(1L)).willReturn(List.of(pendingOrder));
            given(orderRepository.findByTableId(2L)).willReturn(List.of(cookingOrder));
            given(orderRepository.findByTableId(3L)).willReturn(Collections.emptyList());
            given(orderMenuRepository.findByOrderId(1L)).willReturn(List.of(orderMenu1));
            given(orderMenuRepository.findByOrderId(2L)).willReturn(List.of(orderMenu2));
            given(menuRepository.findById(1L)).willReturn(Optional.of(testMenu));

            // when
            List<StoreTableDetailResponse> responses = storeTableService.getStoreTables(userId, storeId, null);

            // then
            assertEquals(3, responses.size());
            assertEquals(OrderStatus.PENDING, responses.get(0).orderStatus());
            assertEquals(OrderStatus.COOKING, responses.get(1).orderStatus());
            assertNull(responses.get(2).orderStatus());
        }

        @Test
        @DisplayName("삭제된 메뉴가 있는 경우 - '삭제된 메뉴'로 표시")
        void getStoreTables_DeletedMenu() {
            // given
            Order order = createOrder(1L, OrderStatus.PENDING);
            OrderMenu orderMenu = createOrderMenu(1L, 999L, 1L, 5000L);

            given(storeRepository.findById(storeId)).willReturn(Optional.of(testStore));
            given(storeTableRepository.findByStoreIdAndStatusNot(storeId, TableStatus.CLOSED))
                .willReturn(List.of(testTable));
            given(orderRepository.findByTableId(testTable.getId()))
                .willReturn(List.of(order));
            given(orderMenuRepository.findByOrderId(1L))
                .willReturn(List.of(orderMenu));
            given(menuRepository.findById(999L))
                .willReturn(Optional.empty());

            // when
            List<StoreTableDetailResponse> responses = storeTableService.getStoreTables(userId, storeId, null);

            // then
            assertEquals(1, responses.size());
            assertEquals("삭제된 메뉴", responses.get(0).orderMenus().get(0).menuName());
        }

        private Order createOrder(Long id, OrderStatus status) {
            return Order.builder()
                .id(id)
                .tableId(testTable.getId())
                .storeId(storeId)
                .status(status)
                .totalPrice(5000L)
                .build();
        }

        private OrderMenu createOrderMenu(Long orderId, Long menuId, Long quantity, Long price) {
            return OrderMenu.builder()
                .id(orderId)
                .orderId(orderId)
                .menuId(menuId)
                .quantity(quantity)
                .price(price)
                .build();
        }
    }
}
