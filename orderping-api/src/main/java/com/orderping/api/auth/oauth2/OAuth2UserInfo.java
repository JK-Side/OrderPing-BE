package com.orderping.api.auth.oauth2;

import java.util.Map;

public record OAuth2UserInfo(
    String socialId,
    String nickname,
    String email
) {
    public static OAuth2UserInfo fromKakao(Map<String, Object> attributes) {
        String socialId = String.valueOf(attributes.get("id"));

        @SuppressWarnings("unchecked")
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");

        @SuppressWarnings("unchecked")
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        String nickname = (String) profile.get("nickname");
        String email = kakaoAccount != null ? (String) kakaoAccount.get("email") : null;

        return new OAuth2UserInfo(socialId, nickname, email);
    }
}
