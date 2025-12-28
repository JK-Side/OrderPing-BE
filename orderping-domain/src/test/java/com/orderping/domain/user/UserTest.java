package com.orderping.domain.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.orderping.domain.enums.Role;

class UserTest {

    @Test
    @DisplayName("User 객체 생성 테스트")
    void createUser() {
        // given
        LocalDateTime now = LocalDateTime.now();

        // when
        User user = User.builder()
            .id(1L)
            .role(Role.OWNER)
            .nickname("테스트사장님")
            .createdAt(now)
            .updatedAt(now)
            .build();

        // then
        assertNotNull(user);
        assertEquals(1L, user.getId());
        assertEquals(Role.OWNER, user.getRole());
        assertEquals("테스트사장님", user.getNickname());
        assertEquals(now, user.getCreatedAt());
        assertEquals(now, user.getUpdatedAt());
    }

    @Test
    @DisplayName("User는 OWNER 역할을 가질 수 있다")
    void userCanHaveOwnerRole() {
        // when
        User user = User.builder()
            .id(1L)
            .role(Role.OWNER)
            .nickname("주점사장")
            .build();

        // then
        assertEquals(Role.OWNER, user.getRole());
    }
}
