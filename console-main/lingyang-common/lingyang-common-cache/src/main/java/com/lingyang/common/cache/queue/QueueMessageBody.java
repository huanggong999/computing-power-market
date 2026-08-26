package com.lingyang.common.cache.queue;

import lombok.Data;

import java.util.Date;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/18 18:45
 */
@Data
public class QueueMessageBody {
    /**
     * 消息唯一标识
     */
    private String id;
    /**
     * 消息类型 如 订单 支付 代表不同业务类型
     * 为消费时不同类去处理
     */
    private String messageType;

    /**
     * 具体消息 json
     */
    private String body;

    /**
     * 延时时间 被消费时间  取当前时间戳+延迟时间
     */
    private Long delayTime;

    /**
     * 创建时间
     */
    private Date createTime;
}
