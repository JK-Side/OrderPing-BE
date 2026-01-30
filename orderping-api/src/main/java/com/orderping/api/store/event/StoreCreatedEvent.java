package com.orderping.api.store.event;

import com.orderping.domain.store.Store;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class StoreCreatedEvent {

    private final Store store;
    private final String ownerNickname;
}
