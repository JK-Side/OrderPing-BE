package com.orderping.api.menu.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.orderping.api.menu.dto.MenuResponse;
import com.orderping.api.menu.dto.MenuUpdateRequest;
import com.orderping.domain.menu.Menu;
import com.orderping.domain.menu.repository.MenuRepository;
import com.orderping.domain.store.Store;
import com.orderping.domain.store.repository.StoreRepository;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private MenuService menuService;

    private Store testStore;
    private Menu existingMenu;
    private Long userId = 1L;
    private Long storeId = 1L;
    private Long menuId = 1L;

    @BeforeEach
    void setUp() {
        testStore = Store.builder()
            .id(storeId)
            .userId(userId)
            .name("테스트 주점")
            .isOpen(true)
            .build();

        existingMenu = Menu.builder()
            .id(menuId)
            .storeId(storeId)
            .categoryId(1L)
            .name("기존 메뉴")
            .price(10000L)
            .description("기존 설명")
            .imageUrl("http://example.com/old.jpg")
            .initialStock(100L)
            .stock(50L)
            .isSoldOut(false)
            .version(1L)
            .build();
    }

    @Test
    @DisplayName("메뉴 수정 시 version이 올바르게 전달된다")
    void updateMenu_VersionPreserved() {
        // given
        MenuUpdateRequest request = new MenuUpdateRequest(
            null, "수정된 메뉴", 15000L, null, null, null, null, null
        );

        given(menuRepository.findById(menuId)).willReturn(Optional.of(existingMenu));
        given(storeRepository.findById(storeId)).willReturn(Optional.of(testStore));
        given(menuRepository.save(any(Menu.class))).willAnswer(invocation -> {
            Menu menu = invocation.getArgument(0);
            return Menu.builder()
                .id(menu.getId())
                .storeId(menu.getStoreId())
                .categoryId(menu.getCategoryId())
                .name(menu.getName())
                .price(menu.getPrice())
                .description(menu.getDescription())
                .imageUrl(menu.getImageUrl())
                .initialStock(menu.getInitialStock())
                .stock(menu.getStock())
                .isSoldOut(menu.getIsSoldOut())
                .version(menu.getVersion() + 1)
                .build();
        });

        // when
        MenuResponse response = menuService.updateMenu(userId, menuId, request);

        // then
        ArgumentCaptor<Menu> menuCaptor = ArgumentCaptor.forClass(Menu.class);
        verify(menuRepository).save(menuCaptor.capture());

        Menu savedMenu = menuCaptor.getValue();
        assertEquals(1L, savedMenu.getVersion(), "기존 version이 그대로 전달되어야 함");
        assertEquals("수정된 메뉴", savedMenu.getName());
        assertEquals(15000L, savedMenu.getPrice());
    }

    @Test
    @DisplayName("메뉴 수정 시 version이 null이면 안 된다")
    void updateMenu_VersionNotNull() {
        // given
        MenuUpdateRequest request = new MenuUpdateRequest(
            null, "수정된 메뉴", null, null, null, null, null, null
        );

        given(menuRepository.findById(menuId)).willReturn(Optional.of(existingMenu));
        given(storeRepository.findById(storeId)).willReturn(Optional.of(testStore));
        given(menuRepository.save(any(Menu.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        menuService.updateMenu(userId, menuId, request);

        // then
        ArgumentCaptor<Menu> menuCaptor = ArgumentCaptor.forClass(Menu.class);
        verify(menuRepository).save(menuCaptor.capture());

        Menu savedMenu = menuCaptor.getValue();
        assertNotNull(savedMenu.getVersion(), "version은 null이 아니어야 함");
        assertEquals(existingMenu.getVersion(), savedMenu.getVersion());
    }

    @Test
    @DisplayName("메뉴 수정 시 변경되지 않은 필드는 기존 값 유지")
    void updateMenu_PreserveUnchangedFields() {
        // given
        MenuUpdateRequest request = new MenuUpdateRequest(
            null, "수정된 메뉴", null, null, null, null, null, null
        );

        given(menuRepository.findById(menuId)).willReturn(Optional.of(existingMenu));
        given(storeRepository.findById(storeId)).willReturn(Optional.of(testStore));
        given(menuRepository.save(any(Menu.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        menuService.updateMenu(userId, menuId, request);

        // then
        ArgumentCaptor<Menu> menuCaptor = ArgumentCaptor.forClass(Menu.class);
        verify(menuRepository).save(menuCaptor.capture());

        Menu savedMenu = menuCaptor.getValue();
        assertEquals("수정된 메뉴", savedMenu.getName());
        assertEquals(existingMenu.getPrice(), savedMenu.getPrice());
        assertEquals(existingMenu.getDescription(), savedMenu.getDescription());
        assertEquals(existingMenu.getCategoryId(), savedMenu.getCategoryId());
        assertEquals(existingMenu.getVersion(), savedMenu.getVersion());
    }

    @Test
    @DisplayName("version 0인 새 메뉴도 정상 처리")
    void updateMenu_VersionZero() {
        // given
        Menu menuWithVersionZero = Menu.builder()
            .id(menuId)
            .storeId(storeId)
            .categoryId(1L)
            .name("새 메뉴")
            .price(10000L)
            .initialStock(100L)
            .stock(100L)
            .isSoldOut(false)
            .version(0L)
            .build();

        MenuUpdateRequest request = new MenuUpdateRequest(
            null, "수정된 메뉴", null, null, null, null, null, null
        );

        given(menuRepository.findById(menuId)).willReturn(Optional.of(menuWithVersionZero));
        given(storeRepository.findById(storeId)).willReturn(Optional.of(testStore));
        given(menuRepository.save(any(Menu.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        menuService.updateMenu(userId, menuId, request);

        // then
        ArgumentCaptor<Menu> menuCaptor = ArgumentCaptor.forClass(Menu.class);
        verify(menuRepository).save(menuCaptor.capture());

        Menu savedMenu = menuCaptor.getValue();
        assertEquals(0L, savedMenu.getVersion());
    }
}
