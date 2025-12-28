package com.orderping.api.user.dto;

import java.time.LocalDateTime;

import com.orderping.domain.enums.Role;
import com.orderping.domain.user.User;

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
