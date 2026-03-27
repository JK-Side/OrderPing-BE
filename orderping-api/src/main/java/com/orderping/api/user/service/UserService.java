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
import com.orderping.domain.exception.UserWithdrawException;
import com.orderping.domain.menu.repository.MenuRepository;
import com.orderping.domain.order.repository.OrderMenuRepository;
import com.orderping.domain.order.repository.OrderRepository;
import com.orderping.domain.payment.repository.PaymentRepository;
import com.orderping.domain.store.StoreAccount;
import com.orderping.domain.store.repository.StoreAccountRepository;
import com.orderping.domain.store.repository.StoreRepository;
import com.orderping.domain.store.repository.StoreTableRepository;
import com.orderping.domain.user.User;
import com.orderping.domain.user.repository.AuthAccountRepository;
import com.orderping.domain.user.repository.RefreshTokenRepository;
import com.orderping.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final AuthAccountRepository authAccountRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final StoreRepository storeRepository;
    private final StoreAccountRepository storeAccountRepository;
    private final StoreTableRepository storeTableRepository;
    private final MenuRepository menuRepository;
    private final OrderRepository orderRepository;
    private final OrderMenuRepository orderMenuRepository;
    private final PaymentRepository paymentRepository;
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
        // 결제 → 주문메뉴 → 주문 → 메뉴 → 테이블 → 주점계좌 → 주점 → 인증 정보 → 유저 순으로 삭제
        storeRepository.findByUserId(id).forEach(store -> {
            Long storeId = store.getId();
            try {
                List<Long> orderIds = orderRepository.findByStoreId(storeId).stream()
                    .map(order -> order.getId())
                    .toList();
                paymentRepository.deleteByOrderIds(orderIds);
                orderMenuRepository.deleteByOrderIds(orderIds);
                orderRepository.deleteByStoreId(storeId);
                menuRepository.deleteByStoreId(storeId);
                storeTableRepository.deleteByStoreId(storeId);
                storeAccountRepository.deleteByStoreId(storeId);
            } catch (Exception e) {
                throw new UserWithdrawException("storeId=" + storeId + " 관련 데이터 삭제 실패", e);
            }
        });
        try {
            storeRepository.deleteByUserId(id);
        } catch (Exception e) {
            throw new UserWithdrawException("주점 삭제 실패", e);
        }
        try {
            refreshTokenRepository.deleteByUserId(id);
            authAccountRepository.deleteByUserId(id);
        } catch (Exception e) {
            throw new UserWithdrawException("인증 정보 삭제 실패", e);
        }
        try {
            userRepository.deleteById(id);
        } catch (Exception e) {
            throw new UserWithdrawException("유저 삭제 실패", e);
        }
    }

    public MyPageResponse getMyPage(Long userId) {
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
                return new MyPageResponse.StoreInfo(store.getId(), store.getName(), store.getDescription(), store.getImageUrl(), accountInfo);
            })
            .toList();

        return new MyPageResponse(userId, storeInfos);
    }
}
