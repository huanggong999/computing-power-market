package com.lingyang.cloud.take;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.api.model.edit.PcInstancesStatusHandlerEdit;
import com.lingyang.cloud.api.service.pc.PcConsoleService;
import com.lingyang.cloud.common.constant.CacheQueueConstant;
import com.lingyang.cloud.entity.*;
import com.lingyang.cloud.enums.customer.SysTransactionType;
import com.lingyang.cloud.enums.order.OrderStatusEnum;
import com.lingyang.cloud.enums.source.EcsStatusEnum;
import com.lingyang.cloud.enums.source.SourceRegionsEnum;
import com.lingyang.cloud.enums.source.SourceTypeEnum;
import com.lingyang.cloud.enums.system.SystemConfigEnum;
import com.lingyang.cloud.mapper.*;
import com.lingyang.cloud.model.config.SystemPriceRationConfigModel;
import com.lingyang.cloud.model.vo.feilian.UpdateUserStatusVO;
import com.lingyang.cloud.model.vo.product.SysOpenProductUserPwdVO;
import com.lingyang.cloud.service.SysConfigService;
import com.lingyang.cloud.service.SysCustomerCreditLineService;
import com.lingyang.cloud.service.SysCustomerService;
import com.lingyang.cloud.service.SysCustomerVoucherService;
import com.lingyang.cloud.utils.FeiLianUtils;
import com.lingyang.cloud.utils.SendSmsUtils;
import com.lingyang.common.cache.CacheQueueService;
import com.lingyang.common.cache.queue.QueueMessageBody;
import com.lingyang.common.core.utils.Builder;
import com.lingyang.common.core.utils.DateUtils;
import com.lingyang.common.core.utils.IdUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.lingyang.cloud.common.constant.CacheQueueConstant.CUSTOMER_NEW_DAY_BILL_PRODUCT_QUEUE_TYPE;
import static com.lingyang.cloud.enums.order.OrderTypeEnum.PRODUCT;
import static com.lingyang.cloud.enums.source.SourceChargeTypeEnum.POSTPAID_BY_MONTH;
import static com.lingyang.cloud.enums.source.SourceChargeTypeEnum.POSTPAID_BY_YEAR;

@Slf4j
@Component
public class MyTask {


    @Autowired
    private SysOrderMapper sysOrderMapper;

    @Autowired
    private SysCustomerMapper sysCustomerMapper;

    @Resource
    private SysNetworkValueMapper sysNetworkValueMapper;

    @Autowired
    private SysExtendMapper sysExtendMapper;

    @Resource
    private SysCustomerService sysCustomerService;

    @Resource
    private SysCustomerInstancesMapper sysCustomerInstancesMapper;

    @Resource
    private SysCustomerEcsWorkMapper sysCustomerEcsWorkMapper;
    @Resource
    private SysCustomerEcsWorkEipMapper sysCustomerEcsWorkEipMapper;

    @Resource
    private FeiLianUtils flashLianUtils;

    @Resource
    private CacheQueueService cacheQueueService;

    @Resource
    private SysNetworkProductIpMapper sysNetworkProductIpMapper;

    @Resource
    private SysCustomerBillMapper sysCustomerBillMapper;
    @Resource
    private SysCustomerVoucherService sysCustomerVoucherService;
    @Resource
    private SysCustomerCreditLineService sysCustomerCreditLineService;

    @Resource
    private SysNetworkProductMapper sysNetworkProductMapper;

    @Resource
    private SysEcsMapper sysEcsMapper;

    @Resource
    private SysCustomerDiscountMapper sysCustomerDiscountMapper;

    @Resource
    private SysConfigService sysConfigService;

    @Resource
    private PcConsoleService pcConsoleService;


