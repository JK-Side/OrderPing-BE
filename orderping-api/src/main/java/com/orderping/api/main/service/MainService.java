package com.orderping.api.main.service;

import com.orderping.api.main.dto.MainResponse;
import com.orderping.api.main.dto.MainResponse.StoreSimpleResponse;
import com.orderping.domain.store.Store;
import com.orderping.domain.store.repository.StoreRepository;
import com.orderping.domain.user.User;
import com.orderping.domain.user.repository.UserRepository;
import com.orderping.domain.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MainService {

    private final UserRepository userRepository;
    private final StoreRepository storeRepository;

    public MainResponse getMainInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

        List<Store> stores = storeRepository.findByUserId(userId);

        List<StoreSimpleResponse> storeResponses = stores.stream()
                .map(store -> new StoreSimpleResponse(
                        store.getId(),
                        store.getName(),
                        store.getImageUrl()
                ))
                .toList();

        return new MainResponse(user.getNickname(), storeResponses);
    }
}
