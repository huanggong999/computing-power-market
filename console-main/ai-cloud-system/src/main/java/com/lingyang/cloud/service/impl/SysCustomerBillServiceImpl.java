package com.lingyang.cloud.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.entity.*;
import com.lingyang.cloud.enums.customer.SysTransactionType;
import com.lingyang.cloud.enums.order.OrderStatusEnum;
import com.lingyang.cloud.enums.source.SourceTypeEnum;
import com.lingyang.cloud.mapper.*;
import com.lingyang.cloud.model.query.customer.SysCustomerBillQuery;
import com.lingyang.cloud.model.vo.customer.SysCustomerBillOverviewVO;
import com.lingyang.cloud.service.SysCustomerBillService;
import com.lingyang.cloud.service.SysCustomerService;
import com.lingyang.cloud.service.SysCustomerVoucherService;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.utils.DateUtils;
import com.lingyang.common.core.utils.Optional;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.lingyang.cloud.enums.source.SourceTypeEnum.ECS;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/12/3 10:37
 */
@Slf4j
@Service
public class SysCustomerBillServiceImpl implements SysCustomerBillService {
    @Resource
    private SysCustomerBillMapper sysCustomerBillMapper;
    @Resource
    private SysCustomerMapper sysCustomerMapper;
    @Resource
    private SysCustomerService sysCustomerService;
    @Resource
    private SysCustomerVoucherService sysCustomerVoucherService;

    @Autowired
    private SysOrderMapper sysOrderMapper;

    @Resource
    private SysOrderSourceMapper sysOrderSourceMapper;

    @Autowired
    private SysNetworkValueMapper sysNetworkValueMapper;

    @Autowired
    private SysNetworkProductMapper sysNetworkProductMapper;


    @Resource
    private SysCustomerInstancesMapper sysCustomerInstancesMapper;

    @Override
    public PageResult<SysCustomerBillOverviewVO> getBillOverviewPage(PageQuery<SysCustomerBillQuery> pageQuery) {
        pageQuery.startPage();
        List<SysCustomerBillOverviewVO> billOverviewPage = sysCustomerBillMapper.getBillOverviewPage(pageQuery.getQuery());
        if (ObjectUtils.isNotEmpty(billOverviewPage)) {
            billOverviewPage.forEach(b -> {
                Date lastDayOfMonth = DateUtils.getLastDayOfMonth(DateUtils.toDate(b.getBill(), "yyyy-MM"));
                if (b.getArrearsAmount().compareTo(BigDecimal.ZERO) > 0 || !b.getBillDate().equals(DateUtils.parseDateToStr("yyyy-MM-dd", lastDayOfMonth))) {
                    b.setStatus("出账中");
                } else {
                    b.setStatus("已结清");
                }
            });
        }
        return PageResult.of(billOverviewPage);
    }

