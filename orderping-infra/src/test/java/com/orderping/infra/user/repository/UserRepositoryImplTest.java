package com.orderping.infra.user.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.orderping.domain.enums.Role;
import com.orderping.domain.user.User;
import com.orderping.infra.config.TestConfig;

@SpringBootTest(classes = TestConfig.class)
@Transactional
class UserRepositoryImplTest {

    @Autowired
    private UserRepositoryImpl userRepository;

    @Test
    @DisplayName("User 저장 및 조회 테스트")
    void saveAndFindUser() {
        // given
        User user = User.builder()
            .role(Role.OWNER)
            .nickname("테스트사장님")
            .build();

        // when
        User savedUser = userRepository.save(user);

        // then
        assertNotNull(savedUser.getId());
        assertEquals(Role.OWNER, savedUser.getRole());
        assertEquals("테스트사장님", savedUser.getNickname());
        assertNotNull(savedUser.getCreatedAt());
    }

    @Test
    @DisplayName("User ID로 조회 테스트")
    void findById() {
        // given
        User user = User.builder()
            .role(Role.OWNER)
            .nickname("조회테스트")
            .build();
        User savedUser = userRepository.save(user);

        // when
        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        // then
        assertTrue(foundUser.isPresent());
        assertEquals(savedUser.getId(), foundUser.get().getId());
        assertEquals("조회테스트", foundUser.get().getNickname());
    }

    @Test
    @DisplayName("존재하지 않는 User 조회 시 빈 Optional 반환")
    void findByIdNotFound() {
        // when
        Optional<User> foundUser = userRepository.findById(999L);

        // then
        assertTrue(foundUser.isEmpty());
    }

    @Test
    @DisplayName("User 삭제 테스트")
    void deleteUser() {
        // given
        User user = User.builder()
            .role(Role.OWNER)
            .nickname("삭제테스트")
            .build();
        User savedUser = userRepository.save(user);

        // when
        userRepository.deleteById(savedUser.getId());

        // then
        Optional<User> foundUser = userRepository.findById(savedUser.getId());
        assertTrue(foundUser.isEmpty());
    }
}
