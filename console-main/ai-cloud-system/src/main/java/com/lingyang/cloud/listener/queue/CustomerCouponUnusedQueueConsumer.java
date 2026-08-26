package com.lingyang.cloud.listener.queue;

import com.alibaba.fastjson2.JSONArray;
import com.lingyang.cloud.common.constant.CacheQueueConstant;
import com.lingyang.cloud.entity.SysCustomerCouponEntity;
import com.lingyang.cloud.enums.coupon.CouponUseStatusEnum;
import com.lingyang.cloud.mapper.SysCustomerCouponMapper;
import com.lingyang.common.cache.CacheQueueService;
import com.lingyang.common.cache.queue.CacheQueueConsumer;
import com.lingyang.common.core.utils.DateUtils;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static com.lingyang.cloud.common.constant.CacheQueueConstant.CUSTOMER_COUPON_UNUSED_QUEUE_TYPE;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/19 14:09
 */
@Service
public class CustomerCouponUnusedQueueConsumer implements CacheQueueConsumer {
    @Resource
    private SysCustomerCouponMapper customerCouponMapper;
    @Resource
    private CacheQueueService cacheQueueService;

    @Override
    public void invoke(String messageContent) {
        List<SysCustomerCouponEntity> customerCouponList = JSONArray.parseArray(messageContent, SysCustomerCouponEntity.class);
        if (ObjectUtils.isEmpty(customerCouponList)) {
            return;
        }
        customerCouponList.forEach( e -> e.setStatus(CouponUseStatusEnum.WAITING));
        customerCouponMapper.batchUpdateById(customerCouponList);
        Date useTimeEnd = customerCouponList.get(0).getUseTimeEnd();
        cacheQueueService.addDelayQueue(CacheQueueConstant.CUSTOMER_COUPON_EXPIRE_QUEUE_TYPE, messageContent, useTimeEnd.getTime() - DateUtils.getNowDate().getTime());
    }

    @Override
    public String messageType() {
        return CUSTOMER_COUPON_UNUSED_QUEUE_TYPE;
    }
}
