package com.orderping.domain.store;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.orderping.domain.enums.TableStatus;

class StoreTableTest {

    @Test
    @DisplayName("StoreTable 객체 생성 테스트")
    void createStoreTable() {
        // when
        StoreTable table = StoreTable.builder()
            .id(1L)
            .storeId(1L)
            .tableNum(1)
            .status(TableStatus.EMPTY)
            .build();

        // then
        assertNotNull(table);
        assertEquals(1L, table.getId());
        assertEquals(1L, table.getStoreId());
        assertEquals(1, table.getTableNum());
        assertEquals(TableStatus.EMPTY, table.getStatus());
    }

    @Test
    @DisplayName("테이블 상태 변경 테스트")
    void tableStatusTest() {
        // given
        StoreTable emptyTable = StoreTable.builder()
            .id(1L)
            .storeId(1L)
            .tableNum(1)
            .status(TableStatus.EMPTY)
            .build();

        StoreTable occupiedTable = StoreTable.builder()
            .id(2L)
            .storeId(1L)
            .tableNum(2)
            .status(TableStatus.OCCUPIED)
            .build();

        StoreTable reservedTable = StoreTable.builder()
            .id(3L)
            .storeId(1L)
            .tableNum(3)
            .status(TableStatus.RESERVED)
            .build();

        // then
        assertEquals(TableStatus.EMPTY, emptyTable.getStatus());
        assertEquals(TableStatus.OCCUPIED, occupiedTable.getStatus());
        assertEquals(TableStatus.RESERVED, reservedTable.getStatus());
    }
}
