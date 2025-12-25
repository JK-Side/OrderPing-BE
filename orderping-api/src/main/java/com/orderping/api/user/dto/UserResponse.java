package com.orderping.api.user.dto;

import com.orderping.domain.enums.Role;
import com.orderping.domain.user.User;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        Role role,
        String nickname,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getRole(),
                user.getNickname(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
