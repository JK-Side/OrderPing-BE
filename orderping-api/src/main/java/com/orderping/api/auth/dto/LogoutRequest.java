package com.orderping.api.auth.dto;

public record LogoutRequest(
    String refreshToken
) {
}
