package com.orderping.api.order.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.dao.DataIntegrityViolationException;
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
import com.orderping.domain.exception.BadRequestException;
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
        if (request.idempotencyKey() != null) {
            Optional<Order> existing = orderRepository.findByIdempotencyKey(request.idempotencyKey());
            if (existing.isPresent()) {
                return OrderResponse.from(existing.get());
            }
        }

        StoreTable table = tableResolverService.resolveActiveTable(request.storeId(), request.tableNum());

        // 메뉴 조회 및 재고 검증 (pre-check, 실제 차감은 주문 저장 후 deductStockForOrder에서 락과 함께 처리)
        Map<Long, Menu> menuMap = new java.util.HashMap<>();
        List<OutOfStockException.StockItem> stockFailures = new java.util.ArrayList<>();

        for (OrderCreateRequest.OrderMenuRequest menuRequest : request.menus()) {
            Menu menu = menuRepository.findById(menuRequest.menuId())
                .orElseThrow(() -> new NotFoundException("메뉴 ID " + menuRequest.menuId() + "를 찾을 수 없습니다."));

            if (!menu.getStoreId().equals(request.storeId())) {
                throw new BadRequestException("메뉴 ID " + menuRequest.menuId() + "는 해당 주점의 메뉴가 아닙니다.");
            }

            if (!Boolean.TRUE.equals(menu.getIsTableFee()) && menuRequest.quantity() > menu.getStock()) {
                stockFailures.add(new OutOfStockException.StockItem(
                    menu.getId(), menu.getName(), menuRequest.quantity(), menu.getStock()));
            }

            menuMap.put(menu.getId(), menu);
        }

        if (!stockFailures.isEmpty()) {
            throw new OutOfStockException("재고가 부족합니다.", stockFailures);
        }

        long totalPrice = request.menus().stream()
            .mapToLong(mr -> menuMap.get(mr.menuId()).getPrice() * mr.quantity())
            .sum();

        Long couponAmount = request.couponAmount() != null ? request.couponAmount() : 0L;
        if (couponAmount > totalPrice) {
            throw new BadRequestException("쿠폰 금액이 주문 금액을 초과할 수 없습니다.");
        }

        Order order = Order.builder()
            .tableId(table.getId())
            .tableNum(table.getTableNum())
            .storeId(request.storeId())
            .depositorName(request.depositorName())
            .status(OrderStatus.PENDING)
            .totalPrice(totalPrice)
            .couponAmount(couponAmount)
            .idempotencyKey(request.idempotencyKey())
            .build();

        Order savedOrder;
        try {
            savedOrder = orderRepository.save(order);
        } catch (DataIntegrityViolationException e) {
            return orderRepository.findByIdempotencyKey(request.idempotencyKey())
                .map(OrderResponse::from)
                .orElseThrow(() -> e);
        }

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

        deductStockForOrder(savedOrder.getId());

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

            if (!menu.getStoreId().equals(request.storeId())) {
                throw new BadRequestException("메뉴 ID " + menuRequest.menuId() + "는 해당 주점의 메뉴가 아닙니다.");
            }

            if (menu.getStock() < menuRequest.quantity()) {
                throw new OutOfStockException("재고가 부족합니다.", List.of(new OutOfStockException.StockItem(
                    menu.getId(), menu.getName(), menuRequest.quantity(), menu.getStock())));
            }

            long newStock = menu.getStock() - menuRequest.quantity();
            menuRepository.save(Menu.builder()
                .id(menu.getId()).storeId(menu.getStoreId()).categoryId(menu.getCategoryId())
                .name(menu.getName()).price(menu.getPrice()).description(menu.getDescription())
                .imageUrl(menu.getImageUrl()).initialStock(menu.getInitialStock())
                .stock(newStock).isSoldOut(newStock == 0)
                .isTableFee(menu.getIsTableFee())
                .version(menu.getVersion())
                .build());
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

    public OrderDetailResponse getOrder(Long userId, Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("주문을 찾을 수 없습니다."));
        validateStoreOwner(order.getStoreId(), userId);
        long storeOrderNumber = orderRepository.countByStoreIdUpToId(order.getStoreId(), id);
        List<OrderDetailResponse.OrderMenuDetail> menuDetails = buildMenuDetails(order);
        return OrderDetailResponse.from(order, menuDetails, storeOrderNumber);
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

        List<Long> allStoreOrderIds = orderRepository.findByStoreId(storeId).stream()
            .map(Order::getId)
            .sorted()
            .toList();
        Map<Long, Integer> storeOrderNumberMap = new java.util.HashMap<>();
        for (int i = 0; i < allStoreOrderIds.size(); i++) {
            storeOrderNumberMap.put(allStoreOrderIds.get(i), i + 1);
        }

        return orders.stream()
            .map(order -> toCustomerOrderDetailResponse(order, storeOrderNumberMap.getOrDefault(order.getId(), 0)))
            .toList();
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
                om.getPrice() * om.getQuantity(),
                om.getIsService()
            ))
            .toList();
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long userId, Long id, OrderStatusUpdateRequest request) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("주문을 찾을 수 없습니다."));
        validateStoreOwner(order.getStoreId(), userId);

        OrderStatus newStatus = request.status();

        Order updated = Order.builder()
            .id(order.getId())
            .tableId(order.getTableId())
            .tableNum(order.getTableNum())
            .storeId(order.getStoreId())
            .depositorName(order.getDepositorName())
            .status(newStatus)
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

        recoverStockForOrder(id);
        orderRepository.deleteById(id);
    }

    private void deductStockForOrder(Long orderId) {
        List<OrderMenu> orderMenus = orderMenuRepository.findByOrderId(orderId);
        for (OrderMenu orderMenu : orderMenus) {
            Menu menu = menuRepository.findByIdWithLock(orderMenu.getMenuId()).orElse(null);
            if (menu == null || Boolean.TRUE.equals(menu.getIsTableFee()))
                continue;
            if (menu.getStock() < orderMenu.getQuantity()) {
                throw new OutOfStockException("재고가 부족합니다.", List.of(new OutOfStockException.StockItem(
                    menu.getId(), menu.getName(), orderMenu.getQuantity(), menu.getStock())));
            }
            long newStock = menu.getStock() - orderMenu.getQuantity();
            menuRepository.save(Menu.builder()
                .id(menu.getId()).storeId(menu.getStoreId()).categoryId(menu.getCategoryId())
                .name(menu.getName()).price(menu.getPrice()).description(menu.getDescription())
                .imageUrl(menu.getImageUrl()).initialStock(menu.getInitialStock())
                .stock(newStock).isSoldOut(newStock == 0)
                .isTableFee(menu.getIsTableFee())
                .version(menu.getVersion())
                .build());
        }
    }

    private void recoverStockForOrder(Long orderId) {
        List<OrderMenu> orderMenus = orderMenuRepository.findByOrderId(orderId);
        List<Long> menuIds = orderMenus.stream().map(OrderMenu::getMenuId).distinct().toList();
        Map<Long, Menu> existingMenus = menuRepository.findAllByIds(menuIds).stream()
            .collect(Collectors.toMap(Menu::getId, m -> m));
        for (OrderMenu orderMenu : orderMenus) {
            Menu menu = existingMenus.get(orderMenu.getMenuId());
            if (menu == null) {
                log.warn("삭제된 메뉴의 재고를 복구할 수 없습니다: menuId={}, orderId={}", orderMenu.getMenuId(), orderId);
                continue;
            }
            if (Boolean.TRUE.equals(menu.getIsTableFee()))
                continue;
            menuRepository.increaseStock(orderMenu.getMenuId(), orderMenu.getQuantity());
        }
    }

    private void validateStoreOwner(Long storeId, Long userId) {
        Store store = storeRepository.findById(storeId)
            .orElseThrow(() -> new NotFoundException("매장을 찾을 수 없습니다."));
        if (!store.getUserId().equals(userId)) {
            throw new ForbiddenException("본인 매장의 주문만 관리할 수 있습니다.");
        }
    }
}
