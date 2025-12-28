package com.orderping.infra.bank.repository;

import com.orderping.infra.bank.entity.BankEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BankJpaRepository extends JpaRepository<BankEntity, String> {
    List<BankEntity> findByIsActiveTrue();
}
