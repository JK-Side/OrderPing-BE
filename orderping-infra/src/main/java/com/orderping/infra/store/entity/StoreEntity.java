package com.orderping.infra.store.entity;

import com.orderping.domain.store.Store;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "stores")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_open", nullable = false)
    private Boolean isOpen;

    @Column(name = "image_url", length = 256)
    private String imageUrl;

    @Builder
    public StoreEntity(Long id, Long userId, String name, String description, LocalDateTime createdAt, Boolean isOpen, String imageUrl) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.isOpen = isOpen;
        this.imageUrl = imageUrl;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.isOpen == null) {
            this.isOpen = false;
        }
    }

    // Domain -> Entity
    public static StoreEntity from(Store store) {
        return StoreEntity.builder()
                .id(store.getId())
                .userId(store.getUserId())
                .name(store.getName())
                .description(store.getDescription())
                .createdAt(store.getCreatedAt())
                .isOpen(store.getIsOpen())
                .imageUrl(store.getImageUrl())
                .build();
    }

    // Entity -> Domain
    public Store toDomain() {
        return Store.builder()
                .id(this.id)
                .userId(this.userId)
                .name(this.name)
                .description(this.description)
                .createdAt(this.createdAt)
                .isOpen(this.isOpen)
                .imageUrl(this.imageUrl)
                .build();
    }
}
