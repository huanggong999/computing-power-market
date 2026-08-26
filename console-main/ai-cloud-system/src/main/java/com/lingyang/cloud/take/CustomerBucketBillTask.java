package com.lingyang.cloud.take;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.entity.*;
import com.lingyang.cloud.enums.customer.SysTransactionType;
import com.lingyang.cloud.enums.order.OrderStatusEnum;
import com.lingyang.cloud.enums.source.SourceTypeEnum;
import com.lingyang.cloud.enums.system.SystemConfigEnum;
import com.lingyang.cloud.mapper.*;
import com.lingyang.cloud.model.config.SystemPriceRationConfigModel;
import com.lingyang.cloud.service.SysConfigService;
import com.lingyang.cloud.service.SysCustomerCreditLineService;
import com.lingyang.cloud.service.SysCustomerService;
import com.lingyang.cloud.service.SysCustomerVoucherService;
import com.lingyang.common.core.utils.DateUtils;
import com.lingyang.common.core.utils.IdUtils;
import com.lingyang.common.core.utils.Optional;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static com.lingyang.cloud.enums.source.SourceChargeTypeEnum.POSTPAID_BY_HOUR;
import static com.lingyang.cloud.enums.source.SourceTypeEnum.CLOUD_OBJECT_STORAGE;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/2/26 15:02
 */
@Slf4j
@Component
public class CustomerBucketBillTask {

    @Resource
    private SysFileMapper sysFileMapper;
    @Resource
    private SysCustomerBillMapper sysCustomerBillMapper;
    @Resource
    private SysCustomerBucketMapper sysCustomerBucketMapper;
    @Resource
    private SysCustomerDiscountMapper sysCustomerDiscountMapper;
    @Resource
    private SysConfigService sysConfigService;
    @Resource
    private SysCustomerVoucherService sysCustomerVoucherService;
    @Resource
    private SysCustomerCreditLineService sysCustomerCreditLineService;
    @Resource
    private SysCustomerMapper sysCustomerMapper;
    @Resource
    private SysCustomerService sysCustomerService;

    /**
     * 每天早上6点执行
     */
    @Scheduled(cron = "0 0 6 * * ?")
    public void calculateDailyStorageBill() {
        log.info("开始执行计算存储费用");
        // 获取前一天早上6点的时间
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1).withHour(6).withMinute(0).withSecond(0).withNano(0);
        Date yesterdayDate = Date.from(yesterday.atZone(ZoneId.systemDefault()).toInstant());

        // 查询昨天的存储文件记录
        List<SysFile> files = sysFileMapper.selectList(new LambdaQueryWrapper<SysFile>()
                .eq(SysFile::getDelFlag,0)
                .eq(SysFile::getType,2)
                .ge(SysFile::getCreateTime, yesterdayDate)
                .le(SysFile::getCreateTime, new Date())
        );
        log.info("查询到昨天{}的文件有：{}", yesterdayDate,files);
        if (files.isEmpty()) {
            return;
        }
        // 按用户ID分组
        Map<Long, List<SysFile>> userFiles = files.stream()
                .collect(Collectors.groupingBy(SysFile::getUserId));

        // 获取基础价格
        BigDecimal standardPrice = new BigDecimal("0.099");
        BigDecimal redundantPrice = new BigDecimal("0.15");

