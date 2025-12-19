package com.orderping.domain.menu;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MenuTest {

    @Test
    @DisplayName("Menu 객체 생성 테스트")
    void createMenu() {
        // when
        Menu menu = Menu.builder()
                .id(1L)
                .storeId(1L)
                .categoryId(1L)
                .name("소주")
                .price(5000L)
                .description("참이슬 후레쉬")
                .imageUrl("https://example.com/soju.jpg")
                .stock(100L)
                .isSoldOut(false)
                .build();

        // then
        assertNotNull(menu);
        assertEquals(1L, menu.getId());
        assertEquals(1L, menu.getStoreId());
        assertEquals(1L, menu.getCategoryId());
        assertEquals("소주", menu.getName());
        assertEquals(5000L, menu.getPrice());
        assertEquals("참이슬 후레쉬", menu.getDescription());
        assertEquals(100L, menu.getStock());
        assertFalse(menu.getIsSoldOut());
    }

    @Test
    @DisplayName("품절 메뉴 테스트")
    void soldOutMenuTest() {
        // when
        Menu soldOutMenu = Menu.builder()
                .id(1L)
                .storeId(1L)
                .categoryId(1L)
                .name("인기안주")
                .price(15000L)
                .stock(0L)
                .isSoldOut(true)
                .build();

        // then
        assertTrue(soldOutMenu.getIsSoldOut());
        assertEquals(0L, soldOutMenu.getStock());
    }
}
