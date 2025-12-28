package com.orderping.api.bank.service;

import com.orderping.api.bank.dto.BankResponse;
import com.orderping.domain.bank.repository.BankRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BankService {

    private final BankRepository bankRepository;

    public List<BankResponse> getAllBanks() {
        return bankRepository.findAllActive().stream()
                .map(BankResponse::from)
                .toList();
    }
}
