package com.orderping.external.discord;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class DiscordConfig {

    @Bean("discordRestTemplate")
    public RestTemplate discordRestTemplate() {
        return new RestTemplate();
    }
}
