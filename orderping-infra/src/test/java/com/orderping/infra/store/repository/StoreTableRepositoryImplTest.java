package com.orderping.infra.store.repository;

import com.orderping.domain.enums.Role;
import com.orderping.domain.enums.TableStatus;
import com.orderping.domain.store.Store;
import com.orderping.domain.store.StoreTable;
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
class StoreTableRepositoryImplTest {

    @Autowired
    private StoreTableRepositoryImpl storeTableRepository;

    @Autowired
    private StoreRepositoryImpl storeRepository;

    @Autowired
    private UserRepositoryImpl userRepository;

    private Store savedStore;

    @BeforeEach
    void setUp() {
        User savedUser = userRepository.save(User.builder()
                .role(Role.OWNER)
                .nickname("사장님")
                .build());

        savedStore = storeRepository.save(Store.builder()
                .userId(savedUser.getId())
                .name("테스트 가게")
                .isOpen(true)
                .build());
    }

    @Test
    @DisplayName("테이블 저장 및 조회 테스트")
    void saveAndFindTable() {
        // given
        StoreTable table = StoreTable.builder()
                .storeId(savedStore.getId())
                .tableNum(1)
                .status(TableStatus.EMPTY)
                .build();

        // when
        StoreTable savedTable = storeTableRepository.save(table);

        // then
        assertNotNull(savedTable.getId());
        assertEquals(1, savedTable.getTableNum());
        assertEquals(TableStatus.EMPTY, savedTable.getStatus());
    }

    @Test
    @DisplayName("가게 ID와 상태로 테이블 조회")
    void findByStoreIdAndStatus() {
        // given
        storeTableRepository.save(StoreTable.builder()
                .storeId(savedStore.getId())
                .tableNum(1)
                .status(TableStatus.EMPTY)
                .build());

        storeTableRepository.save(StoreTable.builder()
                .storeId(savedStore.getId())
                .tableNum(2)
                .status(TableStatus.OCCUPIED)
                .build());

        storeTableRepository.save(StoreTable.builder()
                .storeId(savedStore.getId())
                .tableNum(3)
                .status(TableStatus.EMPTY)
                .build());

        // when
        List<StoreTable> emptyTables = storeTableRepository.findByStoreIdAndStatus(
                savedStore.getId(), TableStatus.EMPTY);

        // then
        assertEquals(2, emptyTables.size());
    }

    @Test
    @DisplayName("가게 ID와 테이블 번호로 조회")
    void findByStoreIdAndTableNum() {
        // given
        storeTableRepository.save(StoreTable.builder()
                .storeId(savedStore.getId())
                .tableNum(5)
                .status(TableStatus.RESERVED)
                .build());

        // when
        Optional<StoreTable> foundTable = storeTableRepository.findByStoreIdAndTableNum(
                savedStore.getId(), 5);

        // then
        assertTrue(foundTable.isPresent());
        assertEquals(5, foundTable.get().getTableNum());
        assertEquals(TableStatus.RESERVED, foundTable.get().getStatus());
    }
}
