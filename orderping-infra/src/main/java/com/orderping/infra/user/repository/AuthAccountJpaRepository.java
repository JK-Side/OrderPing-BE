package com.orderping.infra.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderping.domain.enums.AuthProvider;
import com.orderping.infra.user.entity.AuthAccountEntity;

public interface AuthAccountJpaRepository extends JpaRepository<AuthAccountEntity, Long> {

    Optional<AuthAccountEntity> findByProviderAndSocialId(AuthProvider provider, String socialId);

    Optional<AuthAccountEntity> findByUserId(Long userId);
}
