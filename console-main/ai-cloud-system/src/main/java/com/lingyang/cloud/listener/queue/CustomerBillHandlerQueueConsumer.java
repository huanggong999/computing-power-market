package com.lingyang.cloud.listener.queue;

import com.lingyang.cloud.service.SysCustomerBillService;
import com.lingyang.common.cache.queue.CacheQueueConsumer;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import static com.lingyang.cloud.common.constant.CacheQueueConstant.CUSTOMER_BILL_HANDLER_QUEUE_TYPE;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/12/5 16:28
 */
@Service
public class CustomerBillHandlerQueueConsumer implements CacheQueueConsumer {
    @Resource
    private SysCustomerBillService sysCustomerBillService;

    @Override
    public void invoke(String messageContent) throws Exception {
        sysCustomerBillService.handlerCustomerBill(Long.valueOf(messageContent));
    }

    @Override
    public String messageType() {
        return CUSTOMER_BILL_HANDLER_QUEUE_TYPE;
    }
}
