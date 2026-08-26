package com.lingyang.common.cache.redis;

import com.lingyang.common.cache.CacheService;
import com.lingyang.common.cache.HashCacheService;
import com.lingyang.common.cache.ListCacheService;
import com.lingyang.common.cache.CacheQueueService;
import com.lingyang.common.core.utils.StringUtils;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2023/8/7 16:49
 */
public class RedisCacheImpl implements CacheService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    @Override
    public void setCacheObject(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void setCacheObject(String key, Object value, long captchaExpiration, TimeUnit minutes) {
        redisTemplate.opsForValue().set(key, value, captchaExpiration, minutes);
    }

    @Override
    @SuppressWarnings(value = {"unchecked"})
    public <T> T getCacheObject(String key) {
        return (T) redisTemplate.opsForValue().get(key);
    }

    @Override
    public Long getIncrement(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    @Override
    public void deleteObject(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 删除集合对象
     *
     * @param collection 多个对象
     */
    @Override
    public void deleteObject(final Collection<String> collection) {
        redisTemplate.delete(collection);
    }

    @Override
    public Collection<String> keys(String pattern) {
        if (StringUtils.isEmpty(pattern)) {
            return null;
        }
        if (!pattern.endsWith("*")) {
            pattern = pattern + "*";
        }
        Cursor<String> scan = redisTemplate.scan(ScanOptions.scanOptions().match(pattern).build());
        Set<String> keys = new HashSet<>();
        while (scan.hasNext()) {
            keys.add(scan.next());
        }
        scan.close();
        return keys;
    }

    @Override
    public ListCacheService list(String key) {
        return new RedisListCacheImpl(key, redisTemplate);
    }

    @Override
    public HashCacheService hash(String key) {
        return new RedisHashCacheImpl(key, redisTemplate);
    }

    @Override
    public Long getExpire(String cacheKey, TimeUnit timeUnit) {
        return  redisTemplate.getExpire(cacheKey, timeUnit);
    }

    @Override
    public CacheQueueService queue(String key) {
        return new RedisQueueCacheServiceImpl(key, redisTemplate);
    }

}