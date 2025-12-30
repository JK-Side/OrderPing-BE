package com.orderping.api.order.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderping.api.order.dto.OrderCreateRequest;
import com.orderping.api.order.dto.OrderResponse;
import com.orderping.api.order.dto.OrderStatusUpdateRequest;
import com.orderping.domain.enums.OrderStatus;
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
import com.orderping.domain.store.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMenuRepository orderMenuRepository;
    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;

    @Transactional
    public OrderResponse createOrder(OrderCreateRequest request) {
        // 비관적 락으로 메뉴 조회 및 재고 검증
        for (OrderCreateRequest.OrderMenuRequest menuRequest : request.menus()) {
            Menu menu = menuRepository.findByIdWithLock(menuRequest.menuId())
                .orElseThrow(() -> new NotFoundException("메뉴 ID " + menuRequest.menuId() + "를 찾을 수 없습니다."));

            if (menu.getStock() < menuRequest.quantity()) {
                throw new OutOfStockException(
                    String.format("'%s' 메뉴의 재고가 부족합니다. (현재: %d, 요청: %d)",
                        menu.getName(), menu.getStock(), menuRequest.quantity()));
            }

            menuRepository.decreaseStock(menuRequest.menuId(), menuRequest.quantity());
        }

        // 서비스 메뉴는 가격 계산에서 제외 (0원)
        long totalPrice = request.menus().stream()
            .filter(m -> !Boolean.TRUE.equals(m.isService()))
            .mapToLong(m -> m.price() * m.quantity())
            .sum();

        Long couponAmount = request.couponAmount() != null ? request.couponAmount() : 0L;

        Order order = Order.builder()
            .tableId(request.tableId())
            .storeId(request.storeId())
            .sessionId(request.sessionId())
            .depositorName(request.depositorName())
            .status(OrderStatus.PENDING)
            .totalPrice(totalPrice)
            .couponAmount(couponAmount)
            .build();

        Order savedOrder = orderRepository.save(order);

        for (OrderCreateRequest.OrderMenuRequest menuRequest : request.menus()) {
            boolean isService = Boolean.TRUE.equals(menuRequest.isService());
            OrderMenu orderMenu = OrderMenu.builder()
                .orderId(savedOrder.getId())
                .menuId(menuRequest.menuId())
                .quantity(menuRequest.quantity())
                .price(isService ? 0L : menuRequest.price())
                .isService(isService)
                .build();
            orderMenuRepository.save(orderMenu);
        }

        return OrderResponse.from(savedOrder);
    }

    public OrderResponse getOrder(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("주문을 찾을 수 없습니다."));
        return OrderResponse.from(order);
    }

    public List<OrderResponse> getOrdersByStoreId(Long userId, Long storeId) {
        validateStoreOwner(storeId, userId);
        return orderRepository.findByStoreId(storeId).stream()
            .map(OrderResponse::from)
            .toList();
    }

    public List<OrderResponse> getOrdersByStoreIdAndStatus(Long userId, Long storeId, OrderStatus status) {
        validateStoreOwner(storeId, userId);
        return orderRepository.findByStoreIdAndStatus(storeId, status).stream()
            .map(OrderResponse::from)
            .toList();
    }

    public List<OrderResponse> getOrdersByTableId(Long tableId) {
        return orderRepository.findByTableId(tableId).stream()
            .map(OrderResponse::from)
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
            .storeId(order.getStoreId())
            .sessionId(order.getSessionId())
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
