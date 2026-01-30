package com.orderping.api.store.event;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.orderping.domain.store.Store;
import com.orderping.external.discord.DiscordWebhookService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class StoreEventListener {

    private static final int DISCORD_COLOR_GREEN = 0x00FF00;

    private final DiscordWebhookService discordWebhookService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleStoreCreated(StoreCreatedEvent event) {
        Store store = event.getStore();
        String ownerNickname = event.getOwnerNickname();

        String title = "새로운 가게가 등록되었습니다!";
        String description = String.format(
            "**가게명:** %s\\n**사장님:** %s\\n**가게 ID:** %d",
            store.getName(),
            ownerNickname,
            store.getId()
        );

        discordWebhookService.sendEmbed(title, description, DISCORD_COLOR_GREEN);
        log.info("Store created event handled: storeId={}, storeName={}", store.getId(), store.getName());
    }
}
