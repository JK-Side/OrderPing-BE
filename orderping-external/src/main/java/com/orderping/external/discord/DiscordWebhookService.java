package com.orderping.external.discord;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DiscordWebhookService {

    private final DiscordProperties discordProperties;
    private final RestTemplate restTemplate;

    public DiscordWebhookService(
        DiscordProperties discordProperties,
        @Qualifier("discordRestTemplate") RestTemplate restTemplate
    ) {
        this.discordProperties = discordProperties;
        this.restTemplate = restTemplate;
    }

    @Async
    public void sendMessage(String content) {
        if (discordProperties.getWebhookUrl() == null || discordProperties.getWebhookUrl().isBlank()) {
            log.warn("Discord webhook URL is not configured");
            return;
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String payload = String.format("{\"content\": \"%s\"}", escapeJson(content));
            HttpEntity<String> request = new HttpEntity<>(payload, headers);

            restTemplate.postForEntity(discordProperties.getWebhookUrl(), request, String.class);
            log.info("Discord notification sent successfully");
        } catch (Exception e) {
            log.error("Failed to send Discord notification", e);
        }
    }

    @Async
    public void sendEmbed(String title, String description, int color) {
        if (discordProperties.getWebhookUrl() == null || discordProperties.getWebhookUrl().isBlank()) {
            log.warn("Discord webhook URL is not configured");
            return;
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String payload = String.format("""
                {
                    "embeds": [{
                        "title": "%s",
                        "description": "%s",
                        "color": %d
                    }]
                }
                """, escapeJson(title), escapeJson(description), color);

            HttpEntity<String> request = new HttpEntity<>(payload, headers);

            restTemplate.postForEntity(discordProperties.getWebhookUrl(), request, String.class);
            log.info("Discord embed notification sent successfully");
        } catch (Exception e) {
            log.error("Failed to send Discord embed notification", e);
        }
    }

    private String escapeJson(String text) {
        if (text == null) {
            return "";
        }
        return text
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }
}
