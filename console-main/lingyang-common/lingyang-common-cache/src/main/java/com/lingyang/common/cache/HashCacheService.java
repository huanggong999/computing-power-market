package com.lingyang.common.cache;

import java.util.List;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2023/8/22 14:32
 */
public interface HashCacheService {

    <T> List<T> getAll();

    <T> T get(String key);

    void put(String key, Object value);

    boolean hasKey(String key);

    void delete(String key);
}
