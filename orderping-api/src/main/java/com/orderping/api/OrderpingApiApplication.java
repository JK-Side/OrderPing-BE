package com.orderping.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication(scanBasePackages = {
    "com.orderping.api",
    "com.orderping.infra",
    "com.orderping.external"
})
@EntityScan(basePackages = "com.orderping.infra")
@EnableJpaRepositories(basePackages = "com.orderping.infra")
public class OrderpingApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderpingApiApplication.class, args);
    }
}
