package com.orderping.api.menu.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderping.api.menu.dto.MenuCreateRequest;
import com.orderping.api.menu.dto.MenuResponse;
import com.orderping.api.menu.dto.MenuUpdateRequest;
import com.orderping.domain.exception.BadRequestException;
import com.orderping.domain.exception.ConflictException;
import com.orderping.domain.exception.ForbiddenException;
import com.orderping.domain.exception.NotFoundException;
import com.orderping.domain.menu.Category;
import com.orderping.domain.menu.Menu;
import com.orderping.domain.menu.repository.CategoryRepository;
import com.orderping.domain.menu.repository.MenuRepository;
import com.orderping.domain.store.Store;
import com.orderping.domain.store.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuService {

    private final MenuRepository menuRepository;
    private final CategoryRepository categoryRepository;
    private final StoreRepository storeRepository;

    @Transactional
    public MenuResponse createMenu(Long userId, MenuCreateRequest request) {
        validateStoreOwner(request.storeId(), userId);

        Category category = categoryRepository.findById(request.categoryId())
            .orElseThrow(() -> new NotFoundException("카테고리를 찾을 수 없습니다."));

        boolean isTableFee = Boolean.TRUE.equals(category.getIsTableFee());

        if (isTableFee && !menuRepository.findByStoreIdAndCategoryId(request.storeId(), request.categoryId())
            .isEmpty()) {
            throw new ConflictException("테이블비 메뉴는 주점당 하나만 등록할 수 있습니다.");
        }

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
            .isTableFee(isTableFee)
            .build();

        Menu saved = menuRepository.save(menu);
        return MenuResponse.from(saved);
    }

    public MenuResponse getMenu(Long userId, Long id) {
        Menu menu = menuRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("메뉴를 찾을 수 없습니다."));
        validateStoreOwner(menu.getStoreId(), userId);
        return MenuResponse.from(menu);
    }

    public List<MenuResponse> getMenusByStoreId(Long userId, Long storeId) {
        validateStoreOwner(storeId, userId);
        return menuRepository.findByStoreId(storeId).stream()
            .map(MenuResponse::from)
            .toList();
    }

    public List<MenuResponse> getMenusByCategoryId(Long categoryId) {
        return menuRepository.findByCategoryId(categoryId).stream()
            .map(MenuResponse::from)
            .toList();
    }

    public List<MenuResponse> getMenus(Long userId, Long storeId, Long categoryId) {
        if (storeId == null && categoryId == null) {
            throw new BadRequestException("storeId 또는 categoryId 중 하나는 필수입니다.");
        }
        if (storeId != null && categoryId != null) {
            validateStoreOwner(storeId, userId);
            return menuRepository.findByStoreIdAndCategoryId(storeId, categoryId).stream()
                .map(MenuResponse::from)
                .toList();
        } else if (storeId != null) {
            return getMenusByStoreId(userId, storeId);
        } else {
            return getMenusByCategoryId(categoryId);
        }
    }

    public List<MenuResponse> getAvailableMenusByStoreId(Long userId, Long storeId) {
        validateStoreOwner(storeId, userId);
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

        Long newCategoryId = request.categoryId() != null ? request.categoryId() : existing.getCategoryId();
        Category category = categoryRepository.findById(newCategoryId)
            .orElseThrow(() -> new NotFoundException("카테고리를 찾을 수 없습니다."));

        boolean isTableFee = Boolean.TRUE.equals(category.getIsTableFee());
        boolean categoryChanged = !newCategoryId.equals(existing.getCategoryId());

        if (isTableFee && categoryChanged &&
            !menuRepository.findByStoreIdAndCategoryId(existing.getStoreId(), newCategoryId).isEmpty()) {
            throw new ConflictException("테이블비 메뉴는 주점당 하나만 등록할 수 있습니다.");
        }

        long newStock = request.stock() != null ? request.stock() : existing.getStock();
        long stockDiff = newStock - existing.getStock();
        long newInitialStock = existing.getInitialStock() + stockDiff;

        Menu updated = Menu.builder()
            .id(existing.getId())
            .storeId(existing.getStoreId())
            .categoryId(newCategoryId)
            .name(request.name() != null ? request.name() : existing.getName())
            .price(request.price() != null ? request.price() : existing.getPrice())
            .description(request.description() != null ? request.description() : existing.getDescription())
            .imageUrl(request.imageUrl() != null ? request.imageUrl() : existing.getImageUrl())
            .initialStock(newInitialStock)
            .stock(newStock)
            .isSoldOut(request.isSoldOut() != null ? request.isSoldOut() : existing.getIsSoldOut())
            .isTableFee(isTableFee)
            .version(existing.getVersion())
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
