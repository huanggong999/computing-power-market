package com.lingyang.common.core.lock.distributed;

import com.lingyang.common.core.exception.MethodExecutionException;
import com.lingyang.common.core.lock.DistributedLock;
import com.lingyang.common.core.utils.DateUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2023/9/26 18:31
 */
public class DistributedExecute<T> {
    private final DistributedLock lock;
    private String lockKey;
    private final T param;
    private boolean flag = false;

    private DistributedExecute(DistributedLock lock, T param) {
        this.lock = lock;
        this.param = param;
        if (this.lock == null) {
            throw new MethodExecutionException("lock is null");
        }
    }

    /**
     *
     * @param lock 锁
     * @return DistributedExecute
     * @param <T> 参数类型
     */
    public static <T> DistributedExecute<T> of(DistributedLock lock) {
        return of(lock, null);
    }

    /**
     *
     * @param lock 锁
     * @param param 参数
     * @return DistributedExecute
     * @param <T> 参数类型
     */
    public static <T> DistributedExecute<T> of(DistributedLock lock, T param) {
        return new DistributedExecute<>(lock, param);
    }

    /**
     * 获取锁成功执行方法
     * @param function 执行的方法
     * @return 获取锁失败,返回 null
     * @param <R> 返回值类型
     */
    public <R> R onSuccessFunction(Function<T, R> function) {
        try {
            if (flag) {
                return function.apply(param);
            }
        }finally {
            this.lock.releaseLock(this.lockKey);
        }
       return null;
    }

    /**
     * 获取锁失败执行方法
     * @param function 执行的方法
     * @return 获取锁成功,返回 null
     * @param <R> 返回值类型
     */
    public <R> R onErrorFunction(Function<T, R> function) {
        try {
            if (!flag) {
                return function.apply(param);
            }
        }finally {
            this.lock.releaseLock(this.lockKey);
        }
        return null;
    }


    /**
     * 获取锁成功执行方法, 获取失败不执行
     *
     * @param consumer 执行的方法
     */
    public void onSuccessFunction(Consumer<T> consumer) {
        try {
            if (flag) {
                consumer.accept(param);
            }
        }finally {
            this.lock.releaseLock(this.lockKey);
        }
    }

    /**
     * 获取锁失败执行方法, 获取成功不执行
     * @param consumer 执行的方法
     */
    public DistributedExecute<T> onErrorFunction(Consumer<T> consumer) {
        try {
            if (!flag) {
                consumer.accept(param);
            }
        }finally {
            this.lock.releaseLock(this.lockKey);
        }
        return this;
    }



    /**
     * 获取锁  会阻塞当前线程
     * @param lockKey key
     * @return this
     */
    public DistributedExecute<T> lock(String lockKey) {
        return lock(lockKey, null, null);
    }


    /**
     * 获取锁  会阻塞当前线程
     * @param lockKey key
     * @param expireTime 锁到期时间
     * @param timeUnit 到期时间单位
     * @return this
     */
    public DistributedExecute<T> lock(String lockKey, Long expireTime, TimeUnit timeUnit) {
        this.lockKey = lockKey;
        do {
            this.flag = lock.lock(lockKey, expireTime, timeUnit);
        } while (!this.flag);
        return this;
    }


    /**
     * 获取锁  会阻塞当前线程
     * @param lockKey key
     * @param timeout 获取超时时间 (单位秒)
     * @return this
     */
    public DistributedExecute<T> lock(String lockKey, Integer timeout) {
        Date endDate = DateUtils.updateDate(DateUtils.getNowDate(), timeout, Calendar.SECOND);
        this.lockKey = lockKey;
        do {
            this.flag = lock.lock(lockKey);
            if (endDate.getTime() >= DateUtils.getNowDate().getTime()) {
                break;
            }
        } while (this.flag);
        return this;
    }
}