    @Override
    public PageResult<SysCustomerBillEntity> getBillPage(PageQuery<SysCustomerBillQuery> pageQuery) {
        pageQuery.startPage();
        SysCustomerBillQuery query = pageQuery.getQuery();
        return PageResult.of(sysCustomerBillMapper.getList(query));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handlerCustomerBill(Long userId) {
        // 充值，抵扣账单
        List<SysCustomerBillEntity> billList = sysCustomerBillMapper.selectList(Wrappers.lambdaQuery(SysCustomerBillEntity.class)
                .eq(SysCustomerBillEntity::getCustomerId, userId)
                .eq(SysCustomerBillEntity::getPayStatus, OrderStatusEnum.UNPAID)
                .orderByAsc(SysCustomerBillEntity::getBillNo));
        if (ObjectUtils.isEmpty(billList)) {
            return;
        }
        // 获取代金卷
        final BigDecimal[] userVoucherBalance = {sysCustomerVoucherService.getUserVoucherBalance(userId)};
        billList.forEach(b -> {
            BigDecimal payAmount = b.getArrearsAmount();
            if (userVoucherBalance[0].compareTo(BigDecimal.ZERO) > 0) {
                if (payAmount.compareTo(userVoucherBalance[0]) < 1) {
                    userVoucherBalance[0] = userVoucherBalance[0].subtract(payAmount);
                    sysCustomerVoucherService.useVoucher(b.getId(), b.getBillNo(), b.getArrearsAmount(), userId);
                    b.setArrearsAmount(BigDecimal.ZERO);
                    b.setVoucherAmount(payAmount);
                    b.setPayStatus(OrderStatusEnum.PAID);
                    b.setPayTime(DateUtils.getNowDate());
                }else {
                    b.setArrearsAmount(payAmount.subtract(userVoucherBalance[0]));
                    b.setVoucherAmount(userVoucherBalance[0]);
                    sysCustomerVoucherService.useVoucher(b.getId(), b.getBillNo(), userVoucherBalance[0], userId);
                    userVoucherBalance[0] = BigDecimal.ZERO;
                }
            }
        });
        SysCustomerEntity customer = sysCustomerMapper.selectById(userId);
        final BigDecimal[] balance = {customer.getBalance()};
        billList.forEach(b -> {
            if (b.getPayStatus().equals(OrderStatusEnum.UNPAID) && balance[0].compareTo(BigDecimal.ZERO)  > 0) {
                BigDecimal payAmount = b.getArrearsAmount();
                if (payAmount.compareTo(balance[0]) < 1) {
                    balance[0] = balance[0].subtract(payAmount);
                    b.setArrearsAmount(BigDecimal.ZERO);
                    b.setBalancePayAmount(payAmount);
                    b.setPayStatus(OrderStatusEnum.PAID);
                    b.setPayTime(DateUtils.getNowDate());
                    sysCustomerService.updateCustomerBalance(b.getId(), b.getBillNo(), userId, payAmount, SysTransactionType.PAY_DISCOUNT);
                } else {
                    b.setArrearsAmount(payAmount.subtract(balance[0]));
                    b.setBalancePayAmount(balance[0]);
                    sysCustomerService.updateCustomerBalance(b.getId(), b.getBillNo(), userId, balance[0], SysTransactionType.PAY_DISCOUNT);
                    balance[0] = BigDecimal.ZERO;
                }
            }
        });
        sysCustomerBillMapper.batchUpdateById(billList);
    }

    @Override
    public BigDecimal getArrearsAmount(Long userId) {
        LambdaQueryWrapper<SysCustomerBillEntity> wrapper = Wrappers.lambdaQuery(SysCustomerBillEntity.class)
                .eq(SysCustomerBillEntity::getCustomerId, userId)
                .eq(SysCustomerBillEntity::getPayStatus, OrderStatusEnum.UNPAID);
        return Optional.of( sysCustomerBillMapper.selectList(wrapper))
                .flatMap(list -> list.stream().map(SysCustomerBillEntity::getArrearsAmount).reduce(BigDecimal::add).orElse(BigDecimal.ZERO))
                .orElse(BigDecimal.ZERO);
    }

    @Override
    public void handlerCustomerBillProduct(Long valueOf) {
        // 查询产品订单
        log.info("生成产品订单账单: {}", valueOf);
        SysOrderEntity sysOrder = sysOrderMapper.selectById(valueOf);
        SysNetworkValueEntity sysNetworkValueEntity = sysNetworkValueMapper.selectById(sysOrder.getNetworkValueId());
        // 不是授信额支付的就直接当天计算生成账单
        if (sysNetworkValueEntity != null){
            SysCustomerBillEntity billEntity = new SysCustomerBillEntity();
            billEntity.setCustomerId(sysOrder.getCreateById());
            billEntity.setBill(DateUtils.parseDateToStr("yyyy-MM", new Date()));
            billEntity.setBillNo(UUID.randomUUID().toString());
            billEntity.setBillDate(new Date());
            billEntity.setSourceType(SourceTypeEnum.AGIC);
            billEntity.setSourceId(sysOrder.getId());
            billEntity.setChargeType(sysNetworkValueEntity.getChargeType());
            billEntity.setDuration(sysNetworkValueEntity.getDuration());
            billEntity.setChargeUnit(ObjectUtils.isNotEmpty(sysNetworkValueEntity.getDurationUnit())?sysNetworkValueEntity.getDurationUnit().getDesc():null);
            billEntity.setBillType("消费-使用");
            billEntity.setSettleType("结算");
            billEntity.setInstanceId(sysOrder.getNetworkProductId().toString());
            SysNetworkProductEntity sysNetworkProductEntity = sysNetworkProductMapper.selectById(sysOrder.getNetworkProductId());
            if (sysNetworkProductEntity != null){
                billEntity.setInstanceName(sysNetworkProductEntity.getName());
            }
            billEntity.setUnitId("");
            billEntity.setRegion("");
            billEntity.setZone("");
            billEntity.setPriceType("一次性付款");
            billEntity.setPrice(sysOrder.getFinalPayAmount());
            billEntity.setUsage(BigDecimal.valueOf(sysOrder.getNetworkCount()));
            billEntity.setUsageUnit("台");
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

            sysCustomerBillMapper.insert(billEntity);
        }
    }

    @Override
    public void handlerCustomerBillSelfBuild(Long valueOf) {
        // 查询自建服务器订单
        log.info("生成自建服务器订单账单: {}", valueOf);
        SysOrderEntity sysOrder = sysOrderMapper.selectById(valueOf);
        List<SysOrderSourceEntity> sysOrderSourceEntityList = sysOrderSourceMapper.selectList(Wrappers.lambdaQuery(SysOrderSourceEntity.class)
                .eq(SysOrderSourceEntity::getOrderId, sysOrder.getId())
                .eq(SysOrderSourceEntity::getSourceType, ECS));
        if (sysOrderSourceEntityList != null){
            for (SysOrderSourceEntity sysOrderSourceEntity : sysOrderSourceEntityList) {
                //todo 暂时先计算包年包月的账单，按量计费的账单后面处理
                SysCustomerInstancesEntity sysCustomerInstancesEntity = sysCustomerInstancesMapper.selectOne(Wrappers.lambdaQuery(SysCustomerInstancesEntity.class)
                        .eq(SysCustomerInstancesEntity::getOrderSourceUid, sysOrderSourceEntity.getUid()));
                SysCustomerBillEntity billEntity = new SysCustomerBillEntity();
                billEntity.setCustomerId(sysOrder.getCreateById());
                billEntity.setBill(DateUtils.parseDateToStr("yyyy-MM", new Date()));
                billEntity.setBillNo(UUID.randomUUID().toString());
                billEntity.setBillDate(new Date());
                billEntity.setSourceType(ECS);
                billEntity.setSourceId(sysOrder.getId());
                billEntity.setChargeType(sysOrderSourceEntity.getChargeType());
                billEntity.setDuration(sysOrderSourceEntity.getDuration());
                billEntity.setChargeUnit(sysOrderSourceEntity.getDurationUnit().getDesc());
                billEntity.setBillType("消费-使用");
                billEntity.setSettleType("结算");
                billEntity.setInstanceId(sysCustomerInstancesEntity.getId().toString());
                billEntity.setInstanceName(sysCustomerInstancesEntity.getInstanceName());
                billEntity.setUnitId("");
                billEntity.setRegion(sysCustomerInstancesEntity.getRegion().getName());
                billEntity.setZone(sysCustomerInstancesEntity.getZoneId());
                billEntity.setPriceType("一次性付款");
                billEntity.setPrice(sysOrder.getFinalPayAmount());
                billEntity.setUsage(BigDecimal.valueOf(sysOrderSourceEntity.getNumber()));
                billEntity.setUsageUnit("台");
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

                sysCustomerBillMapper.insert(billEntity);
            }
        }
    }
}
