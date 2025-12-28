package com.orderping.domain.user;

import java.time.LocalDateTime;

import com.orderping.domain.enums.AuthProvider;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthAccount {
    private final Long id;
    private final Long userId;
    private final AuthProvider provider;
    private final String socialId;
    private final String email;
    private final LocalDateTime createdAt;
}