        Set<Long> ids = files.stream().map(SysFile::getUserId).collect(Collectors.toSet());
        if (ids.isEmpty()){
            return;
        }
        Map<Long, Map<SourceTypeEnum, BigDecimal>> customerDiscountMap = Optional.of(sysCustomerDiscountMapper.selectList(Wrappers.lambdaQuery(SysCustomerDiscountEntity.class)
                        .in(SysCustomerDiscountEntity::getCustomerId, ids)))
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
        List<SysCustomerBillEntity> billList = new ArrayList<>();
        // 处理每个用户的账单
        for (Map.Entry<Long, List<SysFile>> entry : userFiles.entrySet()) {
            Long customerId = entry.getKey();
            List<SysFile> customerFiles = entry.getValue();
            Map<Long, BigDecimal> bucketSizeMap = customerFiles.stream()
                    .collect(Collectors.groupingBy(
                            SysFile::getBucketId,
                            Collectors.reducing(
                                    BigDecimal.ZERO,
                                    file -> file.getSize().divide(new BigDecimal(1024), 4, RoundingMode.HALF_UP),  // 转换为GB并保留2位小数
                                    BigDecimal::add
                            )
                    ));
            // 计算存储费用
            BigDecimal discount;
            for (Map.Entry<Long, BigDecimal> bucketEntry : bucketSizeMap.entrySet()) {
                Long bucketId = bucketEntry.getKey();
                BigDecimal sizeInGb = bucketEntry.getValue();

                // 根据冗余类型选择价格
                SysCustomerBucketEntity sysCustomerBucketEntity = sysCustomerBucketMapper.selectOne(Wrappers.lambdaQuery(SysCustomerBucketEntity.class)
                        .eq(SysCustomerBucketEntity::getId, bucketId));
                if (sysCustomerBucketEntity == null) {
                    log.error("Bucket not found for id: {}", bucketId);
                    continue;
                }
                //单价
                BigDecimal price = sysCustomerBucketEntity.getRedundancyType() == 0 ? standardPrice : redundantPrice;

                // 获取平台溢价后的价格
                Map<SourceTypeEnum, BigDecimal> map = customerDiscountMap.get(customerId);

                if (map == null) {
                    discount = BigDecimal.ONE;
                } else {
                    discount = map.get(CLOUD_OBJECT_STORAGE);
                }
                if (discount == null) {
                    discount = BigDecimal.ONE;
                }
                //原价
                BigDecimal originalPrice = sizeInGb.multiply(price.divide(BigDecimal.valueOf(3600), 8, RoundingMode.UP));

                //溢价价格
                BigDecimal premiumPrice = sellPriceRatioConfig.calculatePremium(originalPrice);

                // 用户折扣金额
                BigDecimal userDiscountAmount = premiumPrice.multiply(discount).setScale(8, RoundingMode.UP);


                // 创建账单记录
                SysCustomerBillEntity bill = new SysCustomerBillEntity();
                bill.setBillNo(IdUtils.simpleUUID());
                bill.setBill(DateUtils.parseDateToStr("yyyy-MM", yesterdayDate));
                bill.setCustomerId(customerId);
                bill.setBillDate(yesterdayDate);
                // 存储费用类型
                bill.setBillType("消费-使用");
                bill.setSettleType("结算");
                bill.setUnitId(CLOUD_OBJECT_STORAGE.getDesc());
                bill.setSourceType(CLOUD_OBJECT_STORAGE);
                bill.setChargeType(POSTPAID_BY_HOUR);
                bill.setRegion("华北2（北京）");
                bill.setPriceType("固定单价");
                bill.setUsage(sizeInGb);
                bill.setUsageUnit("GB");
                bill.setPrice(price);
                bill.setOriginalPrice(originalPrice);
                bill.setUserDiscountAmount(userDiscountAmount);
                bill.setPayPrice(originalPrice.setScale(2, RoundingMode.UP));
                billList.add(bill);
            }
            // 处理代金卷
            Map<Long, BigDecimal> customerVoucherBalanceMap = sysCustomerVoucherService.batchUserVoucherBalance(ids);
            billList.forEach(entity -> {
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
            Map<Long, BigDecimal> customerCreditLineMap = sysCustomerCreditLineService.batchUserCreditLine(ids);
            billList.forEach(entity -> {
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
                    .in(SysCustomerEntity::getId, ids));
            Map<Long, BigDecimal> customerBalanceMap = customerList.stream().collect(Collectors.toMap(SysCustomerEntity::getId, SysCustomerEntity::getBalance));
            billList.forEach(entity -> {
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
            log.info("昨天{}的账单处理完成，账单信息：{}",yesterdayDate, billList);
            // 保存账单
            sysCustomerBillMapper.batchInsert(billList);
        }
    }
}
