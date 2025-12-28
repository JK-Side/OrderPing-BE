package com.orderping.infra.menu.entity;

import com.orderping.domain.menu.Menu;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "menus")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MenuEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id")
    private Long id;

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private Long price;

    @Column(length = 100)
    private String description;

    @Column(name = "image_url", length = 256)
    private String imageUrl;

    @Column(name = "initial_stock", nullable = false)
    private Long initialStock;

    @Column(nullable = false)
    private Long stock;

    @Column(name = "is_sold_out", nullable = false)
    private Boolean isSoldOut;

    @Version
    private Long version;

    @Builder
    public MenuEntity(Long id, Long storeId, Long categoryId, String name, Long price, String description,
        String imageUrl, Long initialStock, Long stock, Boolean isSoldOut) {
        this.id = id;
        this.storeId = storeId;
        this.categoryId = categoryId;
        this.name = name;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
        this.initialStock = initialStock;
        this.stock = stock;
        this.isSoldOut = isSoldOut;
    }

    // Domain -> Entity
    public static MenuEntity from(Menu menu) {
        return MenuEntity.builder()
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
            .build();
    }

    @PrePersist
    protected void onCreate() {
        if (this.initialStock == null) {
            this.initialStock = 0L;
        }
        if (this.stock == null) {
            this.stock = 0L;
        }
        if (this.isSoldOut == null) {
            this.isSoldOut = false;
        }
    }

    // Entity -> Domain
    public Menu toDomain() {
        return Menu.builder()
            .id(this.id)
            .storeId(this.storeId)
            .categoryId(this.categoryId)
            .name(this.name)
            .price(this.price)
            .description(this.description)
            .imageUrl(this.imageUrl)
            .initialStock(this.initialStock)
            .stock(this.stock)
            .isSoldOut(this.isSoldOut)
            .build();
    }
}
