package com.lingyang.cloud.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.common.constant.CacheQueueConstant;
import com.lingyang.cloud.entity.SysCouponEntity;
import com.lingyang.cloud.entity.SysCustomerCouponEntity;
import com.lingyang.cloud.entity.SysOrderEntity;
import com.lingyang.cloud.enums.coupon.CouponRangeEnum;
import com.lingyang.cloud.enums.coupon.CouponReceiveEnum;
import com.lingyang.cloud.enums.coupon.CouponUseStatusEnum;
import com.lingyang.cloud.enums.order.OrderStatusEnum;
import com.lingyang.cloud.enums.source.SourceTypeEnum;
import com.lingyang.cloud.enums.system.StatusEnum;
import com.lingyang.cloud.mapper.SysCustomerCouponMapper;
import com.lingyang.cloud.mapper.SysOrderMapper;
import com.lingyang.cloud.model.query.coupon.SysCustomerCouponQuery;
import com.lingyang.cloud.model.query.coupon.SysCustomerCouponSourceQuery;
import com.lingyang.cloud.model.vo.customer.SysCustomerCouponListVO;
import com.lingyang.cloud.service.SysCustomerCouponService;
import com.lingyang.common.cache.CacheQueueService;
import com.lingyang.common.core.exception.http.HttpServiceException;
import com.lingyang.common.core.utils.DateUtils;
import com.lingyang.common.core.utils.Optional;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/10/24 11:17
 */
@Slf4j
@Service
public class SysCustomerCouponServiceImpl implements SysCustomerCouponService {
    @Resource
    private SysCustomerCouponMapper sysCustomerCouponMapper;

    @Resource
    private SysOrderMapper sysOrderMapper;
    @Resource
    private CacheQueueService cacheQueueService;

    @Override
    public boolean receiveCoupon(Long userId, List<SysCouponEntity> couponEntity) {
        if (userId == null || ObjectUtils.isEmpty(couponEntity)) {
            return false;
        }
        Date nowDate = DateUtils.getNowDate();
        long nowTime = nowDate.getTime();
        List<SysCustomerCouponEntity> insertList = new ArrayList<>(couponEntity.size());
        for (SysCouponEntity entity : couponEntity) {
            Date useTimeStart = entity.getUseTimeStart();
            Date useTimeEnd = entity.getUseTimeEnd();
            CouponUseStatusEnum statusEnum;
            if (nowTime < useTimeStart.getTime()) {
                statusEnum = CouponUseStatusEnum.UNUSED;
            } else {
                statusEnum = CouponUseStatusEnum.WAITING;
            }
            if (nowTime > useTimeEnd.getTime()) {
                statusEnum = CouponUseStatusEnum.EXPIRED;
            }
            // 新增客户优惠卷
            SysCustomerCouponEntity sysCustomerCouponEntity = new SysCustomerCouponEntity();
            sysCustomerCouponEntity.setCouponId(entity.getId());
            sysCustomerCouponEntity.setCustomerId(userId);
            sysCustomerCouponEntity.setUseTimeStart(useTimeStart);
            sysCustomerCouponEntity.setUseTimeEnd(useTimeEnd);
            sysCustomerCouponEntity.setStatus(statusEnum);
            sysCustomerCouponEntity.setCreateTime(nowDate);
            insertList.add(sysCustomerCouponEntity);
        }
        boolean flag = sysCustomerCouponMapper.batchInsert(insertList);
        if (flag) {
            for (SysCustomerCouponEntity customerCouponEntity : insertList) {
                CouponUseStatusEnum statusEnum = customerCouponEntity.getStatus();
                if (statusEnum.equals(CouponUseStatusEnum.UNUSED)) {
                    cacheQueueService.addDelayQueue(CacheQueueConstant.CUSTOMER_COUPON_UNUSED_QUEUE_TYPE, JSONObject.toJSONString(insertList), customerCouponEntity.getUseTimeStart().getTime() - nowTime);
                } else if (statusEnum.equals(CouponUseStatusEnum.WAITING)) {
                    cacheQueueService.addDelayQueue(CacheQueueConstant.CUSTOMER_COUPON_EXPIRE_QUEUE_TYPE, JSONObject.toJSONString(insertList), customerCouponEntity.getUseTimeEnd().getTime() - nowTime);
                }
            }
        }
        return flag;
    }

