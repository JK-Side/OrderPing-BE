package com.orderping.api.order.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderping.api.order.dto.CustomerOrderDetailResponse;
import com.orderping.api.order.dto.OrderCreateRequest;
import com.orderping.api.order.dto.OrderDetailResponse;
import com.orderping.api.order.dto.OrderResponse;
import com.orderping.api.order.dto.OrderStatusUpdateRequest;
import com.orderping.api.order.dto.ServiceOrderCreateRequest;
import com.orderping.api.table.service.TableResolverService;
import com.orderping.domain.enums.OrderStatus;
import com.orderping.domain.enums.TableStatus;
import com.orderping.domain.exception.ForbiddenException;
import com.orderping.domain.exception.NotFoundException;
import com.orderping.domain.exception.OutOfStockException;
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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMenuRepository orderMenuRepository;
    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;
    private final StoreTableRepository storeTableRepository;
    private final TableResolverService tableResolverService;

    @Transactional
    public OrderResponse createOrder(OrderCreateRequest request) {
        StoreTable table = tableResolverService.resolveActiveTable(request.storeId(), request.tableNum());

        // 메뉴 조회, 재고 검증 및 가격 계산
        long totalPrice = 0L;
        Map<Long, Menu> menuMap = new java.util.HashMap<>();

        for (OrderCreateRequest.OrderMenuRequest menuRequest : request.menus()) {
            Menu menu = menuRepository.findByIdWithLock(menuRequest.menuId())
                .orElseThrow(() -> new NotFoundException("메뉴 ID " + menuRequest.menuId() + "를 찾을 수 없습니다."));

            if (menu.getStock() < menuRequest.quantity()) {
                throw new OutOfStockException(
                    String.format("'%s' 메뉴의 재고가 부족합니다. (현재: %d, 요청: %d)",
                        menu.getName(), menu.getStock(), menuRequest.quantity()));
            }

            menuRepository.decreaseStock(menuRequest.menuId(), menuRequest.quantity());
            menuMap.put(menu.getId(), menu);
            totalPrice += menu.getPrice() * menuRequest.quantity();
        }

        Long couponAmount = request.couponAmount() != null ? request.couponAmount() : 0L;

        Order order = Order.builder()
            .tableId(table.getId())
            .tableNum(table.getTableNum())
            .storeId(request.storeId())
            .depositorName(request.depositorName())
            .status(OrderStatus.PENDING)
            .totalPrice(totalPrice)
            .couponAmount(couponAmount)
            .build();

        Order savedOrder = orderRepository.save(order);

        for (OrderCreateRequest.OrderMenuRequest menuRequest : request.menus()) {
            Menu menu = menuMap.get(menuRequest.menuId());
            OrderMenu orderMenu = OrderMenu.builder()
                .orderId(savedOrder.getId())
                .menuId(menuRequest.menuId())
                .quantity(menuRequest.quantity())
                .price(menu.getPrice())
                .isService(false)
                .build();
            orderMenuRepository.save(orderMenu);
        }

        return OrderResponse.from(savedOrder);
    }

    @Transactional
    public OrderResponse createServiceOrder(ServiceOrderCreateRequest request) {
        StoreTable table = tableResolverService.resolveActiveTable(request.storeId(), request.tableNum());

        // 메뉴 조회 및 재고 검증
        Map<Long, Menu> menuMap = new java.util.HashMap<>();

        for (ServiceOrderCreateRequest.ServiceMenuRequest menuRequest : request.menus()) {
            Menu menu = menuRepository.findByIdWithLock(menuRequest.menuId())
                .orElseThrow(() -> new NotFoundException("메뉴 ID " + menuRequest.menuId() + "를 찾을 수 없습니다."));

            if (menu.getStock() < menuRequest.quantity()) {
                throw new OutOfStockException(
                    String.format("'%s' 메뉴의 재고가 부족합니다. (현재: %d, 요청: %d)",
                        menu.getName(), menu.getStock(), menuRequest.quantity()));
            }

            menuRepository.decreaseStock(menuRequest.menuId(), menuRequest.quantity());
            menuMap.put(menu.getId(), menu);
        }

        // 서비스 주문은 총액 0원, 즉시 완료 처리
        Order order = Order.builder()
            .tableId(table.getId())
            .tableNum(table.getTableNum())
            .storeId(request.storeId())
            .depositorName("서비스")
            .status(OrderStatus.COMPLETE)
            .totalPrice(0L)
            .couponAmount(0L)
            .build();

        Order savedOrder = orderRepository.save(order);

        for (ServiceOrderCreateRequest.ServiceMenuRequest menuRequest : request.menus()) {
            OrderMenu orderMenu = OrderMenu.builder()
                .orderId(savedOrder.getId())
                .menuId(menuRequest.menuId())
                .quantity(menuRequest.quantity())
                .price(0L)
                .isService(true)
                .build();
            orderMenuRepository.save(orderMenu);
        }

        return OrderResponse.from(savedOrder);
    }

    public OrderDetailResponse getOrder(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("주문을 찾을 수 없습니다."));
        return toOrderDetailResponse(order);
    }

    public List<OrderResponse> getOrdersByStore(Long userId, Long storeId, OrderStatus status) {
        validateStoreOwner(storeId, userId);

        List<Order> orders;
        if (status != null) {
            orders = orderRepository.findByStoreIdAndStatus(storeId, status);
        } else {
            orders = orderRepository.findByStoreId(storeId);
        }

        // 종료된 테이블의 주문 제외
        Set<Long> closedTableIds = getClosedTableIds(orders);

        return orders.stream()
            .filter(order -> !closedTableIds.contains(order.getTableId()))
            .map(OrderResponse::from)
            .toList();
    }

    private Set<Long> getClosedTableIds(List<Order> orders) {
        List<Long> tableIds = orders.stream()
            .map(Order::getTableId)
            .distinct()
            .toList();

        if (tableIds.isEmpty()) {
            return Set.of();
        }

        return storeTableRepository.findAllByIds(tableIds).stream()
            .filter(table -> table.getStatus() == TableStatus.CLOSED)
            .map(StoreTable::getId)
            .collect(Collectors.toSet());
    }

    public List<OrderResponse> getOrdersByTableId(Long tableId) {
        return orderRepository.findByTableId(tableId).stream()
            .map(OrderResponse::from)
            .toList();
    }

    public List<OrderDetailResponse> getOrdersWithMenusByTableId(Long tableId) {
        List<Order> orders = orderRepository.findByTableId(tableId);
        return orders.stream()
            .map(this::toOrderDetailResponse)
            .toList();
    }

    public List<CustomerOrderDetailResponse> getOrdersWithMenusByStoreAndTableNum(Long storeId, Integer tableNum) {
        StoreTable table = storeTableRepository.findActiveByStoreIdAndTableNum(storeId, tableNum)
            .orElseThrow(() -> new NotFoundException("테이블을 찾을 수 없습니다."));
        List<Order> orders = orderRepository.findByTableIdOrderById(table.getId());
        var result = new java.util.ArrayList<CustomerOrderDetailResponse>();
        for (int i = 0; i < orders.size(); i++) {
            result.add(toCustomerOrderDetailResponse(orders.get(i), i + 1));
        }
        return result;
    }

    private OrderDetailResponse toOrderDetailResponse(Order order) {
        List<OrderDetailResponse.OrderMenuDetail> menuDetails = buildMenuDetails(order);
        return OrderDetailResponse.from(order, menuDetails);
    }

    private CustomerOrderDetailResponse toCustomerOrderDetailResponse(Order order, int orderIndex) {
        List<OrderDetailResponse.OrderMenuDetail> menuDetails = buildMenuDetails(order);
        return CustomerOrderDetailResponse.from(order, menuDetails, orderIndex);
    }

    private List<OrderDetailResponse.OrderMenuDetail> buildMenuDetails(Order order) {
        List<OrderMenu> orderMenus = orderMenuRepository.findByOrderId(order.getId());

        List<Long> menuIds = orderMenus.stream()
            .map(OrderMenu::getMenuId)
            .distinct()
            .toList();

        Map<Long, String> menuNames = menuRepository.findAllByIds(menuIds).stream()
            .collect(Collectors.toMap(Menu::getId, Menu::getName));

        return orderMenus.stream()
            .map(om -> new OrderDetailResponse.OrderMenuDetail(
                om.getMenuId(),
                menuNames.getOrDefault(om.getMenuId(), "삭제된 메뉴"),
                om.getQuantity(),
                om.getPrice(),
                om.getIsService()
            ))
            .toList();
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long userId, Long id, OrderStatusUpdateRequest request) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("주문을 찾을 수 없습니다."));
        validateStoreOwner(order.getStoreId(), userId);

        Order updated = Order.builder()
            .id(order.getId())
            .tableId(order.getTableId())
            .tableNum(order.getTableNum())
            .storeId(order.getStoreId())
            .depositorName(order.getDepositorName())
            .status(request.status())
            .totalPrice(order.getTotalPrice())
            .couponAmount(order.getCouponAmount())
            .createdAt(order.getCreatedAt())
            .build();

        Order saved = orderRepository.save(updated);
        return OrderResponse.from(saved);
    }

    @Transactional
    public void deleteOrder(Long userId, Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("주문을 찾을 수 없습니다."));
        validateStoreOwner(order.getStoreId(), userId);

        // 주문 취소 시 재고 복구
        List<OrderMenu> orderMenus = orderMenuRepository.findByOrderId(id);
        for (OrderMenu orderMenu : orderMenus) {
            int updated = menuRepository.increaseStock(orderMenu.getMenuId(), orderMenu.getQuantity());
            if (updated == 0) {
                log.warn("재고 복구 실패 - 메뉴가 존재하지 않습니다: menuId={}, quantity={}, orderId={}",
                    orderMenu.getMenuId(), orderMenu.getQuantity(), id);
            }
        }

        orderRepository.deleteById(id);
    }

    private void validateStoreOwner(Long storeId, Long userId) {
        Store store = storeRepository.findById(storeId)
            .orElseThrow(() -> new NotFoundException("매장을 찾을 수 없습니다."));
        if (!store.getUserId().equals(userId)) {
            throw new ForbiddenException("본인 매장의 주문만 관리할 수 있습니다.");
        }
    }
}
