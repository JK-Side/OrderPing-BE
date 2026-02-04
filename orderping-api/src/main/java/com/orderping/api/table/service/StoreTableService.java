package com.orderping.api.table.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderping.api.qr.service.QrTokenProvider;
import com.orderping.api.table.dto.OrderMenuSummary;
import com.orderping.api.table.dto.StoreTableBulkCreateRequest;
import com.orderping.api.table.dto.StoreTableBulkQrUpdateRequest;
import com.orderping.api.table.dto.StoreTableCreateRequest;
import com.orderping.api.table.dto.StoreTableDetailResponse;
import com.orderping.api.table.dto.StoreTableResponse;
import com.orderping.api.table.dto.StoreTableStatusUpdateRequest;
import com.orderping.api.table.dto.StoreTableUpdateRequest;
import com.orderping.domain.enums.OrderStatus;
import com.orderping.domain.enums.TableStatus;
import com.orderping.domain.exception.BadRequestException;
import com.orderping.domain.exception.ForbiddenException;
import com.orderping.domain.exception.NotFoundException;
import com.orderping.domain.menu.Menu;
import com.orderping.domain.menu.repository.MenuRepository;
import com.orderping.domain.order.Order;
import com.orderping.domain.order.OrderMenu;
import com.orderping.domain.order.repository.OrderMenuRepository;
import com.orderping.domain.order.repository.OrderRepository;
import com.orderping.domain.store.Store;
import com.orderping.domain.store.StoreTable;
import com.orderping.domain.store.repository.StoreRepository;
import com.orderping.domain.store.repository.StoreTableRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreTableService {

    private final StoreTableRepository storeTableRepository;
    private final StoreRepository storeRepository;
    private final OrderRepository orderRepository;
    private final OrderMenuRepository orderMenuRepository;
    private final MenuRepository menuRepository;
    private final QrTokenProvider qrTokenProvider;

    @Transactional
    public StoreTableResponse createStoreTable(Long userId, StoreTableCreateRequest request) {
        validateStoreOwner(request.storeId(), userId);
        validateNoDuplicateActiveTable(request.storeId(), request.tableNum());

        StoreTable storeTable = StoreTable.builder()
            .storeId(request.storeId())
            .tableNum(request.tableNum())
            .status(TableStatus.EMPTY)
            .qrImageUrl(request.qrImageUrl())
            .build();

        StoreTable saved = storeTableRepository.save(storeTable);

        // QR 토큰 생성
        String qrToken = qrTokenProvider.createTableToken(
            saved.getStoreId(),
            saved.getId(),
            saved.getTableNum()
        );
        String qrUrl = qrTokenProvider.buildTableQrUrl(qrToken);

        return StoreTableResponse.from(saved, qrToken, qrUrl);
    }

    public StoreTableResponse getStoreTable(Long id) {
        StoreTable storeTable = storeTableRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("테이블을 찾을 수 없습니다."));
        return StoreTableResponse.from(storeTable);
    }

    public List<StoreTableDetailResponse> getStoreTablesByStoreId(Long userId, Long storeId) {
        validateStoreOwner(storeId, userId);
        return storeTableRepository.findByStoreIdAndStatusNot(storeId, TableStatus.CLOSED).stream()
            .map(this::toDetailResponse)
            .toList();
    }

    public List<StoreTableDetailResponse> getStoreTablesByStoreIdAndStatus(Long userId, Long storeId,
        TableStatus status) {
        validateStoreOwner(storeId, userId);
        return storeTableRepository.findByStoreIdAndStatus(storeId, status).stream()
            .map(this::toDetailResponse)
            .toList();
    }

    public List<StoreTableDetailResponse> getStoreTables(Long userId, Long storeId, TableStatus status) {
        if (status != null) {
            return getStoreTablesByStoreIdAndStatus(userId, storeId, status);
        }
        return getStoreTablesByStoreId(userId, storeId);
    }

    private StoreTableDetailResponse toDetailResponse(StoreTable storeTable) {
        List<Order> orders = orderRepository.findByTableId(storeTable.getId());

        // menuId -> (menuName, totalQuantity, price) 집계용 Map
        Map<Long, MenuAggregate> menuAggregateMap = new LinkedHashMap<>();
        long totalAmount = 0L;
        OrderStatus highestPriorityStatus = null;

        for (Order order : orders) {
            // 우선순위: PENDING > COOKING > COMPLETE
            if (highestPriorityStatus == null) {
                highestPriorityStatus = order.getStatus();
            } else if (order.getStatus() == OrderStatus.PENDING) {
                highestPriorityStatus = OrderStatus.PENDING;
            } else if (order.getStatus() == OrderStatus.COOKING && highestPriorityStatus == OrderStatus.COMPLETE) {
                highestPriorityStatus = OrderStatus.COOKING;
            }

            List<OrderMenu> menus = orderMenuRepository.findByOrderId(order.getId());
            for (OrderMenu orderMenu : menus) {
                Long menuId = orderMenu.getMenuId();
                Long quantity = orderMenu.getQuantity();
                Long price = orderMenu.getPrice();

                menuAggregateMap.compute(menuId, (id, existing) -> {
                    if (existing == null) {
                        String menuName = menuRepository.findById(menuId)
                            .map(Menu::getName)
                            .orElse("삭제된 메뉴");
                        return new MenuAggregate(menuName, quantity, price);
                    } else {
                        return new MenuAggregate(existing.menuName, existing.quantity + quantity, existing.price);
                    }
                });

                totalAmount += price * quantity;
            }
        }

        List<OrderMenuSummary> orderMenus = menuAggregateMap.entrySet().stream()
            .map(entry -> new OrderMenuSummary(
                entry.getKey(),
                entry.getValue().menuName,
                entry.getValue().quantity,
                entry.getValue().price
            ))
            .toList();

        return StoreTableDetailResponse.from(storeTable, orderMenus, totalAmount, highestPriorityStatus);
    }

    @Transactional
    public StoreTableResponse updateStoreTableStatus(Long userId, Long id, StoreTableStatusUpdateRequest request) {
        StoreTable storeTable = storeTableRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("테이블을 찾을 수 없습니다."));
        validateStoreOwner(storeTable.getStoreId(), userId);

        StoreTable updated = StoreTable.builder()
            .id(storeTable.getId())
            .storeId(storeTable.getStoreId())
            .tableNum(storeTable.getTableNum())
            .status(request.status())
            .qrImageUrl(storeTable.getQrImageUrl())
            .build();

        StoreTable saved = storeTableRepository.save(updated);
        return StoreTableResponse.from(saved);
    }

    @Transactional
    public void deleteStoreTable(Long userId, Long id) {
        StoreTable storeTable = storeTableRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("테이블을 찾을 수 없습니다."));
        validateStoreOwner(storeTable.getStoreId(), userId);
        validateNoOrders(id);
        storeTableRepository.deleteById(id);
    }

    @Transactional
    public void deleteStoreTablesByTableNums(Long userId, Long storeId, List<Integer> tableNums) {
        validateStoreOwner(storeId, userId);

        List<StoreTable> tables = storeTableRepository.findByStoreIdAndStatusNot(storeId, TableStatus.CLOSED).stream()
            .filter(table -> tableNums.contains(table.getTableNum()))
            .toList();

        if (tables.isEmpty()) {
            throw new NotFoundException("삭제할 테이블을 찾을 수 없습니다.");
        }

        List<Integer> tablesWithOrders = new ArrayList<>();
        for (StoreTable table : tables) {
            if (!orderRepository.findByTableId(table.getId()).isEmpty()) {
                tablesWithOrders.add(table.getTableNum());
            }
        }

        if (!tablesWithOrders.isEmpty()) {
            throw new BadRequestException("주문이 존재하는 테이블은 삭제할 수 없습니다. 테이블 번호: " + tablesWithOrders);
        }

        for (StoreTable table : tables) {
            storeTableRepository.deleteById(table.getId());
        }
    }

    private void validateNoOrders(Long tableId) {
        if (!orderRepository.findByTableId(tableId).isEmpty()) {
            throw new BadRequestException("주문이 존재하는 테이블은 삭제할 수 없습니다.");
        }
    }

    @Transactional
    public StoreTableResponse updateStoreTable(Long userId, Long id, StoreTableUpdateRequest request) {
        StoreTable storeTable = storeTableRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("테이블을 찾을 수 없습니다."));
        validateStoreOwner(storeTable.getStoreId(), userId);

        StoreTable updated = StoreTable.builder()
            .id(storeTable.getId())
            .storeId(storeTable.getStoreId())
            .tableNum(storeTable.getTableNum())
            .status(storeTable.getStatus())
            .qrImageUrl(request.qrImageUrl())
            .build();

        StoreTable saved = storeTableRepository.save(updated);
        return StoreTableResponse.from(saved);
    }

    @Transactional
    public StoreTableResponse clearTable(Long userId, Long id) {
        StoreTable currentTable = storeTableRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("테이블을 찾을 수 없습니다."));
        validateStoreOwner(currentTable.getStoreId(), userId);

        // 기존 테이블 종료 처리
        StoreTable closedTable = StoreTable.builder()
            .id(currentTable.getId())
            .storeId(currentTable.getStoreId())
            .tableNum(currentTable.getTableNum())
            .status(TableStatus.CLOSED)
            .qrImageUrl(currentTable.getQrImageUrl())
            .build();
        storeTableRepository.save(closedTable);

        // 같은 번호의 새 테이블 생성 (QR URL 유지)
        StoreTable newTable = StoreTable.builder()
            .storeId(currentTable.getStoreId())
            .tableNum(currentTable.getTableNum())
            .status(TableStatus.EMPTY)
            .qrImageUrl(currentTable.getQrImageUrl())
            .build();
        StoreTable saved = storeTableRepository.save(newTable);

        return StoreTableResponse.from(saved);
    }

    @Transactional
    public List<StoreTableResponse> createStoreTablesBulk(Long userId, StoreTableBulkCreateRequest request) {
        validateStoreOwner(request.storeId(), userId);

        // 기존 활성 테이블 번호들 조회
        List<Integer> existingTableNums = storeTableRepository.findByStoreIdAndStatusNot(request.storeId(),
                TableStatus.CLOSED)
            .stream()
            .map(StoreTable::getTableNum)
            .toList();

        // 중복 체크
        List<Integer> duplicateNums = new ArrayList<>();
        for (int i = 1; i <= request.count(); i++) {
            if (existingTableNums.contains(i)) {
                duplicateNums.add(i);
            }
        }

        if (!duplicateNums.isEmpty()) {
            throw new BadRequestException("이미 존재하는 테이블 번호입니다: " + duplicateNums);
        }

        List<StoreTableResponse> responses = new ArrayList<>();

        for (int i = 1; i <= request.count(); i++) {
            StoreTable storeTable = StoreTable.builder()
                .storeId(request.storeId())
                .tableNum(i)
                .status(TableStatus.EMPTY)
                .build();

            StoreTable saved = storeTableRepository.save(storeTable);
            responses.add(StoreTableResponse.from(saved));
        }

        return responses;
    }

    @Transactional
    public List<StoreTableResponse> updateStoreTableQrBulk(Long userId, Long storeId,
        StoreTableBulkQrUpdateRequest request) {
        validateStoreOwner(storeId, userId);

        List<StoreTableResponse> responses = new ArrayList<>();
        List<Long> notFoundIds = new ArrayList<>();

        for (StoreTableBulkQrUpdateRequest.TableQrUpdate update : request.updates()) {
            StoreTable table = storeTableRepository.findById(update.tableId()).orElse(null);

            if (table == null || !table.getStoreId().equals(storeId)) {
                notFoundIds.add(update.tableId());
                continue;
            }

            StoreTable updated = StoreTable.builder()
                .id(table.getId())
                .storeId(table.getStoreId())
                .tableNum(table.getTableNum())
                .status(table.getStatus())
                .qrImageUrl(update.qrImageUrl())
                .build();

            StoreTable saved = storeTableRepository.save(updated);
            responses.add(StoreTableResponse.from(saved));
        }

        if (!notFoundIds.isEmpty()) {
            throw new NotFoundException("테이블을 찾을 수 없습니다. ID: " + notFoundIds);
        }

        return responses;
    }

    private void validateStoreOwner(Long storeId, Long userId) {
        Store store = storeRepository.findById(storeId)
            .orElseThrow(() -> new NotFoundException("매장을 찾을 수 없습니다."));
        if (!store.getUserId().equals(userId)) {
            throw new ForbiddenException("본인 매장의 테이블만 관리할 수 있습니다.");
        }
    }

    private void validateNoDuplicateActiveTable(Long storeId, Integer tableNum) {
        boolean exists = storeTableRepository.findActiveByStoreIdAndTableNum(storeId, tableNum).isPresent();
        if (exists) {
            throw new BadRequestException("이미 존재하는 테이블 번호입니다: " + tableNum);
        }
    }

    private record MenuAggregate(String menuName, Long quantity, Long price) {
    }
}
