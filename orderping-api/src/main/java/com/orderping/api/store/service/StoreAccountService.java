package com.orderping.api.store.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderping.api.store.dto.StoreAccountCreateRequest;
import com.orderping.api.store.dto.StoreAccountResponse;
import com.orderping.api.store.dto.StoreAccountUpdateRequest;
import com.orderping.domain.exception.ForbiddenException;
import com.orderping.domain.exception.NotFoundException;
import com.orderping.domain.store.Store;
import com.orderping.domain.store.StoreAccount;
import com.orderping.domain.store.repository.StoreAccountRepository;
import com.orderping.domain.store.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreAccountService {

    private final StoreAccountRepository storeAccountRepository;
    private final StoreRepository storeRepository;

    @Transactional
    public StoreAccountResponse createStoreAccount(Long userId, StoreAccountCreateRequest request) {
        validateStoreOwner(request.storeId(), userId);
        StoreAccount storeAccount = StoreAccount.builder()
            .storeId(request.storeId())
            .bankCode(request.bankCode())
            .accountHolder(request.accountHolder())
            .accountNumberEnc(request.accountNumber()) // JPA Converter가 자동 암호화
            .accountNumberMask(maskAccountNumber(request.accountNumber()))
            .isActive(true)
            .build();

        StoreAccount saved = storeAccountRepository.save(storeAccount);
        return StoreAccountResponse.from(saved);
    }

    public StoreAccountResponse getStoreAccount(Long id) {
        StoreAccount storeAccount = storeAccountRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("계좌 정보를 찾을 수 없습니다."));
        return StoreAccountResponse.from(storeAccount);
    }

    public List<StoreAccountResponse> getStoreAccountsByStoreId(Long userId, Long storeId) {
        validateStoreOwner(storeId, userId);
        return storeAccountRepository.findByStoreId(storeId).stream()
            .map(StoreAccountResponse::from)
            .toList();
    }

    @Transactional
    public StoreAccountResponse updateStoreAccount(Long userId, Long id, StoreAccountUpdateRequest request) {
        StoreAccount existing = storeAccountRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("계좌 정보를 찾을 수 없습니다."));
        validateStoreOwner(existing.getStoreId(), userId);

        String accountNumber = existing.getAccountNumberEnc(); // 이미 복호화된 값
        String accountNumberMask = existing.getAccountNumberMask();

        if (request.accountNumber() != null) {
            accountNumber = request.accountNumber();
            accountNumberMask = maskAccountNumber(request.accountNumber());
        }

        StoreAccount updated = StoreAccount.builder()
            .id(existing.getId())
            .storeId(existing.getStoreId())
            .bankCode(request.bankCode() != null ? request.bankCode() : existing.getBankCode())
            .accountHolder(request.accountHolder() != null ? request.accountHolder() : existing.getAccountHolder())
            .accountNumberEnc(accountNumber) // JPA Converter가 자동 암호화
            .accountNumberMask(accountNumberMask)
            .isActive(existing.getIsActive())
            .build();

        StoreAccount saved = storeAccountRepository.save(updated);
        return StoreAccountResponse.from(saved);
    }

    @Transactional
    public void deleteStoreAccount(Long userId, Long id) {
        StoreAccount storeAccount = storeAccountRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("계좌 정보를 찾을 수 없습니다."));
        validateStoreOwner(storeAccount.getStoreId(), userId);
        storeAccountRepository.deleteById(id);
    }

    private void validateStoreOwner(Long storeId, Long userId) {
        Store store = storeRepository.findById(storeId)
            .orElseThrow(() -> new NotFoundException("매장을 찾을 수 없습니다."));
        if (!store.getUserId().equals(userId)) {
            throw new ForbiddenException("본인 매장의 계좌만 관리할 수 있습니다.");
        }
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