    //@Scheduled(fixedRate = 30000)
    public void orderS() {
        log.info("执行分销结算");
        List<SysOrderEntity> entities = sysOrderMapper.selectList(new LambdaQueryWrapper<SysOrderEntity>()
                .eq(SysOrderEntity::getSettlementStatus, 1)
                .isNotNull(SysOrderEntity::getPayTime)
                .isNotNull(SysOrderEntity::getFirstUserId)
        );
        if (CollectionUtils.isNotEmpty(entities)) {
            log.info("执行分销结算: {} 个", entities.size());
            for (SysOrderEntity entity : entities) {
                SysExtend extend = sysExtendMapper.selectOne(
                        new LambdaQueryWrapper<SysExtend>()
                                .eq(SysExtend::getUserId, entity.getFirstUserId())
                );
                sysExtendMapper.update(null,
                        new LambdaUpdateWrapper<SysExtend>()
                                .setSql("can_withdrawal_amount = can_withdrawal_amount + " + entity.getFirstUserCommission())
                                .eq(SysExtend::getId, extend.getId())
                );
                if (entity.getTwoUserId() != null) {
                    SysExtend extend2 = sysExtendMapper.selectOne(
                            new LambdaQueryWrapper<SysExtend>()
                                    .eq(SysExtend::getUserId, entity.getTwoUserId())
                    );
                    sysExtendMapper.update(null,
                            new LambdaUpdateWrapper<SysExtend>()
                                    .setSql("can_withdrawal_amount = can_withdrawal_amount + " + entity.getTwoUserCommission())
                                    .eq(SysExtend::getId, extend2.getId())
                    );
                }


                sysOrderMapper.update(null,
                        new LambdaUpdateWrapper<SysOrderEntity>()
                                .set(SysOrderEntity::getSettlementStatus, 2)
                                .eq(SysOrderEntity::getId, entity.getId())
                );
            }
        }
    }


