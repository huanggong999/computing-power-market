package com.lingyang.common.cache;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2023/8/7 16:48
 */
public interface CacheService {
    /**
     * 是否包含当前key
     *
     * @param key key
     * @return true
     */
    boolean hasKey(String key);

    /**
     * 获取缓存
     *
     * @param key   缓存key
     * @param value 缓存值
     */
    void setCacheObject(String key, Object value);

    /**
     * 设置缓存
     *
     * @param key               缓存key
     * @param value             缓存值
     * @param captchaExpiration 过期时间
     * @param minutes           过期时间单位
     */
    void setCacheObject(String key, Object value, long captchaExpiration, TimeUnit minutes);

    /**
     * 获取缓存
     *
     * @param key 缓存key
     * @param <T> 缓存值类型
     * @return 缓存值
     */
    <T> T getCacheObject(String key);

    /**
     * 获取递增结果
     *
     * @param key key
     * @return 递增结果
     */
    Long getIncrement(String key);

    /**
     * 删除缓存
     *
     * @param key 缓存key
     */
    void deleteObject(String key);

    /**
     * 删除缓存
     *
     * @param collection 缓存key
     */
    void deleteObject(final Collection<String> collection);

    /**
     * 模糊搜索缓存key
     * ( * ) 表示匹配所有
     *
     * @param pattern 字符串前缀
     * @return key列表
     */
    Collection<String> keys(final String pattern);

    /**
     * 获取list缓存
     *
     * @param key key
     * @return list缓存操作对象
     */
    ListCacheService list(final String key);

    /**
     * 获取hash缓存
     *
     * @param key hash主key
     * @return hash缓存操作对象
     */
    HashCacheService hash(final String key);

    /**
     * 获取到期时间
     *
     * @param cacheKey 缓存key
     * @param timeUnit 时间单位
     * @return 过期时间，-1 表示永久
     */
    Long getExpire(String cacheKey, TimeUnit timeUnit);
    /**
     * 队列
     * @param key 队列key
     * @return 队列缓存对象
     */
    CacheQueueService queue(String key);
}