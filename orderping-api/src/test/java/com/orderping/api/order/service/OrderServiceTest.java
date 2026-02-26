package com.orderping.api.order.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.orderping.api.order.dto.OrderCreateRequest;
import com.orderping.api.order.dto.OrderCreateRequest.OrderMenuRequest;
import com.orderping.api.order.dto.ServiceOrderCreateRequest;
import com.orderping.api.order.dto.ServiceOrderCreateRequest.ServiceMenuRequest;
import com.orderping.api.table.service.TableResolverService;
import com.orderping.domain.enums.OrderStatus;
import com.orderping.domain.enums.TableStatus;
import com.orderping.domain.exception.NotFoundException;
import com.orderping.domain.menu.Menu;
import com.orderping.domain.menu.repository.MenuRepository;
import com.orderping.domain.order.Order;
import com.orderping.domain.order.repository.OrderMenuRepository;
import com.orderping.domain.order.repository.OrderRepository;
import com.orderping.domain.store.Store;
import com.orderping.domain.store.StoreTable;
import com.orderping.domain.store.repository.StoreRepository;
import com.orderping.domain.store.repository.StoreTableRepository;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private OrderMenuRepository orderMenuRepository;
    @Mock private MenuRepository menuRepository;
    @Mock private StoreRepository storeRepository;
    @Mock private StoreTableRepository storeTableRepository;
    @Mock private TableResolverService tableResolverService;

    @InjectMocks
    private OrderService orderService;

    private final Long storeId = 1L;
    private final Long tableId = 10L;
    private final Integer tableNum = 3;
    private final Long menuId = 100L;

    private StoreTable activeTable;
    private Menu testMenu;

    @BeforeEach
    void setUp() {
        activeTable = StoreTable.builder()
            .id(tableId)
            .storeId(storeId)
            .tableNum(tableNum)
            .status(TableStatus.OCCUPIED)
            .build();

        testMenu = Menu.builder()
            .id(menuId)
            .storeId(storeId)
            .name("소주")
            .price(5000L)
            .stock(100L)
            .isSoldOut(false)
            .build();
    }

    @Nested
    @DisplayName("주문 생성 (createOrder)")
    class CreateOrder {

        @Test
        @DisplayName("storeId + tableNum으로 테이블을 조회해 주문을 생성한다")
        void createOrder_ResolvesTableByStoreAndTableNum() {
            OrderCreateRequest request = new OrderCreateRequest(
                tableNum, storeId, "홍길동", 0L, List.of(new OrderMenuRequest(menuId, 2L))
            );

            given(tableResolverService.resolveActiveTable(storeId, tableNum)).willReturn(activeTable);
            given(menuRepository.findByIdWithLock(menuId)).willReturn(Optional.of(testMenu));
            given(orderRepository.save(any())).willAnswer(inv -> {
                Order o = inv.getArgument(0);
                return Order.builder().id(1L).tableId(o.getTableId()).tableNum(o.getTableNum())
                    .storeId(o.getStoreId()).depositorName(o.getDepositorName())
                    .status(o.getStatus()).totalPrice(o.getTotalPrice()).couponAmount(o.getCouponAmount()).build();
            });

            orderService.createOrder(request);

            ArgumentCaptor<Order> captor = forClass(Order.class);
            verify(orderRepository).save(captor.capture());

            Order saved = captor.getValue();
            assertEquals(tableId, saved.getTableId());
            assertEquals(tableNum, saved.getTableNum());
            assertEquals(storeId, saved.getStoreId());
        }

        @Test
        @DisplayName("가격은 서버의 메뉴 DB 기준으로 계산된다")
        void createOrder_PriceCalculatedFromMenu() {
            OrderCreateRequest request = new OrderCreateRequest(
                tableNum, storeId, "홍길동", 0L, List.of(new OrderMenuRequest(menuId, 3L))
            );

            given(tableResolverService.resolveActiveTable(storeId, tableNum)).willReturn(activeTable);
            given(menuRepository.findByIdWithLock(menuId)).willReturn(Optional.of(testMenu));
            given(orderRepository.save(any())).willAnswer(inv -> {
                Order o = inv.getArgument(0);
                return Order.builder().id(1L).tableId(o.getTableId()).tableNum(o.getTableNum())
                    .storeId(o.getStoreId()).depositorName(o.getDepositorName())
                    .status(o.getStatus()).totalPrice(o.getTotalPrice()).couponAmount(o.getCouponAmount()).build();
            });

            orderService.createOrder(request);

            ArgumentCaptor<Order> captor = forClass(Order.class);
            verify(orderRepository).save(captor.capture());

            assertEquals(15000L, captor.getValue().getTotalPrice()); // 5000 * 3
        }

        @Test
        @DisplayName("쿠폰 금액이 null이면 0으로 처리된다")
        void createOrder_NullCouponAmount_TreatedAsZero() {
            OrderCreateRequest request = new OrderCreateRequest(
                tableNum, storeId, "홍길동", null, List.of(new OrderMenuRequest(menuId, 1L))
            );

            given(tableResolverService.resolveActiveTable(storeId, tableNum)).willReturn(activeTable);
            given(menuRepository.findByIdWithLock(menuId)).willReturn(Optional.of(testMenu));
            given(orderRepository.save(any())).willAnswer(inv -> {
                Order o = inv.getArgument(0);
                return Order.builder().id(1L).tableId(o.getTableId()).tableNum(o.getTableNum())
                    .storeId(o.getStoreId()).status(o.getStatus()).totalPrice(o.getTotalPrice())
                    .couponAmount(o.getCouponAmount()).build();
            });

            orderService.createOrder(request);

            ArgumentCaptor<Order> captor = forClass(Order.class);
            verify(orderRepository).save(captor.capture());
            assertEquals(0L, captor.getValue().getCouponAmount());
        }

        @Test
        @DisplayName("테이블을 찾을 수 없으면 NotFoundException 발생")
        void createOrder_TableNotFound_ThrowsNotFoundException() {
            OrderCreateRequest request = new OrderCreateRequest(
                tableNum, storeId, "홍길동", 0L, List.of(new OrderMenuRequest(menuId, 1L))
            );

            given(tableResolverService.resolveActiveTable(storeId, tableNum))
                .willThrow(new NotFoundException("테이블을 찾을 수 없습니다."));

            assertThrows(NotFoundException.class, () -> orderService.createOrder(request));
        }

        @Test
        @DisplayName("메뉴를 찾을 수 없으면 NotFoundException 발생")
        void createOrder_MenuNotFound_ThrowsNotFoundException() {
            OrderCreateRequest request = new OrderCreateRequest(
                tableNum, storeId, "홍길동", 0L, List.of(new OrderMenuRequest(999L, 1L))
            );

            given(tableResolverService.resolveActiveTable(storeId, tableNum)).willReturn(activeTable);
            given(menuRepository.findByIdWithLock(999L)).willReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> orderService.createOrder(request));
        }

        @Test
        @DisplayName("주문 상태는 PENDING으로 생성된다")
        void createOrder_StatusIsPending() {
            OrderCreateRequest request = new OrderCreateRequest(
                tableNum, storeId, "홍길동", 0L, List.of(new OrderMenuRequest(menuId, 1L))
            );

            given(tableResolverService.resolveActiveTable(storeId, tableNum)).willReturn(activeTable);
            given(menuRepository.findByIdWithLock(menuId)).willReturn(Optional.of(testMenu));
            given(orderRepository.save(any())).willAnswer(inv -> {
                Order o = inv.getArgument(0);
                return Order.builder().id(1L).tableId(o.getTableId()).tableNum(o.getTableNum())
                    .storeId(o.getStoreId()).status(o.getStatus()).totalPrice(o.getTotalPrice())
                    .couponAmount(o.getCouponAmount()).build();
            });

            orderService.createOrder(request);

            ArgumentCaptor<Order> captor = forClass(Order.class);
            verify(orderRepository).save(captor.capture());
            assertEquals(OrderStatus.PENDING, captor.getValue().getStatus());
        }
    }

    @Nested
    @DisplayName("서비스 주문 생성 (createServiceOrder)")
    class CreateServiceOrder {

        @Test
        @DisplayName("storeId + tableNum으로 테이블을 조회해 서비스 주문을 생성한다")
        void createServiceOrder_ResolvesTableByStoreAndTableNum() {
            ServiceOrderCreateRequest request = new ServiceOrderCreateRequest(
                tableNum, storeId, List.of(new ServiceMenuRequest(menuId, 1L))
            );

            given(tableResolverService.resolveActiveTable(storeId, tableNum)).willReturn(activeTable);
            given(menuRepository.findByIdWithLock(menuId)).willReturn(Optional.of(testMenu));
            given(orderRepository.save(any())).willAnswer(inv -> {
                Order o = inv.getArgument(0);
                return Order.builder().id(1L).tableId(o.getTableId()).tableNum(o.getTableNum())
                    .storeId(o.getStoreId()).depositorName(o.getDepositorName())
                    .status(o.getStatus()).totalPrice(o.getTotalPrice()).couponAmount(o.getCouponAmount()).build();
            });

            orderService.createServiceOrder(request);

            ArgumentCaptor<Order> captor = forClass(Order.class);
            verify(orderRepository).save(captor.capture());

            Order saved = captor.getValue();
            assertEquals(tableId, saved.getTableId());
            assertEquals(tableNum, saved.getTableNum());
        }

        @Test
        @DisplayName("서비스 주문은 총액 0원, 즉시 COMPLETE 처리, 주문자명 '서비스'")
        void createServiceOrder_ZeroPriceCompleteStatus() {
            ServiceOrderCreateRequest request = new ServiceOrderCreateRequest(
                tableNum, storeId, List.of(new ServiceMenuRequest(menuId, 2L))
            );

            given(tableResolverService.resolveActiveTable(storeId, tableNum)).willReturn(activeTable);
            given(menuRepository.findByIdWithLock(menuId)).willReturn(Optional.of(testMenu));
            given(orderRepository.save(any())).willAnswer(inv -> {
                Order o = inv.getArgument(0);
                return Order.builder().id(1L).tableId(o.getTableId()).tableNum(o.getTableNum())
                    .storeId(o.getStoreId()).depositorName(o.getDepositorName())
                    .status(o.getStatus()).totalPrice(o.getTotalPrice()).couponAmount(o.getCouponAmount()).build();
            });

            orderService.createServiceOrder(request);

            ArgumentCaptor<Order> captor = forClass(Order.class);
            verify(orderRepository).save(captor.capture());

            Order saved = captor.getValue();
            assertEquals(0L, saved.getTotalPrice());
            assertEquals(OrderStatus.COMPLETE, saved.getStatus());
            assertEquals("서비스", saved.getDepositorName());
        }

        @Test
        @DisplayName("테이블을 찾을 수 없으면 NotFoundException 발생")
        void createServiceOrder_TableNotFound_ThrowsNotFoundException() {
            ServiceOrderCreateRequest request = new ServiceOrderCreateRequest(
                tableNum, storeId, List.of(new ServiceMenuRequest(menuId, 1L))
            );

            given(tableResolverService.resolveActiveTable(storeId, tableNum))
                .willThrow(new NotFoundException("테이블을 찾을 수 없습니다."));

            assertThrows(NotFoundException.class, () -> orderService.createServiceOrder(request));
        }
    }
}
