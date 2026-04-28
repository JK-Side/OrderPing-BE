package com.orderping.api.user.service;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.orderping.domain.bank.repository.BankRepository;
import com.orderping.domain.menu.repository.MenuRepository;
import com.orderping.domain.order.Order;
import com.orderping.domain.order.repository.OrderMenuRepository;
import com.orderping.domain.order.repository.OrderRepository;
import com.orderping.domain.payment.repository.PaymentRepository;
import com.orderping.domain.store.Store;
import com.orderping.domain.store.repository.StoreAccountRepository;
import com.orderping.domain.store.repository.StoreRepository;
import com.orderping.domain.store.repository.StoreTableRepository;
import com.orderping.domain.user.repository.AuthAccountRepository;
import com.orderping.domain.user.repository.RefreshTokenRepository;
import com.orderping.domain.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthAccountRepository authAccountRepository;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private StoreRepository storeRepository;
    @Mock
    private StoreAccountRepository storeAccountRepository;
    @Mock
    private StoreTableRepository storeTableRepository;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderMenuRepository orderMenuRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private BankRepository bankRepository;

    @InjectMocks
    private UserService userService;

    @Nested
    @DisplayName("deleteUser - 주점이 있는 경우")
    class DeleteUserWithStore {

        @Test
        @DisplayName("주문메뉴 → 주문 → 메뉴 → 테이블 → 주점계좌 → 주점 → 인증정보 → 유저 순으로 삭제된다")
        void deleteUser_withStore_deletesInCorrectOrder() {
            // given
            Long userId = 1L;
            Long storeId = 10L;
            Long orderId1 = 100L;
            Long orderId2 = 101L;

            Store store = Store.builder().id(storeId).userId(userId).name("테스트 주점").isOpen(true).build();
            Order order1 = Order.builder().id(orderId1).storeId(storeId).build();
            Order order2 = Order.builder().id(orderId2).storeId(storeId).build();

            given(storeRepository.findByUserId(userId)).willReturn(List.of(store));
            given(orderRepository.findByStoreId(storeId)).willReturn(List.of(order1, order2));

            // when
            userService.deleteUser(userId);

            // then - 삭제 순서 검증
            InOrder inOrder = inOrder(
                paymentRepository, orderMenuRepository, orderRepository, menuRepository,
                storeTableRepository, storeAccountRepository,
                storeRepository, refreshTokenRepository, authAccountRepository, userRepository
            );

            inOrder.verify(paymentRepository).deleteByOrderIds(List.of(orderId1, orderId2));
            inOrder.verify(orderMenuRepository).deleteByOrderIds(List.of(orderId1, orderId2));
            inOrder.verify(orderRepository).deleteByStoreId(storeId);
            inOrder.verify(menuRepository).deleteByStoreId(storeId);
            inOrder.verify(storeTableRepository).deleteByStoreId(storeId);
            inOrder.verify(storeAccountRepository).deleteByStoreId(storeId);
            inOrder.verify(storeRepository).deleteByUserId(userId);
            inOrder.verify(refreshTokenRepository).deleteByUserId(userId);
            inOrder.verify(authAccountRepository).deleteByUserId(userId);
            inOrder.verify(userRepository).deleteById(userId);
        }

        @Test
        @DisplayName("주점에 주문이 없어도 정상 삭제된다")
        void deleteUser_withStoreButNoOrders_deletesSuccessfully() {
            // given
            Long userId = 1L;
            Long storeId = 10L;

            Store store = Store.builder().id(storeId).userId(userId).name("주문없는 주점").isOpen(false).build();

            given(storeRepository.findByUserId(userId)).willReturn(List.of(store));
            given(orderRepository.findByStoreId(storeId)).willReturn(List.of());

            // when
            userService.deleteUser(userId);

            // then
            verify(paymentRepository).deleteByOrderIds(List.of());
            verify(orderMenuRepository).deleteByOrderIds(List.of());
            verify(orderRepository).deleteByStoreId(storeId);
            verify(menuRepository).deleteByStoreId(storeId);
            verify(storeTableRepository).deleteByStoreId(storeId);
            verify(storeAccountRepository).deleteByStoreId(storeId);
            verify(storeRepository).deleteByUserId(userId);
            verify(userRepository).deleteById(userId);
        }

        @Test
        @DisplayName("주점이 여러 개인 경우 각 주점의 데이터가 모두 삭제된다")
        void deleteUser_withMultipleStores_deletesAllStores() {
            // given
            Long userId = 1L;
            Long storeId1 = 10L;
            Long storeId2 = 20L;

            Store store1 = Store.builder().id(storeId1).userId(userId).name("주점1").isOpen(true).build();
            Store store2 = Store.builder().id(storeId2).userId(userId).name("주점2").isOpen(true).build();

            given(storeRepository.findByUserId(userId)).willReturn(List.of(store1, store2));
            given(orderRepository.findByStoreId(storeId1)).willReturn(List.of());
            given(orderRepository.findByStoreId(storeId2)).willReturn(List.of());

            // when
            userService.deleteUser(userId);

            // then
            verify(orderRepository).deleteByStoreId(storeId1);
            verify(orderRepository).deleteByStoreId(storeId2);
            verify(menuRepository).deleteByStoreId(storeId1);
            verify(menuRepository).deleteByStoreId(storeId2);
            verify(storeTableRepository).deleteByStoreId(storeId1);
            verify(storeTableRepository).deleteByStoreId(storeId2);
            verify(storeAccountRepository).deleteByStoreId(storeId1);
            verify(storeAccountRepository).deleteByStoreId(storeId2);
            verify(storeRepository).deleteByUserId(userId);
            verify(userRepository).deleteById(userId);
        }
    }

    @Nested
    @DisplayName("deleteUser - 주점이 없는 경우")
    class DeleteUserWithoutStore {

        @Test
        @DisplayName("주점 없어도 유저 관련 데이터가 정상 삭제된다")
        void deleteUser_withoutStore_deletesUserRelatedData() {
            // given
            Long userId = 1L;

            given(storeRepository.findByUserId(userId)).willReturn(List.of());

            // when
            userService.deleteUser(userId);

            // then
            verify(paymentRepository, never()).deleteByOrderIds(anyList());
            verify(orderMenuRepository, never()).deleteByOrderIds(anyList());
            verify(orderRepository, never()).deleteByStoreId(anyLong());
            verify(menuRepository, never()).deleteByStoreId(anyLong());
            verify(storeTableRepository, never()).deleteByStoreId(anyLong());
            verify(storeAccountRepository, never()).deleteByStoreId(anyLong());

            verify(storeRepository).deleteByUserId(userId);
            verify(refreshTokenRepository).deleteByUserId(userId);
            verify(authAccountRepository).deleteByUserId(userId);
            verify(userRepository).deleteById(userId);
        }

        @Test
        @DisplayName("주점 없을 때 삭제 순서도 올바르다")
        void deleteUser_withoutStore_deletesInCorrectOrder() {
            // given
            Long userId = 1L;

            given(storeRepository.findByUserId(userId)).willReturn(List.of());

            // when
            userService.deleteUser(userId);

            // then
            InOrder inOrder = inOrder(
                storeRepository, refreshTokenRepository, authAccountRepository, userRepository
            );
            inOrder.verify(storeRepository).deleteByUserId(userId);
            inOrder.verify(refreshTokenRepository).deleteByUserId(userId);
            inOrder.verify(authAccountRepository).deleteByUserId(userId);
            inOrder.verify(userRepository).deleteById(userId);
        }
    }
}