    //(fixedRate = 30000)
    @Transactional(rollbackFor = Exception.class)
    public void task2() {
        log.info("执行AGI-C产品过期状态");

        List<SysNetworkValueEntity> ss = sysNetworkValueMapper.selectList(
                new LambdaQueryWrapper<SysNetworkValueEntity>()
                        .eq(SysNetworkValueEntity::getActualStatus, 2)
                        .in(SysNetworkValueEntity::getChargeType, 2,3)
                        .le(SysNetworkValueEntity::getActualAgiExpireTime, new Date())
        );
        if (CollectionUtils.isNotEmpty(ss)) {
            log.info("过期的产品有1111: {} 个", ss.size());
            log.info("过时的产品有: {}", ss);
            for (SysNetworkValueEntity sysNetworkValue : ss) {

                Integer isAutoRenew = sysNetworkValue.getIsAutoRenew();
                if (isAutoRenew == 1){
                    //自动续费
                    log.info("自动续费产品，sysNetworkValue：{}",sysNetworkValue);
                    SysOrderEntity sysOrder = sysOrderMapper.selectOne(Wrappers.lambdaQuery(SysOrderEntity.class)
                            .eq(SysOrderEntity::getNetworkValueId, sysNetworkValue.getId())
                            .eq(SysOrderEntity::getOrderType,PRODUCT));
//                    List<SysOrderEntity> sysOrderList = sysOrderMapper.selectList(Wrappers.lambdaQuery(SysOrderEntity.class)
//                            .eq(SysOrderEntity::getNetworkValueId, sysNetworkValue.getId())
//                            .eq(SysOrderEntity::getOrderType,UPGRADE_PRODUCT));
                    SysCustomerBillEntity sysCustomerBillEntity = Builder.of(SysCustomerBillEntity::new)
                            .set(SysCustomerBillEntity::setId, IdUtils.nextId())
                            .set(SysCustomerBillEntity::setBill, DateUtils.parseDateToStr("yyyy-MM", new Date()))
                            .set(SysCustomerBillEntity::setCustomerId, sysNetworkValue.getUserId())
                            .set(SysCustomerBillEntity::setBillNo, UUID.randomUUID().toString())
                            .set(SysCustomerBillEntity::setBillDate, new Date())
                            .set(SysCustomerBillEntity::setSourceId, sysOrder.getId())
                            .set(SysCustomerBillEntity::setSourceType, SourceTypeEnum.AGIC)
                            .set(SysCustomerBillEntity::setChargeType,sysNetworkValue.getChargeType())
                            .set(SysCustomerBillEntity::setDuration, sysNetworkValue.getDuration())
                            .set(SysCustomerBillEntity::setChargeUnit, sysNetworkValue.getDurationUnit().getDesc())
                            .set(SysCustomerBillEntity::setBillType,"消费-使用")
                            .set(SysCustomerBillEntity::setSettleType,"续费")
                            .set(SysCustomerBillEntity::setInstanceId, sysNetworkValue.getProductId().toString())
                            .set(SysCustomerBillEntity::setInstanceName, sysNetworkProductMapper.selectById(sysNetworkValue.getProductId()).getName())
                            .set(SysCustomerBillEntity::setUnitId, "")
                            .set(SysCustomerBillEntity::setRegion, "")
                            .set(SysCustomerBillEntity::setZone, "")
                            .set(SysCustomerBillEntity::setPriceType, "一次性付款")
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
                            .set(SysCustomerBillEntity::setPayStatus, OrderStatusEnum.UNPAID)
                            .set(SysCustomerBillEntity::setArrearsAmount,BigDecimal.ZERO)
                            .build();
//                    if (sysOrderList != null) {
//                        for (SysOrderEntity sysOrderEntity : sysOrderList) {
//                            sysCustomerBillEntity.setPayPrice(sysCustomerBillEntity.getPayPrice().add(sysOrderEntity.getFinalPayAmount()));
//                        }
//                    }

                    SysCustomerEntity sysCustomerEntity = sysCustomerMapper.selectById(sysNetworkValue.getUserId());
                    BigDecimal creditAmount = sysCustomerCreditLineService.getCreditAmount(sysCustomerEntity.getId());
                    BigDecimal userVoucherBalance = sysCustomerVoucherService.getUserVoucherBalance(sysCustomerEntity.getId());
                    BigDecimal balance = sysCustomerEntity.getBalance();
                    if (creditAmount.add( balance).add(userVoucherBalance).compareTo(sysCustomerBillEntity.getPayPrice()) >= 0) {
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
                        sysCustomerBillMapper.insert(sysCustomerBillEntity);

                        LocalDateTime openDateTime = sysNetworkValue.getActualAgiExpireTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                        LocalDateTime expireDateTime;
                        if (sysNetworkValue.getChargeType() == POSTPAID_BY_MONTH) {
                            expireDateTime = openDateTime.plusMonths(sysNetworkValue.getDuration());
                        } else if (sysNetworkValue.getChargeType() == POSTPAID_BY_YEAR) {
                            expireDateTime = openDateTime.plusYears(sysNetworkValue.getDuration());
                        }else {
                            expireDateTime = openDateTime;
                        }
                        Date expireDate = Date.from(expireDateTime.atZone(ZoneId.systemDefault()).toInstant());
                        sysNetworkValueMapper.update(null,
                                new LambdaUpdateWrapper<SysNetworkValueEntity>()
                                        .set(SysNetworkValueEntity::getActualAgiExpireTime, expireDate)
                                        .set(SysNetworkValueEntity::getExpireStatus, 8)
                                        .eq(SysNetworkValueEntity::getId, sysNetworkValue.getId())
                        );

                        sysOrderMapper.update(null,
                                new LambdaUpdateWrapper<SysOrderEntity>()
                                        .set(SysOrderEntity::getActualAgiExpireTime, expireDate)
                                        .eq(SysOrderEntity::getNetworkValueId, sysNetworkValue.getId())
                        );
                    }else {
                        sysNetworkValueMapper.update(null,
                                new LambdaUpdateWrapper<SysNetworkValueEntity>()
                                        .set(SysNetworkValueEntity::getActualStatus, 3)
                                        .eq(SysNetworkValueEntity::getId, sysNetworkValue.getId())
                        );

                        sysOrderMapper.update(null,
                                new LambdaUpdateWrapper<SysOrderEntity>()
                                        .set(SysOrderEntity::getActualStatus, 3)
                                        .eq(SysOrderEntity::getNetworkValueId, sysNetworkValue.getId())
                        );

                        //修改用户状态
                        List<SysOpenProductUserPwdVO> userPwdList = JSON.parseArray(sysNetworkValue.getUserpwdList().toJSONString(), SysOpenProductUserPwdVO.class);
                        for (SysOpenProductUserPwdVO sysOpenProductUserPwdVO : userPwdList) {
                            String userId = flashLianUtils.getUserInfo(sysOpenProductUserPwdVO.getEmail()).get("id").toString();
                            UpdateUserStatusVO vo = new UpdateUserStatusVO();
                            vo.setId(userId);
                            vo.setStatus("disable");
                            flashLianUtils.updateUserStatus(vo);
                        }
                    }
                }else {
                    sysNetworkValueMapper.update(null,
                            new LambdaUpdateWrapper<SysNetworkValueEntity>()
                                    .set(SysNetworkValueEntity::getActualStatus, 3)
                                    .eq(SysNetworkValueEntity::getId, sysNetworkValue.getId())
                    );

                    sysOrderMapper.update(null,
                            new LambdaUpdateWrapper<SysOrderEntity>()
                                    .set(SysOrderEntity::getActualStatus, 3)
                                    .eq(SysOrderEntity::getNetworkValueId, sysNetworkValue.getId())
                    );

                    //修改用户状态
                    List<SysOpenProductUserPwdVO> userPwdList = JSON.parseArray(sysNetworkValue.getUserpwdList().toJSONString(), SysOpenProductUserPwdVO.class);
                    for (SysOpenProductUserPwdVO sysOpenProductUserPwdVO : userPwdList) {
                        try {

                            String userId = flashLianUtils.getUserInfo(sysOpenProductUserPwdVO.getEmail()).get("id").toString();
                            UpdateUserStatusVO vo = new UpdateUserStatusVO();
                            vo.setId(userId);
                            vo.setStatus("disable");
                            flashLianUtils.updateUserStatus(vo);
                        }catch (Exception e){
                            log.error("修改飞连用户：{}状态失败: {}",sysOpenProductUserPwdVO.getEmail(), e.getMessage());
                        }
                    }
                }
            }

        }
    }

