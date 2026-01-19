package com.orderping.api.auth.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.orderping.api.auth.dto.TokenResponse;
import com.orderping.api.auth.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Auth", description = "인증 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

    private final AuthService authService;

    @Operation(summary = "토큰 재발급", description = "쿠키의 Refresh Token으로 새로운 Access Token을 발급받습니다.")
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(
        @CookieValue(name = REFRESH_TOKEN_COOKIE_NAME) String refreshToken
    ) {
        TokenResponse response = authService.refreshTokens(refreshToken);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "로그아웃", description = "쿠키의 Refresh Token을 무효화하고 쿠키를 삭제합니다.")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
        @CookieValue(name = REFRESH_TOKEN_COOKIE_NAME) String refreshToken
    ) {
        authService.logout(refreshToken);

        // 쿠키 삭제
        ResponseCookie deleteCookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, "")
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(0)
            .sameSite("None")
            .build();

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
            .build();
    }
}
