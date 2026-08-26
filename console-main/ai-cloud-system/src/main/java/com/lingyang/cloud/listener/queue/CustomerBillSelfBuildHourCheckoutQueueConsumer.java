package com.lingyang.cloud.listener.queue;

import com.lingyang.cloud.api.service.pc.PcConsoleService;
import com.lingyang.common.cache.queue.CacheQueueConsumer;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import static com.lingyang.cloud.common.constant.CacheQueueConstant.CUSTOMER_SELF_BUILD_SERVER_HOURLY_CHECK_QUEUE_TYPE;

/**
 * 客户自建服务器账单处理
 * @author Administrator
 */
@Service
public class CustomerBillSelfBuildHourCheckoutQueueConsumer implements CacheQueueConsumer {
    @Resource
    private PcConsoleService pcConsoleService;

    @Override
    public void invoke(String messageContent) throws Exception {
        pcConsoleService.handlerCustomerBillSelfBuildHourCheckout(Long.valueOf(messageContent));
    }

    @Override
    public String messageType() {
        return CUSTOMER_SELF_BUILD_SERVER_HOURLY_CHECK_QUEUE_TYPE;
    }
}
