package com.lingyang.cloud.listener.queue;

import com.lingyang.cloud.service.SysOrderService;
import com.lingyang.common.cache.queue.CacheQueueConsumer;
import com.lingyang.common.core.exception.http.HttpServiceException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.lingyang.cloud.common.constant.CacheQueueConstant.ORDER_EXPIRE_QUEUE_TYPE;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/19 14:25
 */
@Service
@Slf4j
public class OrderExpireQueueConsumer implements CacheQueueConsumer {
    @Resource
    private SysOrderService sysOrderService;

    @Override
    public void invoke(String messageContent) {
        log.info("监听到到期未支付订单：{}", messageContent);
        try {
            sysOrderService.cancelOrder(Long.valueOf(messageContent));
        }catch (HttpServiceException ignored) {

        }
    }


    @Override
    public String messageType() {
        return ORDER_EXPIRE_QUEUE_TYPE;
    }
}
