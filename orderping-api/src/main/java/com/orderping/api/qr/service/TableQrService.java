package com.orderping.api.qr.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderping.api.qr.dto.TableQrInfoResponse;
import com.orderping.api.qr.dto.TableQrInfoResponse.AccountInfo;
import com.orderping.api.qr.service.QrTokenProvider.TableQrClaims;
import com.orderping.api.store.dto.StoreDetailResponse.CategoryWithMenusResponse;
import com.orderping.domain.bank.Bank;
import com.orderping.domain.bank.repository.BankRepository;
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

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TableQrService {

    private final QrTokenProvider qrTokenProvider;
    private final StoreTableRepository storeTableRepository;
    private final StoreRepository storeRepository;
    private final StoreAccountRepository storeAccountRepository;
    private final CategoryRepository categoryRepository;
    private final MenuRepository menuRepository;
    private final BankRepository bankRepository;

    public TableQrInfoResponse getTableInfoByToken(String token) {
        // 토큰 검증 및 파싱
        TableQrClaims claims;
        try {
            claims = qrTokenProvider.parseTableToken(token);
        } catch (JwtException e) {
            throw new NotFoundException("유효하지 않은 QR 코드입니다: " + e.getMessage());
        }

        // 활성 테이블 조회 (storeId + tableNum으로 CLOSED가 아닌 테이블 찾기)
        StoreTable table = storeTableRepository.findActiveByStoreIdAndTableNum(claims.storeId(), claims.tableNum())
            .orElseThrow(() -> new NotFoundException("테이블을 찾을 수 없습니다."));

        // 주점 정보 조회
        Store store = storeRepository.findById(claims.storeId())
            .orElseThrow(() -> new NotFoundException("주점을 찾을 수 없습니다."));

        // 카테고리 및 메뉴 조회
        List<Category> categories = categoryRepository.findAll();
        List<Menu> menus = menuRepository.findByStoreId(store.getId());

        Map<Long, List<Menu>> menusByCategory = menus.stream()
            .collect(Collectors.groupingBy(Menu::getCategoryId));

        List<CategoryWithMenusResponse> categoryResponses = categories.stream()
            .map(category -> CategoryWithMenusResponse.from(
                category,
                menusByCategory.getOrDefault(category.getId(), List.of()),
                false  // isManage = false (고객용)
            ))
            .toList();

        // 계좌 정보 조회
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

        StoreAccount account = accounts.get(0);  // 첫 번째 활성 계좌 사용

        // 은행 이름 조회
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
