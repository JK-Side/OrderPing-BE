package com.orderping.infra.order.repository;

import com.orderping.domain.order.OrderMenu;
import com.orderping.domain.order.repository.OrderMenuRepository;
import com.orderping.infra.order.entity.OrderMenuEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OrderMenuRepositoryImpl implements OrderMenuRepository {

    private final OrderMenuJpaRepository jpaRepository;

    @Override
    public OrderMenu save(OrderMenu orderMenu) {
        OrderMenuEntity entity = OrderMenuEntity.from(orderMenu);
        OrderMenuEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<OrderMenu> findById(Long id) {
        return jpaRepository.findById(id)
                .map(OrderMenuEntity::toDomain);
    }

    @Override
    public List<OrderMenu> findByOrderId(Long orderId) {
        return jpaRepository.findByOrderId(orderId).stream()
                .map(OrderMenuEntity::toDomain)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
