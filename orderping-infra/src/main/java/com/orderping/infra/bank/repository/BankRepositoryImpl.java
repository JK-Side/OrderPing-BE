package com.orderping.infra.bank.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.orderping.domain.bank.Bank;
import com.orderping.domain.bank.repository.BankRepository;
import com.orderping.infra.bank.entity.BankEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class BankRepositoryImpl implements BankRepository {

    private final BankJpaRepository jpaRepository;

    @Override
    public List<Bank> findAllActive() {
        return jpaRepository.findByIsActiveTrue().stream()
            .map(BankEntity::toDomain)
            .toList();
    }
}
