package com.orderping.api.store.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderping.api.store.dto.CustomerStoreAccountResponse;
import com.orderping.domain.bank.Bank;
import com.orderping.domain.bank.repository.BankRepository;
import com.orderping.domain.exception.NotFoundException;
import com.orderping.domain.store.StoreAccount;
import com.orderping.domain.store.repository.StoreAccountRepository;
import com.orderping.domain.store.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerStoreAccountService {

    private final StoreRepository storeRepository;
    private final StoreAccountRepository storeAccountRepository;
    private final BankRepository bankRepository;

    public CustomerStoreAccountResponse getAccountByStoreId(Long storeId) {
        storeRepository.findById(storeId)
            .orElseThrow(() -> new NotFoundException("주점을 찾을 수 없습니다."));

        StoreAccount account = findActiveAccount(storeId);

        return new CustomerStoreAccountResponse(
            storeId,
            account.getBankCode(),
            resolveBankName(account.getBankCode()),
            account.getAccountHolder(),
            account.getAccountNumberEnc()
        );
    }

    private StoreAccount findActiveAccount(Long storeId) {
        return storeAccountRepository.findActiveByStoreId(storeId).stream()
            .filter(account -> storeId.equals(account.getStoreId()))
            .findFirst()
            .orElseThrow(() -> new NotFoundException("등록된 계좌 정보를 찾을 수 없습니다."));
    }

    private String resolveBankName(String bankCode) {
        return bankRepository.findAllActive().stream()
            .filter(bank -> bank.getCode().equals(bankCode))
            .findFirst()
            .map(Bank::getName)
            .orElse(bankCode);
    }
}
