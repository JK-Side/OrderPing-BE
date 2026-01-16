package com.orderping.api.menu.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderping.api.menu.dto.MenuCreateRequest;
import com.orderping.api.menu.dto.MenuResponse;
import com.orderping.api.menu.dto.MenuUpdateRequest;
import com.orderping.domain.exception.ForbiddenException;
import com.orderping.domain.exception.NotFoundException;
import com.orderping.domain.menu.Menu;
import com.orderping.domain.menu.repository.MenuRepository;
import com.orderping.domain.store.Store;
import com.orderping.domain.store.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuService {

    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;

    @Transactional
    public MenuResponse createMenu(Long userId, MenuCreateRequest request) {
        validateStoreOwner(request.storeId(), userId);
        Long stockValue = request.stock() != null ? request.stock() : 0L;

        Menu menu = Menu.builder()
            .storeId(request.storeId())
            .categoryId(request.categoryId())
            .name(request.name())
            .price(request.price())
            .description(request.description())
            .imageUrl(request.imageUrl())
            .initialStock(stockValue)
            .stock(stockValue)
            .isSoldOut(false)
            .build();

        Menu saved = menuRepository.save(menu);
        return MenuResponse.from(saved);
    }

    public MenuResponse getMenu(Long id) {
        Menu menu = menuRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("메뉴를 찾을 수 없습니다."));
        return MenuResponse.from(menu);
    }

    public List<MenuResponse> getMenusByStoreId(Long storeId) {
        return menuRepository.findByStoreId(storeId).stream()
            .map(MenuResponse::from)
            .toList();
    }

    public List<MenuResponse> getMenusByCategoryId(Long categoryId) {
        return menuRepository.findByCategoryId(categoryId).stream()
            .map(MenuResponse::from)
            .toList();
    }

    public List<MenuResponse> getMenus(Long storeId, Long categoryId) {
        if (storeId != null && categoryId != null) {
            return menuRepository.findByStoreIdAndCategoryId(storeId, categoryId).stream()
                .map(MenuResponse::from)
                .toList();
        } else if (storeId != null) {
            return getMenusByStoreId(storeId);
        } else if (categoryId != null) {
            return getMenusByCategoryId(categoryId);
        }
        return List.of();
    }

    public List<MenuResponse> getAvailableMenusByStoreId(Long storeId) {
        return menuRepository.findAvailableByStoreId(storeId).stream()
            .map(MenuResponse::from)
            .toList();
    }

    @Transactional
    public void deleteMenu(Long userId, Long id) {
        Menu menu = menuRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("메뉴를 찾을 수 없습니다."));
        validateStoreOwner(menu.getStoreId(), userId);
        menuRepository.deleteById(id);
    }

    @Transactional
    public MenuResponse updateMenu(Long userId, Long id, MenuUpdateRequest request) {
        Menu existing = menuRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("메뉴를 찾을 수 없습니다."));
        validateStoreOwner(existing.getStoreId(), userId);

        Menu updated = Menu.builder()
            .id(existing.getId())
            .storeId(existing.getStoreId())
            .categoryId(request.categoryId() != null ? request.categoryId() : existing.getCategoryId())
            .name(request.name() != null ? request.name() : existing.getName())
            .price(request.price() != null ? request.price() : existing.getPrice())
            .description(request.description() != null ? request.description() : existing.getDescription())
            .imageUrl(request.imageUrl() != null ? request.imageUrl() : existing.getImageUrl())
            .initialStock(request.initialStock() != null ? request.initialStock() : existing.getInitialStock())
            .stock(request.stock() != null ? request.stock() : existing.getStock())
            .isSoldOut(request.isSoldOut() != null ? request.isSoldOut() : existing.getIsSoldOut())
            .build();

        Menu saved = menuRepository.save(updated);
        return MenuResponse.from(saved);
    }

    private void validateStoreOwner(Long storeId, Long userId) {
        Store store = storeRepository.findById(storeId)
            .orElseThrow(() -> new NotFoundException("매장을 찾을 수 없습니다."));
        if (!store.getUserId().equals(userId)) {
            throw new ForbiddenException("본인 매장의 메뉴만 관리할 수 있습니다.");
        }
    }
}
