package com.pay.aile.bill.config;

import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/***
 * ThreadPoolConfig.java
 *
 * @author shinelon
 *
 * @date 2017年10月31日
 *
 */
@Configuration
public class ThreadPoolConfig {
    @Bean(name = "mailTaskExecutor")
    public ThreadPoolTaskExecutor cofigPool() {
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        pool.setCorePoolSize(64);
        pool.setMaxPoolSize(256);
        pool.setThreadNamePrefix("mailTask-");
        pool.setKeepAliveSeconds(30);
        pool.setQueueCapacity(2048);
        pool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return pool;
    }
}
