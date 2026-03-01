package com.orderping.api.user.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderping.api.user.dto.MyPageResponse;
import com.orderping.api.user.dto.UserCreateRequest;
import com.orderping.api.user.dto.UserResponse;
import com.orderping.domain.bank.Bank;
import com.orderping.domain.bank.repository.BankRepository;
import com.orderping.domain.exception.NotFoundException;
import com.orderping.domain.store.StoreAccount;
import com.orderping.domain.store.repository.StoreAccountRepository;
import com.orderping.domain.store.repository.StoreRepository;
import com.orderping.domain.user.AuthAccount;
import com.orderping.domain.user.User;
import com.orderping.domain.user.repository.AuthAccountRepository;
import com.orderping.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final AuthAccountRepository authAccountRepository;
    private final StoreRepository storeRepository;
    private final StoreAccountRepository storeAccountRepository;
    private final BankRepository bankRepository;

    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        User user = User.builder()
            .role(request.role())
            .nickname(request.nickname())
            .build();

        User saved = userRepository.save(user);
        return UserResponse.from(saved);
    }

    public UserResponse getUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));
        return UserResponse.from(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public MyPageResponse getMyPage(Long userId) {
        AuthAccount authAccount = authAccountRepository.findByUserId(userId)
            .orElseThrow(() -> new NotFoundException("계정 정보를 찾을 수 없습니다."));

        Map<String, String> bankNameByCode = bankRepository.findAllActive().stream()
            .collect(Collectors.toMap(Bank::getCode, Bank::getName));

        List<MyPageResponse.StoreInfo> storeInfos = storeRepository.findByUserId(userId).stream()
            .map(store -> {
                List<StoreAccount> accounts = storeAccountRepository.findActiveByStoreId(store.getId());
                MyPageResponse.AccountInfo accountInfo = accounts.isEmpty()
                    ? MyPageResponse.AccountInfo.empty()
                    : new MyPageResponse.AccountInfo(
                        accounts.get(0).getBankCode(),
                        bankNameByCode.getOrDefault(accounts.get(0).getBankCode(), "알 수 없는 은행"),
                        accounts.get(0).getAccountHolder(),
                        accounts.get(0).getAccountNumberEnc()
                    );
                return new MyPageResponse.StoreInfo(store.getId(), store.getName(), store.getDescription(), accountInfo);
            })
            .toList();

        return new MyPageResponse(authAccount.getEmail(), storeInfos);
    }
}
