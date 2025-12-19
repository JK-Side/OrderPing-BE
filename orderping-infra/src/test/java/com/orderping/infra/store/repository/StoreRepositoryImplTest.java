package com.orderping.infra.store.repository;

import com.orderping.domain.enums.Role;
import com.orderping.domain.store.Store;
import com.orderping.domain.user.User;
import com.orderping.infra.config.TestConfig;
import com.orderping.infra.user.repository.UserRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TestConfig.class)
@Transactional
class StoreRepositoryImplTest {

    @Autowired
    private StoreRepositoryImpl storeRepository;

    @Autowired
    private UserRepositoryImpl userRepository;

    private User savedUser;

    @BeforeEach
    void setUp() {
        savedUser = userRepository.save(User.builder()
                .role(Role.OWNER)
                .nickname("사장님")
                .build());
    }

    @Test
    @DisplayName("Store 저장 및 조회 테스트")
    void saveAndFindStore() {
        // given
        Store store = Store.builder()
                .userId(savedUser.getId())
                .name("맛있는 포차")
                .description("분위기 좋은 포장마차")
                .isOpen(false)
                .build();

        // when
        Store savedStore = storeRepository.save(store);

        // then
        assertNotNull(savedStore.getId());
        assertEquals("맛있는 포차", savedStore.getName());
        assertEquals("분위기 좋은 포장마차", savedStore.getDescription());
        assertFalse(savedStore.getIsOpen());
    }

    @Test
    @DisplayName("UserId로 Store 목록 조회")
    void findByUserId() {
        // given
        Store store1 = storeRepository.save(Store.builder()
                .userId(savedUser.getId())
                .name("1호점")
                .isOpen(true)
                .build());

        Store store2 = storeRepository.save(Store.builder()
                .userId(savedUser.getId())
                .name("2호점")
                .isOpen(false)
                .build());

        // when
        List<Store> stores = storeRepository.findByUserId(savedUser.getId());

        // then
        assertEquals(2, stores.size());
    }

    @Test
    @DisplayName("Store ID로 조회 테스트")
    void findById() {
        // given
        Store store = storeRepository.save(Store.builder()
                .userId(savedUser.getId())
                .name("테스트 가게")
                .isOpen(true)
                .build());

        // when
        Optional<Store> foundStore = storeRepository.findById(store.getId());

        // then
        assertTrue(foundStore.isPresent());
        assertEquals("테스트 가게", foundStore.get().getName());
    }
}
