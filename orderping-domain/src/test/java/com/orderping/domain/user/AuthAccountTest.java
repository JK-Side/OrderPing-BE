package com.orderping.domain.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.orderping.domain.enums.AuthProvider;

class AuthAccountTest {

    @Test
    @DisplayName("AuthAccount 객체 생성 테스트 - Google")
    void createAuthAccountWithGoogle() {
        // given
        LocalDateTime now = LocalDateTime.now();

        // when
        AuthAccount authAccount = AuthAccount.builder()
            .id(1L)
            .userId(1L)
            .provider(AuthProvider.GOOGLE)
            .socialId("google_12345")
            .email("test@gmail.com")
            .createdAt(now)
            .build();

        // then
        assertNotNull(authAccount);
        assertEquals(1L, authAccount.getId());
        assertEquals(1L, authAccount.getUserId());
        assertEquals(AuthProvider.GOOGLE, authAccount.getProvider());
        assertEquals("google_12345", authAccount.getSocialId());
        assertEquals("test@gmail.com", authAccount.getEmail());
    }

    @Test
    @DisplayName("AuthAccount 객체 생성 테스트 - Kakao")
    void createAuthAccountWithKakao() {
        // when
        AuthAccount authAccount = AuthAccount.builder()
            .id(2L)
            .userId(1L)
            .provider(AuthProvider.KAKAO)
            .socialId("kakao_67890")
            .email("test@kakao.com")
            .build();

        // then
        assertEquals(AuthProvider.KAKAO, authAccount.getProvider());
        assertEquals("kakao_67890", authAccount.getSocialId());
    }
}
