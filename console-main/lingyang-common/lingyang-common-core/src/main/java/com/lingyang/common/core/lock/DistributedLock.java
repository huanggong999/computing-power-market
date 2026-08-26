package com.lingyang.common.core.lock;

import java.util.concurrent.TimeUnit;

/**
 * @Description: 分布式锁接口
 * @Author: 王小龙
 * @Date: 2023/9/26 15:44
 */
public interface DistributedLock {
    /**
     * 获取锁 (当前方法只会获取一次, 不会阻塞线程)
     * @param lockKey    key
     * @return 成功true, 其它情况false
     */
   default boolean lock(String lockKey) {
       return lock(lockKey, null, null);
   }

    /**
     * 获取锁 (当前方法只会获取一次, 不会阻塞线程)
     * @param lockKey    key
     * @param expireTime 过期时间 (可能会出现锁已到期,锁的方法未执行完成,导致锁失效)
     * @param timeUnit   时间单位
     * @return 成功true, 其它情况false
     */
    boolean lock(String lockKey, Long expireTime, TimeUnit timeUnit);

    /**
     * 释放锁
     *
     * @param lockKey key
     */
    void releaseLock(String lockKey);
}