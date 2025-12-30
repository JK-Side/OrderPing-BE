package com.orderping.api.auth.oauth2;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.orderping.api.auth.jwt.JwtTokenProvider;
import com.orderping.domain.user.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final String frontendUrl;

    public OAuth2AuthenticationSuccessHandler(
        JwtTokenProvider jwtTokenProvider,
        @Value("${oauth2.frontend-url:http://localhost:3000}") String frontendUrl
    ) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.frontendUrl = frontendUrl;
    }

    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) throws IOException {
        OAuth2UserPrincipal principal = (OAuth2UserPrincipal) authentication.getPrincipal();
        User user = principal.getUser();

        String token = jwtTokenProvider.createToken(user.getId(), user.getNickname());

        String targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/oauth/callback")
            .queryParam("token", token)
            .build()
            .toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
