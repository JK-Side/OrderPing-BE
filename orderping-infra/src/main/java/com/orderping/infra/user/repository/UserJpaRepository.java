package com.orderping.infra.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderping.infra.user.entity.UserEntity;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
}
