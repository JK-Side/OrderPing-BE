package com.orderping.api.auth.oauth2;

import java.util.Optional;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderping.domain.enums.AuthProvider;
import com.orderping.domain.enums.Role;
import com.orderping.domain.user.AuthAccount;
import com.orderping.domain.user.User;
import com.orderping.domain.user.repository.AuthAccountRepository;
import com.orderping.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final AuthAccountRepository authAccountRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        AuthProvider provider = AuthProvider.valueOf(registrationId.toUpperCase());

        OAuth2UserInfo userInfo = extractUserInfo(registrationId, oAuth2User);

        User user = findOrCreateUser(provider, userInfo);

        return new OAuth2UserPrincipal(user, oAuth2User.getAttributes());
    }

    private OAuth2UserInfo extractUserInfo(String registrationId, OAuth2User oAuth2User) {
        if ("kakao".equals(registrationId)) {
            return OAuth2UserInfo.fromKakao(oAuth2User.getAttributes());
        }
        throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인입니다: " + registrationId);
    }

    private User findOrCreateUser(AuthProvider provider, OAuth2UserInfo userInfo) {
        Optional<AuthAccount> existingAccount = authAccountRepository
            .findByProviderAndSocialId(provider, userInfo.socialId());

        if (existingAccount.isPresent()) {
            return userRepository.findById(existingAccount.get().getUserId())
                .orElseThrow(() -> new OAuth2AuthenticationException("사용자를 찾을 수 없습니다."));
        }

        User newUser = User.builder()
            .role(Role.OWNER)
            .nickname(userInfo.nickname())
            .build();

        User savedUser = userRepository.save(newUser);

        AuthAccount authAccount = AuthAccount.builder()
            .userId(savedUser.getId())
            .provider(provider)
            .socialId(userInfo.socialId())
            .email(userInfo.email())
            .build();

        authAccountRepository.save(authAccount);

        return savedUser;
    }
}
