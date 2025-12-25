package com.orderping.api.table.service;

import com.orderping.api.table.dto.StoreTableCreateRequest;
import com.orderping.api.table.dto.StoreTableResponse;
import com.orderping.api.table.dto.StoreTableStatusUpdateRequest;
import com.orderping.domain.enums.TableStatus;
import com.orderping.domain.store.StoreTable;
import com.orderping.domain.store.repository.StoreTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreTableService {

    private final StoreTableRepository storeTableRepository;

    @Transactional
    public StoreTableResponse createStoreTable(StoreTableCreateRequest request) {
        StoreTable storeTable = StoreTable.builder()
                .storeId(request.storeId())
                .tableNum(request.tableNum())
                .status(TableStatus.EMPTY)
                .build();

        StoreTable saved = storeTableRepository.save(storeTable);
        return StoreTableResponse.from(saved);
    }

    public StoreTableResponse getStoreTable(Long id) {
        StoreTable storeTable = storeTableRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("StoreTable not found: " + id));
        return StoreTableResponse.from(storeTable);
    }

    public List<StoreTableResponse> getStoreTablesByStoreId(Long storeId) {
        return storeTableRepository.findByStoreId(storeId).stream()
                .map(StoreTableResponse::from)
                .toList();
    }

    public List<StoreTableResponse> getStoreTablesByStoreIdAndStatus(Long storeId, TableStatus status) {
        return storeTableRepository.findByStoreIdAndStatus(storeId, status).stream()
                .map(StoreTableResponse::from)
                .toList();
    }

    @Transactional
    public StoreTableResponse updateStoreTableStatus(Long id, StoreTableStatusUpdateRequest request) {
        StoreTable storeTable = storeTableRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("StoreTable not found: " + id));

        StoreTable updated = StoreTable.builder()
                .id(storeTable.getId())
                .storeId(storeTable.getStoreId())
                .tableNum(storeTable.getTableNum())
                .status(request.status())
                .build();

        StoreTable saved = storeTableRepository.save(updated);
        return StoreTableResponse.from(saved);
    }

    @Transactional
    public void deleteStoreTable(Long id) {
        storeTableRepository.deleteById(id);
    }

    @Transactional
    public StoreTableResponse clearTable(Long id) {
        StoreTable currentTable = storeTableRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("StoreTable not found: " + id));

        // 기존 테이블 종료 처리
        StoreTable closedTable = StoreTable.builder()
                .id(currentTable.getId())
                .storeId(currentTable.getStoreId())
                .tableNum(currentTable.getTableNum())
                .status(TableStatus.CLOSED)
                .build();
        storeTableRepository.save(closedTable);

        // 같은 번호의 새 테이블 생성
        StoreTable newTable = StoreTable.builder()
                .storeId(currentTable.getStoreId())
                .tableNum(currentTable.getTableNum())
                .status(TableStatus.EMPTY)
                .build();
        StoreTable saved = storeTableRepository.save(newTable);

        return StoreTableResponse.from(saved);
    }
}
