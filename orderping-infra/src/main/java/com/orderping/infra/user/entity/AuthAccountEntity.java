package com.orderping.infra.user.entity;

import java.time.LocalDateTime;

import com.orderping.domain.enums.AuthProvider;
import com.orderping.domain.user.AuthAccount;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "auth_accounts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthAccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AuthProvider provider;

    @Column(name = "social_id", nullable = false, length = 100)
    private String socialId;

    @Column(length = 100)
    private String email;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public AuthAccountEntity(Long id, Long userId, AuthProvider provider, String socialId, String email,
        LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.provider = provider;
        this.socialId = socialId;
        this.email = email;
        this.createdAt = createdAt;
    }

    // Domain -> Entity
    public static AuthAccountEntity from(AuthAccount authAccount) {
        return AuthAccountEntity.builder()
            .id(authAccount.getId())
            .userId(authAccount.getUserId())
            .provider(authAccount.getProvider())
            .socialId(authAccount.getSocialId())
            .email(authAccount.getEmail())
            .createdAt(authAccount.getCreatedAt())
            .build();
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Entity -> Domain
    public AuthAccount toDomain() {
        return AuthAccount.builder()
            .id(this.id)
            .userId(this.userId)
            .provider(this.provider)
            .socialId(this.socialId)
            .email(this.email)
            .createdAt(this.createdAt)
            .build();
    }
}
