package com.lingyang.cloud.listener.queue;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.api.service.pc.PcConsoleService;
import com.lingyang.cloud.entity.SysCustomerBillEntity;
import com.lingyang.cloud.entity.SysCustomerEntity;
import com.lingyang.cloud.entity.SysNetworkValueEntity;
import com.lingyang.cloud.entity.SysOrderEntity;
import com.lingyang.cloud.enums.customer.SysTransactionType;
import com.lingyang.cloud.enums.order.OrderStatusEnum;
import com.lingyang.cloud.enums.source.SourceTypeEnum;
import com.lingyang.cloud.mapper.*;
import com.lingyang.cloud.model.vo.feilian.UpdateUserStatusVO;
import com.lingyang.cloud.model.vo.product.SysOpenProductUserPwdVO;
import com.lingyang.cloud.service.SysConfigService;
import com.lingyang.cloud.service.SysCustomerCreditLineService;
import com.lingyang.cloud.service.SysCustomerService;
import com.lingyang.cloud.service.SysCustomerVoucherService;
import com.lingyang.cloud.utils.FeiLianUtils;
import com.lingyang.common.cache.CacheQueueService;
import com.lingyang.common.cache.queue.CacheQueueConsumer;
import com.lingyang.common.core.utils.Builder;
import com.lingyang.common.core.utils.DateUtils;
import com.lingyang.common.core.utils.IdUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.lingyang.cloud.common.constant.CacheQueueConstant.CUSTOMER_NEW_DAY_BILL_PRODUCT_QUEUE_TYPE;
import static com.lingyang.cloud.enums.order.OrderTypeEnum.PRODUCT;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/06/03 15:06
 */
