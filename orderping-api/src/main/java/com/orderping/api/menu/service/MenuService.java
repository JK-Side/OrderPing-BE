package com.orderping.api.menu.service;

import com.orderping.api.menu.dto.MenuCreateRequest;
import com.orderping.api.menu.dto.MenuResponse;
import com.orderping.api.menu.dto.MenuUpdateRequest;
import com.orderping.domain.menu.Menu;
import com.orderping.domain.menu.repository.MenuRepository;
import com.orderping.domain.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuService {

    private final MenuRepository menuRepository;

    @Transactional
    public MenuResponse createMenu(MenuCreateRequest request) {
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

    public List<MenuResponse> getAvailableMenusByStoreId(Long storeId) {
        return menuRepository.findAvailableByStoreId(storeId).stream()
                .map(MenuResponse::from)
                .toList();
    }

    @Transactional
    public void deleteMenu(Long id) {
        menuRepository.deleteById(id);
    }

    @Transactional
    public MenuResponse updateMenu(Long id, MenuUpdateRequest request) {
        Menu existing = menuRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("메뉴를 찾을 수 없습니다."));

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
}
