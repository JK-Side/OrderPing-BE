package com.orderping.domain.bank.repository;

import com.orderping.domain.bank.Bank;

import java.util.List;

public interface BankRepository {
    List<Bank> findAllActive();
}
