package com.orderping.api.menu.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderping.api.menu.dto.CustomerMenuDetailResponse;
import com.orderping.api.qr.dto.TableQrInfoResponse;
import com.orderping.api.table.service.CustomerTableService;
import com.orderping.domain.exception.NotFoundException;
import com.orderping.domain.menu.Menu;
import com.orderping.domain.menu.repository.MenuRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerMenuService {

    private final CustomerTableService customerTableService;
    private final MenuRepository menuRepository;

    public TableQrInfoResponse getMenusByTable(Long tableId) {
        return customerTableService.getTableInfo(tableId);
    }

    public CustomerMenuDetailResponse getMenuDetail(Long menuId) {
        Menu menu = menuRepository.findById(menuId)
            .orElseThrow(() -> new NotFoundException("메뉴를 찾을 수 없습니다."));
        return CustomerMenuDetailResponse.from(menu);
    }
}
