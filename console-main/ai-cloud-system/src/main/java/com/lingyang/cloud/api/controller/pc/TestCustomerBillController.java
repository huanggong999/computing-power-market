package com.lingyang.cloud.api.controller.pc;

import com.lingyang.cloud.entity.SysCustomerBillEntity;
import com.lingyang.cloud.entity.SysNetworkValueEntity;
import com.lingyang.cloud.entity.SysOrderEntity;
import com.lingyang.cloud.enums.order.OrderStatusEnum;
import com.lingyang.cloud.enums.source.SourceTypeEnum;
import com.lingyang.cloud.mapper.*;
import com.lingyang.common.core.utils.DateUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @Author: 吴思镇
 * @CreateTime: 2025-10-28
 * @Version: 1.0
 */
@RestController
@RequestMapping("/pc/customer/bill/test")
@Slf4j
public class TestCustomerBillController {

    @Autowired
    private SysOrderMapper sysOrderMapper;

    @Autowired
    private SysNetworkValueMapper sysNetworkValueMapper;

    @Resource
    private SysCustomerBillMapper sysCustomerBillMapper;

    @Autowired
    private SysNetworkProductMapper sysNetworkProductMapper;


    @RequestMapping("/test1")
    public void test1(@RequestParam Long customerId) {
        // 查询产品订单
        List<SysOrderEntity> sysOrders = sysOrderMapper.getTestList(customerId);
        List<SysCustomerBillEntity> sysCustomerBills = new ArrayList<>();
        for (SysOrderEntity sysOrder : sysOrders) {
            SysNetworkValueEntity sysNetworkValueEntity = sysNetworkValueMapper.selectById(sysOrder.getNetworkValueId());
            // 不是授信额支付的就直接当天计算生成账单
            if (sysNetworkValueEntity != null){
                SysCustomerBillEntity billEntity = new SysCustomerBillEntity();
                billEntity.setCustomerId(sysOrder.getCreateById());
                billEntity.setBill(DateUtils.parseDateToStr("yyyy-MM", DateUtils.addDays(sysOrder.getPayTime(), 1)));
                billEntity.setBillNo(UUID.randomUUID().toString());
                billEntity.setBillDate(DateUtils.addDays(sysOrder.getPayTime(), 1));
                billEntity.setSourceType(SourceTypeEnum.AGIC);
                billEntity.setSourceId(sysOrder.getId());
                billEntity.setChargeType(sysNetworkValueEntity.getChargeType());
                billEntity.setDuration(sysNetworkValueEntity.getDuration());
                billEntity.setChargeUnit(ObjectUtils.isNotEmpty(sysNetworkValueEntity.getDurationUnit())?sysNetworkValueEntity.getDurationUnit().getDesc():null);
                billEntity.setBillType("消费-使用");
                billEntity.setSettleType("结算");
                billEntity.setInstanceId(sysOrder.getNetworkProductId().toString());
                billEntity.setInstanceName(sysNetworkProductMapper.selectById(sysOrder.getNetworkProductId()).getName());
                billEntity.setUnitId("");
                billEntity.setRegion("");
                billEntity.setZone("");
                billEntity.setPriceType("一次性付款");
                billEntity.setPrice(sysOrder.getFinalPayAmount());
                billEntity.setUsage(BigDecimal.valueOf(sysOrder.getNetworkCount()));
                billEntity.setUsageUnit("个");
                billEntity.setOriginalPrice(sysOrder.getOriginalPrice());
                billEntity.setPremiumPrice(sysOrder.getPremiumPrice());
                billEntity.setUserDiscountAmount(sysOrder.getUserDiscountAmount());
                billEntity.setPayPrice(sysOrder.getFinalPayAmount());
                billEntity.setVoucherAmount(sysOrder.getVoucherAmount());
                billEntity.setCreditLineAmount(sysOrder.getCreditLineAmount());
                billEntity.setBalancePayAmount(sysOrder.getBalancePayAmount());
//            billEntity.setArrearsAmount();
                billEntity.setPayStatus(OrderStatusEnum.PAID);
                billEntity.setPayTime(sysOrder.getPayTime());
                billEntity.setBillStartTime(new Date());
//            billEntity.setBillEndTime();
                sysCustomerBills.add(billEntity);
            }
        }
        log.info("生成账单数据量：{}，{}", sysCustomerBills.size(),sysCustomerBills);
        sysCustomerBillMapper.batchInsert(sysCustomerBills);
    }
}
