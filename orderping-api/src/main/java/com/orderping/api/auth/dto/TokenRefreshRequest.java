package com.orderping.api.auth.dto;

public record TokenRefreshRequest(
    String refreshToken
) {
}
