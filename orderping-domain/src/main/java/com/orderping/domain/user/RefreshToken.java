package com.orderping.domain.user;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RefreshToken {
    private final Long id;
    private final Long userId;
    private final String token;
    private final LocalDateTime expiresAt;
    private final LocalDateTime createdAt;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}
