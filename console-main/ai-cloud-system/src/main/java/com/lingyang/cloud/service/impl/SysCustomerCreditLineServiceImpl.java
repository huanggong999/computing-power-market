package com.lingyang.cloud.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.entity.SysCustomerAmountLogEntity;
import com.lingyang.cloud.entity.SysCustomerCreditLineEntity;
import com.lingyang.cloud.entity.SysCustomerCreditLineUseLogEntity;
import com.lingyang.cloud.enums.customer.SysCustomerAmountType;
import com.lingyang.cloud.enums.customer.SysTransactionType;
import com.lingyang.cloud.enums.system.StatusEnum;
import com.lingyang.cloud.mapper.SysCustomerAmountLogMapper;
import com.lingyang.cloud.mapper.SysCustomerCreditLineMapper;
import com.lingyang.cloud.mapper.SysCustomerCreditLineUseLogMapper;
import com.lingyang.cloud.service.SysCustomerCreditLineService;
import com.lingyang.common.cache.CacheQueueService;
import com.lingyang.common.core.exception.http.HttpServiceException;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.utils.DateUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Stream;

import static com.lingyang.cloud.common.constant.CacheQueueConstant.CUSTOMER_BILL_HANDLER_QUEUE_TYPE;

/**
 * @author 吴思镇
 */
@Service
public class SysCustomerCreditLineServiceImpl implements SysCustomerCreditLineService {

    @Resource
    private SysCustomerCreditLineMapper sysCustomerCreditLineMapper;
    @Resource
    private SysCustomerCreditLineUseLogMapper sysCustomerCreditLineUseLogMapper;

    @Resource
    private SysCustomerAmountLogMapper sysCustomerAmountLogMapper;

    @Resource
    private CacheQueueService cacheQueueService;

