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

    @Column(nullable = false, length = 100, unique = true)
    private String name;

    @Builder
    public CategoryEntity(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    // Domain -> Entity
    public static CategoryEntity from(Category category) {
        return CategoryEntity.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    // Entity -> Domain
    public Category toDomain() {
        return Category.builder()
                .id(this.id)
                .name(this.name)
                .build();
    }
}
