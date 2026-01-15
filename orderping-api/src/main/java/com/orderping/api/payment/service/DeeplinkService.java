package com.orderping.api.payment.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import com.orderping.api.payment.dto.DeeplinkResponse;
import com.orderping.api.payment.dto.DeeplinkResponse.AccountInfo;
import com.orderping.domain.bank.Bank;
import com.orderping.domain.bank.repository.BankRepository;
import com.orderping.domain.exception.NotFoundException;
import com.orderping.domain.store.StoreAccount;
import com.orderping.domain.store.repository.StoreAccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeeplinkService {

    private final StoreAccountRepository storeAccountRepository;
    private final BankRepository bankRepository;

    public DeeplinkResponse getDeeplink(Long storeId, Long amount) {
        // 활성 계좌 조회
        List<StoreAccount> accounts = storeAccountRepository.findActiveByStoreId(storeId);
        if (accounts.isEmpty()) {
            throw new NotFoundException("등록된 계좌가 없습니다.");
        }

        StoreAccount account = accounts.get(0);  // 첫 번째 활성 계좌 사용

        // JPA Converter가 이미 복호화했으므로 그대로 사용
        String decryptedAccountNumber = account.getAccountNumberEnc();

        // 은행 이름 조회
        String bankName = bankRepository.findAllActive().stream()
            .filter(bank -> bank.getCode().equals(account.getBankCode()))
            .findFirst()
            .map(Bank::getName)
            .orElse(account.getBankCode());

        // 토스 딥링크 생성
        String tossDeeplink = buildTossDeeplink(
            account.getBankCode(),
            decryptedAccountNumber,
            amount
        );

        // 계좌번호 포맷팅 (하이픈 추가)
        String formattedAccountNumber = formatAccountNumber(decryptedAccountNumber);

        AccountInfo accountInfo = new AccountInfo(
            account.getBankCode(),
            bankName,
            account.getAccountHolder(),
            formattedAccountNumber
        );

        return new DeeplinkResponse(amount, tossDeeplink, accountInfo);
    }

    private String buildTossDeeplink(String bankCode, String accountNumber, Long amount) {
        return UriComponentsBuilder.newInstance()
            .scheme("supertoss")
            .host("send")
            .queryParam("bank", bankCode)
            .queryParam("accountNo", accountNumber)
            .queryParam("amount", amount)
            .build()
            .toUriString();
    }

    private String formatAccountNumber(String accountNumber) {
        // 간단한 포맷팅: 숫자만 추출 후 반환
        // 실제로는 은행별로 다른 포맷이 필요할 수 있음
        return accountNumber;
    }
}
