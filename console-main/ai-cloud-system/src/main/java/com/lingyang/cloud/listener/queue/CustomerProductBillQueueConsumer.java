package com.lingyang.cloud.listener.queue;

import com.lingyang.cloud.service.SysCustomerBillService;
import com.lingyang.common.cache.queue.CacheQueueConsumer;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import static com.lingyang.cloud.common.constant.CacheQueueConstant.CUSTOMER_BILL_HANDLER_QUEUE_TYPE;
import static com.lingyang.cloud.common.constant.CacheQueueConstant.CUSTOMER_BILL_PRODUCT_QUEUE_TYPE;

/**
 * 客户产品账单处理
 */
@Service
public class CustomerProductBillQueueConsumer implements CacheQueueConsumer {
    @Resource
    private SysCustomerBillService sysCustomerBillService;

    @Override
    public void invoke(String messageContent) throws Exception {
        sysCustomerBillService.handlerCustomerBillProduct(Long.valueOf(messageContent));
    }

    @Override
    public String messageType() {
        return CUSTOMER_BILL_PRODUCT_QUEUE_TYPE;
    }
}
