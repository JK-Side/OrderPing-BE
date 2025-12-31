package com.orderping.infra.user.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.orderping.domain.user.RefreshToken;
import com.orderping.domain.user.repository.RefreshTokenRepository;
import com.orderping.infra.user.entity.RefreshTokenEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

    private final RefreshTokenJpaRepository jpaRepository;

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        RefreshTokenEntity entity = RefreshTokenEntity.from(refreshToken);
        RefreshTokenEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return jpaRepository.findByToken(token)
            .map(RefreshTokenEntity::toDomain);
    }

    @Override
    public Optional<RefreshToken> findByUserId(Long userId) {
        return jpaRepository.findByUserId(userId)
            .map(RefreshTokenEntity::toDomain);
    }

    @Override
    @Transactional
    public void deleteByUserId(Long userId) {
        jpaRepository.deleteByUserId(userId);
    }

    @Override
    @Transactional
    public void deleteByToken(String token) {
        jpaRepository.deleteByToken(token);
    }
}
