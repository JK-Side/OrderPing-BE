package com.orderping.api.table.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderping.api.qr.service.QrTokenProvider;
import com.orderping.api.table.dto.StoreTableCreateRequest;
import com.orderping.api.table.dto.StoreTableResponse;
import com.orderping.api.table.dto.StoreTableStatusUpdateRequest;
import com.orderping.api.table.dto.StoreTableUpdateRequest;
import com.orderping.domain.enums.TableStatus;
import com.orderping.domain.exception.ForbiddenException;
import com.orderping.domain.exception.NotFoundException;
import com.orderping.domain.store.Store;
import com.orderping.domain.store.StoreTable;
import com.orderping.domain.store.repository.StoreRepository;
import com.orderping.domain.store.repository.StoreTableRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreTableService {

    private final StoreTableRepository storeTableRepository;
    private final StoreRepository storeRepository;
    private final QrTokenProvider qrTokenProvider;

    @Transactional
    public StoreTableResponse createStoreTable(Long userId, StoreTableCreateRequest request) {
        validateStoreOwner(request.storeId(), userId);
        StoreTable storeTable = StoreTable.builder()
            .storeId(request.storeId())
            .tableNum(request.tableNum())
            .status(TableStatus.EMPTY)
            .build();

        StoreTable saved = storeTableRepository.save(storeTable);

        // QR 토큰 생성
        String qrToken = qrTokenProvider.createTableToken(
            saved.getStoreId(),
            saved.getId(),
            saved.getTableNum()
        );
        String qrUrl = qrTokenProvider.buildTableQrUrl(qrToken);

        return StoreTableResponse.from(saved, qrToken, qrUrl);
    }

    public StoreTableResponse getStoreTable(Long id) {
        StoreTable storeTable = storeTableRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("테이블을 찾을 수 없습니다."));
        return StoreTableResponse.from(storeTable);
    }

    public List<StoreTableResponse> getStoreTablesByStoreId(Long userId, Long storeId) {
        validateStoreOwner(storeId, userId);
        return storeTableRepository.findByStoreId(storeId).stream()
            .map(StoreTableResponse::from)
            .toList();
    }

    public List<StoreTableResponse> getStoreTablesByStoreIdAndStatus(Long userId, Long storeId, TableStatus status) {
        validateStoreOwner(storeId, userId);
        return storeTableRepository.findByStoreIdAndStatus(storeId, status).stream()
            .map(StoreTableResponse::from)
            .toList();
    }

    @Transactional
    public StoreTableResponse updateStoreTableStatus(Long userId, Long id, StoreTableStatusUpdateRequest request) {
        StoreTable storeTable = storeTableRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("테이블을 찾을 수 없습니다."));
        validateStoreOwner(storeTable.getStoreId(), userId);

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
    public void deleteStoreTable(Long userId, Long id) {
        StoreTable storeTable = storeTableRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("테이블을 찾을 수 없습니다."));
        validateStoreOwner(storeTable.getStoreId(), userId);
        storeTableRepository.deleteById(id);
    }

    @Transactional
    public StoreTableResponse updateStoreTable(Long userId, Long id, StoreTableUpdateRequest request) {
        StoreTable storeTable = storeTableRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("테이블을 찾을 수 없습니다."));
        validateStoreOwner(storeTable.getStoreId(), userId);

        StoreTable updated = StoreTable.builder()
            .id(storeTable.getId())
            .storeId(storeTable.getStoreId())
            .tableNum(storeTable.getTableNum())
            .status(storeTable.getStatus())
            .qrImageUrl(request.qrImageUrl())
            .build();

        StoreTable saved = storeTableRepository.save(updated);
        return StoreTableResponse.from(saved);
    }

    @Transactional
    public StoreTableResponse clearTable(Long userId, Long id) {
        StoreTable currentTable = storeTableRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("테이블을 찾을 수 없습니다."));
        validateStoreOwner(currentTable.getStoreId(), userId);

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

    private void validateStoreOwner(Long storeId, Long userId) {
        Store store = storeRepository.findById(storeId)
            .orElseThrow(() -> new NotFoundException("매장을 찾을 수 없습니다."));
        if (!store.getUserId().equals(userId)) {
            throw new ForbiddenException("본인 매장의 테이블만 관리할 수 있습니다.");
        }
    }
}
