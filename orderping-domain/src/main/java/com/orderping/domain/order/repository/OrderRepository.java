package com.orderping.domain.order.repository;

import java.util.List;
import java.util.Optional;

import com.orderping.domain.enums.OrderStatus;
import com.orderping.domain.order.Order;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(Long id);

    List<Order> findByStoreId(Long storeId);

    List<Order> findByTableId(Long tableId);

    List<Order> findByTableIdOrderById(Long tableId);

    List<Order> findByStoreIdAndStatus(Long storeId, OrderStatus status);

    void deleteById(Long id);
}
