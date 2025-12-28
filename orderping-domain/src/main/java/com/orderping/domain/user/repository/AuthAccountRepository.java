package com.orderping.domain.user.repository;

import java.util.Optional;

import com.orderping.domain.enums.AuthProvider;
import com.orderping.domain.user.AuthAccount;

public interface AuthAccountRepository {

    AuthAccount save(AuthAccount authAccount);

    Optional<AuthAccount> findById(Long id);

    Optional<AuthAccount> findByProviderAndSocialId(AuthProvider provider, String socialId);

    Optional<AuthAccount> findByUserId(Long userId);

    void deleteById(Long id);
}