@Service
@Slf4j
public class CustomerNewProductDayBillQueueConsumer implements CacheQueueConsumer {
    @Resource
    private CacheQueueService cacheQueueService;
    @Resource
    private SysConfigService sysConfigService;
    @Resource
    private SysCustomerDiscountMapper sysCustomerDiscountMapper;
    @Resource
    private SysCustomerBillMapper sysCustomerBillMapper;
    @Resource
    private SysCustomerVoucherService sysCustomerVoucherService;
    @Resource
    private SysCustomerCreditLineService sysCustomerCreditLineService;
    @Resource
    private SysCustomerMapper sysCustomerMapper;
    @Resource
    private PcConsoleService pcConsoleService;
    @Resource
    private SysCustomerService sysCustomerService;
    @Resource
    private SysNetworkValueMapper sysNetworkValueMapper;
    @Resource
    private SysNetworkProductMapper sysNetworkProductMapper;
    @Resource
    private SysOrderMapper sysOrderMapper;
    @Resource
    private FeiLianUtils flashLianUtils;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void invoke(String messageContent) throws Exception {
        log.info("进行AGIC按天计费账单结算,valueId:{}",messageContent);
        SysNetworkValueEntity sysNetworkValue = sysNetworkValueMapper.selectById(Long.parseLong(messageContent));
        SysOrderEntity sysOrder = sysOrderMapper.selectOne(Wrappers.lambdaQuery(SysOrderEntity.class)
                .eq(SysOrderEntity::getNetworkValueId, sysNetworkValue.getId())
                .eq(SysOrderEntity::getOrderType,PRODUCT));
//        List<SysOrderEntity> sysOrderList = sysOrderMapper.selectList(Wrappers.lambdaQuery(SysOrderEntity.class)
//                .eq(SysOrderEntity::getNetworkValueId, sysNetworkValue.getId())
//                .eq(SysOrderEntity::getOrderType,UPGRADE_PRODUCT));
        SysCustomerBillEntity sysCustomerBillEntity = Builder.of(SysCustomerBillEntity::new)
                .set(SysCustomerBillEntity::setId, IdUtils.nextId())
                .set(SysCustomerBillEntity::setBill, DateUtils.parseDateToStr("yyyy-MM", new Date()))
                .set(SysCustomerBillEntity::setCustomerId, sysNetworkValue.getUserId())
                .set(SysCustomerBillEntity::setBillNo, UUID.randomUUID().toString())
                .set(SysCustomerBillEntity::setBillDate, new Date())
                .set(SysCustomerBillEntity::setSourceId, sysOrder.getId())
                .set(SysCustomerBillEntity::setSourceType, SourceTypeEnum.AGIC)
                .set(SysCustomerBillEntity::setChargeType,sysNetworkValue.getChargeType())
                .set(SysCustomerBillEntity::setChargeUnit, "天")
                .set(SysCustomerBillEntity::setBillType,"消费-使用")
                .set(SysCustomerBillEntity::setSettleType,"结算")
                .set(SysCustomerBillEntity::setInstanceId, sysNetworkValue.getProductId().toString())
                .set(SysCustomerBillEntity::setInstanceName, sysNetworkProductMapper.selectById(sysNetworkValue.getProductId()).getName())
                .set(SysCustomerBillEntity::setUnitId, "")
                .set(SysCustomerBillEntity::setRegion, "")
                .set(SysCustomerBillEntity::setZone, "")
                .set(SysCustomerBillEntity::setPriceType, "按天计费")
                .set(SysCustomerBillEntity::setPrice, sysNetworkValue.getUnitPrice())
                .set(SysCustomerBillEntity::setUsage, BigDecimal.valueOf(sysNetworkValue.getNetworkCount()))
                .set(SysCustomerBillEntity::setUsageUnit, "个")
                .set(SysCustomerBillEntity::setOriginalPrice, sysNetworkValue.getOriginalAmount())
                .set(SysCustomerBillEntity::setPremiumPrice,sysOrder.getPremiumPrice())
                .set(SysCustomerBillEntity::setUserDiscountAmount, sysOrder.getUserDiscountAmount())
                .set(SysCustomerBillEntity::setPayPrice, sysNetworkValue.getPayAmount())
                .set(SysCustomerBillEntity::setVoucherAmount, sysOrder.getVoucherAmount())
                .set(SysCustomerBillEntity::setCreditLineAmount, sysOrder.getCreditLineAmount())
                .set(SysCustomerBillEntity::setBalancePayAmount, sysOrder.getBalancePayAmount())
                .set(SysCustomerBillEntity::setBillStartTime,new Date())
                .set(SysCustomerBillEntity::setPayStatus,OrderStatusEnum.UNPAID)
                .set(SysCustomerBillEntity::setArrearsAmount,BigDecimal.ZERO)
                .build();
//        if (sysOrderList != null) {
//            for (SysOrderEntity sysOrderEntity : sysOrderList) {
//                sysCustomerBillEntity.setPayPrice(sysCustomerBillEntity.getPayPrice().add(sysOrderEntity.getFinalPayAmount()));
//            }
//        }
        List<SysCustomerBillEntity> instanceNoBillEntitList = List.of(sysCustomerBillEntity);
        // 处理代金卷
        Map<Long, BigDecimal> customerVoucherBalanceMap = sysCustomerVoucherService.batchUserVoucherBalance(Set.of(sysCustomerBillEntity.getCustomerId()));
        instanceNoBillEntitList.forEach(entity -> {
            BigDecimal customerVoucherAmount = customerVoucherBalanceMap.get(entity.getCustomerId());
            if (!(ObjectUtils.isEmpty(customerVoucherAmount) ||
                    entity.getPayStatus().equals(OrderStatusEnum.PAID) ||
                    entity.getPayPrice().compareTo(BigDecimal.ZERO) == 0 ||
                    customerVoucherAmount.compareTo(BigDecimal.ZERO) == 0)) {
                if (customerVoucherAmount.compareTo(entity.getPayPrice()) >= 0) {
                    customerVoucherAmount = customerVoucherAmount.subtract(entity.getPayPrice());
                    entity.setPayStatus(OrderStatusEnum.PAID);
                    entity.setPayTime(new Date());
                    entity.setVoucherAmount(entity.getPayPrice());
                    sysCustomerVoucherService.useVoucher(entity.getId(), entity.getBillNo(), entity.getPayPrice(), entity.getCustomerId());
                } else {
                    entity.setVoucherAmount(customerVoucherAmount);
                    entity.setPayStatus(OrderStatusEnum.PAID);
                    sysCustomerVoucherService.useVoucher(entity.getId(), entity.getBillNo(), customerVoucherAmount, entity.getCustomerId());
                    customerVoucherAmount = BigDecimal.ZERO;
                }
                customerVoucherBalanceMap.put(entity.getCustomerId(), customerVoucherAmount);
            }
        });
        // 处理授信额
        Map<Long, BigDecimal> customerCreditLineMap = sysCustomerCreditLineService.batchUserCreditLine(Set.of(sysCustomerBillEntity.getCustomerId()));
        instanceNoBillEntitList.forEach(entity -> {
            BigDecimal customerCreditLine = customerCreditLineMap.get(entity.getCustomerId());
            if (!(ObjectUtils.isEmpty(customerCreditLine) ||
                    entity.getPayStatus().equals(OrderStatusEnum.PAID) ||
                    entity.getPayPrice().compareTo(BigDecimal.ZERO) == 0 ||
                    customerCreditLine.compareTo(BigDecimal.ZERO) == 0)) {
                BigDecimal payPrice = entity.getPayPrice().subtract(entity.getVoucherAmount());
                if (customerCreditLine.compareTo(payPrice) >= 0) {
                    customerCreditLine = customerCreditLine.subtract(payPrice);
                    entity.setPayStatus(OrderStatusEnum.PAID);
                    entity.setPayTime(new Date());
                    entity.setCreditLineAmount(payPrice);
                    sysCustomerCreditLineService.useCreditLine(entity.getId(), entity.getBillNo(), entity.getPayPrice(), entity.getCustomerId());
                } else {
                    entity.setCreditLineAmount(customerCreditLine);
                    entity.setPayStatus(OrderStatusEnum.PAID);
                    sysCustomerCreditLineService.useCreditLine(entity.getId(), entity.getBillNo(), customerCreditLine, entity.getCustomerId());
                    customerCreditLine = BigDecimal.ZERO;
                }
                customerCreditLineMap.put(entity.getCustomerId(), customerCreditLine);
            }
        });
        // 处理余额
        List<SysCustomerEntity> customerList = sysCustomerMapper.selectList(Wrappers.lambdaQuery(SysCustomerEntity.class)
                .in(SysCustomerEntity::getId, Set.of(sysCustomerBillEntity.getCustomerId())));
        Map<Long, BigDecimal> customerBalanceMap = customerList.stream().collect(Collectors.toMap(SysCustomerEntity::getId, SysCustomerEntity::getBalance));
        instanceNoBillEntitList.forEach(entity -> {
            BigDecimal customerBalance = customerBalanceMap.get(entity.getCustomerId());
            if (!(entity.getPayStatus().equals(OrderStatusEnum.PAID) ||
                    customerBalance == null ||
                    customerBalance.compareTo(BigDecimal.ZERO) == 0)) {
                BigDecimal payPrice = entity.getPayPrice()
                        .subtract(entity.getVoucherAmount() != null ? entity.getVoucherAmount() : BigDecimal.ZERO)
                        .subtract(entity.getCreditLineAmount() != null ? entity.getCreditLineAmount() : BigDecimal.ZERO);
                if (customerBalance.compareTo(payPrice) >= 0) {
                    customerBalance = customerBalance.subtract(payPrice);
                    entity.setBalancePayAmount(payPrice);
                    entity.setPayStatus(OrderStatusEnum.PAID);
                    entity.setPayTime(new Date());
                    sysCustomerService.updateCustomerBalance(entity.getId(), entity.getBillNo(), entity.getCustomerId(), payPrice, SysTransactionType.PAY_DISCOUNT);
                } else {
                    entity.setBalancePayAmount(customerBalance);
                    customerBalance = BigDecimal.ZERO;
                    sysCustomerService.updateCustomerBalance(entity.getId(), entity.getBillNo(), entity.getCustomerId(), customerBalance, SysTransactionType.PAY_DISCOUNT);
                }
                customerBalanceMap.put(entity.getCustomerId(), customerBalance);
            }
            if (entity.getPayStatus().equals(OrderStatusEnum.UNPAID)) {
                // 修复方案：添加空值检查
                BigDecimal voucherAmount = entity.getVoucherAmount() != null ? entity.getVoucherAmount() : BigDecimal.ZERO;
                BigDecimal creditLineAmount = entity.getCreditLineAmount() != null ? entity.getCreditLineAmount() : BigDecimal.ZERO;
                BigDecimal balancePayAmount = entity.getBalancePayAmount() != null ? entity.getBalancePayAmount() : BigDecimal.ZERO;

                entity.setArrearsAmount(entity.getPayPrice().subtract(voucherAmount).subtract(creditLineAmount).subtract(balancePayAmount));
            }
            if (entity.getArrearsAmount().compareTo(BigDecimal.ZERO) == 0) {
                entity.setPayStatus(OrderStatusEnum.PAID);
            }
        });

        List<Long> orderIds = instanceNoBillEntitList.stream()
                .filter(entity -> entity.getPayStatus().equals(OrderStatusEnum.UNPAID))
                .map((SysCustomerBillEntity::getSourceId)).toList();

        // 未支付的AGIC，则停止
        if (ObjectUtils.isNotEmpty(orderIds)) {
            for (Long orderId : orderIds) {
                SysOrderEntity sysOrderEntity = sysOrderMapper.selectById(orderId);
                sysOrderEntity.setActualStatus(3);
                sysOrderEntity.setActualAgiExpireTime(new Date());
                sysOrderMapper.updateById(sysOrderEntity);

                SysNetworkValueEntity sysNetworkValueEntity = sysNetworkValueMapper.selectOne(Wrappers.lambdaQuery(SysNetworkValueEntity.class)
                        .eq(SysNetworkValueEntity::getId, sysOrderEntity.getNetworkValueId()));
                sysNetworkValueEntity.setActualStatus(4);
//                sysNetworkValueEntity.setActualAgiExpireTime(new Date());
                sysNetworkValueMapper.updateById(sysNetworkValueEntity);

                //修改用户状态
                List<SysOpenProductUserPwdVO> userPwdList = JSON.parseArray(sysNetworkValueEntity.getUserpwdList().toJSONString(), SysOpenProductUserPwdVO.class);
                for (SysOpenProductUserPwdVO sysOpenProductUserPwdVO : userPwdList) {
                    String userId = flashLianUtils.getUserInfo(sysOpenProductUserPwdVO.getEmail()).get("id").toString();
                    UpdateUserStatusVO vo = new UpdateUserStatusVO();
                    vo.setId(userId);
                    vo.setStatus("disable");
                    flashLianUtils.updateUserStatus(vo);
                }
            }
        }

        List<Long> orderId2s = instanceNoBillEntitList.stream()
                .filter(entity -> entity.getPayStatus().equals(OrderStatusEnum.PAID))
                .map((SysCustomerBillEntity::getSourceId)).toList();
        if (ObjectUtils.isNotEmpty(orderId2s)) {
            for (Long orderId : orderId2s) {
                SysOrderEntity sysOrderEntity = sysOrderMapper.selectById(orderId);
                SysNetworkValueEntity sysNetworkValueEntity = sysNetworkValueMapper.selectOne(Wrappers.lambdaQuery(SysNetworkValueEntity.class)
                        .eq(SysNetworkValueEntity::getId, sysOrderEntity.getNetworkValueId()));
                if (sysNetworkValueEntity.getActualStatus() == 2){
                    sysNetworkValueEntity.setActualAgiExpireTime(DateUtils.addDays(sysNetworkValueEntity.getActualAgiExpireTime(),1));
                    sysNetworkValueMapper.updateById(sysNetworkValueEntity);
                    cacheQueueService.addDelayQueue(CUSTOMER_NEW_DAY_BILL_PRODUCT_QUEUE_TYPE, messageContent, 60 * 60 * 24);
                }
            }

        }

        // 入库
        sysCustomerBillMapper.batchInsert(instanceNoBillEntitList);
        log.info("账单处理完成");
    }

    @Override
    public String messageType() {
        return CUSTOMER_NEW_DAY_BILL_PRODUCT_QUEUE_TYPE;
    }
}