package com.orderping.api.table.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.orderping.domain.enums.TableStatus;
import com.orderping.domain.exception.NotFoundException;
import com.orderping.domain.order.Order;
import com.orderping.domain.order.repository.OrderRepository;
import com.orderping.domain.store.StoreTable;
import com.orderping.domain.store.repository.StoreTableRepository;

@ExtendWith(MockitoExtension.class)
class TableResolverServiceTest {

    private final Long storeId = 1L;
    private final Integer tableNum = 3;
    @Mock
    private StoreTableRepository storeTableRepository;
    @Mock
    private OrderRepository orderRepository;
    @InjectMocks
    private TableResolverService tableResolverService;

    private StoreTable activeTable(Long id) {
        return StoreTable.builder()
            .id(id)
            .storeId(storeId)
            .tableNum(tableNum)
            .status(TableStatus.OCCUPIED)
            .qrImageUrl("https://s3.../qr.png")
            .build();
    }

    private Order order(Long id, Long tableId) {
        return Order.builder()
            .id(id)
            .tableId(tableId)
            .storeId(storeId)
            .build();
    }

    @Nested
    @DisplayName("활성 테이블이 1개인 경우")
    class SingleActiveTable {

        @Test
        @DisplayName("정상 반환")
        void resolve_SingleTable_ReturnsIt() {
            StoreTable table = activeTable(1L);
            given(storeTableRepository.findAllActiveByStoreIdAndTableNum(storeId, tableNum))
                .willReturn(List.of(table));

            StoreTable result = tableResolverService.resolveActiveTable(storeId, tableNum);

            assertEquals(1L, result.getId());
            verify(storeTableRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("활성 테이블이 없는 경우")
    class NoActiveTable {

        @Test
        @DisplayName("NotFoundException 발생")
        void resolve_NoTable_ThrowsNotFoundException() {
            given(storeTableRepository.findAllActiveByStoreIdAndTableNum(storeId, tableNum))
                .willReturn(List.of());

            assertThrows(NotFoundException.class,
                () -> tableResolverService.resolveActiveTable(storeId, tableNum));
        }
    }

    @Nested
    @DisplayName("활성 테이블이 2개 이상인 경우")
    class MultipleActiveTables {

        @Test
        @DisplayName("주문 있는 테이블이 1개면 그것을 선택, 나머지는 CLOSED")
        void resolve_OneTableHasOrders_SelectsIt() {
            StoreTable table1 = activeTable(1L);
            StoreTable table2 = activeTable(2L);

            given(storeTableRepository.findAllActiveByStoreIdAndTableNum(storeId, tableNum))
                .willReturn(List.of(table1, table2));
            given(orderRepository.findByTableId(1L)).willReturn(List.of(order(10L, 1L)));
            given(orderRepository.findByTableId(2L)).willReturn(List.of());
            given(storeTableRepository.save(any())).willAnswer(inv -> inv.getArgument(0));

            StoreTable result = tableResolverService.resolveActiveTable(storeId, tableNum);

            assertEquals(1L, result.getId());

            ArgumentCaptor<StoreTable> captor = forClass(StoreTable.class);
            verify(storeTableRepository).save(captor.capture());
            assertEquals(2L, captor.getValue().getId());
            assertEquals(TableStatus.CLOSED, captor.getValue().getStatus());
        }

        @Test
        @DisplayName("주문 있는 테이블이 2개면 id 높은 것을 선택, 나머지는 CLOSED")
        void resolve_BothTablesHaveOrders_SelectsHigherId() {
            StoreTable table1 = activeTable(1L);
            StoreTable table2 = activeTable(2L);

            given(storeTableRepository.findAllActiveByStoreIdAndTableNum(storeId, tableNum))
                .willReturn(List.of(table1, table2));
            given(orderRepository.findByTableId(1L)).willReturn(List.of(order(10L, 1L)));
            given(orderRepository.findByTableId(2L)).willReturn(List.of(order(11L, 2L)));
            given(storeTableRepository.save(any())).willAnswer(inv -> inv.getArgument(0));

            StoreTable result = tableResolverService.resolveActiveTable(storeId, tableNum);

            assertEquals(2L, result.getId());

            ArgumentCaptor<StoreTable> captor = forClass(StoreTable.class);
            verify(storeTableRepository).save(captor.capture());
            assertEquals(1L, captor.getValue().getId());
            assertEquals(TableStatus.CLOSED, captor.getValue().getStatus());
        }

        @Test
        @DisplayName("주문 없는 테이블만 있으면 id 높은 것을 선택, 나머지는 CLOSED")
        void resolve_NoOrdersOnAny_SelectsHigherId() {
            StoreTable table1 = activeTable(1L);
            StoreTable table2 = activeTable(2L);

            given(storeTableRepository.findAllActiveByStoreIdAndTableNum(storeId, tableNum))
                .willReturn(List.of(table1, table2));
            given(orderRepository.findByTableId(1L)).willReturn(List.of());
            given(orderRepository.findByTableId(2L)).willReturn(List.of());
            given(storeTableRepository.save(any())).willAnswer(inv -> inv.getArgument(0));

            StoreTable result = tableResolverService.resolveActiveTable(storeId, tableNum);

            assertEquals(2L, result.getId());

            ArgumentCaptor<StoreTable> captor = forClass(StoreTable.class);
            verify(storeTableRepository).save(captor.capture());
            assertEquals(1L, captor.getValue().getId());
            assertEquals(TableStatus.CLOSED, captor.getValue().getStatus());
        }

        @Test
        @DisplayName("3개 중 주문 있는 테이블이 1개면 그것을 선택, 나머지 2개는 CLOSED")
        void resolve_ThreeTables_OneHasOrders_SelectsIt_ClosesTwo() {
            StoreTable table1 = activeTable(1L);
            StoreTable table2 = activeTable(2L);
            StoreTable table3 = activeTable(3L);

            given(storeTableRepository.findAllActiveByStoreIdAndTableNum(storeId, tableNum))
                .willReturn(List.of(table1, table2, table3));
            given(orderRepository.findByTableId(1L)).willReturn(List.of());
            given(orderRepository.findByTableId(2L)).willReturn(List.of(order(10L, 2L)));
            given(orderRepository.findByTableId(3L)).willReturn(List.of());
            given(storeTableRepository.save(any())).willAnswer(inv -> inv.getArgument(0));

            StoreTable result = tableResolverService.resolveActiveTable(storeId, tableNum);

            assertEquals(2L, result.getId());

            ArgumentCaptor<StoreTable> captor = forClass(StoreTable.class);
            verify(storeTableRepository, times(2)).save(captor.capture());

            List<StoreTable> closed = captor.getAllValues();
            assertEquals(2, closed.size());
            assertTrue(closed.stream().allMatch(t -> t.getStatus() == TableStatus.CLOSED));
            assertTrue(closed.stream().anyMatch(t -> t.getId().equals(1L)));
            assertTrue(closed.stream().anyMatch(t -> t.getId().equals(3L)));
        }

        @Test
        @DisplayName("CLOSED 처리 시 qrImageUrl이 유지된다")
        void resolve_ClosedTable_PreservesQrImageUrl() {
            StoreTable table1 = StoreTable.builder()
                .id(1L).storeId(storeId).tableNum(tableNum)
                .status(TableStatus.OCCUPIED).qrImageUrl("https://s3.../qr1.png").build();
            StoreTable table2 = activeTable(2L);

            given(storeTableRepository.findAllActiveByStoreIdAndTableNum(storeId, tableNum))
                .willReturn(List.of(table1, table2));
            given(orderRepository.findByTableId(1L)).willReturn(List.of());
            given(orderRepository.findByTableId(2L)).willReturn(List.of());
            given(storeTableRepository.save(any())).willAnswer(inv -> inv.getArgument(0));

            tableResolverService.resolveActiveTable(storeId, tableNum);

            ArgumentCaptor<StoreTable> captor = forClass(StoreTable.class);
            verify(storeTableRepository).save(captor.capture());

            assertEquals("https://s3.../qr1.png", captor.getValue().getQrImageUrl());
        }
    }
}
