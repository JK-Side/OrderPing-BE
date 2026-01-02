package com.orderping.api.auth.oauth2;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class OAuth2AuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final String frontendUrl;

    public OAuth2AuthenticationFailureHandler(
        @Value("${oauth2.frontend-url:http://localhost:5173}") String frontendUrl
    ) {
        this.frontendUrl = frontendUrl;
    }

    @Override
    public void onAuthenticationFailure(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException exception
    ) throws IOException {
        log.error("OAuth2 authentication failed: {}", exception.getMessage(), exception);

        String targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/callback")
            .queryParam("error", "oauth2_failed")
            .queryParam("message", exception.getMessage())
            .build()
            .toUriString();

        response.sendRedirect(targetUrl);
    }
}
