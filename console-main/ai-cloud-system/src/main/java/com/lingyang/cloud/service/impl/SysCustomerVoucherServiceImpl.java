package com.lingyang.cloud.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.entity.SysCustomerAmountLogEntity;
import com.lingyang.cloud.entity.SysCustomerVoucherEntity;
import com.lingyang.cloud.entity.SysCustomerVoucherUseLogEntity;
import com.lingyang.cloud.enums.customer.SysCustomerAmountType;
import com.lingyang.cloud.enums.customer.SysTransactionType;
import com.lingyang.cloud.enums.system.StatusEnum;
import com.lingyang.cloud.mapper.SysCustomerAmountLogMapper;
import com.lingyang.cloud.mapper.SysCustomerVoucherMapper;
import com.lingyang.cloud.mapper.SysCustomerVoucherUseLogMapper;
import com.lingyang.cloud.service.SysCustomerVoucherService;
import com.lingyang.common.cache.CacheQueueService;
import com.lingyang.common.core.exception.http.HttpServiceException;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.utils.DateUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Stream;

import static com.lingyang.cloud.common.constant.CacheQueueConstant.CUSTOMER_BILL_HANDLER_QUEUE_TYPE;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/10/30 15:17
 */
@Service
public class SysCustomerVoucherServiceImpl implements SysCustomerVoucherService {

    @Resource
    private SysCustomerVoucherMapper sysCustomerVoucherMapper;
    @Resource
    private SysCustomerVoucherUseLogMapper sysCustomerVoucherUseLogMapper;
    @Resource
    private SysCustomerAmountLogMapper sysCustomerAmountLogMapper;
    @Resource
    private CacheQueueService cacheQueueService;

    @Override
    public PageResult<SysCustomerVoucherEntity> getPage(PageQuery<SysCustomerVoucherEntity> pageQuery) {
        pageQuery.startPage();
        SysCustomerVoucherEntity query = pageQuery.getQuery();
        LambdaQueryWrapper<SysCustomerVoucherEntity> wrapper = Wrappers.lambdaQuery(SysCustomerVoucherEntity.class)
                .eq(query.getCustomerId() != null, SysCustomerVoucherEntity::getCustomerId, query.getCustomerId())
                .orderByAsc(SysCustomerVoucherEntity::getUseTimeEnd);
        return PageResult.of(sysCustomerVoucherMapper.selectList(wrapper));
    }

    @Override
    public Boolean addCustomerVoucher(SysCustomerVoucherEntity entity) {
        BigDecimal userVoucherBalance = getUserVoucherBalance(entity.getCustomerId());
        boolean flag = sysCustomerVoucherMapper.insert(entity) > 0;
        if (flag) {
            SysCustomerAmountLogEntity amountLogEntity = new SysCustomerAmountLogEntity();
            amountLogEntity.setCustomerId(entity.getCustomerId());
            amountLogEntity.setAmountType(SysCustomerAmountType.VOUCHER);
            amountLogEntity.setTransactionType(SysTransactionType.PLATFORM_GRANT);
            amountLogEntity.setBeforeAmount(userVoucherBalance);
            amountLogEntity.setAmount(entity.getTotalAmount());
            amountLogEntity.setAfterAmount(userVoucherBalance.add(entity.getTotalAmount()));
            amountLogEntity.setCreateTime(DateUtils.getNowDate());
            sysCustomerAmountLogMapper.insert(amountLogEntity);
            // 发布账单处理
            cacheQueueService.addDelayQueue(CUSTOMER_BILL_HANDLER_QUEUE_TYPE, entity.getCustomerId().toString(), 5);
        }
        return flag;
    }

