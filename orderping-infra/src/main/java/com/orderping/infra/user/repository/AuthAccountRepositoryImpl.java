package com.orderping.infra.user.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.orderping.domain.enums.AuthProvider;
import com.orderping.domain.user.AuthAccount;
import com.orderping.domain.user.repository.AuthAccountRepository;
import com.orderping.infra.user.entity.AuthAccountEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AuthAccountRepositoryImpl implements AuthAccountRepository {

    private final AuthAccountJpaRepository jpaRepository;

    @Override
    public AuthAccount save(AuthAccount authAccount) {
        AuthAccountEntity entity = AuthAccountEntity.from(authAccount);
        AuthAccountEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<AuthAccount> findById(Long id) {
        return jpaRepository.findById(id)
            .map(AuthAccountEntity::toDomain);
    }

    @Override
    public Optional<AuthAccount> findByProviderAndSocialId(AuthProvider provider, String socialId) {
        return jpaRepository.findByProviderAndSocialId(provider, socialId)
            .map(AuthAccountEntity::toDomain);
    }

    @Override
    public Optional<AuthAccount> findByUserId(Long userId) {
        return jpaRepository.findByUserId(userId)
            .map(AuthAccountEntity::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
