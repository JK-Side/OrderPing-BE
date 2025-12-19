package com.orderping.infra.user.repository;

import com.orderping.domain.enums.AuthProvider;
import com.orderping.infra.user.entity.AuthAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthAccountJpaRepository extends JpaRepository<AuthAccountEntity, Long> {

    Optional<AuthAccountEntity> findByProviderAndSocialId(AuthProvider provider, String socialId);

    Optional<AuthAccountEntity> findByUserId(Long userId);
}
