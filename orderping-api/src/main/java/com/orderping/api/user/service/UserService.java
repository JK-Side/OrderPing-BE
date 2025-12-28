package com.orderping.api.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderping.api.user.dto.UserCreateRequest;
import com.orderping.api.user.dto.UserResponse;
import com.orderping.domain.exception.NotFoundException;
import com.orderping.domain.user.User;
import com.orderping.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        User user = User.builder()
            .role(request.role())
            .nickname(request.nickname())
            .build();

        User saved = userRepository.save(user);
        return UserResponse.from(saved);
    }

    public UserResponse getUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));
        return UserResponse.from(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
