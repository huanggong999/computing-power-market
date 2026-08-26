package com.lingyang.common.cache.redis.lock;

import com.lingyang.common.core.exception.MethodExecutionException;
import com.lingyang.common.core.lock.DistributedLock;
import com.lingyang.common.core.utils.IdUtils;
import com.lingyang.common.core.utils.StringUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2023/9/26 16:16
 */
@Service
@Slf4j
public class RedisLock implements DistributedLock {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean lock(String lockKey, Long expireTime, TimeUnit timeUnit) {
        try {
            if (StringUtils.isEmpty(lockKey)) {
                throw new MethodExecutionException("lockKey Is Empty");
            }
            if (ObjectUtils.isNotEmpty(expireTime)) {
                if (ObjectUtils.isEmpty(timeUnit)) {
                    throw new MethodExecutionException("timeUnit Is Null");
                }
                if (expireTime == 0L) {
                    expireTime = -1L;
                }
            }else {
                expireTime = -1L;
            }
            return Boolean.TRUE.equals(this.redisTemplate.opsForValue().setIfAbsent(lockKey, IdUtils.nextId(), expireTime, timeUnit));
        } catch (Error e) {
            log.error("RedisLock error: ", e);
            return false;
        }
    }

    @Override
    public void releaseLock(String lockKey) {
        redisTemplate.delete(lockKey);
    }
}
