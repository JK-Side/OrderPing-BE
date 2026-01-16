package com.orderping.api.auth.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderping.api.auth.dto.TokenResponse;
import com.orderping.api.auth.jwt.JwtTokenProvider;
import com.orderping.domain.exception.NotFoundException;
import com.orderping.domain.exception.UnauthorizedException;
import com.orderping.domain.user.RefreshToken;
import com.orderping.domain.user.User;
import com.orderping.domain.user.repository.RefreshTokenRepository;
import com.orderping.domain.user.repository.UserRepository;

import io.jsonwebtoken.ExpiredJwtException;
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
        refreshTokenRepository.deleteByUserId(userId);

        String accessToken = jwtTokenProvider.createAccessToken(userId, nickname);
        String refreshTokenValue = jwtTokenProvider.createRefreshToken(userId);

        RefreshToken refreshToken = RefreshToken.builder()
            .userId(userId)
            .token(refreshTokenValue)
            .expiresAt(jwtTokenProvider.getRefreshTokenExpiryDate())
            .build();

        refreshTokenRepository.save(refreshToken);

        return new TokenResponse(accessToken, refreshTokenValue);
    }

    @Transactional
    public TokenResponse refreshTokens(String refreshTokenValue) {
        if (!jwtTokenProvider.validateRefreshToken(refreshTokenValue)) {
            throw new UnauthorizedException("만료되었거나 유효하지 않은 Refresh Token입니다.");
        }

        refreshTokenRepository.findByToken(refreshTokenValue)
            .orElseThrow(() -> new UnauthorizedException("로그아웃된 Refresh Token입니다."));

        Long userId;
        try {
            userId = jwtTokenProvider.getUserIdFromRefreshToken(refreshTokenValue);
        } catch (ExpiredJwtException e) {
            refreshTokenRepository.deleteByToken(refreshTokenValue);
            throw new UnauthorizedException("만료된 Refresh Token입니다. 다시 로그인해주세요.");
        }

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

        String newAccessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getNickname());

        return new TokenResponse(newAccessToken, refreshTokenValue);
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
