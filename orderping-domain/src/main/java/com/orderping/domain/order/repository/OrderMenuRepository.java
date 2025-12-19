package com.orderping.domain.order.repository;

import com.orderping.domain.order.OrderMenu;

import java.util.List;
import java.util.Optional;

public interface OrderMenuRepository {

    OrderMenu save(OrderMenu orderMenu);

    Optional<OrderMenu> findById(Long id);

    List<OrderMenu> findByOrderId(Long orderId);

    void deleteById(Long id);
}
