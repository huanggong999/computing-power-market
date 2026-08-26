package com.lingyang.common.cache.config;

import com.lingyang.common.cache.CacheQueueService;
import com.lingyang.common.cache.CacheService;
import com.lingyang.common.core.utils.SpringUtils;
import com.lingyang.common.core.utils.StringUtils;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/18 19:02
 */
@ConditionalOnProperty(value = "cache.queue.enable", havingValue = "true")
@Configuration
@ConfigurationProperties(prefix = "cache.queue")
@Data
public class CacheQueueConfig {

    /**
     * 是否开启缓存队列，默认false
     */
    private boolean enable = false;

    /**
     * 队列key
     */
    private String queueKey;

    @Bean
    public CacheQueueService cacheQueueService() {
        CacheService cacheService = SpringUtils.getBean(CacheService.class);
        return cacheService.queue(getQueueKey());
    }

    public String getQueueKey() {
        return StringUtils.isEmpty(queueKey) ? SpringUtils.getApplicationContext().getId() + "-" + SpringUtils.getActive() + "-CACHE-QUEUE" : queueKey;
    }
}
