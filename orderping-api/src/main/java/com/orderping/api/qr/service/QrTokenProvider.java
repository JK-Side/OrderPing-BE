package com.orderping.api.qr.service;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class QrTokenProvider {

    private final SecretKey secretKey;
    private final String baseUrl;

    public QrTokenProvider(
        @Value("${qr.secret-key}") String secret,
        @Value("${qr.base-url}") String baseUrl
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.baseUrl = baseUrl;
    }

    public String createTableToken(Long storeId, Long tableId, Integer tableNum) {
        return Jwts.builder()
            .claim("storeId", storeId)
            .claim("tableId", tableId)
            .claim("tableNum", tableNum)
            .claim("type", "table")
            .signWith(secretKey)
            .compact();
    }

    public TableQrClaims parseTableToken(String token) {
        Claims claims = parseClaims(token);

        String type = claims.get("type", String.class);
        if (!"table".equals(type)) {
            throw new JwtException("Invalid token type");
        }

        return new TableQrClaims(
            claims.get("storeId", Long.class),
            claims.get("tableId", Long.class),
            claims.get("tableNum", Integer.class)
        );
    }

    public String buildTableQrUrl(String token) {
        return baseUrl + "/order/" + token;
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    public record TableQrClaims(Long storeId, Long tableId, Integer tableNum) {}
}
