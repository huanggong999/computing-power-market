package com.lingyang.common.cache.queue;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/18 19:29
 */
public interface CacheQueueConsumer {

    /**
     * 执行消费操作
     * @throws Exception 异常，当抛出此异常，当前消息会重新添加队列
     */
    void invoke(String messageContent)  throws Exception;

    /**
     * 消息类型
     * @return 消息类型
     */
    String messageType();
}
