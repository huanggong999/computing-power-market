package com.lingyang.common.cache;

import com.lingyang.common.cache.queue.QueueMessageBody;

import java.util.Set;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/18 18:13
 */
public interface CacheQueueService {
    /**
     * 添加延迟队列
     * @param messageType 消息类型
     * @param messageContent 队列消息
     * @param delay 延迟时间，单位秒
     */
    void addDelayQueue(String messageType, String messageContent, long delay);


    /**
     * 获取队列
     *
     * @return 队列
     */
    Set<Object> getQueue();

    /**
     * 删除消息
     * @param messageBody 消息主体
     */
    void remove(QueueMessageBody messageBody);

    Set<Object> getAllQueue();
}