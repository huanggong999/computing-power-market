package com.lingyang.cloud.listener.queue;

import com.lingyang.cloud.service.SysCustomerBillService;
import com.lingyang.common.cache.queue.CacheQueueConsumer;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import static com.lingyang.cloud.common.constant.CacheQueueConstant.CUSTOMER_BILL_SELF_BUILD_QUEUE_TYPE;

/**
 * 客户自建服务器账单处理
 * @author Administrator
 */
@Service
public class CustomerBillSelfBuildQueueConsumer implements CacheQueueConsumer {
    @Resource
    private SysCustomerBillService sysCustomerBillService;

    @Override
    public void invoke(String messageContent) throws Exception {
        sysCustomerBillService.handlerCustomerBillSelfBuild(Long.valueOf(messageContent));
    }

    @Override
    public String messageType() {
        return CUSTOMER_BILL_SELF_BUILD_QUEUE_TYPE;
    }
}
