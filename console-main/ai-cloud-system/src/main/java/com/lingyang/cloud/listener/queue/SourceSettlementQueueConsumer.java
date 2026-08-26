package com.lingyang.cloud.listener.queue;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.api.model.edit.PcInstancesStatusHandlerEdit;
import com.lingyang.cloud.api.service.pc.PcConsoleService;
import com.lingyang.cloud.entity.*;
import com.lingyang.cloud.enums.customer.SysTransactionType;
import com.lingyang.cloud.enums.order.OrderStatusEnum;
import com.lingyang.cloud.enums.source.EcsStatusEnum;
import com.lingyang.cloud.enums.source.SourceChargeTypeEnum;
import com.lingyang.cloud.enums.source.SourceRegionsEnum;
import com.lingyang.cloud.enums.source.SourceTypeEnum;
import com.lingyang.cloud.enums.system.SystemConfigEnum;
import com.lingyang.cloud.mapper.*;
import com.lingyang.cloud.model.config.EcsSystemVolumeConfigModel;
import com.lingyang.cloud.model.config.EipAddressConfigModel;
import com.lingyang.cloud.model.config.SystemPriceRationConfigModel;
import com.lingyang.cloud.service.SysConfigService;
import com.lingyang.cloud.service.SysCustomerCreditLineService;
import com.lingyang.cloud.service.SysCustomerService;
import com.lingyang.cloud.service.SysCustomerVoucherService;
import com.lingyang.cloud.voengine.client.VoEngineApiClient;
import com.lingyang.cloud.voengine.config.VolEngineConfig;
import com.lingyang.common.cache.CacheQueueService;
import com.lingyang.common.cache.queue.CacheQueueConsumer;
import com.lingyang.common.cache.queue.QueueMessageBody;
import com.lingyang.common.core.utils.Optional;
import com.lingyang.common.core.utils.*;
import com.volcengine.ApiException;
import com.volcengine.billing.BillingApi;
import com.volcengine.billing.model.ListBillDetailRequest;
import com.volcengine.billing.model.ListBillDetailResponse;
import com.volcengine.billing.model.ListForListBillDetailOutput;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.lingyang.cloud.common.constant.CacheQueueConstant.SOURCE_SETTLEMENT_QUEUE_TYPE;
import static com.lingyang.cloud.enums.source.SourceChargeTypeEnum.POSTPAID_BY_HOUR;
import static com.lingyang.cloud.enums.source.SourceTypeEnum.ECS;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/26 15:06
 */
@Service
@Slf4j
public class SourceSettlementQueueConsumer implements CacheQueueConsumer, ApplicationListener<ApplicationStartedEvent> {
    @Resource
    private CacheQueueService cacheQueueService;
    @Resource
    private SysCustomerInstancesMapper sysCustomerInstancesMapper;
    @Resource
    private SysConfigService sysConfigService;
    @Resource
    private VolEngineConfig volEngineConfig;
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
    private SysCustomerVolumeMapper sysCustomerVolumeMapper;
    @Resource
    private SysCustomerEipMapper sysCustomerEipMapper;
    @Resource
    private SysCustomerService sysCustomerService;
    @Resource
    private SysCustomerContainerMapper sysCustomerContainerMapper;
    @Resource
    private SysImageRepositoryMapper sysImageRepositoryMapper;

