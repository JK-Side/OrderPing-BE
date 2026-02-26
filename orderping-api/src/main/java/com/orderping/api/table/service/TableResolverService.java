package com.orderping.api.table.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderping.domain.enums.TableStatus;
import com.orderping.domain.exception.NotFoundException;
import com.orderping.domain.order.repository.OrderRepository;
import com.orderping.domain.store.StoreTable;
import com.orderping.domain.store.repository.StoreTableRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TableResolverService {

    private final StoreTableRepository storeTableRepository;
    private final OrderRepository orderRepository;

    /**
     * storeId + tableNum으로 활성 테이블을 조회한다.
     * 활성 테이블이 2개 이상인 경우:
     * 1. 주문이 있는 테이블 선택 (복수면 가장 최근 생성 기준)
     * 2. 주문이 없으면 가장 최근 생성된 테이블 선택
     * 3. 선택되지 않은 테이블은 CLOSED 처리
     */
    @Transactional
    public StoreTable resolveActiveTable(Long storeId, Integer tableNum) {
        List<StoreTable> activeTables = storeTableRepository.findAllActiveByStoreIdAndTableNum(storeId, tableNum);

        if (activeTables.isEmpty()) {
            throw new NotFoundException("테이블을 찾을 수 없습니다.");
        }

        if (activeTables.size() == 1) {
            return activeTables.get(0);
        }

        // 주문이 있는 테이블만 추려냄
        List<StoreTable> tablesWithOrders = activeTables.stream()
            .filter(table -> !orderRepository.findByTableId(table.getId()).isEmpty())
            .toList();

        StoreTable selected;
        if (!tablesWithOrders.isEmpty()) {
            // 주문 있는 것 중 가장 최근 생성(id 기준)
            selected = tablesWithOrders.stream()
                .max(Comparator.comparing(StoreTable::getId))
                .get();
        } else {
            // 모두 주문 없으면 가장 최근 생성
            selected = activeTables.stream()
                .max(Comparator.comparing(StoreTable::getId))
                .get();
        }

        // 선택되지 않은 테이블 CLOSED 처리
        activeTables.stream()
            .filter(table -> !table.getId().equals(selected.getId()))
            .forEach(table -> storeTableRepository.save(
                StoreTable.builder()
                    .id(table.getId())
                    .storeId(table.getStoreId())
                    .tableNum(table.getTableNum())
                    .status(TableStatus.CLOSED)
                    .qrImageUrl(table.getQrImageUrl())
                    .build()
            ));

        return selected;
    }
}
