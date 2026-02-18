package com.orderping.api.table.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderping.api.qr.dto.TableQrInfoResponse;
import com.orderping.api.qr.dto.TableQrInfoResponse.AccountInfo;
import com.orderping.api.store.dto.StoreDetailResponse.CategoryWithMenusResponse;
import com.orderping.domain.bank.Bank;
import com.orderping.domain.bank.repository.BankRepository;
import com.orderping.domain.enums.TableStatus;
import com.orderping.domain.exception.NotFoundException;
import com.orderping.domain.menu.Category;
import com.orderping.domain.menu.Menu;
import com.orderping.domain.menu.repository.CategoryRepository;
import com.orderping.domain.menu.repository.MenuRepository;
import com.orderping.domain.store.Store;
import com.orderping.domain.store.StoreAccount;
import com.orderping.domain.store.StoreTable;
import com.orderping.domain.store.repository.StoreAccountRepository;
import com.orderping.domain.store.repository.StoreRepository;
import com.orderping.domain.store.repository.StoreTableRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerTableService {

    private final StoreTableRepository storeTableRepository;
    private final StoreRepository storeRepository;
    private final StoreAccountRepository storeAccountRepository;
    private final CategoryRepository categoryRepository;
    private final MenuRepository menuRepository;
    private final BankRepository bankRepository;

    public TableQrInfoResponse getTableInfo(Long tableId) {
        StoreTable table = storeTableRepository.findById(tableId)
            .orElseThrow(() -> new NotFoundException("테이블을 찾을 수 없습니다."));

        if (table.getStatus() == TableStatus.CLOSED) {
            throw new NotFoundException("종료된 테이블입니다.");
        }

        Store store = storeRepository.findById(table.getStoreId())
            .orElseThrow(() -> new NotFoundException("주점을 찾을 수 없습니다."));

        return buildTableInfoResponse(store, table);
    }

    private TableQrInfoResponse buildTableInfoResponse(Store store, StoreTable table) {
        List<Category> categories = categoryRepository.findAll();
        List<Menu> menus = menuRepository.findByStoreId(store.getId());

        Map<Long, List<Menu>> menusByCategory = menus.stream()
            .collect(Collectors.groupingBy(Menu::getCategoryId));

        List<CategoryWithMenusResponse> categoryResponses = categories.stream()
            .map(category -> CategoryWithMenusResponse.from(
                category,
                menusByCategory.getOrDefault(category.getId(), List.of()),
                false
            ))
            .toList();

        AccountInfo accountInfo = getAccountInfo(store.getId());

        return new TableQrInfoResponse(
            store.getId(),
            table.getId(),
            table.getTableNum(),
            store.getName(),
            store.getDescription(),
            store.getImageUrl(),
            store.getIsOpen(),
            categoryResponses,
            accountInfo
        );
    }

    private AccountInfo getAccountInfo(Long storeId) {
        List<StoreAccount> accounts = storeAccountRepository.findActiveByStoreId(storeId);
        if (accounts.isEmpty()) {
            return null;
        }

        StoreAccount account = accounts.get(0);

        String bankName = bankRepository.findAllActive().stream()
            .filter(bank -> bank.getCode().equals(account.getBankCode()))
            .findFirst()
            .map(Bank::getName)
            .orElse(account.getBankCode());

        return new AccountInfo(
            account.getBankCode(),
            bankName,
            account.getAccountHolder(),
            account.getAccountNumberMask()
        );
    }
}

