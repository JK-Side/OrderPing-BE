package com.orderping.domain.store;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class StoreTest {

    @Test
    @DisplayName("Store 객체 생성 테스트")
    void createStore() {
        // given
        LocalDateTime now = LocalDateTime.now();

        // when
        Store store = Store.builder()
                .id(1L)
                .userId(1L)
                .name("맛있는 포차")
                .description("분위기 좋은 포장마차")
                .createdAt(now)
                .isOpen(false)
                .imageUrl("https://example.com/store.jpg")
                .build();

        // then
        assertNotNull(store);
        assertEquals(1L, store.getId());
        assertEquals(1L, store.getUserId());
        assertEquals("맛있는 포차", store.getName());
        assertEquals("분위기 좋은 포장마차", store.getDescription());
        assertFalse(store.getIsOpen());
    }

    @Test
    @DisplayName("Store는 영업 중 상태를 가질 수 있다")
    void storeCanBeOpen() {
        // when
        Store store = Store.builder()
                .id(1L)
                .userId(1L)
                .name("영업중인 포차")
                .isOpen(true)
                .build();

        // then
        assertTrue(store.getIsOpen());
    }
}
