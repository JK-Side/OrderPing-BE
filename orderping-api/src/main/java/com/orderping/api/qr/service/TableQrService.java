package com.orderping.api.qr.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderping.api.qr.dto.TableQrInfoResponse;
import com.orderping.api.qr.service.QrTokenProvider.TableQrClaims;
import com.orderping.api.table.service.CustomerTableService;
import com.orderping.domain.exception.NotFoundException;
import com.orderping.domain.store.StoreTable;
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
    private final CustomerTableService customerTableService;

    public TableQrInfoResponse getTableInfoByToken(String token) {
        // 토큰 검증 및 파싱
        TableQrClaims claims;
        try {
            claims = qrTokenProvider.parseTableToken(token);
        } catch (JwtException e) {
            throw new NotFoundException("유효하지 않은 QR 코드입니다: " + e.getMessage());
        }

        StoreTable table = storeTableRepository.findActiveByStoreIdAndTableNum(claims.storeId(), claims.tableNum())
            .orElseThrow(() -> new NotFoundException("테이블을 찾을 수 없습니다."));

        storeRepository.findById(claims.storeId())
            .orElseThrow(() -> new NotFoundException("주점을 찾을 수 없습니다."));

        return customerTableService.getTableInfo(table.getId());
    }

    public TableQrInfoResponse getTableInfoByTableId(Long tableId) {
        return customerTableService.getTableInfo(tableId);
    }
}
