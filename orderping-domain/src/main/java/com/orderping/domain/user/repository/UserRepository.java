package com.orderping.domain.user.repository;

import java.util.Optional;

import com.orderping.domain.user.User;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(Long id);

    void deleteById(Long id);
}
