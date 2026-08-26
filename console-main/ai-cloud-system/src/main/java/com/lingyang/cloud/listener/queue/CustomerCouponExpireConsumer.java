package com.lingyang.cloud.listener.queue;

import com.alibaba.fastjson2.JSONArray;
import com.lingyang.cloud.common.constant.CacheQueueConstant;
import com.lingyang.cloud.entity.SysCustomerCouponEntity;
import com.lingyang.cloud.enums.coupon.CouponUseStatusEnum;
import com.lingyang.cloud.mapper.SysCustomerCouponMapper;
import com.lingyang.common.cache.queue.CacheQueueConsumer;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/18 20:52
 */
@Service
@Slf4j
public class CustomerCouponExpireConsumer implements CacheQueueConsumer {
    @Resource
    private SysCustomerCouponMapper customerCouponMapper;

    @Override
    public void invoke(String messageContent) {
        log.info("监听到客户优惠卷到期处理");
        List<SysCustomerCouponEntity> customerCouponList = JSONArray.parseArray(messageContent, SysCustomerCouponEntity.class);
        if (ObjectUtils.isEmpty(customerCouponList)) {
            return;
        }
        customerCouponList.forEach( e -> e.setStatus(CouponUseStatusEnum.EXPIRED));
        customerCouponMapper.batchUpdateById(customerCouponList);
    }

    @Override
    public String messageType() {
        return CacheQueueConstant.CUSTOMER_COUPON_EXPIRE_QUEUE_TYPE;
    }
}
