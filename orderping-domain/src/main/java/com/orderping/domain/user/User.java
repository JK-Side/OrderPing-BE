package com.orderping.domain.user;

import com.orderping.domain.enums.Role;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class User {
    private final Long id;
    private final Role role;
    private final String nickname;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
