package com.orderping.api.store.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.orderping.api.store.dto.CustomerStoreAccountResponse;
import com.orderping.domain.bank.Bank;
import com.orderping.domain.bank.repository.BankRepository;
import com.orderping.domain.exception.NotFoundException;
import com.orderping.domain.store.Store;
import com.orderping.domain.store.StoreAccount;
import com.orderping.domain.store.repository.StoreAccountRepository;
import com.orderping.domain.store.repository.StoreRepository;

@ExtendWith(MockitoExtension.class)
class CustomerStoreAccountServiceTest {

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private StoreAccountRepository storeAccountRepository;

    @Mock
    private BankRepository bankRepository;

    @InjectMocks
    private CustomerStoreAccountService customerStoreAccountService;

    private final Long storeId = 1L;
    private Store store;
    private StoreAccount storeAccount;

    @BeforeEach
    void setUp() {
        store = Store.builder()
            .id(storeId)
            .userId(10L)
            .name("테스트 주점")
            .isOpen(true)
            .build();

        storeAccount = StoreAccount.builder()
            .id(100L)
            .storeId(storeId)
            .bankCode("003")
            .accountHolder("홍길동")
            .accountNumberEnc("12345678901234")
            .accountNumberMask("************1234")
            .isActive(true)
            .build();
    }

    @Test
    @DisplayName("storeId로 계좌 조회 - 성공")
    void getAccountByStoreId_success() {
        // given
        Bank bank = Bank.builder()
            .code("003")
            .name("기업은행")
            .isActive(true)
            .build();

        given(storeRepository.findById(storeId)).willReturn(Optional.of(store));
        given(storeAccountRepository.findActiveByStoreId(storeId)).willReturn(List.of(storeAccount));
        given(bankRepository.findAllActive()).willReturn(List.of(bank));

        // when
        CustomerStoreAccountResponse response = customerStoreAccountService.getAccountByStoreId(storeId);

        // then
        assertThat(response.storeId()).isEqualTo(storeId);
        assertThat(response.bankCode()).isEqualTo("003");
        assertThat(response.bankName()).isEqualTo("기업은행");
        assertThat(response.accountHolder()).isEqualTo("홍길동");
        assertThat(response.accountNumber()).isEqualTo("12345678901234");
    }

    @Test
    @DisplayName("storeId로 계좌 조회 - 주점 없음")
    void getAccountByStoreId_storeNotFound() {
        // given
        given(storeRepository.findById(storeId)).willReturn(Optional.empty());

        // when & then
        assertThrows(NotFoundException.class, () -> customerStoreAccountService.getAccountByStoreId(storeId));
    }

    @Test
    @DisplayName("storeId로 계좌 조회 - 활성 계좌 없음")
    void getAccountByStoreId_accountNotFound() {
        // given
        given(storeRepository.findById(storeId)).willReturn(Optional.of(store));
        given(storeAccountRepository.findActiveByStoreId(storeId)).willReturn(Collections.emptyList());

        // when & then
        assertThrows(NotFoundException.class, () -> customerStoreAccountService.getAccountByStoreId(storeId));
    }

    @Test
    @DisplayName("storeId로 계좌 조회 - 다른 매장 계좌 반환 시 예외")
    void getAccountByStoreId_accountMismatch() {
        // given
        StoreAccount otherStoreAccount = StoreAccount.builder()
            .id(101L)
            .storeId(999L)
            .bankCode("004")
            .accountHolder("임꺽정")
            .accountNumberEnc("98765432109876")
            .accountNumberMask("************9876")
            .isActive(true)
            .build();

        given(storeRepository.findById(storeId)).willReturn(Optional.of(store));
        given(storeAccountRepository.findActiveByStoreId(storeId)).willReturn(List.of(otherStoreAccount));

        // when & then
        assertThrows(NotFoundException.class, () -> customerStoreAccountService.getAccountByStoreId(storeId));
    }
}