    @Override
    public List<SysCustomerCouponListVO> getList(SysCustomerCouponQuery query) {
        Optional.of(query.getSourceList())
                .ifPresent(sourceList -> {
                    Map<SourceTypeEnum, List<SysCustomerCouponSourceQuery>> sourceTypeMap = sourceList.stream().collect(Collectors.groupingBy(SysCustomerCouponSourceQuery::getSourceType));
                    Map<Integer, BigDecimal> priceMap = new HashMap<>(sourceTypeMap.size());
                    for (Map.Entry<SourceTypeEnum, List<SysCustomerCouponSourceQuery>> entry : sourceTypeMap.entrySet()) {
                        CouponRangeEnum couponRangeEnum = getCouponRangeEnum(entry);
                        BigDecimal sourceTotalPrice = entry.getValue().stream().map(SysCustomerCouponSourceQuery::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
                        if (couponRangeEnum != null ) {
                            priceMap.put(couponRangeEnum.getCode(), sourceTotalPrice);
                        }
                        priceMap.put(CouponRangeEnum.ALL.getCode(), priceMap.getOrDefault(CouponRangeEnum.ALL.getCode(), BigDecimal.ZERO).add(sourceTotalPrice));
                    }
                    query.setPriceMap(priceMap);
                });
        log.info("query: {}", query);
        return sysCustomerCouponMapper.getList(query);
    }

    private static CouponRangeEnum getCouponRangeEnum(Map.Entry<SourceTypeEnum, List<SysCustomerCouponSourceQuery>> entry) {
        CouponRangeEnum couponRangeEnum = null;
        switch (entry.getKey()) {
            case ECS -> couponRangeEnum = CouponRangeEnum.SERVER;
            case CR -> couponRangeEnum = CouponRangeEnum.IMAGE_REPOSITORY;
            case CLOUD_NETWORK -> couponRangeEnum = CouponRangeEnum.NETWORK;
            case CONTAINER -> couponRangeEnum = CouponRangeEnum.CONTAINER;
            case CLOUD_OBJECT_STORAGE -> couponRangeEnum = CouponRangeEnum.OBJECT_STORAGE;
            case AGIC -> couponRangeEnum = CouponRangeEnum.AGIC;
        }
        return couponRangeEnum;
    }

    @Override
    public void checkCouponReceive(Long userId,  SysCouponEntity couponEntity) {
        if (!couponEntity.getStatus().equals(StatusEnum.OK)) {
            throw new HttpServiceException("优惠卷未上架，无法领取");
        }
        Date nowDate = DateUtils.getNowDate();
        Date receiveTimeEnd = couponEntity.getReceiveTimeEnd();
        Date receiveTimeStart = couponEntity.getReceiveTimeStart();
        long nowTime = nowDate.getTime();
        if (nowTime < receiveTimeStart.getTime()) {
            throw new HttpServiceException("优惠卷领取时间未开始，无法领取");
        }
        if (nowTime > receiveTimeEnd.getTime()) {
            throw new HttpServiceException("优惠卷领取时间已过，无法领取");
        }
        if (couponEntity.getReceiveType().equals(CouponReceiveEnum.NEW_USER)) {
            Long count = sysOrderMapper.selectCount(Wrappers.lambdaQuery(SysOrderEntity.class)
                    .eq(SysOrderEntity::getCreateById, userId)
                    .eq(SysOrderEntity::getOrderStatus, OrderStatusEnum.PAID));
            if (count != null && count > 0) {
                throw new HttpServiceException("非新用户，无法领取");
            }
        }
        Long count = sysCustomerCouponMapper.selectCount(Wrappers.lambdaQuery(SysCustomerCouponEntity.class)
                .eq(SysCustomerCouponEntity::getCouponId, couponEntity.getId())
                .eq(SysCustomerCouponEntity::getCustomerId, userId));
        if (count != null && count > 0) {
            throw new HttpServiceException("您已领取，无法重复领取");
        }
    }
}