    //(fixedRate = 30000)
    public void task3() {
        log.info("执行自建服务器过期状态");

        List<SysCustomerInstancesEntity> entityList = sysCustomerInstancesMapper.selectList(Wrappers.lambdaQuery(SysCustomerInstancesEntity.class)
                .eq(SysCustomerInstancesEntity::getProductType, 2)
                .le(SysCustomerInstancesEntity::getExpireTime, new Date()));
        if (CollectionUtils.isNotEmpty(entityList)) {
            log.info("过期的自建服务器有: {} 个", entityList.size());
            for (SysCustomerInstancesEntity sysCustomerInstancesEntity : entityList) {
                SysCustomerEcsWorkEntity sysCustomerEcsWorkEntity = sysCustomerEcsWorkMapper.selectOne(Wrappers.lambdaQuery(SysCustomerEcsWorkEntity.class)
                        .eq(SysCustomerEcsWorkEntity::getOrderId, sysCustomerInstancesEntity.getOrderId()));
                List<SysCustomerEcsWorkEipEntity> sysCustomerEcsWorkEipEntities = sysCustomerEcsWorkEipMapper.selectList(Wrappers.lambdaQuery(SysCustomerEcsWorkEipEntity.class)
                        .eq(SysCustomerEcsWorkEipEntity::getEcsWorkId, sysCustomerEcsWorkEntity.getId()));
                if (CollectionUtils.isNotEmpty(sysCustomerEcsWorkEipEntities)) {
                    for (SysCustomerEcsWorkEipEntity sysCustomerEcsWorkEipEntity : sysCustomerEcsWorkEipEntities) {
                        sysCustomerEcsWorkEipEntity.setOperationStatus(6);
                        sysCustomerEcsWorkEipMapper.updateById(sysCustomerEcsWorkEipEntity);
                    }
                }
            }
        }
    }

