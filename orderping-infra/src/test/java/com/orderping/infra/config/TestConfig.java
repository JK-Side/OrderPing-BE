package com.orderping.infra.config;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "com.orderping.infra")
@EntityScan(basePackages = "com.orderping.infra")
@EnableJpaRepositories(basePackages = "com.orderping.infra")
public class TestConfig {
}
