package com.orderping.api.auth.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderping.api.auth.dto.TokenRefreshRequest;
import com.orderping.api.auth.dto.TokenResponse;
import com.orderping.api.auth.jwt.JwtTokenProvider;
import com.orderping.domain.exception.NotFoundException;
import com.orderping.domain.exception.UnauthorizedException;
import com.orderping.domain.user.RefreshToken;
import com.orderping.domain.user.User;
import com.orderping.domain.user.repository.RefreshTokenRepository;
import com.orderping.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public TokenResponse createTokens(Long userId, String nickname) {
        // 기존 refresh token 삭제
        refreshTokenRepository.deleteByUserId(userId);

        // 새 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(userId, nickname);
        String refreshTokenValue = jwtTokenProvider.createRefreshToken();

        // Refresh Token 저장
        RefreshToken refreshToken = RefreshToken.builder()
            .userId(userId)
            .token(refreshTokenValue)
            .expiresAt(jwtTokenProvider.getRefreshTokenExpiryDate())
            .build();

        refreshTokenRepository.save(refreshToken);

        return new TokenResponse(accessToken, refreshTokenValue);
    }

    @Transactional
    public TokenResponse refreshTokens(TokenRefreshRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.refreshToken())
            .orElseThrow(() -> new UnauthorizedException("유효하지 않은 Refresh Token입니다."));

        if (refreshToken.isExpired()) {
            refreshTokenRepository.deleteByToken(request.refreshToken());
            throw new UnauthorizedException("만료된 Refresh Token입니다. 다시 로그인해주세요.");
        }

        User user = userRepository.findById(refreshToken.getUserId())
            .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

        // 새 Access Token만 발급 (Refresh Token은 유지)
        String newAccessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getNickname());

        return new TokenResponse(newAccessToken, request.refreshToken());
    }

    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepository.deleteByToken(refreshToken);
    }

    @Transactional
    public void logoutByUserId(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }
}
