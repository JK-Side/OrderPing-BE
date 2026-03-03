package com.orderping.api.store.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderping.api.store.dto.CustomerStoreAccountResponse;
import com.orderping.domain.bank.Bank;
import com.orderping.domain.bank.repository.BankRepository;
import com.orderping.domain.exception.NotFoundException;
import com.orderping.domain.store.StoreAccount;
import com.orderping.domain.store.repository.StoreAccountRepository;
import com.orderping.domain.store.repository.StoreRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerStoreAccountService {

    private final StoreRepository storeRepository;
    private final StoreAccountRepository storeAccountRepository;
    private final BankRepository bankRepository;
    private final Map<String, String> bankNameCache = new ConcurrentHashMap<>();

    @PostConstruct
    public void initBankCache() {
        Map<String, String> bankNameByCode = bankRepository.findAllActive().stream()
            .collect(Collectors.toMap(Bank::getCode, Bank::getName));
        bankNameCache.clear();
        bankNameCache.putAll(bankNameByCode);
    }

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
            .findFirst()
            .orElseThrow(() -> new NotFoundException("등록된 계좌 정보를 찾을 수 없습니다."));
    }

    private String resolveBankName(String bankCode) {
        if (bankNameCache.isEmpty()) {
            initBankCache();
        }
        return bankNameCache.getOrDefault(bankCode, bankCode);
    }
}
