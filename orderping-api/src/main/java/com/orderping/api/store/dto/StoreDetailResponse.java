package com.orderping.api.store.dto;

import com.orderping.domain.menu.Category;
import com.orderping.domain.menu.Menu;
import com.orderping.domain.store.Store;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record StoreDetailResponse(
        Long id,
        String name,
        String description,
        String imageUrl,
        Boolean isOpen,
        List<CategoryWithMenusResponse> categories
) {
    public static StoreDetailResponse forManage(Store store, List<Category> categories, List<Menu> menus) {
        return buildResponse(store, categories, menus, true);
    }

    public static StoreDetailResponse forOrder(Store store, List<Category> categories, List<Menu> menus) {
        return buildResponse(store, categories, menus, false);
    }

    private static StoreDetailResponse buildResponse(Store store, List<Category> categories, List<Menu> menus, boolean isManage) {
        Map<Long, List<Menu>> menusByCategory = menus.stream()
                .collect(Collectors.groupingBy(Menu::getCategoryId));

        List<CategoryWithMenusResponse> categoryResponses = categories.stream()
                .map(category -> CategoryWithMenusResponse.from(
                        category,
                        menusByCategory.getOrDefault(category.getId(), List.of()),
                        isManage
                ))
                .toList();

        return new StoreDetailResponse(
                store.getId(),
                store.getName(),
                store.getDescription(),
                store.getImageUrl(),
                store.getIsOpen(),
                categoryResponses
        );
    }

    public record CategoryWithMenusResponse(
            Long id,
            String name,
            List<MenuSummaryResponse> menus
    ) {
        public static CategoryWithMenusResponse from(Category category, List<Menu> menus, boolean isManage) {
            return new CategoryWithMenusResponse(
                    category.getId(),
                    category.getName(),
                    menus.stream()
                            .map(menu -> MenuSummaryResponse.from(menu, isManage))
                            .toList()
            );
        }
    }

    public record MenuSummaryResponse(
            Long id,
            String name,
            Long price,
            String imageUrl,
            Long stock,
            Boolean isSoldOut
    ) {
        public static MenuSummaryResponse from(Menu menu, boolean isManage) {
            return new MenuSummaryResponse(
                    menu.getId(),
                    menu.getName(),
                    menu.getPrice(),
                    menu.getImageUrl(),
                    isManage ? menu.getStock() : null,  // 운영자만 재고 표시
                    menu.getIsSoldOut()                  // 둘 다 품절 여부 표시
            );
        }
    }
}
