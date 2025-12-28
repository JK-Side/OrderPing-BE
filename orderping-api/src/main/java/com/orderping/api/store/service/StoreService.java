package com.orderping.api.store.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderping.api.store.dto.StoreCreateRequest;
import com.orderping.api.store.dto.StoreDetailResponse;
import com.orderping.api.store.dto.StoreResponse;
import com.orderping.api.store.dto.StoreUpdateRequest;
import com.orderping.domain.exception.ForbiddenException;
import com.orderping.domain.exception.NotFoundException;
import com.orderping.domain.menu.Category;
import com.orderping.domain.menu.Menu;
import com.orderping.domain.menu.repository.CategoryRepository;
import com.orderping.domain.menu.repository.MenuRepository;
import com.orderping.domain.store.Store;
import com.orderping.domain.store.StoreAccount;
import com.orderping.domain.store.repository.StoreAccountRepository;
import com.orderping.domain.store.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreService {

    private final StoreRepository storeRepository;
    private final StoreAccountRepository storeAccountRepository;
    private final CategoryRepository categoryRepository;
    private final MenuRepository menuRepository;

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
            .orElseThrow(() -> new NotFoundException("주점을 찾을 수 없습니다."));
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
            .orElseThrow(() -> new NotFoundException("주점을 찾을 수 없습니다."));

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

    public StoreDetailResponse getStoreForManage(Long storeId, Long userId) {
        Store store = storeRepository.findById(storeId)
            .orElseThrow(() -> new NotFoundException("주점을 찾을 수 없습니다."));

        if (!store.getUserId().equals(userId)) {
            throw new ForbiddenException("본인 가게만 조회할 수 있습니다.");
        }

        List<Category> categories = categoryRepository.findAll();
        List<Menu> menus = menuRepository.findByStoreId(storeId);

        return StoreDetailResponse.forManage(store, categories, menus);
    }

    public StoreDetailResponse getStoreForOrder(Long storeId) {
        Store store = storeRepository.findById(storeId)
            .orElseThrow(() -> new NotFoundException("주점을 찾을 수 없습니다."));

        List<Category> categories = categoryRepository.findAll();
        List<Menu> menus = menuRepository.findByStoreId(storeId);

        return StoreDetailResponse.forOrder(store, categories, menus);
    }
}
