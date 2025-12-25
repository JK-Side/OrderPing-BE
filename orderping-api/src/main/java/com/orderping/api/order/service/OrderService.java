package com.orderping.api.order.service;

import com.orderping.api.order.dto.OrderCreateRequest;
import com.orderping.api.order.dto.OrderResponse;
import com.orderping.api.order.dto.OrderStatusUpdateRequest;
import com.orderping.domain.enums.OrderStatus;
import com.orderping.domain.order.Order;
import com.orderping.domain.order.OrderMenu;
import com.orderping.domain.order.repository.OrderMenuRepository;
import com.orderping.domain.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMenuRepository orderMenuRepository;

    @Transactional
    public OrderResponse createOrder(OrderCreateRequest request) {
        // 서비스 메뉴는 가격 계산에서 제외 (0원)
        long totalPrice = request.menus().stream()
                .filter(m -> !Boolean.TRUE.equals(m.isService()))
                .mapToLong(m -> m.price() * m.quantity())
                .sum();

        Order order = Order.builder()
                .tableId(request.tableId())
                .storeId(request.storeId())
                .sessionId(request.sessionId())
                .depositorName(request.depositorName())
                .status(OrderStatus.PENDING)
                .totalPrice(totalPrice)
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
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));
        return OrderResponse.from(order);
    }

    public List<OrderResponse> getOrdersByStoreId(Long storeId) {
        return orderRepository.findByStoreId(storeId).stream()
                .map(OrderResponse::from)
                .toList();
    }

    public List<OrderResponse> getOrdersByStoreIdAndStatus(Long storeId, OrderStatus status) {
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
    public OrderResponse updateOrderStatus(Long id, OrderStatusUpdateRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));

        Order updated = Order.builder()
                .id(order.getId())
                .tableId(order.getTableId())
                .storeId(order.getStoreId())
                .sessionId(order.getSessionId())
                .depositorName(order.getDepositorName())
                .status(request.status())
                .totalPrice(order.getTotalPrice())
                .createdAt(order.getCreatedAt())
                .build();

        Order saved = orderRepository.save(updated);
        return OrderResponse.from(saved);
    }

    @Transactional
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }
}
