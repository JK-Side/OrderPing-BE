package com.orderping.domain.bank.repository;

import java.util.List;

import com.orderping.domain.bank.Bank;

public interface BankRepository {
    List<Bank> findAllActive();
}
