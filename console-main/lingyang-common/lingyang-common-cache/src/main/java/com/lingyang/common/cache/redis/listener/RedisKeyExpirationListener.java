package com.lingyang.common.cache.redis.listener;

import com.lingyang.common.cache.listener.CacheExpireListener;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2023/9/11 18:45
 */
@Service
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {

    private Map<String, CacheExpireListener> listenerMap;


    public RedisKeyExpirationListener(@Autowired RedisMessageListenerContainer redisMessageListenerContainer,
                                      @Autowired(required = false) List<CacheExpireListener> cacheExpireListenerLis) {
        super(redisMessageListenerContainer);
        if (ObjectUtils.isNotEmpty(cacheExpireListenerLis)) {
            listenerMap = new ConcurrentHashMap<>(cacheExpireListenerLis.size());
            for (CacheExpireListener cacheExpireListener : cacheExpireListenerLis) {
                listenerMap.put(cacheExpireListener.key(), cacheExpireListener);
            }
        }
    }

    @Override
    public void onMessage(@NotNull Message message, byte[] pattern) {
        if (listenerMap == null) {
            return;
        }
        String expireKey = message.toString();
        for (Map.Entry<String, CacheExpireListener> entry : listenerMap.entrySet()) {
            String listenerKey = entry.getKey();
            if (expireKey.equals(listenerKey) || expireKey.startsWith(listenerKey)) {
                CacheExpireListener listener = entry.getValue();
                listener.expireHandler(expireKey.split(listener.key())[1]);
            }
        }
    }
}