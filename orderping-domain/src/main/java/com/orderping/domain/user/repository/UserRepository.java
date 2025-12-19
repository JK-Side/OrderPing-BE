package com.orderping.domain.user.repository;

import com.orderping.domain.user.User;

import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(Long id);

    void deleteById(Long id);
}
