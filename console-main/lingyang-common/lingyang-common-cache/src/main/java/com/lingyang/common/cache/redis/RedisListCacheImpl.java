package com.lingyang.common.cache.redis;

import com.lingyang.common.cache.ListCacheService;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class RedisListCacheImpl implements ListCacheService {
    private final BoundListOperations<String, Object> operations;

    public RedisListCacheImpl(String key, RedisTemplate<String, Object> redisTemplate) {
        this.operations = redisTemplate.opsForList().getOperations().boundListOps(key);
    }

    /**
     * 判断当前值是否存在当前key的列表
     *
     * @param value 值
     * @return 存在 - true
     */
    @Override
    public boolean valueExistList(Object value) {
        List<Object> values = getAllValue();
        if (CollectionUtils.isEmpty(values)) {
            return false;
        }
        for (Object redisValue : values) {
            if (redisValue.equals(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 在相同key中添加一个值，这个key是个列表
     *
     * @param value 值
     */
    @Override
    public void addValue(Object value) {
        operations.leftPush(value);
    }

    /**
     * 在相同key中添加多个值，这个key是个列表
     *
     * @param values 值
     */
    @Override
    public void addValues(Object... values) {
        operations.leftPushAll(values);
    }

    /**
     * 删除key中的值， 这个key是个列表
     *
     * @param value 值
     */
    @Override
    public void delValue(Object value) {
        operations.remove(0, value);
    }

    /**
     * 删除key中的值， 这个key是个列表
     *
     * @param values 值：一个或多个
     */
    @Override
    public void delValues(List<?> values) {
        for (Object value : values) {
            operations.remove(0, value);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> getAllValue() {
        return (List<T>) operations.range(0, -1);
    }
}