    @Override
    public BigDecimal getUserVoucherBalance(Long customerId) {
        Date nowDate = DateUtils.getNowDate();
        List<SysCustomerVoucherEntity> entityList = sysCustomerVoucherMapper.selectList(Wrappers.lambdaQuery(SysCustomerVoucherEntity.class)
                .eq(SysCustomerVoucherEntity::getCustomerId, customerId)
                .eq(SysCustomerVoucherEntity::getStatus, StatusEnum.OK)
                .le(SysCustomerVoucherEntity::getUseTimeStart, nowDate)
                .ge(SysCustomerVoucherEntity::getUseTimeEnd, nowDate));
        if (ObjectUtils.isEmpty(entityList)) {
            return BigDecimal.ZERO;
        }
        return entityList.stream()
                .flatMap(entity -> Stream.of(entity.getTotalAmount().subtract(entity.getUseAmount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public Map<Long, BigDecimal> batchUserVoucherBalance(Collection<Long> userIdList) {
        Date nowDate = DateUtils.getNowDate();
        List<SysCustomerVoucherEntity> entityList = sysCustomerVoucherMapper.selectList(Wrappers.lambdaQuery(SysCustomerVoucherEntity.class)
                .in(SysCustomerVoucherEntity::getCustomerId, userIdList)
                .eq(SysCustomerVoucherEntity::getStatus, StatusEnum.OK)
                .le(SysCustomerVoucherEntity::getUseTimeStart, nowDate)
                .ge(SysCustomerVoucherEntity::getUseTimeEnd, nowDate));
        Map<Long, BigDecimal> resultMap = new HashMap<>(userIdList.size());
        for (Long userId : userIdList) {
            resultMap.putIfAbsent(userId, BigDecimal.ZERO);
        }
        if (ObjectUtils.isEmpty(entityList)) {
            return resultMap;
        }
        for (SysCustomerVoucherEntity entity : entityList) {
            resultMap.put(entity.getCustomerId(), resultMap.getOrDefault(entity.getCustomerId(), BigDecimal.ZERO).add(entity.getTotalAmount().subtract(entity.getUseAmount())));
        }
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void useVoucher(Long orderId, String orderNo, BigDecimal voucherAmount, Long customerId) {
        if (voucherAmount == null || voucherAmount.compareTo(BigDecimal.ZERO) < 0) {
            return;
        }
        Date nowDate = DateUtils.getNowDate();
        List<SysCustomerVoucherEntity> entityList = sysCustomerVoucherMapper.selectList(Wrappers.lambdaQuery(SysCustomerVoucherEntity.class)
                .eq(SysCustomerVoucherEntity::getCustomerId, customerId)
                .eq(SysCustomerVoucherEntity::getStatus, StatusEnum.OK)
                .le(SysCustomerVoucherEntity::getUseTimeStart, nowDate)
                .ge(SysCustomerVoucherEntity::getUseTimeEnd, nowDate)
                .orderByAsc(SysCustomerVoucherEntity::getUseTimeEnd));
        if (ObjectUtils.isEmpty(entityList)) {
            throw new HttpServiceException("代金卷不足，无法抵扣");
        }
        BigDecimal userVoucherBalance = getUserVoucherBalance(customerId);
        SysCustomerAmountLogEntity amountLogEntity = new SysCustomerAmountLogEntity();
        amountLogEntity.setOrderId(orderId);
        amountLogEntity.setOrderNo(orderNo);
        amountLogEntity.setCustomerId(customerId);
        amountLogEntity.setAmountType(SysCustomerAmountType.VOUCHER);
        amountLogEntity.setTransactionType(SysTransactionType.PAY_DISCOUNT);
        amountLogEntity.setBeforeAmount(userVoucherBalance);
        amountLogEntity.setAmount(voucherAmount);
        amountLogEntity.setAfterAmount(userVoucherBalance.subtract(voucherAmount));
        amountLogEntity.setCreateTime(nowDate);
        sysCustomerAmountLogMapper.insert(amountLogEntity);

        List<SysCustomerVoucherUseLogEntity> useLogList = new ArrayList<>(entityList.size());
        List<SysCustomerVoucherEntity> updateEntitList = new ArrayList<>(entityList.size());
        for (SysCustomerVoucherEntity entity : entityList) {
            if (voucherAmount.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }
            BigDecimal toBeUseAmount = entity.getTotalAmount().subtract(entity.getUseAmount());
            if (toBeUseAmount.compareTo(BigDecimal.ZERO) < 0) {
                continue;
            }
            SysCustomerVoucherUseLogEntity logEntity = new SysCustomerVoucherUseLogEntity();
            logEntity.setCustomerVoucherId(entity.getId());
            logEntity.setOrderId(orderId);
            if (toBeUseAmount.compareTo(voucherAmount) >= 0) {
                entity.setUseAmount(entity.getUseAmount().add(voucherAmount));
                logEntity.setUseAmount(voucherAmount);
                voucherAmount = BigDecimal.ZERO;
            } else {
                entity.setUseAmount(entity.getTotalAmount());
                voucherAmount = voucherAmount.subtract(toBeUseAmount);
                logEntity.setUseAmount(toBeUseAmount);
            }
            updateEntitList.add(entity);
            useLogList.add(logEntity);
        }
        if (ObjectUtils.isNotEmpty(updateEntitList)) {
            sysCustomerVoucherMapper.batchUpdateById(updateEntitList);
            sysCustomerVoucherUseLogMapper.batchInsert(useLogList);
        }
    }
}
