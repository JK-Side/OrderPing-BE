package com.orderping.api.store.service;

import com.orderping.api.store.dto.StoreCreateRequest;
import com.orderping.api.store.dto.StoreResponse;
import com.orderping.api.store.dto.StoreUpdateRequest;
import com.orderping.domain.store.Store;
import com.orderping.domain.store.StoreAccount;
import com.orderping.domain.store.repository.StoreAccountRepository;
import com.orderping.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreService {

    private final StoreRepository storeRepository;
    private final StoreAccountRepository storeAccountRepository;

    @Transactional
    public StoreResponse createStore(StoreCreateRequest request) {
        Store store = Store.builder()
                .userId(request.userId())
                .name(request.name())
                .description(request.description())
                .imageUrl(request.imageUrl())
                .isOpen(false)
                .build();

        Store saved = storeRepository.save(store);

        if (request.accountNumber() != null && request.bankCode() != null) {
            StoreAccount storeAccount = StoreAccount.builder()
                    .storeId(saved.getId())
                    .bankCode(request.bankCode())
                    .accountHolder(request.accountHolder())
                    .accountNumberEnc(encryptAccountNumber(request.accountNumber()))
                    .accountNumberMask(maskAccountNumber(request.accountNumber()))
                    .isActive(true)
                    .build();
            storeAccountRepository.save(storeAccount);
        }

        return StoreResponse.from(saved);
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

    public StoreResponse getStore(Long id) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Store not found: " + id));
        return StoreResponse.from(store);
    }

    public List<StoreResponse> getStoresByUserId(Long userId) {
        return storeRepository.findByUserId(userId).stream()
                .map(StoreResponse::from)
                .toList();
    }

    @Transactional
    public void deleteStore(Long id) {
        storeRepository.deleteById(id);
    }

    @Transactional
    public StoreResponse updateStore(Long id, StoreUpdateRequest request) {
        Store existing = storeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Store not found: " + id));

        Store updated = Store.builder()
                .id(existing.getId())
                .userId(existing.getUserId())
                .name(request.name() != null ? request.name() : existing.getName())
                .description(request.description() != null ? request.description() : existing.getDescription())
                .imageUrl(request.imageUrl() != null ? request.imageUrl() : existing.getImageUrl())
                .createdAt(existing.getCreatedAt())
                .isOpen(existing.getIsOpen())
                .build();

        Store saved = storeRepository.save(updated);
        return StoreResponse.from(saved);
    }
}
