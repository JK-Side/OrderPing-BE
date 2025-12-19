package com.orderping.infra.user.entity;

import com.orderping.domain.enums.Role;
import com.orderping.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public UserEntity(Long id, Role role, String nickname, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.role = role;
        this.nickname = nickname;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Domain -> Entity
    public static UserEntity from(User user) {
        return UserEntity.builder()
                .id(user.getId())
                .role(user.getRole())
                .nickname(user.getNickname())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    // Entity -> Domain
    public User toDomain() {
        return User.builder()
                .id(this.id)
                .role(this.role)
                .nickname(this.nickname)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }
}
