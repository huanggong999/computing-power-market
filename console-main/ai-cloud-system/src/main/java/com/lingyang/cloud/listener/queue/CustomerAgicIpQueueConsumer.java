package com.lingyang.cloud.listener.queue;

import com.lingyang.cloud.service.SysNetworkProductIpService;
import com.lingyang.common.cache.queue.CacheQueueConsumer;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import static com.lingyang.cloud.common.constant.CacheQueueConstant.AGI_C_IP_QUEUE_TYPE;

/**
 * 客户Agic的ip处理
 * @author Administrator
 */
@Service
public class CustomerAgicIpQueueConsumer implements CacheQueueConsumer {
    @Resource
    private SysNetworkProductIpService sysNetworkProductIpService;

    @Override
    public void invoke(String messageContent) throws Exception {
        sysNetworkProductIpService.handlerCustomerAgicIp(messageContent);
    }

    @Override
    public String messageType() {
        return AGI_C_IP_QUEUE_TYPE;
    }
}
