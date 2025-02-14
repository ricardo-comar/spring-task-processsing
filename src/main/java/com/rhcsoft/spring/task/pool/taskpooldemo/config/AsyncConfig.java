package com.rhcsoft.spring.task.pool.taskpooldemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1); // Número mínimo de threads
        executor.setMaxPoolSize(1); // Número máximo de threads
        executor.setQueueCapacity(10000); // Capacidade da fila
        executor.setThreadNamePrefix("EventProcessor-");
        executor.initialize();
        return executor;
    }
}
