package com.lingyang.common.cache.config;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author oldti
 */
@ConditionalOnProperty(value = "cache.queue.enable", havingValue = "true")
@Configuration
@ConfigurationProperties(prefix = "cache.queue.pool")
@Data
public class CacheQueuePoolConfig {
    /**
     * 线程池核心线程数
     */
    private int corePoolSize = 5;
    /**
     * 线程池最大线程数
     */
    private int maxPoolSize = 15;
    /**
     * 线程池等待队列容量
     */
    private int queueCapacity = 30;
    /**
     * 线程池等待时间
     */
    private int keepAliveSeconds = 300;

    @Bean("cache-queue-consumer-executor")
    public ThreadPoolTaskExecutor initExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(this.corePoolSize);
        taskExecutor.setMaxPoolSize(this.maxPoolSize);
        taskExecutor.setQueueCapacity(this.queueCapacity);
        taskExecutor.setKeepAliveSeconds(this.keepAliveSeconds);
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        taskExecutor.setThreadNamePrefix("cache-queue-consumer-executor");
        taskExecutor.initialize();
        return taskExecutor;
    }
}