    //@Scheduled(fixedRate = 600000)
    public void task4() {
        log.info("执行AGIC撤线状态  过期超过7天就撤线");
        List<SysNetworkValueEntity> sysNetworkValueEntities = sysNetworkValueMapper.selectList(Wrappers.lambdaQuery(SysNetworkValueEntity.class)
                .eq(SysNetworkValueEntity::getActualStatus, 3)
                .le(SysNetworkValueEntity::getActualAgiExpireTime, DateUtils.addDays(new Date(), -7)));
        log.info("AGIC撤线状态有: {} 个", sysNetworkValueEntities.size());
        if (CollectionUtils.isNotEmpty(sysNetworkValueEntities)) {
            for (SysNetworkValueEntity sysNetworkValueEntity : sysNetworkValueEntities) {
                sysNetworkValueEntity.setActualStatus(5);
                sysNetworkValueMapper.updateById(sysNetworkValueEntity);
                log.info("-------------11111111--------------");
                //修改用户状态
                List<SysOpenProductUserPwdVO> userPwdList = JSON.parseArray(sysNetworkValueEntity.getUserpwdList().toJSONString(), SysOpenProductUserPwdVO.class);
                for (SysOpenProductUserPwdVO sysOpenProductUserPwdVO : userPwdList) {
                    String userId = flashLianUtils.getUserInfo(sysOpenProductUserPwdVO.getEmail()).get("id").toString();

                    UpdateUserStatusVO vo = new UpdateUserStatusVO();
                    vo.setId(userId);
                    vo.setStatus("offline");
                    flashLianUtils.updateUserStatus(vo);
                    log.info("-------------222222222--------------");
                    sysNetworkProductIpMapper.update(null,new LambdaUpdateWrapper<SysNetworkProductIpEntity>()
                            .set(SysNetworkProductIpEntity::getStatus, 2)
                            .set(SysNetworkProductIpEntity::getUpdateTime, new Date())
                            .eq(SysNetworkProductIpEntity::getIp, sysOpenProductUserPwdVO.getIp()));

                    for (Object queue : cacheQueueService.getAllQueue()) {
                        if (queue instanceof QueueMessageBody messageBody) {
                            if (messageBody.getMessageType().equals(CUSTOMER_NEW_DAY_BILL_PRODUCT_QUEUE_TYPE) && messageBody.getBody().equals(sysNetworkValueEntity.getId().toString())) {
                                cacheQueueService.remove(messageBody);
                                log.info("产品{}已撤线", sysNetworkValueEntity.getId());
                            }
                        }
                    }

                    //如果客户撤线，从撤线时间开始先改为等待中，2个月后再改为未使用。
                    cacheQueueService.addDelayQueue(CacheQueueConstant.AGI_C_IP_QUEUE_TYPE, sysOpenProductUserPwdVO.getIp(), 60 * 60 * 24 * 30 * 2);
                }
                log.info("-------------33333333333--------------");
            }
        }
        log.info("AGIC撤线状态处理完成");
    }

    /**
     * 每天十点获取用户余额小于100的，短信提示
     *
     * 只提醒三天
     */
//    @Scheduled(cron = "0 0 10 * * ?")
//    public void usdbal() {
//        log.info("每天十点获取用户余额小于100");
//        List<SysCustomerEntity> customerEntities = sysCustomerMapper.selectList(new LambdaQueryWrapper<SysCustomerEntity>()
//                .le(SysCustomerEntity::getBalance, 100)
//        );
//
//        if (CollectionUtils.isNotEmpty(customerEntities)) {
//            for (SysCustomerEntity customerEntity : customerEntities) {
//                String phone = customerEntity.getPhone();
//                SendSmsUtils.sendB(phone);
//            }
//        }
//
//    }

    /**
     * 每天8点获取用户AGIC产品，若到期日期小于7天，进行短信提示
     */
    //@Scheduled(cron = "0 0 8 * * ?")
    public void aGICDueDateReminder() {
        log.info("每天8点获取用户AGIC产品，若到期日期小于7天，进行短信提示");
        LocalDate now = LocalDate.now();
        Date now2 = new Date(); // 当前时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now2);
        calendar.add(Calendar.DAY_OF_YEAR, 7);
        // 设置为7天后当天的23:59:59
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        Date end = calendar.getTime();
        // 查询7天内到期的有效产品
        List<SysNetworkValueEntity> expireList = sysNetworkValueMapper.selectList(
                Wrappers.lambdaQuery(SysNetworkValueEntity.class)
                        .eq(SysNetworkValueEntity::getActualStatus, 2)
                        .eq(SysNetworkValueEntity::getDelFlag, 0)
                        .eq(SysNetworkValueEntity::getPayStatus, 1)
                        .in(SysNetworkValueEntity::getChargeType, 2,3)
                        .ge(SysNetworkValueEntity::getActualAgiExpireTime, now2)
                        .lt(SysNetworkValueEntity::getActualAgiExpireTime, end)
        );
        log.info("查询即将到期的AGIC集合:{}", expireList);

        expireList.forEach(entity -> {
            // 计算剩余天数
            long daysRemaining = ChronoUnit.DAYS.between(
                    now,
                    entity.getActualAgiExpireTime().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
            );

            // 设置到期状态（0-7对应1天内到7天）
            int expireStatus = (int) Math.max(0, daysRemaining);
            entity.setExpireStatus(expireStatus);

            // 更新数据库
            sysNetworkValueMapper.updateById(entity);
            if (daysRemaining == 7L){
                log.info("AGIC到期提醒：{}", entity);
                SendSmsUtils.sendAGICDueDateReminder(entity.getMobile());
            }
         });
    }

