package com.orderping.domain.user.repository;

import com.orderping.domain.enums.AuthProvider;
import com.orderping.domain.user.AuthAccount;

import java.util.Optional;

public interface AuthAccountRepository {

    AuthAccount save(AuthAccount authAccount);

    Optional<AuthAccount> findById(Long id);

    Optional<AuthAccount> findByProviderAndSocialId(AuthProvider provider, String socialId);

    Optional<AuthAccount> findByUserId(Long userId);

    void deleteById(Long id);
}
