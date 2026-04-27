package com.orderping.infra.menu.entity;

import com.orderping.domain.menu.Category;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

    @Column(name = "is_table_fee", nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isTableFee;

    @Builder
    public CategoryEntity(Long id, String name, Boolean isTableFee) {
        this.id = id;
        this.name = name;
        this.isTableFee = isTableFee != null ? isTableFee : false;
    }

    // Domain -> Entity
    public static CategoryEntity from(Category category) {
        return CategoryEntity.builder()
            .id(category.getId())
            .name(category.getName())
            .isTableFee(category.getIsTableFee())
            .build();
    }

    // Entity -> Domain
    public Category toDomain() {
        return Category.builder()
            .id(this.id)
            .name(this.name)
            .isTableFee(this.isTableFee)
            .build();
    }
}
