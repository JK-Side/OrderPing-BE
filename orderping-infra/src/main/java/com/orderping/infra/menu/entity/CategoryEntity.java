package com.orderping.infra.menu.entity;

import com.orderping.domain.menu.Category;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "categories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @Column(nullable = false, length = 100)
    private String name;

    @Builder
    public CategoryEntity(Long id, Long storeId, String name) {
        this.id = id;
        this.storeId = storeId;
        this.name = name;
    }

    // Domain -> Entity
    public static CategoryEntity from(Category category) {
        return CategoryEntity.builder()
                .id(category.getId())
                .storeId(category.getStoreId())
                .name(category.getName())
                .build();
    }

    // Entity -> Domain
    public Category toDomain() {
        return Category.builder()
                .id(this.id)
                .storeId(this.storeId)
                .name(this.name)
                .build();
    }
}
