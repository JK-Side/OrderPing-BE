package com.orderping.api.store.service;

import com.orderping.api.store.dto.StoreAccountCreateRequest;
import com.orderping.api.store.dto.StoreAccountResponse;
import com.orderping.api.store.dto.StoreAccountUpdateRequest;
import com.orderping.domain.store.StoreAccount;
import com.orderping.domain.store.repository.StoreAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreAccountService {

    private final StoreAccountRepository storeAccountRepository;

    @Transactional
    public StoreAccountResponse createStoreAccount(StoreAccountCreateRequest request) {
        StoreAccount storeAccount = StoreAccount.builder()
                .storeId(request.storeId())
                .bankCode(request.bankCode())
                .accountHolder(request.accountHolder())
                .accountNumberEnc(encryptAccountNumber(request.accountNumber()))
                .accountNumberMask(maskAccountNumber(request.accountNumber()))
                .isActive(true)
                .build();

        StoreAccount saved = storeAccountRepository.save(storeAccount);
        return StoreAccountResponse.from(saved);
    }

    public StoreAccountResponse getStoreAccount(Long id) {
        StoreAccount storeAccount = storeAccountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("StoreAccount not found: " + id));
        return StoreAccountResponse.from(storeAccount);
    }

    public List<StoreAccountResponse> getStoreAccountsByStoreId(Long storeId) {
        return storeAccountRepository.findByStoreId(storeId).stream()
                .map(StoreAccountResponse::from)
                .toList();
    }

    @Transactional
    public StoreAccountResponse updateStoreAccount(Long id, StoreAccountUpdateRequest request) {
        StoreAccount existing = storeAccountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("StoreAccount not found: " + id));

        String accountNumberEnc = existing.getAccountNumberEnc();
        String accountNumberMask = existing.getAccountNumberMask();

        if (request.accountNumber() != null) {
            accountNumberEnc = encryptAccountNumber(request.accountNumber());
            accountNumberMask = maskAccountNumber(request.accountNumber());
        }

        StoreAccount updated = StoreAccount.builder()
                .id(existing.getId())
                .storeId(existing.getStoreId())
                .bankCode(request.bankCode() != null ? request.bankCode() : existing.getBankCode())
                .accountHolder(request.accountHolder() != null ? request.accountHolder() : existing.getAccountHolder())
                .accountNumberEnc(accountNumberEnc)
                .accountNumberMask(accountNumberMask)
                .isActive(existing.getIsActive())
                .build();

        StoreAccount saved = storeAccountRepository.save(updated);
        return StoreAccountResponse.from(saved);
    }

    @Transactional
    public void deleteStoreAccount(Long id) {
        storeAccountRepository.deleteById(id);
    }

    private String encryptAccountNumber(String accountNumber) {
        // TODO: 실제 암호화 로직 구현 필요
        return accountNumber;
    }

    private String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() < 4) {
            return "****";
        }
        int visibleLength = 4;
        String masked = "*".repeat(accountNumber.length() - visibleLength);
        return masked + accountNumber.substring(accountNumber.length() - visibleLength);
    }
}
