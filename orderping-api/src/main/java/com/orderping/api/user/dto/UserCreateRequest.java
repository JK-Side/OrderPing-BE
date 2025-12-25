package com.orderping.api.user.dto;

import com.orderping.domain.enums.Role;

public record UserCreateRequest(
        Role role,
        String nickname
) {
}