    /**
     * 处理包月自动续费的服务器，每个月15号0点执行
     */
//    @Scheduled(cron = "0 0 0 15 * ?")
    public void autoRenewalEcs() {
        log.info("处理包月自动续费的服务器，每个月15号0点执行");
        SysCustomerInstancesEntity sysCustomerInstances= sysCustomerInstancesMapper.selectOne(Wrappers.lambdaQuery(SysCustomerInstancesEntity.class)
                .eq(SysCustomerInstancesEntity::getCustomerId, 15)
                .eq(SysCustomerInstancesEntity::getInstanceId,"i-ydwbeblo1swh2yp0rg3p"));

        SysEcsEntity sysEcsEntity = sysEcsMapper.selectOne(Wrappers.lambdaQuery(SysEcsEntity.class)
                .eq(SysEcsEntity::getEcsScale, sysCustomerInstances.getEcsScale())
                .eq(SysEcsEntity::getRegionsZones, sysCustomerInstances.getRegion()));

        SysCustomerBillEntity sysCustomerBillEntity = Builder.of(SysCustomerBillEntity::new)
                .set(SysCustomerBillEntity::setId, IdUtils.nextId())
                .set(SysCustomerBillEntity::setBill, DateUtils.parseDateToStr("yyyy-MM", new Date()))
                .set(SysCustomerBillEntity::setCustomerId, sysCustomerInstances.getCustomerId())
                .set(SysCustomerBillEntity::setBillNo, UUID.randomUUID().toString())
                .set(SysCustomerBillEntity::setBillDate, new Date())
                .set(SysCustomerBillEntity::setSourceId, sysCustomerInstances.getId())
                .set(SysCustomerBillEntity::setSourceType, SourceTypeEnum.ECS)
                .set(SysCustomerBillEntity::setChargeType,POSTPAID_BY_MONTH)
                .set(SysCustomerBillEntity::setChargeUnit, "月")
                .set(SysCustomerBillEntity::setBillType,"消费-使用")
                .set(SysCustomerBillEntity::setSettleType,"结算")
                .set(SysCustomerBillEntity::setInstanceId, sysCustomerInstances.getInstanceId())
                .set(SysCustomerBillEntity::setInstanceName, sysCustomerInstances.getInstanceName())
                .set(SysCustomerBillEntity::setUnitId, "ecs.gni3")
                .set(SysCustomerBillEntity::setRegion, "华北2（北京）")
                .set(SysCustomerBillEntity::setZone, "可用区C")
                .set(SysCustomerBillEntity::setPriceType, "包月")
                .set(SysCustomerBillEntity::setPrice, sysEcsEntity.getMonthPrice())
                .set(SysCustomerBillEntity::setUsage, BigDecimal.ONE)
                .set(SysCustomerBillEntity::setUsageUnit, "台")
                .set(SysCustomerBillEntity::setOriginalPrice, sysEcsEntity.getMonthPrice())
                .set(SysCustomerBillEntity::setBillStartTime,new Date())
                .set(SysCustomerBillEntity::setPayStatus,OrderStatusEnum.UNPAID)
                .set(SysCustomerBillEntity::setArrearsAmount,BigDecimal.ZERO)
                .build();

        SysCustomerDiscountEntity sysCustomerDiscountEntity = sysCustomerDiscountMapper.selectOne(Wrappers.lambdaQuery(SysCustomerDiscountEntity.class)
                .eq(SysCustomerDiscountEntity::getCustomerId, sysCustomerInstances.getCustomerId())
                .eq(SysCustomerDiscountEntity::getSourceType, SourceTypeEnum.ECS));

        BigDecimal discountRation = sysCustomerDiscountEntity.getDiscountRation();

        // 计算折扣
        SystemPriceRationConfigModel sellPriceRatioConfig = sysConfigService.getConfig(SystemConfigEnum.SELL_PRICE_RATIO).getConfigValue().to(SystemPriceRationConfigModel.class);

        BigDecimal originalPrice = sysCustomerBillEntity.getPrice();
        sysCustomerBillEntity.setOriginalPrice(originalPrice.setScale(8, RoundingMode.UP));
        sysCustomerBillEntity.setPremiumPrice(sellPriceRatioConfig.calculatePremium(sysCustomerBillEntity.getOriginalPrice()));
        sysCustomerBillEntity.setUserDiscountAmount(sysCustomerBillEntity.getPremiumPrice().multiply(discountRation).setScale(8, RoundingMode.UP));
        sysCustomerBillEntity.setPayPrice(sysCustomerBillEntity.getUserDiscountAmount().setScale(2, RoundingMode.UP));

        // 处理代金卷
        Map<Long, BigDecimal> customerVoucherBalanceMap = sysCustomerVoucherService.batchUserVoucherBalance(Set.of(sysCustomerBillEntity.getCustomerId()));
        BigDecimal customerVoucherAmount = customerVoucherBalanceMap.get(sysCustomerBillEntity.getCustomerId());
        if (!(ObjectUtils.isEmpty(customerVoucherAmount) ||
                sysCustomerBillEntity.getPayStatus().equals(OrderStatusEnum.PAID) ||
                sysCustomerBillEntity.getPayPrice().compareTo(BigDecimal.ZERO) == 0 ||
                customerVoucherAmount.compareTo(BigDecimal.ZERO) == 0)) {
            if (customerVoucherAmount.compareTo(sysCustomerBillEntity.getPayPrice()) >= 0) {
                customerVoucherAmount = customerVoucherAmount.subtract(sysCustomerBillEntity.getPayPrice());
                sysCustomerBillEntity.setPayStatus(OrderStatusEnum.PAID);
                sysCustomerBillEntity.setPayTime(new Date());
                sysCustomerBillEntity.setVoucherAmount(sysCustomerBillEntity.getPayPrice());
                sysCustomerVoucherService.useVoucher(sysCustomerBillEntity.getId(), sysCustomerBillEntity.getBillNo(), sysCustomerBillEntity.getPayPrice(), sysCustomerBillEntity.getCustomerId());
            } else {
                sysCustomerBillEntity.setVoucherAmount(customerVoucherAmount);
                sysCustomerVoucherService.useVoucher(sysCustomerBillEntity.getId(), sysCustomerBillEntity.getBillNo(), customerVoucherAmount, sysCustomerBillEntity.getCustomerId());
                customerVoucherAmount = BigDecimal.ZERO;
            }
            customerVoucherBalanceMap.put(sysCustomerBillEntity.getCustomerId(), customerVoucherAmount);
        }
        // 处理授信额
        Map<Long, BigDecimal> customerCreditLineMap = sysCustomerCreditLineService.batchUserCreditLine(Set.of(sysCustomerBillEntity.getCustomerId()));
        BigDecimal customerCreditLine = customerCreditLineMap.get(sysCustomerBillEntity.getCustomerId());
        if (!(ObjectUtils.isEmpty(customerCreditLine) ||
                sysCustomerBillEntity.getPayStatus().equals(OrderStatusEnum.PAID) ||
                sysCustomerBillEntity.getPayPrice().compareTo(BigDecimal.ZERO) == 0 ||
                customerCreditLine.compareTo(BigDecimal.ZERO) == 0)) {
            if (customerCreditLine.compareTo(sysCustomerBillEntity.getPayPrice().subtract(sysCustomerBillEntity.getVoucherAmount())) >= 0) {
                customerCreditLine = customerCreditLine.subtract(sysCustomerBillEntity.getPayPrice().subtract(sysCustomerBillEntity.getVoucherAmount()));
                sysCustomerBillEntity.setPayStatus(OrderStatusEnum.PAID);
                sysCustomerBillEntity.setPayTime(new Date());
                sysCustomerBillEntity.setCreditLineAmount(sysCustomerBillEntity.getPayPrice());
                sysCustomerCreditLineService.useCreditLine(sysCustomerBillEntity.getId(), sysCustomerBillEntity.getBillNo(), sysCustomerBillEntity.getPayPrice(), sysCustomerBillEntity.getCustomerId());
            } else {
                sysCustomerBillEntity.setCreditLineAmount(customerCreditLine);
                sysCustomerCreditLineService.useCreditLine(sysCustomerBillEntity.getId(), sysCustomerBillEntity.getBillNo(), customerCreditLine, sysCustomerBillEntity.getCustomerId());
                customerCreditLine = BigDecimal.ZERO;
            }
            customerCreditLineMap.put(sysCustomerBillEntity.getCustomerId(), customerCreditLine);
        }
        // 处理余额
        List<SysCustomerEntity> customerList = sysCustomerMapper.selectList(Wrappers.lambdaQuery(SysCustomerEntity.class)
                .in(SysCustomerEntity::getId, Set.of(sysCustomerBillEntity.getCustomerId())));
        Map<Long, BigDecimal> customerBalanceMap = customerList.stream().collect(Collectors.toMap(SysCustomerEntity::getId, SysCustomerEntity::getBalance));
        BigDecimal customerBalance = customerBalanceMap.get(sysCustomerBillEntity.getCustomerId());
        if (!(sysCustomerBillEntity.getPayStatus().equals(OrderStatusEnum.PAID) ||
                customerBalance == null ||
                customerBalance.compareTo(BigDecimal.ZERO) == 0)) {
            BigDecimal payPrice = sysCustomerBillEntity.getPayPrice().subtract(sysCustomerBillEntity.getVoucherAmount()).subtract(sysCustomerBillEntity.getCreditLineAmount());
            if (customerBalance.compareTo(payPrice) >= 0) {
                customerBalance = customerBalance.subtract(payPrice);
                sysCustomerBillEntity.setBalancePayAmount(payPrice);
                sysCustomerBillEntity.setPayStatus(OrderStatusEnum.PAID);
                sysCustomerBillEntity.setPayTime(new Date());
                sysCustomerService.updateCustomerBalance(sysCustomerBillEntity.getId(), sysCustomerBillEntity.getBillNo(), sysCustomerBillEntity.getCustomerId(), payPrice, SysTransactionType.PAY_DISCOUNT);
            } else {
                sysCustomerBillEntity.setBalancePayAmount(customerBalance);
                customerBalance = BigDecimal.ZERO;
                sysCustomerService.updateCustomerBalance(sysCustomerBillEntity.getId(), sysCustomerBillEntity.getBillNo(), sysCustomerBillEntity.getCustomerId(), customerBalance, SysTransactionType.PAY_DISCOUNT);
            }
            customerBalanceMap.put(sysCustomerBillEntity.getCustomerId(), customerBalance);
        }
        if (sysCustomerBillEntity.getPayStatus().equals(OrderStatusEnum.UNPAID)) {
            sysCustomerBillEntity.setArrearsAmount(sysCustomerBillEntity.getPayPrice().subtract(sysCustomerBillEntity.getVoucherAmount()).subtract(sysCustomerBillEntity.getBalancePayAmount()));
        }
        if (sysCustomerBillEntity.getArrearsAmount().compareTo(BigDecimal.ZERO) == 0) {
            sysCustomerBillEntity.setPayStatus(OrderStatusEnum.PAID);
        }


        // 未支付的实例，停止实例
        if (sysCustomerBillEntity.getPayStatus().equals(OrderStatusEnum.UNPAID)) {
            log.info("包月账单未支付，停止实例");
                PcInstancesStatusHandlerEdit edit = new PcInstancesStatusHandlerEdit();
                edit.setIdList(List.of(sysCustomerBillEntity.getSourceId()));
                edit.setStatusEnum(EcsStatusEnum.STOPPING);
                edit.setStoppedMode("StopCharging");
                pcConsoleService.handlerInstancesStatus(edit, SourceRegionsEnum.getByName(sysCustomerBillEntity.getRegion()));

        }
        // 入库
        sysCustomerBillMapper.insert(sysCustomerBillEntity);
        log.info("账单处理完成");
    }

}