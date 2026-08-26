package com.lingyang.common.cache.redis;

import com.lingyang.common.cache.HashCacheService;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2023/6/27 16:33
 */
public class RedisHashCacheImpl implements HashCacheService {

    private final String key;

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisHashCacheImpl(String key, RedisTemplate<String, Object> redisTemplate) {
        this.key = key;
        this.redisTemplate = redisTemplate;
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public <T> List<T> getAll() {
       return (List<T>) redisTemplate.opsForHash().values(key);
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public <T> T get(String key) {
        return (T) redisTemplate.opsForHash().get(this.key, key);
    }

    @Override
    public void put(String key, Object value) {
        redisTemplate.opsForHash().put(this.key, key, value);
    }

    @Override
    public boolean hasKey(String key) {
        return redisTemplate.opsForHash().hasKey(this.key, key);
    }

    @Override
    public void delete(String key) {
        redisTemplate.opsForHash().delete(this.key, key);
    }
}