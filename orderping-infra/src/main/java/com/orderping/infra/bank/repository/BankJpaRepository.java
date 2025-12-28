package com.orderping.infra.bank.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderping.infra.bank.entity.BankEntity;

public interface BankJpaRepository extends JpaRepository<BankEntity, String> {
    List<BankEntity> findByIsActiveTrue();
}
