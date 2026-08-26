package com.lingyang.common.cache.listener;

/**
 * @Description: 缓存到期监听
 * @Author: 王小龙
 * @Date: 2023/9/11 18:36
 */
public interface CacheExpireListener {
    /**
     * 处理
     */
    void expireHandler(String key);

    /**
     * 缓存key
     * @return 缓存key，前缀或者相等
     */
    String key();

}