    @Resource
    private SysOrderSourceMapper sysOrderSourceMapper;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void invoke(String messageContent) throws Exception {
        log.info("进行账单结算");
        BillingApi billingApi = VoEngineApiClient.getBillingApi();
        // 获取昨天日期
        Date date = DateUtils.getDate(DateUtils.getNowDate(), -1, Calendar.DAY_OF_MONTH);
        ListBillDetailRequest request = new ListBillDetailRequest();
        request.setLimit(300);
        request.setBillPeriod(DateUtils.parseDateToStr("yyyy-MM", date));
        request.setExpenseDate(DateUtils.parseDateToStr("yyyy-MM-dd", date));
        request.setGroupTerm(0);
        request.setGroupPeriod(1);
        request.setNeedRecordNum(1);
        request.setOffset(0);
        log.info("进行账单结算，查询参数: {}", JSON.toJSONString(request, true));
        List<ListForListBillDetailOutput> voengineBillList = getVoengineBillList(billingApi, request);
        if (ObjectUtils.isEmpty(voengineBillList)) {
            addDelayQueue();
            return;
        }
        List<SysCustomerBillEntity> instanceNoBillEntitList = voengineBillList.stream()
                .flatMap(output -> {
                    SourceTypeEnum sourceTypeEnum = SourceTypeEnum.getByVolcengineBillDesc(output.getProduct());
                    if (sourceTypeEnum == null || !volEngineConfig.getProjectName().equals(output.getProject()) || "IP配置费".equals(output.getElement())) {
                        return Stream.empty();
                    }
                    SysCustomerBillEntity entity = Builder.of(SysCustomerBillEntity::new)
                            .set(SysCustomerBillEntity::setId, IdUtils.nextId())
                            .set(SysCustomerBillEntity::setBill, output.getBillPeriod())
                            .set(SysCustomerBillEntity::setBillNo, StringUtils.isEmpty(output.getBillID()) ? IdUtils.simpleUUID() : output.getBillID())
                            .set(SysCustomerBillEntity::setBillDate, DateUtils.toDate(output.getExpenseDate(), "yyyy-MM-dd"))
                            .set(SysCustomerBillEntity::setSourceType, sourceTypeEnum)
                            .set(SysCustomerBillEntity::setChargeType, SourceChargeTypeEnum.getByDesc(output.getBillingMode()))
                            .set(StringUtils.isNotEmpty(output.getUseDuration()), SysCustomerBillEntity::setDuration, () -> Integer.valueOf(output.getUseDuration()))
                            .set(SysCustomerBillEntity::setChargeUnit, output.getUseDurationUnit())
                            .set(SysCustomerBillEntity::setBillType, output.getBillCategory())
                            .set(SysCustomerBillEntity::setSettleType, output.getSettlementType())
                            .set(SysCustomerBillEntity::setInstanceId, output.getInstanceNo())
                            .set(SysCustomerBillEntity::setInstanceName, output.getInstanceName())
                            .set(SysCustomerBillEntity::setUnitId, output.getElement())
                            .set(SysCustomerBillEntity::setRegion, output.getRegion())
                            .set(SysCustomerBillEntity::setZone, output.getZone())
                            .set(SysCustomerBillEntity::setPriceType, output.getBillingFunction())
                            .set(SysCustomerBillEntity::setPrice, new BigDecimal(StringUtils.isEmpty(output.getPrice()) ? "0" : output.getPrice()))
                            .set(SysCustomerBillEntity::setUsage, new BigDecimal(output.getCount()))
                            .set(SysCustomerBillEntity::setUsageUnit, output.getUnit())
                            .set(SysCustomerBillEntity::setOriginalPrice, new BigDecimal(output.getOriginalBillAmount()))
                            .set(StringUtils.isNotEmpty(output.getExpenseBeginTime()), SysCustomerBillEntity::setBillStartTime, () -> DateUtils.toDate(output.getExpenseBeginTime(), "yyyy-MM-dd"))
                            .set(StringUtils.isNotEmpty(output.getExpenseEndTime()), SysCustomerBillEntity::setBillEndTime, () -> DateUtils.toDate(output.getExpenseEndTime(), "yyyy-MM-dd"))
                            .build();
                    return Stream.of(entity);
                })
                .toList();
        if (ObjectUtils.isEmpty(instanceNoBillEntitList)) {
            addDelayQueue();
            return;
        }
        // 查询本地实列资源
        Map<String, SysCustomerInstancesEntity> localCustomerInstanceIdMap = sysCustomerInstancesMapper.selectList(Wrappers.lambdaQuery(SysCustomerInstancesEntity.class))
                .stream()
                .filter(d -> StringUtils.isNotEmpty(d.getInstanceId()))
                .collect(Collectors.toMap(SysCustomerInstancesEntity::getInstanceId, d -> d));
        if (ObjectUtils.isEmpty(localCustomerInstanceIdMap)) {
            addDelayQueue();
            return;
        }
        // 查询本地网络资源
        EipAddressConfigModel eipConfig = sysConfigService.getConfig(SystemConfigEnum.EIP_ADDRESS).getConfigValue().to(EipAddressConfigModel.class);
        Map<String, SysCustomerEipEntity> localCustomerEipIdMap = sysCustomerEipMapper.selectList(Wrappers.lambdaQuery(SysCustomerEipEntity.class))
                .stream()
                .filter(d -> StringUtils.isNotEmpty(d.getEipId()))
                .collect(Collectors.toMap(SysCustomerEipEntity::getEipId, d -> d));

        // 查询本地云盘资源
        EcsSystemVolumeConfigModel sysVolumeConfig = sysConfigService.getConfig(SystemConfigEnum.ECS_SYSTEM_VOLUME).getConfigValue().to(EcsSystemVolumeConfigModel.class);
        Map<String, SysCustomerVolumeEntity> localCustomerVolumeIdMap = sysCustomerVolumeMapper.selectList(Wrappers.lambdaQuery(SysCustomerVolumeEntity.class))
                .stream().filter(d -> StringUtils.isNotEmpty(d.getVolumeId()))
                .collect(Collectors.toMap(SysCustomerVolumeEntity::getVolumeId, d -> d));

        // 查询本地容器资源
        Map<String, SysCustomerContainerEntity> localCustomerContainerEntityMap = sysCustomerContainerMapper.selectList(Wrappers.lambdaQuery(SysCustomerContainerEntity.class))
                .stream().filter(d -> StringUtils.isNotEmpty(d.getClusterId()))
                .collect(Collectors.toMap(SysCustomerContainerEntity::getClusterId, d -> d));
        Map<String, SysCustomerContainerEntity> localCustomerContainerEntityMap2 = sysCustomerContainerMapper.selectList(Wrappers.lambdaQuery(SysCustomerContainerEntity.class))
                .stream().filter(d -> StringUtils.isNotEmpty(d.getClusterId()))
                .collect(Collectors.toMap(SysCustomerContainerEntity::getNatId, d -> d));
        Map<String, SysCustomerContainerEntity> localCustomerContainerEntityMap3 = sysCustomerContainerMapper.selectList(Wrappers.lambdaQuery(SysCustomerContainerEntity.class))
                .stream().filter(d -> StringUtils.isNotEmpty(d.getClusterId()))
                .collect(Collectors.toMap(SysCustomerContainerEntity::getClbId, d -> d));
        // 查询本地镜像仓库资源
        Map<String, SysCustomerImageRepositoryEntity> localCustomerRepositoryEntityMap = sysImageRepositoryMapper.selectList(Wrappers.lambdaQuery(SysCustomerImageRepositoryEntity.class))
                .stream().filter(d -> StringUtils.isNotEmpty(d.getInstanceName()))
                .collect(Collectors.toMap(SysCustomerImageRepositoryEntity::getInstanceName, d -> d));

        instanceNoBillEntitList.forEach(entity -> {
            switch (entity.getSourceType()) {
                case ECS, GPU_SERVER -> {
                    if (localCustomerInstanceIdMap.containsKey(entity.getInstanceId())) {
                        SysCustomerInstancesEntity instancesEntity = localCustomerInstanceIdMap.get(entity.getInstanceId());
                        entity.setCustomerId(instancesEntity.getCustomerId());
                        entity.setSourceId(instancesEntity.getId());
                        entity.setBillNo(instancesEntity.getOrderSourceUid());
                        if (entity.getChargeType() == null ) {
                            entity.setChargeType(instancesEntity.getChargeType());
                        }
                        SysOrderSourceEntity sysOrderSourceEntity = sysOrderSourceMapper.selectOne(Wrappers.lambdaQuery(SysOrderSourceEntity.class)
                                .eq(SysOrderSourceEntity::getUid, instancesEntity.getOrderSourceUid())
                                .eq(SysOrderSourceEntity::getSourceType, ECS));
                        if ("i-ydwbeblo1swh2yp0rg3p".equals(entity.getInstanceId()) && entity.getCustomerId() == 15){
                            entity.setPrice(new BigDecimal("787.50"));
                        }else {
                            entity.setPrice(sysOrderSourceEntity.getUnitPrice());
                        }
                    }
                }
                case CLOUD_NETWORK -> {
                    entity.setPrice(eipConfig.getTrafficPrice());
                    if (localCustomerEipIdMap.containsKey(entity.getInstanceId())) {
                        SysCustomerEipEntity eipEntity = localCustomerEipIdMap.get(entity.getInstanceId());
                        entity.setCustomerId(eipEntity.getCustomerId());
                        entity.setBillNo(eipEntity.getOrderSourceUid());
                        entity.setSourceId(eipEntity.getId());
                        if (entity.getChargeType() == null ) {
                            entity.setChargeType(POSTPAID_BY_HOUR);
                        }
                    }

                }
                case CLOUD_STORAGE -> {
                    entity.setPrice(sysVolumeConfig.getHoursPrice());
                    if (localCustomerVolumeIdMap.containsKey(entity.getInstanceId())) {
                        SysCustomerVolumeEntity volume = localCustomerVolumeIdMap.get(entity.getInstanceId());
                        entity.setCustomerId(volume.getCustomerId());
                        entity.setBillNo(volume.getOrderSourceUid());
                        entity.setSourceId(volume.getId());
                        if (entity.getChargeType() == null ) {
                            entity.setChargeType(volume.getChargeType());
                        }
                    }
                }
                case VKE -> {
                    if (localCustomerContainerEntityMap.containsKey(entity.getInstanceId())){
                        SysCustomerContainerEntity containerEntity = localCustomerContainerEntityMap.get(entity.getInstanceId());
                        entity.setCustomerId(containerEntity.getCustomerId());
                        entity.setSourceId(containerEntity.getId());
                        entity.setBillNo(containerEntity.getOrderSourceUid());
                        if (entity.getChargeType() == null ) {
                            SysCustomerInstancesEntity sysCustomerInstancesEntity = sysCustomerInstancesMapper.selectOne(Wrappers.lambdaQuery(SysCustomerInstancesEntity.class)
                                    .eq(SysCustomerInstancesEntity::getId, containerEntity.getCustomerInstancesId()));
                            entity.setChargeType(sysCustomerInstancesEntity.getChargeType());
                        }
                    }
                }
                case NAT_GATEWAY -> {
                    if (localCustomerContainerEntityMap2.containsKey(entity.getInstanceId())){
                        SysCustomerContainerEntity containerEntity = localCustomerContainerEntityMap2.get(entity.getInstanceId());
                        entity.setCustomerId(containerEntity.getCustomerId());
                        entity.setSourceId(containerEntity.getId());
                        entity.setBillNo(containerEntity.getOrderSourceUid());
                        if (entity.getChargeType() == null ) {
                            entity.setChargeType(POSTPAID_BY_HOUR);
                        }
                    }
                }
                case CLB -> {
                    if (localCustomerContainerEntityMap3.containsKey(entity.getInstanceId())){
                        SysCustomerContainerEntity containerEntity = localCustomerContainerEntityMap3.get(entity.getInstanceId());
                        entity.setCustomerId(containerEntity.getCustomerId());
                        entity.setSourceId(containerEntity.getId());
                        entity.setBillNo(containerEntity.getOrderSourceUid());
                        if (entity.getChargeType() == null ) {
                            entity.setChargeType(POSTPAID_BY_HOUR);
                        }
                    }
                }
                case CR -> {
                    if (localCustomerRepositoryEntityMap.containsKey(entity.getInstanceId())){
                        SysCustomerImageRepositoryEntity imageRepositoryEntity = localCustomerRepositoryEntityMap.get(entity.getInstanceId());
                        entity.setCustomerId(imageRepositoryEntity.getCustomerId());
                        entity.setSourceId(imageRepositoryEntity.getId());
                        entity.setBillNo(imageRepositoryEntity.getOrderSourceUid());
                        if (entity.getChargeType() == null ) {
                            entity.setChargeType(imageRepositoryEntity.getChargeType());
                        }
                    }
                }
            }
        });
        Set<Long> customerIdSet = instanceNoBillEntitList.stream().map(SysCustomerBillEntity::getCustomerId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (ObjectUtils.isEmpty(customerIdSet)) {
            addDelayQueue();
            return;
        }
        Map<Long, Map<SourceTypeEnum, BigDecimal>> customerDiscountMap = Optional.of(sysCustomerDiscountMapper.selectList(Wrappers.lambdaQuery(SysCustomerDiscountEntity.class)
                        .in(SysCustomerDiscountEntity::getCustomerId, customerIdSet)))
                .flatMap(customerDiscountList -> {
                    Map<Long, Map<SourceTypeEnum, BigDecimal>> resultMap = new HashMap<>(customerDiscountList.size());
                    for (SysCustomerDiscountEntity entity : customerDiscountList) {
                        Map<SourceTypeEnum, BigDecimal> map = resultMap.get(entity.getCustomerId());
                        if (map == null) {
                            map = new HashMap<>(SourceTypeEnum.values().length);
                        }
                        map.put(entity.getSourceType(), entity.getDiscountRation());
                        resultMap.put(entity.getCustomerId(), map);
                    }
                    return resultMap;
                })
                .orElse(new HashMap<>(0));
        // 计算折扣
        SystemPriceRationConfigModel sellPriceRatioConfig = sysConfigService.getConfig(SystemConfigEnum.SELL_PRICE_RATIO).getConfigValue().to(SystemPriceRationConfigModel.class);
        // 计算账单价格
        instanceNoBillEntitList.forEach(entity -> {
            SourceTypeEnum sourceType = entity.getSourceType();
            BigDecimal price = entity.getPrice();
            if (price.compareTo(BigDecimal.ZERO) != 0) {
                Map<SourceTypeEnum, BigDecimal> map = customerDiscountMap.get(entity.getCustomerId());
                BigDecimal discount;
                if (map == null) {
                    discount = BigDecimal.ONE;
                } else {
                    if (sourceType.equals(SourceTypeEnum.GPU_SERVER)){
                        sourceType = ECS;
                    }
                    discount = map.get(sourceType);
                }
                if (discount == null) {
                    discount = BigDecimal.ONE;
                }
                BigDecimal originalPrice;
                if (!entity.getChargeType().equals(POSTPAID_BY_HOUR)) {
                    originalPrice = entity.getPrice();
                    entity.setOriginalPrice(originalPrice.setScale(8, RoundingMode.UP));
                    entity.setPremiumPrice(sellPriceRatioConfig.calculatePremium(entity.getOriginalPrice()));
                    entity.setUserDiscountAmount(entity.getPremiumPrice().multiply(discount).setScale(8, RoundingMode.UP));
                    entity.setPayPrice(entity.getUserDiscountAmount().setScale(2, RoundingMode.UP));
                    if ("i-ydwbeblo1swh2yp0rg3p".equals(entity.getInstanceId()) && entity.getCustomerId() == 15){
                        entity.setPayStatus(OrderStatusEnum.UNPAID);
                    }else {
                        entity.setPayStatus(OrderStatusEnum.PAID);
                    }
                }else {
                    if (entity.getSourceType().equals(SourceTypeEnum.CLOUD_NETWORK)) {
                        originalPrice = entity.getUsage().multiply(entity.getPrice()).setScale(8, RoundingMode.UP);
                    } else {
                        originalPrice = BigDecimal.valueOf(entity.getDuration()).multiply(entity.getPrice().divide(BigDecimal.valueOf(3600), 8, RoundingMode.UP));
                    }
                    entity.setOriginalPrice(originalPrice.setScale(8, RoundingMode.UP));
                    entity.setPremiumPrice(sellPriceRatioConfig.calculatePremium(entity.getOriginalPrice()));
                    entity.setUserDiscountAmount(entity.getPremiumPrice().multiply(discount).setScale(8, RoundingMode.UP));
                    entity.setPayPrice(entity.getUserDiscountAmount().setScale(2, RoundingMode.UP));
                    entity.setPayStatus(OrderStatusEnum.UNPAID);
                }
            } else {
                entity.setUserDiscountAmount(BigDecimal.ZERO);
                entity.setPayPrice(BigDecimal.ZERO);
                entity.setOriginalPrice(BigDecimal.ZERO);
                entity.setPayStatus(OrderStatusEnum.PAID);
            }
            entity.setVoucherAmount(BigDecimal.ZERO);
            entity.setBalancePayAmount(BigDecimal.ZERO);
            entity.setArrearsAmount(BigDecimal.ZERO);
        });
        // 过滤不需要的账单
        List<SysCustomerBillEntity> insertList = new ArrayList<>(instanceNoBillEntitList.size());
        for (SysCustomerBillEntity entity : instanceNoBillEntitList) {
            switch (entity.getSourceType()) {
                case ECS, GPU_SERVER -> {
                    if (localCustomerInstanceIdMap.containsKey(entity.getInstanceId())) {
                        insertList.add(entity);
                    }
                }
                case CLOUD_NETWORK -> {
                    if (localCustomerEipIdMap.containsKey(entity.getInstanceId())) {
                        insertList.add(entity);
                    }
                }
                case CLOUD_STORAGE -> {
                    if (localCustomerVolumeIdMap.containsKey(entity.getInstanceId())) {
                        insertList.add(entity);
                    }
                }
                case VKE -> {
                    if (localCustomerContainerEntityMap.containsKey(entity.getInstanceId())) {
                        insertList.add(entity);
                    }
                }
                case NAT_GATEWAY -> {
                    if (localCustomerContainerEntityMap2.containsKey(entity.getInstanceId())) {
                        insertList.add(entity);
                    }
                }
                case CLB ->{
                    if (localCustomerContainerEntityMap3.containsKey(entity.getInstanceId())) {
                        insertList.add(entity);
                    }
                }
                case CR ->{
                    if (localCustomerRepositoryEntityMap.containsKey(entity.getInstanceId())){
                        insertList.add(entity);
                    }
                }
            }
        }
        log.info("需要处理的账单：{}", insertList.size());
        // 处理代金卷
        Map<Long, BigDecimal> customerVoucherBalanceMap = sysCustomerVoucherService.batchUserVoucherBalance(customerIdSet);
        insertList.forEach(entity -> {
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
                    sysCustomerVoucherService.useVoucher(entity.getId(), entity.getBillNo(), customerVoucherAmount, entity.getCustomerId());
                    customerVoucherAmount = BigDecimal.ZERO;
                }
                customerVoucherBalanceMap.put(entity.getCustomerId(), customerVoucherAmount);
            }
        });
        // 处理授信额
        Map<Long, BigDecimal> customerCreditLineMap = sysCustomerCreditLineService.batchUserCreditLine(customerIdSet);
        insertList.forEach(entity -> {
            BigDecimal customerCreditLine = customerCreditLineMap.get(entity.getCustomerId());
            if (!(ObjectUtils.isEmpty(customerCreditLine) ||
                    entity.getPayStatus().equals(OrderStatusEnum.PAID) ||
                    entity.getPayPrice().compareTo(BigDecimal.ZERO) == 0 ||
                    customerCreditLine.compareTo(BigDecimal.ZERO) == 0)) {
                if (customerCreditLine.compareTo(entity.getPayPrice().subtract(entity.getVoucherAmount())) >= 0) {
                    customerCreditLine = customerCreditLine.subtract(entity.getPayPrice());
                    entity.setPayStatus(OrderStatusEnum.PAID);
                    entity.setPayTime(new Date());
                    entity.setCreditLineAmount(entity.getPayPrice());
                    sysCustomerCreditLineService.useCreditLine(entity.getId(), entity.getBillNo(), entity.getPayPrice(), entity.getCustomerId());
                } else {
                    entity.setCreditLineAmount(customerCreditLine);
                    sysCustomerCreditLineService.useCreditLine(entity.getId(), entity.getBillNo(), customerCreditLine, entity.getCustomerId());
                    customerCreditLine = BigDecimal.ZERO;
                }
                customerCreditLineMap.put(entity.getCustomerId(), customerCreditLine);
            }
        });
        // 处理余额
        List<SysCustomerEntity> customerList = sysCustomerMapper.selectList(Wrappers.lambdaQuery(SysCustomerEntity.class)
                .in(SysCustomerEntity::getId, customerIdSet));
        Map<Long, BigDecimal> customerBalanceMap = customerList.stream().collect(Collectors.toMap(SysCustomerEntity::getId, SysCustomerEntity::getBalance));
        insertList.forEach(entity -> {
            BigDecimal customerBalance = customerBalanceMap.get(entity.getCustomerId());
            if (!(entity.getPayStatus().equals(OrderStatusEnum.PAID) ||
                    customerBalance == null ||
                    customerBalance.compareTo(BigDecimal.ZERO) == 0)) {
                // 手动进行 null 检查
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
//        customerList.forEach(c -> c.setBalance(customerBalanceMap.get(c.getId())));
//        sysCustomerMapper.batchUpdateById(customerList);

        Map<SourceRegionsEnum, List<Long>> stopInstanceIdMap = insertList.stream()
                .filter(entity -> (entity.getSourceType().equals(ECS) || entity.getSourceType().equals(SourceTypeEnum.GPU_SERVER)) &&
                        entity.getPayStatus().equals(OrderStatusEnum.UNPAID)
                        && SourceRegionsEnum.getByName(entity.getRegion()) != null
                )
                .collect(Collectors.groupingBy(entity -> SourceRegionsEnum.getByName(entity.getRegion()),
                        Collectors.mapping(SysCustomerBillEntity::getSourceId, Collectors.toList())));

        // 未支付的实例，停止实例
        if (ObjectUtils.isNotEmpty(stopInstanceIdMap)) {
            for (Map.Entry<SourceRegionsEnum, List<Long>> entry : stopInstanceIdMap.entrySet()) {
                PcInstancesStatusHandlerEdit edit = new PcInstancesStatusHandlerEdit();
                edit.setIdList(entry.getValue());
                edit.setStatusEnum(EcsStatusEnum.STOPPING);
                edit.setStoppedMode("StopCharging");
                pcConsoleService.handlerInstancesStatus(edit, entry.getKey());
            }
        }
        // 入库
        sysCustomerBillMapper.batchInsert(insertList);
        log.info("账单处理完成");
        addDelayQueue();
    }

    private List<ListForListBillDetailOutput> getVoengineBillList(BillingApi billingApi, ListBillDetailRequest request) throws ApiException {
        Integer limit = request.getLimit();
        ListBillDetailResponse response = billingApi.listBillDetail(request);
        List<ListForListBillDetailOutput> resultList = response.getList();
        Integer total = response.getTotal();
        log.info("获取到火山账单总数量：{}", total);
        log.info("获取到火山账单数据：{}", JSON.toJSONString(resultList));
        if (total > limit) {
            // 需要分页获取
            int totalPage;
            if ((total & limit) == 0) {
                totalPage = total / limit;
            } else {
                totalPage = total / limit + 1;
            }
            int offset = limit;
            for (int i = 1; i < totalPage; i++) {
                // 计算每次偏移量
                request.setOffset(offset);
                // 获取下一页
                ListBillDetailResponse response1 = billingApi.listBillDetail(request);
                resultList.addAll(response1.getList());
                offset = offset + limit;
            }
        }
        log.info("获取到火山账单最终获取数量：{}", resultList.size());
        return resultList;
    }

    @Override
    public String messageType() {
        return SOURCE_SETTLEMENT_QUEUE_TYPE;
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        for (Object queue : cacheQueueService.getAllQueue()) {
            if (queue instanceof QueueMessageBody messageBody) {
                if (messageBody.getMessageType().equals(messageType())) {
                    return;
                }
            }
        }
        addDelayQueue();
    }

    private void addDelayQueue() {
        // 获取明天凌晨3点的时间
        for (Object queue : cacheQueueService.getAllQueue()) {
            if (queue instanceof QueueMessageBody messageBody) {
                if (messageBody.getMessageType().equals(messageType())) {
                    cacheQueueService.remove(messageBody);
                }
            }
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrowOne = now.toLocalDate().plusDays(1).atTime(LocalTime.of(3, 0));
        cacheQueueService.addDelayQueue(messageType(), "init", ChronoUnit.SECONDS.between(now, tomorrowOne));
//        cacheQueueService.addDelayQueue(messageType(), "init", 60);
    }
}