    @Override
    public Boolean addCustomerCreditLine(SysCustomerCreditLineEntity entity) {

        BigDecimal userVoucherBalance = getCreditAmount(entity.getCustomerId());
        boolean flag = sysCustomerCreditLineMapper.insert(entity) > 0;
        if (flag) {
            SysCustomerAmountLogEntity amountLogEntity = new SysCustomerAmountLogEntity();
            amountLogEntity.setCustomerId(entity.getCustomerId());
            amountLogEntity.setAmountType(SysCustomerAmountType.CREDIT);
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
    public BigDecimal getCreditAmount(Long customerId) {
        Date nowDate = DateUtils.getNowDate();
        List<SysCustomerCreditLineEntity> entityList = sysCustomerCreditLineMapper.selectList(Wrappers.lambdaQuery(SysCustomerCreditLineEntity.class)
                .eq(SysCustomerCreditLineEntity::getCustomerId, customerId)
                .eq(SysCustomerCreditLineEntity::getStatus, StatusEnum.OK)
                .le(SysCustomerCreditLineEntity::getUseTimeStart, nowDate)
                .ge(SysCustomerCreditLineEntity::getUseTimeEnd, nowDate));
        if (ObjectUtils.isEmpty(entityList)) {
            return BigDecimal.ZERO;
        }
        return entityList.stream()
                .flatMap(entity -> Stream.of(entity.getTotalAmount().subtract(entity.getUseAmount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public PageResult<SysCustomerCreditLineEntity> getPage(PageQuery<SysCustomerCreditLineEntity> pageQuery) {
        pageQuery.startPage();
        SysCustomerCreditLineEntity query = pageQuery.getQuery();
        LambdaQueryWrapper<SysCustomerCreditLineEntity> wrapper = Wrappers.lambdaQuery(SysCustomerCreditLineEntity.class)
                .eq(query.getCustomerId() != null, SysCustomerCreditLineEntity::getCustomerId, query.getCustomerId())
                .orderByAsc(SysCustomerCreditLineEntity::getUseTimeEnd);
        return PageResult.of(sysCustomerCreditLineMapper.selectList(wrapper));
    }

    @Override
    public Map<Long, BigDecimal> batchUserCreditLine(Set<Long> userIdList) {
        Date nowDate = DateUtils.getNowDate();
        List<SysCustomerCreditLineEntity> entityList = sysCustomerCreditLineMapper.selectList(Wrappers.lambdaQuery(SysCustomerCreditLineEntity.class)
                .in(SysCustomerCreditLineEntity::getCustomerId, userIdList)
                .eq(SysCustomerCreditLineEntity::getStatus, StatusEnum.OK)
                .le(SysCustomerCreditLineEntity::getUseTimeStart, nowDate)
                .ge(SysCustomerCreditLineEntity::getUseTimeEnd, nowDate));
        Map<Long, BigDecimal> resultMap = new HashMap<>(userIdList.size());
        for (Long userId : userIdList) {
            resultMap.putIfAbsent(userId, BigDecimal.ZERO);
        }
        if (ObjectUtils.isEmpty(entityList)) {
            return resultMap;
        }
        for (SysCustomerCreditLineEntity entity : entityList) {
            resultMap.put(entity.getCustomerId(), resultMap.getOrDefault(entity.getCustomerId(), BigDecimal.ZERO).add(entity.getTotalAmount().subtract(entity.getUseAmount())));
        }
        return resultMap;
    }

    @Override
    public void useCreditLine(Long orderId, String orderNo, BigDecimal creditLineAmount, Long customerId) {
        if (creditLineAmount == null || creditLineAmount.compareTo(BigDecimal.ZERO) < 0) {
            return;
        }
        Date nowDate = DateUtils.getNowDate();
        List<SysCustomerCreditLineEntity> entityList = sysCustomerCreditLineMapper.selectList(Wrappers.lambdaQuery(SysCustomerCreditLineEntity.class)
                .eq(SysCustomerCreditLineEntity::getCustomerId, customerId)
                .eq(SysCustomerCreditLineEntity::getStatus, StatusEnum.OK)
                .le(SysCustomerCreditLineEntity::getUseTimeStart, nowDate)
                .ge(SysCustomerCreditLineEntity::getUseTimeEnd, nowDate)
                .orderByAsc(SysCustomerCreditLineEntity::getUseTimeEnd));
        if (ObjectUtils.isEmpty(entityList)) {
            throw new HttpServiceException("授信额不足，无法抵扣");
        }
        BigDecimal userCreditLineBalance = getCreditAmount(customerId);
        SysCustomerAmountLogEntity amountLogEntity = new SysCustomerAmountLogEntity();
        amountLogEntity.setOrderId(orderId);
        amountLogEntity.setOrderNo(orderNo);
        amountLogEntity.setCustomerId(customerId);
        amountLogEntity.setAmountType(SysCustomerAmountType.CREDIT);
        amountLogEntity.setTransactionType(SysTransactionType.PAY_DISCOUNT);
        amountLogEntity.setBeforeAmount(userCreditLineBalance);
        amountLogEntity.setAmount(creditLineAmount);
        amountLogEntity.setAfterAmount(userCreditLineBalance.subtract(creditLineAmount));
        amountLogEntity.setCreateTime(nowDate);
        sysCustomerAmountLogMapper.insert(amountLogEntity);

        List<SysCustomerCreditLineUseLogEntity> useLogList = new ArrayList<>(entityList.size());
        List<SysCustomerCreditLineEntity> updateEntitList = new ArrayList<>(entityList.size());
        for (SysCustomerCreditLineEntity entity : entityList) {
            if (creditLineAmount.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }
            BigDecimal toBeUseAmount = entity.getTotalAmount().subtract(entity.getUseAmount());
            if (toBeUseAmount.compareTo(BigDecimal.ZERO) < 0) {
                continue;
            }
            SysCustomerCreditLineUseLogEntity logEntity = new SysCustomerCreditLineUseLogEntity();
            logEntity.setCustomerCreditId(entity.getId());
            logEntity.setOrderId(orderId);
            if (toBeUseAmount.compareTo(creditLineAmount) >= 0) {
                entity.setUseAmount(entity.getUseAmount().add(creditLineAmount));
                logEntity.setUseAmount(creditLineAmount);
                creditLineAmount = BigDecimal.ZERO;
            } else {
                entity.setUseAmount(entity.getTotalAmount());
                creditLineAmount = creditLineAmount.subtract(toBeUseAmount);
                logEntity.setUseAmount(toBeUseAmount);
            }
            updateEntitList.add(entity);
            useLogList.add(logEntity);
        }
        if (ObjectUtils.isNotEmpty(updateEntitList)) {
            sysCustomerCreditLineMapper.batchUpdateById(updateEntitList);
            sysCustomerCreditLineUseLogMapper.batchInsert(useLogList);
        }
    }
}
