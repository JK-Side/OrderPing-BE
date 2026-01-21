package com.orderping.api.table.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.orderping.api.qr.service.QrTokenProvider;
import com.orderping.api.table.dto.StoreTableBulkCreateRequest;
import com.orderping.api.table.dto.StoreTableResponse;
import com.orderping.domain.enums.TableStatus;
import com.orderping.domain.exception.ForbiddenException;
import com.orderping.domain.exception.NotFoundException;
import com.orderping.domain.store.Store;
import com.orderping.domain.store.StoreTable;
import com.orderping.domain.store.repository.StoreRepository;
import com.orderping.domain.store.repository.StoreTableRepository;

@ExtendWith(MockitoExtension.class)
class StoreTableServiceTest {

    @Mock
    private StoreTableRepository storeTableRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private QrTokenProvider qrTokenProvider;

    @InjectMocks
    private StoreTableService storeTableService;

    private Store testStore;
    private Long userId = 1L;
    private Long storeId = 1L;

    @BeforeEach
    void setUp() {
        testStore = Store.builder()
            .id(storeId)
            .userId(userId)
            .name("테스트 주점")
            .isOpen(true)
            .build();
    }

    @Test
    @DisplayName("테이블 일괄 생성 - 성공")
    void createStoreTablesBulk_Success() {
        // given
        int count = 5;
        StoreTableBulkCreateRequest request = new StoreTableBulkCreateRequest(storeId, count);

        given(storeRepository.findById(storeId)).willReturn(Optional.of(testStore));
        given(storeTableRepository.save(any(StoreTable.class))).willAnswer(invocation -> {
            StoreTable table = invocation.getArgument(0);
            return StoreTable.builder()
                .id((long) table.getTableNum())
                .storeId(table.getStoreId())
                .tableNum(table.getTableNum())
                .status(table.getStatus())
                .build();
        });

        // when
        List<StoreTableResponse> responses = storeTableService.createStoreTablesBulk(userId, request);

        // then
        assertEquals(count, responses.size());
        for (int i = 0; i < count; i++) {
            assertEquals(i + 1, responses.get(i).tableNum());
            assertEquals(TableStatus.EMPTY, responses.get(i).status());
        }
    }

    @Test
    @DisplayName("테이블 일괄 생성 - 매장 없음 예외")
    void createStoreTablesBulk_StoreNotFound() {
        // given
        StoreTableBulkCreateRequest request = new StoreTableBulkCreateRequest(storeId, 5);
        given(storeRepository.findById(storeId)).willReturn(Optional.empty());

        // when & then
        assertThrows(NotFoundException.class, () ->
            storeTableService.createStoreTablesBulk(userId, request));
    }

    @Test
    @DisplayName("테이블 일괄 생성 - 본인 매장 아님 예외")
    void createStoreTablesBulk_NotOwner() {
        // given
        Long otherUserId = 999L;
        StoreTableBulkCreateRequest request = new StoreTableBulkCreateRequest(storeId, 5);
        given(storeRepository.findById(storeId)).willReturn(Optional.of(testStore));

        // when & then
        assertThrows(ForbiddenException.class, () ->
            storeTableService.createStoreTablesBulk(otherUserId, request));
    }

    @Test
    @DisplayName("테이블 일괄 생성 - 1개만 생성")
    void createStoreTablesBulk_SingleTable() {
        // given
        int count = 1;
        StoreTableBulkCreateRequest request = new StoreTableBulkCreateRequest(storeId, count);

        given(storeRepository.findById(storeId)).willReturn(Optional.of(testStore));
        given(storeTableRepository.save(any(StoreTable.class))).willAnswer(invocation -> {
            StoreTable table = invocation.getArgument(0);
            return StoreTable.builder()
                .id(1L)
                .storeId(table.getStoreId())
                .tableNum(table.getTableNum())
                .status(table.getStatus())
                .build();
        });

        // when
        List<StoreTableResponse> responses = storeTableService.createStoreTablesBulk(userId, request);

        // then
        assertEquals(1, responses.size());
        assertEquals(1, responses.get(0).tableNum());
    }
}
