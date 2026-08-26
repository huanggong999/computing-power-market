package com.lingyang.cloud.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.ali.AlipayConfig;
import com.lingyang.cloud.api.model.vo.PcIsEmailExistVO;
import com.lingyang.cloud.api.model.vo.PcUpdateAutoRenewVO;
import com.lingyang.cloud.api.model.vo.PcUpdateCustomerNameVO;
import com.lingyang.cloud.common.constant.CacheQueueConstant;
import com.lingyang.cloud.config.WeiXinConfig;
import com.lingyang.cloud.config.vo.WxSessionResultVO;
import com.lingyang.cloud.entity.*;
import com.lingyang.cloud.enums.coupon.CouponRangeEnum;
import com.lingyang.cloud.enums.coupon.CouponUseStatusEnum;
import com.lingyang.cloud.enums.customer.SysTransactionType;
import com.lingyang.cloud.enums.order.OrderStatusEnum;
import com.lingyang.cloud.enums.order.OrderTypeEnum;
import com.lingyang.cloud.enums.source.ContainerStatusEnum;
import com.lingyang.cloud.enums.source.EcsStatusEnum;
import com.lingyang.cloud.enums.source.SourceChargeTypeEnum;
import com.lingyang.cloud.enums.source.SourceTypeEnum;
import com.lingyang.cloud.enums.system.StatusEnum;
import com.lingyang.cloud.enums.system.SystemConfigEnum;
import com.lingyang.cloud.handler.OnlinePayHandler;
import com.lingyang.cloud.handler.model.OnlinePayParam;
import com.lingyang.cloud.handler.model.OnlinePayRefundsParam;
import com.lingyang.cloud.mapper.*;
import com.lingyang.cloud.model.config.EcsSystemVolumeConfigModel;
import com.lingyang.cloud.model.config.EipAddressConfigModel;
import com.lingyang.cloud.model.config.SystemPriceRationConfigModel;
import com.lingyang.cloud.model.dto.SysActiveCouponDTO;
import com.lingyang.cloud.model.dto.SysActiveCouponDetailsDTO;
import com.lingyang.cloud.model.dto.SysEcsWorkPageDTO;
import com.lingyang.cloud.model.dto.feilian.AddDepartmentDTO;
import com.lingyang.cloud.model.dto.feilian.VpnInfoDTO;
import com.lingyang.cloud.model.edit.order.SysOrderCreateDTO;
import com.lingyang.cloud.model.edit.order.SysOrderSourceCreateDTO;
import com.lingyang.cloud.model.query.coupon.SysCustomerCouponQuery;
import com.lingyang.cloud.model.query.home.SysExtendOrderQuery;
import com.lingyang.cloud.model.query.order.SysOrderQuery;
import com.lingyang.cloud.model.vo.customer.SysCustomerCouponListVO;
import com.lingyang.cloud.model.vo.feilian.*;
import com.lingyang.cloud.model.vo.order.ExtendOrderVO;
import com.lingyang.cloud.model.vo.order.OrderCreateVO;
import com.lingyang.cloud.model.vo.product.SysOpenProductUserPwdVO;
import com.lingyang.cloud.service.*;
import com.lingyang.cloud.tencent.client.WeChatPayClient;
import com.lingyang.cloud.tencent.config.WeChatPayConfig;
import com.lingyang.cloud.utils.FeiLianUtils;
import com.lingyang.common.cache.CacheQueueService;
import com.lingyang.common.core.exception.http.HttpServiceException;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.security.SecurityContext;
import com.lingyang.common.core.security.model.LoginUserInfoDetail;
import com.lingyang.common.core.utils.Optional;
import com.lingyang.common.core.utils.*;
import com.lingyang.common.datasource.model.BaseEntity;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import com.wechat.pay.java.service.payments.nativepay.model.Amount;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayRequest;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.lingyang.cloud.common.constant.CacheQueueConstant.*;
import static com.lingyang.cloud.enums.source.SourceChargeTypeEnum.*;
import static com.lingyang.cloud.enums.source.SourceChargeUnitEnum.*;
import static com.lingyang.cloud.enums.source.SourceTypeEnum.ECS;
import static java.util.Collections.emptyList;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/12 17:30
 */
@Slf4j
@Service
public class SysOrderServiceImpl implements SysOrderService {
    @Resource
    private SysOrderMapper sysOrderMapper;

    @Autowired
    private SysNetworkProductMapper sysNetworkProductMapper;
    @Autowired
    private SysNetworkFormMapper sysNetworkFormMapper;

    @Autowired
    private SysNetworkValueMapper sysNetworkValueMapper;
    @Resource
    private SysOrderSourceMapper sysOrderSourceMapper;
    @Autowired
    private SysExtendMapper sysExtendMapper;

    @Autowired
    private SysExtendWithdrawalRecordMapper sysExtendWithdrawalRecordMapper;
    @Resource
    private SysCustomerCouponMapper sysCustomerCouponMapper;
    @Resource
    private SysEcsMapper sysEcsMapper;
    @Resource
    private SysConfigService sysConfigService;
    @Resource
    private SysCustomerDiscountMapper sysCustomerDiscountMapper;
    @Autowired
    private SysCustomerNetworkMapper sysCustomerNetworkMapper;
    @Resource
    private SysConfigService configService;

    @Resource
    private SysCustomerMapper sysCustomerMapper;
    @Autowired
    private WeiXinConfig weiXinConfig;
    @Resource
    private SysCustomerVoucherService sysCustomerVoucherService;
    @Resource
    private SysCustomerCreditLineService sysCustomerCreditLineService;
    @Resource
    private SysCustomerCouponService sysCustomerCouponService;
    @Resource
    private SysCustomerInstancesMapper sysCustomerInstancesMapper;
    @Resource
    private SysImageRepositoryMapper sysImageRepositoryMapper;
    @Resource
    private SysCustomerVolumeMapper sysCustomerVolumeMapper;
    @Resource
    private SysCustomerContainerMapper sysCustomerContainerMapper;
    @Resource
    private SysCustomerEipMapper sysCustomerEipMapper;
    @Autowired
    private SysCustomerVoucherUseLogMapper sysCustomerVoucherUseLogMapper;
    @Autowired
    private SysCustomerCreditLineUseLogMapper sysCustomerCreditLineUseLogMapper;
    @Autowired
    private SysCustomerVoucherMapper sysCustomerVoucherMapper;
    @Autowired
    private SysCustomerCreditLineMapper sysCustomerCreditLineMapper;
    @Resource
    private CacheQueueService cacheQueueService;
    @Resource
    private SysCustomerBillService sysCustomerBillService;
    @Resource
    private OnlinePayHandler onlinePayHandler;

    @Resource(name = "aliPayHttpClient")
    private DefaultAlipayClient alipayClient;
    @Resource
    private AlipayConfig alipayConfig;
    @Resource
    private SysCustomerService sysCustomerService;
    @Autowired
    private SysRechargeActivityMapper sysRechargeActivityMapper;
    @Autowired
    private SysRechargeActivityRewardsMapper sysRechargeActivityRewardsMapper;
    @Resource
    private SysVolumeMapper sysVolumeMapper;
    @Resource
    private SysActiveRecordMapper sysActiveRecordMapper;
    @Resource
    private SysActiveCouponMapper sysActiveCouponMapper;
    @Resource
    private SysActiveCenterMapper sysActiveCenterMapper;

    @Resource
    private SysCustomerEcsWorkMapper sysCustomerEcsWorkMapper;

    @Resource
    private FeiLianUtils flashLianUtils;

    @Resource
    private SysMessageMapper sysMessageMapper;

    @Value("${feiLian.parentDepartmentId}")
    private String parentDepartmentId;

    @Resource
    private SysNetworkProductIpMapper sysNetworkProductIpMapper;

    @Resource
    private WeChatPayClient weChatPayClient;
    @Resource
    private WeChatPayConfig weChatPayConfig;

    @javax.annotation.Resource
    private com.github.binarywang.wxpay.service.WxPayService wxPayService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderCreateVO createOrder(SysOrderCreateDTO createDTO) {
        Long userId = SecurityContext.getUserInfo().getUserId();
        if (createDTO.getOrderType() != OrderTypeEnum.BALANCE) {
            // 校验是否欠费
            BigDecimal arrearsAmount = sysCustomerBillService.getArrearsAmount(userId);
            if (arrearsAmount.compareTo(BigDecimal.ZERO) > 0) {
                throw new HttpServiceException("当前账户有未缴清的账单，请先缴清后再下单");
            }
        }
        if (createDTO.getOrderSource() != null && !createDTO.getOrderSource().isEmpty()) {
            for (SysOrderSourceCreateDTO sysOrderSourceCreateDTO : createDTO.getOrderSource()) {
                Long count = sysCustomerInstancesMapper.selectCount(
                        Wrappers.lambdaQuery(SysCustomerInstancesEntity.class)
                                .eq(SysCustomerInstancesEntity::getOrderSourceUid, sysOrderSourceCreateDTO.getUid())
                );
                if (count > 0) {
                    throw new HttpServiceException("该实例ID重复，请重新生成");
                }
            }
        }

        SysOrderEntity sysOrderEntity = buildOrderInfo(createDTO);
        Optional.of(sysOrderEntity.getCouponId())
                .ifPresent(couponId -> {
                    SysCustomerCouponEntity entity = new SysCustomerCouponEntity();
                    entity.setId(couponId);
                    entity.setStatus(CouponUseStatusEnum.USED);
                    sysCustomerCouponMapper.updateById(entity);
                });
        // 处理代金卷支付
        sysCustomerVoucherService.useVoucher(sysOrderEntity.getId(), sysOrderEntity.getOrderNo(), sysOrderEntity.getVoucherAmount(), userId);
        // 处理授信额支付
        sysCustomerCreditLineService.useCreditLine(sysOrderEntity.getId(), sysOrderEntity.getOrderNo(), sysOrderEntity.getCreditLineAmount(), userId);
        // 处理余额支付
        Optional.of(sysOrderEntity.getBalancePayAmount())
                .ifPresent(balancePayAmount -> {
                    if (balancePayAmount.compareTo(BigDecimal.ZERO) > 0) {
                        sysCustomerService.updateCustomerBalance(sysOrderEntity.getId(), sysOrderEntity.getOrderNo(), userId, balancePayAmount, SysTransactionType.PAY_DISCOUNT);
                    }
                });
        sysOrderEntity.setBandwidth(createDTO.getBandwidth());
        sysOrderMapper.insert(sysOrderEntity);
        sysOrderSourceMapper.batchInsert(sysOrderEntity.getOrderSourceList());

        OrderCreateVO result = new OrderCreateVO();
        result.setOrderNo(sysOrderEntity.getOrderNo());
        // 处理在线支付
        if (sysOrderEntity.getOnlinePayAmount() != null && sysOrderEntity.getOnlinePayAmount().compareTo(BigDecimal.ZERO) > 0) {
            result.setOnlinePay(sysOrderEntity.getOnlinePayType());
            OnlinePayParam payParam = new OnlinePayParam();
            payParam.setOrderNo(sysOrderEntity.getOrderNo());
            payParam.setTimeExpire(DateUtils.getDate(DateUtils.getNowDate(), 15, Calendar.MINUTE));
            payParam.setDescription("在线支付");
            payParam.setPayAmount(sysOrderEntity.getOnlinePayAmount());
            if (createDTO.getIsApplet() != null && createDTO.getIsApplet()) {
//                SysCustomerEntity entity = sysCustomerMapper.selectById(userId);
//                if (StringUtils.isBlank(entity.getOpenId())) {
                    WxSessionResultVO sessionResultVO = null;
                    try {
                        sessionResultVO = weiXinConfig.getSessionKey(createDTO.getLoginCode());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    log.info("微信小程序获取 openID 返回参数: {}", JSON.toJSONString(sessionResultVO, true));
                    payParam.setOpenid(sessionResultVO.getOpenid());
//                } else {
//                    payParam.setOpenid(entity.getOpenId());
//                }


                payParam.setIp(createDTO.getIp());
                payParam.setIsApplet(true);
            } else {
                payParam.setIsApplet(false);
            }
            result.setOnlinePayParam(onlinePayHandler.pay(sysOrderEntity.getOnlinePayType(), payParam));

            cacheQueueService.addDelayQueue(CacheQueueConstant.ORDER_EXPIRE_QUEUE_TYPE, sysOrderEntity.getId().toString(), 15 * 60);
        } else {
            // 不需要在线支付，表示当前订单要么余额和代金卷抵扣完成，要么为后付费订单
            paySuccess(sysOrderEntity, null);
        }
        //如果该用户是被邀请人，下单金额达标后，将送优惠卷给邀请人
        SysActiveRecordEntity sysActiveRecordEntity = sysActiveRecordMapper.selectOne(Wrappers.lambdaQuery(SysActiveRecordEntity.class)
                .eq(SysActiveRecordEntity::getRegisterUserId, userId));
        if (sysActiveRecordEntity != null) {
            SysActiveCenterEntity sysActiveCenterEntity = sysActiveCenterMapper.selectById(sysActiveRecordEntity.getId());
            if (sysActiveCenterEntity != null){
                if (createDTO.getAmount().compareTo(sysActiveCenterEntity.getTargetAmount()) >= 0){
                    List<SysActiveCouponDetailsDTO> coupons = sysActiveCouponMapper.getCoupons(sysActiveRecordEntity.getActiveId());
                    List<SysActiveCouponDTO> list = filterAndConvertCoupons(coupons);
                    List<SysCouponEntity> couponEntityList = BeanUtil.copyToList(list, SysCouponEntity.class);
                    sysCustomerCouponService.receiveCoupon(sysActiveRecordEntity.getShareUserId(), couponEntityList);
                }
            }
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderCreateVO createAGOrder(SysOrderCreateDTO createDTO) {
        Long userId = SecurityContext.getUserInfo().getUserId();
        if (createDTO.getOrderType() != OrderTypeEnum.BALANCE) {
            // 校验是否欠费
            BigDecimal arrearsAmount = sysCustomerBillService.getArrearsAmount(userId);
            if (arrearsAmount.compareTo(BigDecimal.ZERO) > 0) {
                throw new HttpServiceException("当前账户有未缴清的账单，请先缴清后再下单");
            }
        }
        List<SysNetworkValueEntity> sysNetworkValueEntityList = sysNetworkValueMapper.selectList(Wrappers.lambdaQuery(SysNetworkValueEntity.class)
                .eq(SysNetworkValueEntity::getActualStatus, 2));

        if (StringUtils.isBlank(createDTO.getAgiCustomerName())){
            throw new HttpServiceException("客户名称为空，请重新输入");
        }

        if (createDTO.getAgiCustomerName() != null  && createDTO.getOrderType().equals(OrderTypeEnum.PRODUCT)) {
            // 检查 agiCustomerName 是否已经存在于数据库中
            boolean exists = sysNetworkValueEntityList.stream()
                    .anyMatch(entity -> createDTO.getAgiCustomerName().equals(entity.getAgiCustomerName()));

            if (exists) {
                throw new HttpServiceException("客户名称已存在，请重新输入");
            }
        }

        SysOrderEntity sysOrderEntity = buildAGOrderInfo(createDTO);
        Optional.of(sysOrderEntity.getCouponId())
                .ifPresent(couponId -> {
                    SysCustomerCouponEntity entity = new SysCustomerCouponEntity();
                    entity.setId(couponId);
                    entity.setStatus(CouponUseStatusEnum.USED);
                    sysCustomerCouponMapper.updateById(entity);
                });
        // 处理代金卷支付
        sysCustomerVoucherService.useVoucher(sysOrderEntity.getId(), sysOrderEntity.getOrderNo(), sysOrderEntity.getVoucherAmount(), userId);
        // 处理授信额支付
        sysCustomerCreditLineService.useCreditLine(sysOrderEntity.getId(), sysOrderEntity.getOrderNo(), sysOrderEntity.getCreditLineAmount(), userId);
        // 处理余额支付
        Optional.of(sysOrderEntity.getBalancePayAmount())
                .ifPresent(balancePayAmount -> {
                    if (balancePayAmount.compareTo(BigDecimal.ZERO) > 0) {
                        sysCustomerService.updateCustomerBalance(sysOrderEntity.getId(), sysOrderEntity.getOrderNo(), userId, balancePayAmount, SysTransactionType.PAY_DISCOUNT);
                    }
                });

        // 添加产品支付表单数据
        if (createDTO.getOrderType().equals(OrderTypeEnum.PRODUCT)) {
            SysNetworkProductEntity entity1 = sysNetworkProductMapper.selectById(createDTO.getNetworkProductId());
            SysNetworkValueEntity entity = new SysNetworkValueEntity();
            entity.setFormType(2);
            entity.setProductId(createDTO.getNetworkProductId());
            entity.setUserId(userId);
            entity.setProductName(entity1.getName());
//            entity.setJson(createDTO.getPayFormValue().getJson());
            entity.setBandwidth(createDTO.getBandwidth());
            entity.setNetworkCount(createDTO.getNetworkCount());
            entity.setIpCount(createDTO.getIpCount());
            entity.setActualStatus(1);
            Date date = new Date();
//            LocalDate currentDate = LocalDate.now();
            entity.setActualAgiOpenTime(date);
            SysOrderSourceCreateDTO sysOrderSourceCreateDTO = createDTO.getOrderSource().get(0);
//            if (sysOrderSourceCreateDTO.getChargeType() == POSTPAID_BY_HOUR){
////                entity.setActualAgiExpireTime(DateUtils.addDays(date, sysOrderSourceCreateDTO.getDuration()));
//            }else if (sysOrderSourceCreateDTO.getChargeType() == POSTPAID_BY_MONTH){
//                LocalDate futureDate = currentDate.plusMonths(sysOrderSourceCreateDTO.getDuration());
//                long days = ChronoUnit.DAYS.between(currentDate, futureDate);
//                entity.setActualAgiExpireTime(DateUtils.addDays(date, (int) days));
//            }else if (sysOrderSourceCreateDTO.getChargeType() == POSTPAID_BY_YEAR){
//                LocalDate futureDate = currentDate.plusYears(sysOrderSourceCreateDTO.getDuration());
//                long days = ChronoUnit.DAYS.between(currentDate, futureDate);
//                entity.setActualAgiExpireTime(DateUtils.addDays(date, (int) days));
//            }
            entity.setPayStatus(0);
            entity.setUnitPrice(entity1.getPayPrice());
            entity.setChargeType(sysOrderSourceCreateDTO.getChargeType());
            entity.setDuration(sysOrderSourceCreateDTO.getDuration());
            entity.setDurationUnit(sysOrderSourceCreateDTO.getDurationUnit());
            entity.setMobile(createDTO.getMobile());
            entity.setEmail(createDTO.getEmail());
            entity.setRemark(createDTO.getRemark());
            entity.setAgiCustomerName(createDTO.getAgiCustomerName());
            entity.setIsAutoRenew(createDTO.getIsAutoRenew());
            sysNetworkValueMapper.insert(entity);

            sysOrderEntity.setNetworkValueId(entity.getId());
            sysOrderEntity.setNetworkCount(createDTO.getNetworkCount());
            sysOrderEntity.setNetworkDay(createDTO.getNetworkDay());
            sysOrderEntity.setBandwidth(createDTO.getBandwidth());
            sysOrderEntity.setActualStatus(1);
        }else if (createDTO.getOrderType().equals(OrderTypeEnum.RENEW_PRODUCT) || createDTO.getOrderType().equals(OrderTypeEnum.UPGRADE_PRODUCT)){
            SysOrderEntity sysOrderEntity1 = sysOrderMapper.selectOne(Wrappers.lambdaQuery(SysOrderEntity.class)
                    .eq(SysOrderEntity::getOrderNo, createDTO.getOrderNo()));
            SysNetworkValueEntity entity = sysNetworkValueMapper.selectOne(Wrappers.lambdaQuery(SysNetworkValueEntity.class)
                    .eq(SysNetworkValueEntity::getId, sysOrderEntity1.getNetworkValueId()));
            sysOrderEntity.setNetworkValueId(entity.getId());
            sysOrderEntity.setNetworkCount(createDTO.getNetworkCount());
            sysOrderEntity.setNetworkDay(createDTO.getNetworkDay());
            sysOrderEntity.setBandwidth(createDTO.getBandwidth());
            sysOrderEntity.setIpCount(createDTO.getIpCount());
            sysOrderEntity.setActualStatus(1);
        }

        sysOrderMapper.insert(sysOrderEntity);
        sysOrderSourceMapper.batchInsert(sysOrderEntity.getOrderSourceList());

        OrderCreateVO result = new OrderCreateVO();
        result.setOrderNo(sysOrderEntity.getOrderNo());
        // 处理在线支付
        if (sysOrderEntity.getOnlinePayAmount() != null && sysOrderEntity.getOnlinePayAmount().compareTo(BigDecimal.ZERO) > 0) {
            result.setOnlinePay(sysOrderEntity.getOnlinePayType());
            OnlinePayParam payParam = new OnlinePayParam();
            payParam.setOrderNo(sysOrderEntity.getOrderNo());
            payParam.setTimeExpire(DateUtils.getDate(DateUtils.getNowDate(), 15, Calendar.MINUTE));
            payParam.setDescription("在线支付");
            payParam.setPayAmount(sysOrderEntity.getOnlinePayAmount());
            if (createDTO.getIsApplet() != null && createDTO.getIsApplet()) {
//                SysCustomerEntity entity = sysCustomerMapper.selectById(userId);
//                if (StringUtils.isBlank(entity.getOpenId())) {
                WxSessionResultVO sessionResultVO = null;
                try {
                    sessionResultVO = weiXinConfig.getSessionKey(createDTO.getLoginCode());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                log.info("微信小程序获取 openID 返回参数: {}", JSON.toJSONString(sessionResultVO, true));
                payParam.setOpenid(sessionResultVO.getOpenid());
//                } else {
//                    payParam.setOpenid(entity.getOpenId());
//                }


                payParam.setIp(createDTO.getIp());
                payParam.setIsApplet(true);
            } else {
                payParam.setIsApplet(false);
            }
            result.setOnlinePayParam(onlinePayHandler.pay(sysOrderEntity.getOnlinePayType(), payParam));

            cacheQueueService.addDelayQueue(CacheQueueConstant.ORDER_EXPIRE_QUEUE_TYPE, sysOrderEntity.getId().toString(), 15 * 60);
        } else {
            // 不需要在线支付，表示当前订单要么余额和代金卷抵扣完成，要么为后付费订单
            paySuccess(sysOrderEntity, null);
        }
        //如果该用户是被邀请人，下单金额达标后，将送优惠卷给邀请人
        SysActiveRecordEntity sysActiveRecordEntity = sysActiveRecordMapper.selectOne(Wrappers.lambdaQuery(SysActiveRecordEntity.class)
                .eq(SysActiveRecordEntity::getRegisterUserId, userId));
        if (sysActiveRecordEntity != null) {
            SysActiveCenterEntity sysActiveCenterEntity = sysActiveCenterMapper.selectById(sysActiveRecordEntity.getId());
            if (sysActiveCenterEntity != null){
                if (createDTO.getAmount().compareTo(sysActiveCenterEntity.getTargetAmount()) >= 0){
                    List<SysActiveCouponDetailsDTO> coupons = sysActiveCouponMapper.getCoupons(sysActiveRecordEntity.getActiveId());
                    List<SysActiveCouponDTO> list = filterAndConvertCoupons(coupons);
                    List<SysCouponEntity> couponEntityList = BeanUtil.copyToList(list, SysCouponEntity.class);
                    sysCustomerCouponService.receiveCoupon(sysActiveRecordEntity.getShareUserId(), couponEntityList);
                }
            }
        }
        return result;
    }

    private List<SysActiveCouponDTO> filterAndConvertCoupons(List<SysActiveCouponDetailsDTO> coupons) {
        return coupons.stream()
                .filter(coupon -> coupon.getCouponType() == 2)
                .map(coupon -> BeanUtil.copyProperties(coupon, SysActiveCouponDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public SysOrderEntity buildOrderInfo(SysOrderCreateDTO createDTO) {
        LoginUserInfoDetail userInfo = SecurityContext.getUserInfo();
        // 处理优惠卷。计算优惠金额
        AtomicReference<SysCustomerCouponListVO> couponAtomic = new AtomicReference<>(null);
        Optional.of(createDTO.getCouponId())
                .ifPresent(couponId -> {
                    SysCustomerCouponQuery query = new SysCustomerCouponQuery();
                    query.setCustomerId(userInfo.getUserId());
                    query.setId(couponId);
                    List<SysCustomerCouponListVO> list = sysCustomerCouponMapper.getList(query);
                    if (ObjectUtils.isEmpty(list)) {
                        throw new HttpServiceException("优惠卷不存在");
                    }
                    SysCustomerCouponListVO coupon = list.get(0);
                    if (!coupon.getStatus().equals(CouponUseStatusEnum.WAITING)) {
                        throw new HttpServiceException("优惠卷状态异常");
                    }
                    couponAtomic.set(coupon);
                });
        String orderNo = "Order" + DateUtils.getDate(DateUtils.YYYYMMDDHHMMSS) + RandomUtils.getNumberRandom(6);
        List<SysOrderSourceEntity> orderSourceList = switch (createDTO.getOrderType()) {
            //充值订单
            case BALANCE -> createBalanceOrder(createDTO);
            // 新购资源订单
            case NEW_RESOURCE -> createResourceOrder(createDTO);
            // 续费资源订单
            case RENEW_RESOURCE -> createRenewResourceOrder(createDTO.getOrderNo());
            // 网络产品购买订单
            case PRODUCT -> createProductOrder(createDTO);
            case RENEW_PRODUCT, UPGRADE_PRODUCT -> null;
        };
        if (ObjectUtils.isEmpty(orderSourceList)) {
            throw new HttpServiceException("订单资源列表为空");
        }
        log.info("订单资源列表: {}", JSON.toJSONString(orderSourceList, true));
        // 处理按量计费和包年包月价格
        Long orderId = IdUtils.nextId();
        final BigDecimal[] originalPrice = {BigDecimal.ZERO};
        final BigDecimal[] premiumPrice = {BigDecimal.ZERO};
        final BigDecimal[] userDiscountAmount = {BigDecimal.ZERO};
        final BigDecimal[] finalPayAmount = {BigDecimal.ZERO};
        final BigDecimal[] couponRangeAmount = {BigDecimal.ZERO};
        final BigDecimal[] hourPrice = {BigDecimal.ZERO};
        final int[] couponCount = {0};
        SysOrderEntity sysOrderEntity = new SysOrderEntity();
        orderSourceList.forEach(item -> {
            item.setOrderId(orderId);
            item.setOrderNo(orderNo);
            SourceChargeTypeEnum chargeType = item.getChargeType();
            BigDecimal number = BigDecimal.valueOf(item.getNumber());
            if (ObjectUtils.isNotEmpty(chargeType)) {
                if (!chargeType.equals(POSTPAID_BY_HOUR)) {
                    // 计算优惠卷
                    Optional.of(couponAtomic.get())
                            .ifPresent(coupon -> {
                                CouponRangeEnum rangeType = coupon.getRangeType();
                                if (rangeType.equals(CouponRangeEnum.ALL)) {
                                    couponRangeAmount[0] = couponRangeAmount[0].add(item.getFinalUnitPrice().multiply(number));
                                    couponCount[0] = couponCount[0]++;
                                } else if (rangeType.equals(CouponRangeEnum.SERVER) && item.getSourceType().equals(ECS)) {
                                    couponRangeAmount[0] = couponRangeAmount[0].add(item.getFinalUnitPrice().multiply(number));
                                    couponCount[0] = couponCount[0]++;
                                } else if (rangeType.equals(CouponRangeEnum.NETWORK) && item.getSourceType().equals(SourceTypeEnum.CLOUD_NETWORK)) {
                                    couponRangeAmount[0] = couponRangeAmount[0].add(item.getFinalUnitPrice().multiply(number));
                                    couponCount[0] = couponCount[0]++;
                                }
                            });
                } else {
                    if (item.getConfigDetail() != null) {
//                        long id = item.getConfigDetail().getLongValue("id");
//                        SysEcsEntity ecsEntity = sysEcsMapper.selectById(id);
//                        if (ecsEntity != null) {
//                             SystemPriceRationConfigModel priceRation = configService.getConfig(SystemConfigEnum.SELL_PRICE_RATIO).getValue(SystemPriceRationConfigModel.class);
//                             hourPrice[0] = hourPrice[0].add(priceRation.calculatePremium(ecsEntity.getHoursPrice()));
//                        }

                        BigDecimal hp = item.getConfigDetail().getBigDecimal("hoursPrice");
                        if (hp != null) {
                            hourPrice[0] = hourPrice[0].add(hp);
                        }
                    }
                    item.setFinalUnitPrice(BigDecimal.ZERO);
                }
            }

            // 产品购买的计算金额逻辑不一样
            if (createDTO.getOrderType().equals(OrderTypeEnum.PRODUCT)) {
                SysNetworkValueEntity sysNetworkValueEntity = sysNetworkValueMapper.selectById(createDTO.getNetworkPayValueId());
                createDTO.setBandwidth(sysNetworkValueEntity.getBandwidth());
                originalPrice[0] = originalPrice[0].add(item.getUnitPrice()
                        .multiply(BigDecimal.valueOf(createDTO.getNetworkCount())).multiply(number).multiply(
                        BigDecimal.valueOf(createDTO.getNetworkDay())
                ).multiply(BigDecimal.valueOf(createDTO.getBandwidth())));
                premiumPrice[0] = premiumPrice[0].add(item.getPremiumPrice().multiply(number));
                userDiscountAmount[0] = userDiscountAmount[0].add(item.getUserDiscountAmount().multiply(number));
                finalPayAmount[0] = finalPayAmount[0].add(item.getFinalUnitPrice().multiply(number).multiply(
                        BigDecimal.valueOf(createDTO.getNetworkDay())
                ).multiply(BigDecimal.valueOf(createDTO.getBandwidth()))
                        .multiply(BigDecimal.valueOf(createDTO.getNetworkCount())));
            } else {
                originalPrice[0] = originalPrice[0].add(item.getUnitPrice().multiply(number));
                premiumPrice[0] = premiumPrice[0].add(item.getPremiumPrice().multiply(number));
                userDiscountAmount[0] = userDiscountAmount[0].add(item.getUserDiscountAmount().multiply(number));
                finalPayAmount[0] = finalPayAmount[0].add(item.getFinalUnitPrice().multiply(number));

            }

        });


        // 判断优惠券是否达到满减金额，达到就减掉价格，记录优惠券id和减去的金额
        Optional.of(couponAtomic.get())
                .ifPresent(coupon -> {
                    if (coupon.getThresholdAmount().compareTo(couponRangeAmount[0]) > 0) {
                        throw new HttpServiceException("优惠卷不满足使用条件");
                    }
//                    BigDecimal sourceCouponAmount = coupon.getDeductionAmount().divide(BigDecimal.valueOf(couponCount[0]), 2, RoundingMode.DOWN);
//                    BigDecimal sourceCouponAmount = coupon.getDeductionAmount().subtract(BigDecimal.valueOf(couponCount[0]));
                    // 正确的计算方式应该是：
                    BigDecimal sourceCouponAmount = couponCount[0] > 0
                            ? coupon.getDeductionAmount().divide(BigDecimal.valueOf(couponCount[0]), 2, RoundingMode.DOWN)
                            : BigDecimal.ZERO;
                    sysOrderEntity.setCouponId(createDTO.getCouponId());
                    sysOrderEntity.setCouponAmount(coupon.getDeductionAmount());
                    // 修复逻辑
                    BigDecimal maxDiscount = coupon.getDeductionAmount().min(finalPayAmount[0]); // 取优惠券金额和订单金额的最小值
                    finalPayAmount[0] = finalPayAmount[0].subtract(maxDiscount);
                    CouponRangeEnum rangeType = coupon.getRangeType();
                    orderSourceList.forEach(item -> {
                        if (rangeType.equals(CouponRangeEnum.ALL)) {
                            item.setCouponDiscountAmount(sourceCouponAmount);
                        } else if (rangeType.equals(CouponRangeEnum.SERVER) && item.getSourceType().equals(ECS)) {
                            item.setCouponDiscountAmount(sourceCouponAmount);
                        } else if (rangeType.equals(CouponRangeEnum.NETWORK) && item.getSourceType().equals(SourceTypeEnum.CLOUD_NETWORK)) {
                            item.setCouponDiscountAmount(sourceCouponAmount);
                        }else if (rangeType.equals(CouponRangeEnum.CONTAINER) && item.getSourceType().equals(SourceTypeEnum.CONTAINER)) {
                            item.setCouponDiscountAmount(sourceCouponAmount);
                        }

                        // AGIC 的产品购买
                        if (createDTO.getOrderType().equals(OrderTypeEnum.PRODUCT) && rangeType.equals(CouponRangeEnum.AGIC)) {
                            item.setCouponDiscountAmount(sourceCouponAmount);
                        }

                    });
                });
        sysOrderEntity.setId(orderId);
        sysOrderEntity.setOrderNo(orderNo);
        sysOrderEntity.setOrderType(createDTO.getOrderType());
        sysOrderEntity.setOriginalPrice(originalPrice[0]);
        sysOrderEntity.setPremiumPrice(premiumPrice[0]);
        sysOrderEntity.setUserDiscountAmount(userDiscountAmount[0]);
        sysOrderEntity.setFinalPayAmount(finalPayAmount[0]);
        sysOrderEntity.setHoursPrice(hourPrice[0]);
        sysOrderEntity.setOrderStatus(OrderStatusEnum.UNPAID);
        sysOrderEntity.setOrderSourceList(orderSourceList);
        sysOrderEntity.setNetworkProductId(createDTO.getNetworkProductId());
        if (finalPayAmount[0].compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal payAmount = BigDecimal.valueOf(finalPayAmount[0].doubleValue());
            // 当前订单需要支付，处理代金卷支付金额，授信额支付金额，余额支付金额，在线支付金额
            if (createDTO.getVoucherPay()) {
                // 获取用户代金卷
                BigDecimal userVoucherBalance = sysCustomerVoucherService.getUserVoucherBalance(SecurityContext.getUserInfo().getUserId());
                if (userVoucherBalance.compareTo(payAmount) > 0) {
                    sysOrderEntity.setVoucherAmount(payAmount);
                    payAmount = BigDecimal.ZERO;
                } else {
                    payAmount = payAmount.subtract(userVoucherBalance);
                    sysOrderEntity.setVoucherAmount(userVoucherBalance);
                }
            }
            // 处理授信额支付
            if (createDTO.getCreditLinePay() ) {
                // 获取用户授信额
                BigDecimal creditAmount = sysCustomerCreditLineService.getCreditAmount(SecurityContext.getUserInfo().getUserId());
                if (creditAmount.compareTo(payAmount) > 0) {
                    sysOrderEntity.setCreditLineAmount(payAmount);
                    payAmount = BigDecimal.ZERO;
                } else {
                    payAmount = payAmount.subtract(creditAmount);
                    sysOrderEntity.setCreditLineAmount(creditAmount);
                }
            }
            // 处理余额支付
            if (createDTO.getBalancePay() && payAmount.compareTo(BigDecimal.ZERO) > 0) {
                // 获取用户余额
                SysCustomerEntity customer = sysCustomerMapper.selectById(SecurityContext.getUserInfo().getUserId());
                BigDecimal balance = customer.getBalance();
                if (balance.compareTo(payAmount) > -1) {
                    sysOrderEntity.setBalancePayAmount(payAmount);
                    payAmount = BigDecimal.ZERO;
                } else {
                    payAmount = payAmount.subtract(balance);
                    sysOrderEntity.setBalancePayAmount(balance);
                }
            }
            if (payAmount.compareTo(BigDecimal.ZERO) > 0) {
                sysOrderEntity.setOnlinePayAmount(payAmount);
                sysOrderEntity.setOnlinePayType(createDTO.getOnlinePayType());
            }
        }


        if (createDTO.getNetworkProductId() != null) {
            SysNetworkProductEntity entity = sysNetworkProductMapper.selectById(createDTO.getNetworkProductId());
            sysOrderEntity.setNetworkProductName(entity.getName());
        }
        return sysOrderEntity;
    }

    @Override
    public SysOrderEntity buildAGOrderInfo(SysOrderCreateDTO createDTO) {
        LoginUserInfoDetail userInfo = SecurityContext.getUserInfo();
        // 处理优惠卷。计算优惠金额
        AtomicReference<SysCustomerCouponListVO> couponAtomic = new AtomicReference<>(null);
        Optional.of(createDTO.getCouponId())
                .ifPresent(couponId -> {
                    SysCustomerCouponQuery query = new SysCustomerCouponQuery();
                    query.setCustomerId(userInfo.getUserId());
                    query.setId(couponId);
                    List<SysCustomerCouponListVO> list = sysCustomerCouponMapper.getList(query);
                    if (ObjectUtils.isEmpty(list)) {
                        throw new HttpServiceException("优惠卷不存在");
                    }
                    SysCustomerCouponListVO coupon = list.get(0);
                    if (!coupon.getStatus().equals(CouponUseStatusEnum.WAITING)) {
                        throw new HttpServiceException("优惠卷状态异常");
                    }
                    couponAtomic.set(coupon);
                });

        String orderNo = "Order" + DateUtils.getDate(DateUtils.YYYYMMDDHHMMSS) + RandomUtils.getNumberRandom(6);
        // 网络产品购买订单
        List<SysOrderSourceEntity> orderSourceList = createAGProductOrder(createDTO);
        if (ObjectUtils.isEmpty(orderSourceList)) {
            throw new HttpServiceException("订单资源列表为空");
        }
        // 处理按量计费和包年包月价格
        Long orderId = IdUtils.nextId();
        final BigDecimal[] originalPrice = {BigDecimal.ZERO};
        final BigDecimal[] premiumPrice = {BigDecimal.ZERO};
        final BigDecimal[] userDiscountAmount = {BigDecimal.ZERO};
        final BigDecimal[] finalPayAmount = {BigDecimal.ZERO};
        final BigDecimal[] couponRangeAmount = {BigDecimal.ZERO};
        final BigDecimal[] hourPrice = {BigDecimal.ZERO};
        final int[] couponCount = {0};
        SysOrderEntity sysOrderEntity = new SysOrderEntity();
        orderSourceList.forEach(item -> {
            item.setOrderId(orderId);
            item.setOrderNo(orderNo);
            SourceChargeTypeEnum chargeType = item.getChargeType();
            BigDecimal number = BigDecimal.valueOf(item.getNumber());
            if (ObjectUtils.isNotEmpty(chargeType)) {
                if (chargeType.equals(POSTPAID_BY_HOUR)) {
                    if (item.getConfigDetail() != null) {
                        BigDecimal hp = item.getConfigDetail().getBigDecimal("hoursPrice");
                        if (hp != null) {
                            hourPrice[0] = hourPrice[0].add(hp);
                        }
                    }
                    item.setFinalUnitPrice(BigDecimal.ZERO);
                }
            }

            // 计算优惠卷
            Optional.of(couponAtomic.get())
                    .ifPresent(coupon -> {
                        CouponRangeEnum rangeType = coupon.getRangeType();
                        if (rangeType.equals(CouponRangeEnum.ALL)) {
                            couponRangeAmount[0] = couponRangeAmount[0].add(item.getFinalUnitPrice().multiply(number));
                            couponCount[0] = couponCount[0]++;
                        } else if (rangeType.equals(CouponRangeEnum.AGIC)) {
                            couponRangeAmount[0] = couponRangeAmount[0].add(item.getFinalUnitPrice().multiply(number));
                            couponCount[0] = couponCount[0]++;
                        }
                    });

            // 产品购买的计算金额逻辑不一样
            SysOrderSourceCreateDTO sysOrderSourceCreateDTO = createDTO.getOrderSource().get(0);
            SysNetworkProductEntity sysNetworkProductEntity = sysNetworkProductMapper.selectById(createDTO.getNetworkProductId());
            sysOrderEntity.setNetworkProductName(sysNetworkProductEntity.getName());
            BigDecimal accountPrice = sysNetworkProductEntity.getAccountPrice();
            BigDecimal bandwidthPrice = sysNetworkProductEntity.getBandwidthPrice();
            BigDecimal ipPrice = sysNetworkProductEntity.getIpPrice();

            //以下是处理溢价后的单价
            BigDecimal accountPrice1 = item.getAccountPrice();
            BigDecimal bandwidthPrice1 = item.getBandwidthPrice();
            BigDecimal ipPrice1 = item.getIpPrice();
            //如果是升级套餐，得算出剩下的天数*单价
            if (createDTO.getOrderType().equals(OrderTypeEnum.UPGRADE_PRODUCT)){

                if (sysOrderSourceCreateDTO.getChargeType().equals(SourceChargeTypeEnum.POSTPAID_BY_HOUR)){
                    originalPrice[0] = (accountPrice.divide(BigDecimal.valueOf(20),  8, RoundingMode.UP).multiply(BigDecimal.valueOf(createDTO.getNetworkCount())).setScale(2, RoundingMode.UP))
                            .add((bandwidthPrice.divide(BigDecimal.valueOf(20),  8, RoundingMode.UP).multiply(BigDecimal.valueOf(createDTO.getBandwidth()))).setScale(2, RoundingMode.UP))
                            .add((ipPrice.divide(BigDecimal.valueOf(20),  8, RoundingMode.UP).multiply(BigDecimal.valueOf(createDTO.getIpCount()))).setScale(2, RoundingMode.UP));
                    finalPayAmount[0] = (accountPrice1.divide(BigDecimal.valueOf(20),  8, RoundingMode.UP).multiply(BigDecimal.valueOf(createDTO.getNetworkCount())).setScale(2, RoundingMode.UP))
                            .add((bandwidthPrice1.divide(BigDecimal.valueOf(20),  8, RoundingMode.UP).multiply(BigDecimal.valueOf(createDTO.getBandwidth()))).setScale(2, RoundingMode.UP))
                            .add((ipPrice1.divide(BigDecimal.valueOf(20),  8, RoundingMode.UP).multiply(BigDecimal.valueOf(createDTO.getIpCount()))).setScale(2, RoundingMode.UP));
                }else {
                    SysNetworkValueEntity sysNetworkValueEntity = sysNetworkValueMapper.selectById(createDTO.getNetworkPayValueId());
                    Date actualAgiExpireTime = sysNetworkValueEntity.getActualAgiExpireTime();

                    // 计算剩余天数
                    LocalDate currentDate = LocalDate.now();
                    LocalDate expireDate = actualAgiExpireTime.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();

                    // 如果当前时间已经超过到期时间，则剩余天数为0
                    // 如果当天就是到期日，也算作1天
                    long remainingDays = 0;
                    if (currentDate.isBefore(expireDate)) {
                        remainingDays = ChronoUnit.DAYS.between(currentDate, expireDate) + 1; // 包含当天
                    } else if (currentDate.isEqual(expireDate)) {
                        remainingDays = 1; // 当天到期，算作1天
                    }
                    // 计算每天的价格（按月价格除以30天）
                    BigDecimal dailyAccountPrice = accountPrice.divide(BigDecimal.valueOf(30), 8, RoundingMode.UP);
                    BigDecimal dailyBandwidthPrice = bandwidthPrice.divide(BigDecimal.valueOf(30), 8, RoundingMode.UP);
                    BigDecimal dailyIpPrice = ipPrice.divide(BigDecimal.valueOf(30), 8, RoundingMode.UP);

                    // 计算剩余天数的费用
                    BigDecimal remainingAccountCost = dailyAccountPrice.multiply(BigDecimal.valueOf(remainingDays))
                            .multiply(BigDecimal.valueOf(createDTO.getNetworkCount()))
                            .setScale(2, RoundingMode.UP);
                    BigDecimal remainingBandwidthCost = dailyBandwidthPrice.multiply(BigDecimal.valueOf(remainingDays))
                            .multiply(BigDecimal.valueOf(createDTO.getBandwidth()))
                            .setScale(2, RoundingMode.UP);
                    BigDecimal remainingIpCost = dailyIpPrice.multiply(BigDecimal.valueOf(remainingDays))
                            .multiply(BigDecimal.valueOf(createDTO.getIpCount()))
                            .setScale(2, RoundingMode.UP);

                    // 原价 = 剩余天数费用之和
                    originalPrice[0] = remainingAccountCost.add(remainingBandwidthCost).add(remainingIpCost);

                    // 计算溢价后的每天价格
                    BigDecimal dailyAccountPrice1 = accountPrice1.divide(BigDecimal.valueOf(30), 8, RoundingMode.UP);
                    BigDecimal dailyBandwidthPrice1 = bandwidthPrice1.divide(BigDecimal.valueOf(30), 8, RoundingMode.UP);
                    BigDecimal dailyIpPrice1 = ipPrice1.divide(BigDecimal.valueOf(30), 8, RoundingMode.UP);

                    // 计算溢价后的剩余天数费用
                    BigDecimal remainingAccountCost1 = dailyAccountPrice1.multiply(BigDecimal.valueOf(remainingDays))
                            .multiply(BigDecimal.valueOf(createDTO.getNetworkCount()))
                            .setScale(2, RoundingMode.UP);
                    BigDecimal remainingBandwidthCost1 = dailyBandwidthPrice1.multiply(BigDecimal.valueOf(remainingDays))
                            .multiply(BigDecimal.valueOf(createDTO.getBandwidth()))
                            .setScale(2, RoundingMode.UP);
                    BigDecimal remainingIpCost1 = dailyIpPrice1.multiply(BigDecimal.valueOf(remainingDays))
                            .multiply(BigDecimal.valueOf(createDTO.getIpCount()))
                            .setScale(2, RoundingMode.UP);

                    // 最终价格 = 溢价后剩余天数费用之和
                    finalPayAmount[0] = remainingAccountCost1.add(remainingBandwidthCost1).add(remainingIpCost1);
                }
            }else {
                Integer discount = 100;
                if (sysOrderSourceCreateDTO.getChargeType().equals(SourceChargeTypeEnum.POSTPAID_BY_HOUR)){
                    originalPrice[0] = (accountPrice.divide(BigDecimal.valueOf(20),  8, RoundingMode.UP).multiply(BigDecimal.valueOf(createDTO.getNetworkCount())).setScale(2, RoundingMode.UP))
                            .add((bandwidthPrice.divide(BigDecimal.valueOf(20),  8, RoundingMode.UP).multiply(BigDecimal.valueOf(createDTO.getBandwidth()))).setScale(2, RoundingMode.UP))
                            .add((ipPrice.divide(BigDecimal.valueOf(20),  8, RoundingMode.UP).multiply(BigDecimal.valueOf(createDTO.getIpCount()))).setScale(2, RoundingMode.UP));
                    finalPayAmount[0] = (accountPrice1.divide(BigDecimal.valueOf(20),  8, RoundingMode.UP).multiply(BigDecimal.valueOf(createDTO.getNetworkCount())).setScale(2, RoundingMode.UP))
                            .add((bandwidthPrice1.divide(BigDecimal.valueOf(20),  8, RoundingMode.UP).multiply(BigDecimal.valueOf(createDTO.getBandwidth()))).setScale(2, RoundingMode.UP))
                            .add((ipPrice1.divide(BigDecimal.valueOf(20),  8, RoundingMode.UP).multiply(BigDecimal.valueOf(createDTO.getIpCount()))).setScale(2, RoundingMode.UP));
                }else {
                    if (sysOrderSourceCreateDTO.getChargeType().equals(POSTPAID_BY_MONTH)) {
                        switch (item.getDuration()) {
                            case 1 -> discount = sysNetworkProductEntity.getOneMonthDiscount();
                            case 2 -> discount = sysNetworkProductEntity.getTwoMonthDiscount();
                            case 3 -> discount = sysNetworkProductEntity.getThreeMonthDiscount();
                            case 4 -> discount = sysNetworkProductEntity.getFourMonthDiscount();
                            case 5 -> discount = sysNetworkProductEntity.getFiveMonthDiscount();
                            case 6 -> discount = sysNetworkProductEntity.getSixMonthDiscount();
                            case 7 -> discount = sysNetworkProductEntity.getSevenMonthDiscount();
                            case 8 -> discount = sysNetworkProductEntity.getEightMonthDiscount();
                            case 9 -> discount = sysNetworkProductEntity.getNineMonthDiscount();
                            case 10 -> discount = sysNetworkProductEntity.getTenMonthDiscount();
                            case 11 -> discount = sysNetworkProductEntity.getElevenMonthDiscount();
                        }
                    }else if (sysOrderSourceCreateDTO.getChargeType().equals(POSTPAID_BY_YEAR)){
                        switch (item.getDuration()) {
                            case 1 -> {
                                discount = sysNetworkProductEntity.getOneYearDiscount();
                                item.setDuration(12);
                            }
                            case 2 -> {
                                discount = sysNetworkProductEntity.getTwoYearDiscount();
                                item.setDuration(24);
                            }
                            case 3 -> {
                                discount = sysNetworkProductEntity.getThreeYearDiscount();
                                item.setDuration(36);
                            }
                        }
                    }
                    log.info("原价：accountPrice:{},createDTO:{},item:{},discount:{}", accountPrice,  createDTO,  item,discount);
                    log.info("最终价：createDTO:{},item:{},discount:{}", createDTO,  item,discount);
                    originalPrice[0] = (accountPrice.multiply(BigDecimal.valueOf(createDTO.getNetworkCount())).setScale(2, RoundingMode.UP)
                            .add(bandwidthPrice.multiply(BigDecimal.valueOf(createDTO.getBandwidth())).setScale(2, RoundingMode.UP))
                            .add(ipPrice.multiply(BigDecimal.valueOf(createDTO.getIpCount())).setScale(2, RoundingMode.UP)))
                            .multiply(BigDecimal.valueOf(item.getDuration()))
                            .multiply(new BigDecimal(discount).divide(new BigDecimal(100),  2, RoundingMode.HALF_UP));
                    finalPayAmount[0] = (accountPrice1.multiply(BigDecimal.valueOf(createDTO.getNetworkCount())).setScale(2, RoundingMode.UP)
                            .add(bandwidthPrice1.multiply(BigDecimal.valueOf(createDTO.getBandwidth())).setScale(2, RoundingMode.UP))
                            .add(ipPrice1.multiply(BigDecimal.valueOf(createDTO.getIpCount())).setScale(2, RoundingMode.UP)))
                            .multiply(BigDecimal.valueOf(item.getDuration()))
                            .multiply(new BigDecimal(discount).divide(new BigDecimal(100),  2, RoundingMode.HALF_UP));
                }
            }

            premiumPrice[0] = premiumPrice[0].add(item.getPremiumPrice().multiply(number));
            userDiscountAmount[0] = userDiscountAmount[0].add(item.getUserDiscountAmount().multiply(number));
        });

        // 判断优惠券是否达到满减金额，达到就减掉价格，记录优惠券id和减去的金额
        Optional.of(couponAtomic.get())
                .ifPresent(coupon -> {
                    if (coupon.getThresholdAmount().compareTo(couponRangeAmount[0]) > 0) {
                        throw new HttpServiceException("优惠卷不满足使用条件");
                    }
                    // 正确的计算方式应该是：
                    BigDecimal sourceCouponAmount = couponCount[0] > 0
                            ? coupon.getDeductionAmount().divide(BigDecimal.valueOf(couponCount[0]), 2, RoundingMode.DOWN)
                            : BigDecimal.ZERO;
                    sysOrderEntity.setCouponId(createDTO.getCouponId());
                    sysOrderEntity.setCouponAmount(coupon.getDeductionAmount());
                    // 修复逻辑
                    BigDecimal maxDiscount = coupon.getDeductionAmount().min(finalPayAmount[0]); // 取优惠券金额和订单金额的最小值
                    finalPayAmount[0] = finalPayAmount[0].subtract(maxDiscount);
                    CouponRangeEnum rangeType = coupon.getRangeType();
                    orderSourceList.forEach(item -> {
                        if (rangeType.equals(CouponRangeEnum.ALL) || rangeType.equals(CouponRangeEnum.AGIC)) {
                            item.setCouponDiscountAmount(sourceCouponAmount);
                        }
                    });
                });
        sysOrderEntity.setId(orderId);
        sysOrderEntity.setOrderNo(orderNo);
        sysOrderEntity.setOrderType(createDTO.getOrderType());
        sysOrderEntity.setOriginalPrice(originalPrice[0]);
        sysOrderEntity.setPremiumPrice(premiumPrice[0]);
        sysOrderEntity.setUserDiscountAmount(userDiscountAmount[0]);
        sysOrderEntity.setFinalPayAmount(finalPayAmount[0]);
        sysOrderEntity.setHoursPrice(hourPrice[0]);
        sysOrderEntity.setOrderStatus(OrderStatusEnum.UNPAID);
        sysOrderEntity.setOrderSourceList(orderSourceList);
        sysOrderEntity.setNetworkProductId(createDTO.getNetworkProductId());
        if (createDTO.getOrderType().equals(OrderTypeEnum.UPGRADE_PRODUCT) || createDTO.getOrderType().equals(OrderTypeEnum.RENEW_PRODUCT)){
//            SysNetworkValueEntity sysNetworkValueEntity = sysNetworkValueMapper.selectById(createDTO.getNetworkPayValueId());
            sysOrderEntity.setEmail(createDTO.getEmail());
        }

        if (finalPayAmount[0].compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal payAmount = BigDecimal.valueOf(finalPayAmount[0].doubleValue());
            // 当前订单需要支付，处理代金卷支付金额，授信额支付金额，余额支付金额，在线支付金额
            if (createDTO.getVoucherPay()) {
                // 获取用户代金卷
                BigDecimal userVoucherBalance = sysCustomerVoucherService.getUserVoucherBalance(SecurityContext.getUserInfo().getUserId());
                if (userVoucherBalance.compareTo(payAmount) > 0) {
                    sysOrderEntity.setVoucherAmount(payAmount);
                    payAmount = BigDecimal.ZERO;
                } else {
                    payAmount = payAmount.subtract(userVoucherBalance);
                    sysOrderEntity.setVoucherAmount(userVoucherBalance);
                }
            }
            // 处理授信额支付
            if (createDTO.getCreditLinePay() ) {
                // 获取用户授信额
                BigDecimal creditAmount = sysCustomerCreditLineService.getCreditAmount(SecurityContext.getUserInfo().getUserId());
                if (creditAmount.compareTo(payAmount) > 0) {
                    sysOrderEntity.setCreditLineAmount(payAmount);
                    payAmount = BigDecimal.ZERO;
                } else {
                    payAmount = payAmount.subtract(creditAmount);
                    sysOrderEntity.setCreditLineAmount(creditAmount);
                }
            }
            // 处理余额支付
            if (createDTO.getBalancePay() && payAmount.compareTo(BigDecimal.ZERO) > 0) {
                // 获取用户余额
                SysCustomerEntity customer = sysCustomerMapper.selectById(SecurityContext.getUserInfo().getUserId());
                BigDecimal balance = customer.getBalance();
                if (balance.compareTo(payAmount) > -1) {
                    sysOrderEntity.setBalancePayAmount(payAmount);
                    payAmount = BigDecimal.ZERO;
                } else {
                    payAmount = payAmount.subtract(balance);
                    sysOrderEntity.setBalancePayAmount(balance);
                }
            }
            if (payAmount.compareTo(BigDecimal.ZERO) > 0) {
                sysOrderEntity.setOnlinePayAmount(payAmount);
                sysOrderEntity.setOnlinePayType(createDTO.getOnlinePayType());
            }
        }
        return sysOrderEntity;
    }

    /**
     * 网络产品订单
     */
    private List<SysOrderSourceEntity> createProductOrder(SysOrderCreateDTO createDTO) {
        if (createDTO.getNetworkProductId() == null) {
            throw new HttpServiceException("请选择产品支付");
        }

//        // 处理平台溢价
//        Map<SourceTypeEnum, BigDecimal> sourceDiscount = Optional.of(sysCustomerDiscountMapper.selectList(Wrappers.lambdaQuery(SysCustomerDiscountEntity.class)
//                        .eq(SysCustomerDiscountEntity::getCustomerId, SecurityContext.getUserInfo().getUserId())))
//                .flatMap(list -> list.stream().collect(Collectors.toMap(SysCustomerDiscountEntity::getSourceType, SysCustomerDiscountEntity::getDiscountRation)))
//                .orElse(new HashMap<>(0));
//        BigDecimal sellPriceRatio = sysConfigService.getConfig(SystemConfigEnum.SELL_PRICE_RATIO).getValue(SystemPriceRationConfigModel.class).getPriceRatio();
//        // 处理用户溢价
//
//        sourceDiscount.forEach();
//
//        e.setPremiumPrice(e.getUnitPrice().multiply(sellPriceRatio).setScale(2, RoundingMode.UP));
//        BigDecimal orDefault = sourceDiscount.get(e.getSourceType());
//        if (orDefault == null || orDefault.compareTo(BigDecimal.ONE) == 0) {
//            e.setUserDiscountAmount(BigDecimal.ZERO);
//            e.setFinalUnitPrice(e.getPremiumPrice().setScale(2, RoundingMode.UP));
//        } else {
//            e.setUserDiscountAmount(e.getPremiumPrice().multiply(orDefault).setScale(2, RoundingMode.UP));
//            e.setFinalUnitPrice(e.getUserDiscountAmount().setScale(2, RoundingMode.UP));
//        }

        SysNetworkProductEntity sysNetworkProductEntity = sysNetworkProductMapper.selectById(createDTO.getNetworkProductId());

        SysOrderSourceEntity entity = new SysOrderSourceEntity();
        entity.setUnitPrice(sysNetworkProductEntity.getPayPrice());
        entity.setPremiumPrice(BigDecimal.ZERO);
        entity.setUserDiscountAmount(BigDecimal.ZERO);
        entity.setVoucherDiscountAmount(BigDecimal.ZERO);
        entity.setCouponDiscountAmount(BigDecimal.ZERO);

        LoginUserInfoDetail userInfo = SecurityContext.getUserInfo();

        // 处理折扣
        List<SysCustomerNetwork> networks = sysCustomerNetworkMapper.selectList(
                new LambdaQueryWrapper<SysCustomerNetwork>()
                        .eq(SysCustomerNetwork::getUserId, userInfo.getUserId())
                        .eq(SysCustomerNetwork::getNetworkId, sysNetworkProductEntity.getId())
        );
        BigDecimal orDefault = null;
        if (CollectionUtils.isNotEmpty(networks)) {
            SysCustomerNetwork e = networks.get(0);
            orDefault = e.getDiscountRation();
        } else {
            // 处理平台溢价
            Map<SourceTypeEnum, BigDecimal> sourceDiscount = Optional.of(sysCustomerDiscountMapper.selectList(Wrappers.lambdaQuery(SysCustomerDiscountEntity.class)
                            .eq(SysCustomerDiscountEntity::getCustomerId, SecurityContext.getUserInfo().getUserId())))
                    .flatMap(list -> list.stream().collect(Collectors.toMap(SysCustomerDiscountEntity::getSourceType, SysCustomerDiscountEntity::getDiscountRation)))
                    .orElse(new HashMap<>(0));
            orDefault = sourceDiscount.get(SourceTypeEnum.AGIC);
        }

        if (orDefault == null || orDefault.compareTo(BigDecimal.ONE) == 0) {
            entity.setFinalUnitPrice(sysNetworkProductEntity.getPayPrice());
        } else {
            entity.setFinalUnitPrice(sysNetworkProductEntity.getPayPrice().multiply(orDefault).setScale(2, RoundingMode.UP));
        }


        entity.setNumber(1);

        return List.of(entity);
    }

    /**
     * 网络产品订单
     */
    private List<SysOrderSourceEntity> createAGProductOrder(SysOrderCreateDTO createDTO) {
        if (createDTO.getNetworkProductId() == null) {
            throw new HttpServiceException("请选择产品支付");
        }

        SysNetworkProductEntity sysNetworkProductEntity = sysNetworkProductMapper.selectById(createDTO.getNetworkProductId());
        SysOrderSourceCreateDTO sysOrderSourceCreateDTO = createDTO.getOrderSource().get(0);
        SysOrderSourceEntity entity = BeanUtil.copyProperties(sysOrderSourceCreateDTO, SysOrderSourceEntity.class);
        entity.setUnitPrice(sysNetworkProductEntity.getPayPrice());
        entity.setPremiumPrice(BigDecimal.ZERO);
        entity.setUserDiscountAmount(BigDecimal.ZERO);
        entity.setVoucherDiscountAmount(BigDecimal.ZERO);
        entity.setCouponDiscountAmount(BigDecimal.ZERO);

        LoginUserInfoDetail userInfo = SecurityContext.getUserInfo();

        // 处理折扣
        List<SysCustomerNetwork> networks = sysCustomerNetworkMapper.selectList(
                new LambdaQueryWrapper<SysCustomerNetwork>()
                        .eq(SysCustomerNetwork::getUserId, userInfo.getUserId())
                        .eq(SysCustomerNetwork::getNetworkId, sysNetworkProductEntity.getId())
        );
        BigDecimal orDefault = null;
        if (CollectionUtils.isNotEmpty(networks)) {
            SysCustomerNetwork e = networks.get(0);
            orDefault = e.getDiscountRation();
        } else {
            // 处理平台溢价
            Map<SourceTypeEnum, BigDecimal> sourceDiscount = Optional.of(sysCustomerDiscountMapper.selectList(Wrappers.lambdaQuery(SysCustomerDiscountEntity.class)
                            .eq(SysCustomerDiscountEntity::getCustomerId, SecurityContext.getUserInfo().getUserId())))
                    .flatMap(list -> list.stream().collect(Collectors.toMap(SysCustomerDiscountEntity::getSourceType, SysCustomerDiscountEntity::getDiscountRation)))
                    .orElse(new HashMap<>(0));
            orDefault = sourceDiscount.get(SourceTypeEnum.AGIC);
        }

        if (orDefault == null || orDefault.compareTo(BigDecimal.ONE) == 0) {
            entity.setFinalUnitPrice(sysNetworkProductEntity.getPayPrice());
            entity.setAccountPrice(sysNetworkProductEntity.getAccountPrice());
            entity.setBandwidthPrice(sysNetworkProductEntity.getBandwidthPrice());
            entity.setIpPrice(sysNetworkProductEntity.getIpPrice());
        } else {
            entity.setFinalUnitPrice(sysNetworkProductEntity.getPayPrice().multiply(orDefault).setScale(2, RoundingMode.UP));
            entity.setAccountPrice(sysNetworkProductEntity.getAccountPrice().multiply(orDefault).setScale(2, RoundingMode.UP));
            entity.setBandwidthPrice(sysNetworkProductEntity.getBandwidthPrice().multiply(orDefault).setScale(2, RoundingMode.UP));
            entity.setIpPrice(sysNetworkProductEntity.getIpPrice().multiply(orDefault).setScale(2, RoundingMode.UP));
        }


        entity.setNumber(1);

        return List.of(entity);
    }
    @Override
    public void cancelOrder(Long orderId) {
        log.info("取消订单开始：{}", orderId);
        SysOrderEntity sysOrderEntity = sysOrderMapper.selectById(orderId);
        if (ObjectUtils.isEmpty(sysOrderEntity)) {
            throw new HttpServiceException("订单不存在");
        }
        if (sysOrderEntity.getOrderStatus().equals(OrderStatusEnum.CANCELED) || sysOrderEntity.getOrderStatus().equals(OrderStatusEnum.REFUNDED)) {
            throw new HttpServiceException("订单状态异常, 无法取消");
        }

        // 如果是充值订单并且已支付就不需要取消
        if (sysOrderEntity.getOrderType().equals(OrderTypeEnum.BALANCE) && sysOrderEntity.getOrderStatus().equals(OrderStatusEnum.PAID)) {
            log.info("已经充值成功，不需要取消了");
            return;
        }

        sysOrderEntity.setOrderStatus(OrderStatusEnum.CANCELED);
        sysOrderMapper.updateById(sysOrderEntity);
        // 处理优惠卷
        Optional.of(sysOrderEntity.getCouponId())
                .ifPresent(couponId -> {
                    SysCustomerCouponEntity entity = new SysCustomerCouponEntity();
                    entity.setId(couponId);
                    entity.setStatus(CouponUseStatusEnum.WAITING);
                    sysCustomerCouponMapper.updateById(entity);
                });
        // 处理代金卷退还
        log.info("取消订单，退还代金卷");
        Optional.of(sysCustomerVoucherUseLogMapper.selectList(Wrappers.lambdaQuery(SysCustomerVoucherUseLogEntity.class)
                        .eq(SysCustomerVoucherUseLogEntity::getOrderId, orderId)))
                .ifPresent(useLogEntities -> {
                    List<SysCustomerVoucherEntity> sysCustomerVoucherList = new ArrayList<>(useLogEntities.size());
                    for (SysCustomerVoucherUseLogEntity useLogEntity : useLogEntities) {
                        if (useLogEntity.getUseAmount().compareTo(BigDecimal.ZERO) == 0) {
                            continue;
                        }
                        SysCustomerVoucherEntity entity = new SysCustomerVoucherEntity();
                        entity.setId(useLogEntity.getCustomerVoucherId());
                        entity.setUseAmount(useLogEntity.getUseAmount());
                        sysCustomerVoucherList.add(entity);
                    }
                    sysCustomerVoucherMapper.returnVoucherAmount(sysCustomerVoucherList);
                });
        //todo 处理授信额退还
        log.info("订单取消，处理授信额退还");
        Optional.of(sysCustomerCreditLineUseLogMapper.selectList(Wrappers.lambdaQuery(SysCustomerCreditLineUseLogEntity.class)
                        .eq(SysCustomerCreditLineUseLogEntity::getOrderId, orderId)))
                .ifPresent(useLogEntities -> {
                    List<SysCustomerCreditLineEntity> SysCustomerCreditLineList = new ArrayList<>(useLogEntities.size());
                    for (SysCustomerCreditLineUseLogEntity useLogEntity : useLogEntities) {
                        if (useLogEntity.getUseAmount().compareTo(BigDecimal.ZERO) == 0) {
                            continue;
                        }
                        SysCustomerCreditLineEntity entity = new SysCustomerCreditLineEntity();
                        entity.setId(useLogEntity.getCustomerCreditId());
                        entity.setUseAmount(useLogEntity.getUseAmount());
                        SysCustomerCreditLineList.add(entity);
                    }
                    sysCustomerCreditLineMapper.returnCreditLineAmount(SysCustomerCreditLineList);
                });
        // 余额退还
        log.info("订单取消，退还余额");
        Optional.of(sysOrderEntity.getBalancePayAmount())
                .ifPresent(balancePayAmount -> sysCustomerService.updateCustomerBalance(sysOrderEntity.getId(),
                        sysOrderEntity.getOrderNo(),
                        sysOrderEntity.getCreateById(),
                        balancePayAmount,
                        SysTransactionType.REFUND));
        // 处理在线支付退还
        log.info("订单取消，退还在线支付");
        Optional.of(sysOrderEntity.getOnlinePaySerialNumber())
                .ifPresent(onlinePaySerialNumber -> {
                    OnlinePayRefundsParam refundsParam  = new OnlinePayRefundsParam();
                    refundsParam.setOrderNo(sysOrderEntity.getOrderNo());
                    refundsParam.setAmount(sysOrderEntity.getOnlinePayAmount());
                    refundsParam.setPayNumber(onlinePaySerialNumber);
                    refundsParam.setPayTotalAmount(sysOrderEntity.getOnlinePayAmount());
                    refundsParam.setReason("订单取消");
                    onlinePayHandler.refunds(sysOrderEntity.getOnlinePayType(), refundsParam);
                });
        log.info("取消订单成功");
    }

    @Override
    public PageResult<SysOrderEntity> getPage(PageQuery<SysOrderQuery> query) {
        query.startPage();
        SysOrderQuery orderQuery = query.getQuery();
        return Optional.of(sysOrderMapper.getList(orderQuery))
                .flatMap(entities -> {
                    // 查询订单资源
                    LambdaQueryWrapper<SysOrderSourceEntity> sourceQueryWrapper = Wrappers.lambdaQuery(SysOrderSourceEntity.class)
                            .in(SysOrderSourceEntity::getOrderId, entities.stream().map(SysOrderEntity::getId).toList());
                    Map<Long, List<SysOrderSourceEntity>> orderIdSourceMap = Optional.of(sysOrderSourceMapper.selectList(sourceQueryWrapper))
                            .flatMap(sourceEntities -> sourceEntities.stream().collect(Collectors.groupingBy(SysOrderSourceEntity::getOrderId)))
                            .orElse(new HashMap<>(0));
                    for (SysOrderEntity e : entities) {
                        List<SysOrderSourceEntity> sourceList = orderIdSourceMap.get(e.getId());
                        if (sourceList == null) {
                            sourceList = new ArrayList<>();
                            log.info("订单没有资源:{}", e.getId());
                        }
                        for (SysOrderSourceEntity sysOrderSourceEntity : sourceList) {
                            if (e.getNetworkProductId() != null) {
                                SysNetworkProductEntity productEntity = sysNetworkProductMapper.selectById(e.getNetworkProductId());
                                if (productEntity != null) {
                                    sysOrderSourceEntity.setProductName(productEntity.getName());
                                }
                            }

                        }
                        if (e.getNetworkValueId() != null) {
                            SysNetworkValueEntity valueEntity = sysNetworkValueMapper.selectById(e.getNetworkValueId());
                            if (valueEntity != null) {
//                                e.setNetworkProductJson(valueEntity.getJson());
                                e.setUserpwd(valueEntity.getUserpwd());
                                e.setDuration(valueEntity.getDuration());
                                e.setDurationUnit(valueEntity.getDurationUnit());
                                e.setChargeType(valueEntity.getChargeType());
//                                e.setIpCount(valueEntity.getIpCount());
//                                e.setNetworkCount(valueEntity.getNetworkCount());
//                                e.setBandwidth(valueEntity.getBandwidth());
                                e.setActualStatus(valueEntity.getActualStatus());
                                e.setActualAgiOpenTime(valueEntity.getActualAgiOpenTime());
                                e.setActualAgiExpireTime(valueEntity.getActualAgiExpireTime());
                                e.setMobile(valueEntity.getMobile());
                                e.setExpireStatus(valueEntity.getExpireStatus());

                                if (StringUtils.isNotBlank(valueEntity.getAgiCustomerName())){
                                    e.setAgiCustomerName(valueEntity.getAgiCustomerName());
                                }
                            }
                        }
                        e.setOrderSourceList(sourceList);
                    }
                    return PageResult.of(entities);
                })
                .orElseGet(() -> PageResult.of(List.of()));
    }

    @Override
    public PageResult<SysOrderEntity> getAGICOrderPage(PageQuery<SysOrderQuery> query) {
        query.startPage();
        SysOrderQuery orderQuery = query.getQuery();
        return Optional.of(sysOrderMapper.getAgicList(orderQuery))
                .flatMap(entities -> {
                    // 查询订单资源
                    LambdaQueryWrapper<SysOrderSourceEntity> sourceQueryWrapper = Wrappers.lambdaQuery(SysOrderSourceEntity.class)
                            .in(SysOrderSourceEntity::getOrderId, entities.stream().map(SysOrderEntity::getId).toList());
                    Map<Long, List<SysOrderSourceEntity>> orderIdSourceMap = Optional.of(sysOrderSourceMapper.selectList(sourceQueryWrapper))
                            .flatMap(sourceEntities -> sourceEntities.stream().collect(Collectors.groupingBy(SysOrderSourceEntity::getOrderId)))
                            .orElse(new HashMap<>(0));
                    for (SysOrderEntity e : entities) {
                        List<SysOrderSourceEntity> sourceList = orderIdSourceMap.get(e.getId());
                        if (sourceList == null) {
                            sourceList = new ArrayList<>();
                            log.info("订单没有资源:{}", e.getId());
                        }
                        for (SysOrderSourceEntity sysOrderSourceEntity : sourceList) {
                            if (e.getNetworkProductId() != null) {
                                SysNetworkProductEntity productEntity = sysNetworkProductMapper.selectById(e.getNetworkProductId());
                                if (productEntity != null) {
                                    sysOrderSourceEntity.setProductName(productEntity.getName());
                                    if (productEntity.getName().contains("短视频")){
                                        e.setBandwidth(null);
                                    }
                                }
                            }

                        }
                        if (e.getNetworkValueId() != null) {
                            SysNetworkValueEntity valueEntity = sysNetworkValueMapper.selectById(e.getNetworkValueId());
                            if (valueEntity != null) {
//                                e.setNetworkProductJson(valueEntity.getJson());
                                e.setUserpwd(valueEntity.getUserpwd());
                                e.setDuration(valueEntity.getDuration());
                                e.setDurationUnit(valueEntity.getDurationUnit());
                                e.setChargeType(valueEntity.getChargeType());
                                e.setIpCount(valueEntity.getIpCount());
                                e.setNetworkCount(valueEntity.getNetworkCount());
                                e.setBandwidth(valueEntity.getBandwidth());
                                e.setActualStatus(valueEntity.getActualStatus());
                                e.setActualAgiOpenTime(valueEntity.getActualAgiOpenTime());
                                e.setActualAgiExpireTime(valueEntity.getActualAgiExpireTime());
                                e.setMobile(valueEntity.getMobile());
                                e.setExpireStatus(valueEntity.getExpireStatus());

                                if (StringUtils.isNotBlank(valueEntity.getAgiCustomerName())){
                                    e.setAgiCustomerName(valueEntity.getAgiCustomerName());
                                }
                            }
                        }
                        e.setOrderSourceList(sourceList);
                    }
                    return PageResult.of(entities);
                })
                .orElseGet(() -> PageResult.of(List.of()));
    }

    @Override
    public String getAlipayForm(String orderNo) {
        SysOrderEntity sysOrderEntity = sysOrderMapper.selectOne(Wrappers.lambdaQuery(SysOrderEntity.class)
                .eq(SysOrderEntity::getOrderNo, orderNo));
        if (sysOrderEntity != null){
            // 处理在线支付
            if (sysOrderEntity.getOnlinePayAmount() != null && sysOrderEntity.getOnlinePayAmount().compareTo(BigDecimal.ZERO) > 0) {
                OnlinePayParam payParam = new OnlinePayParam();
                payParam.setOrderNo(sysOrderEntity.getOrderNo());
                payParam.setTimeExpire(DateUtils.getDate(DateUtils.getNowDate(), 15, Calendar.MINUTE));
                payParam.setDescription("在线支付");
                payParam.setPayAmount(sysOrderEntity.getOnlinePayAmount());
                payParam.setIsApplet(false);

                log.info("订单号：{}，进行支付宝支付：{}", payParam.getOrderNo(), JSONObject.toJSONString(payParam));
                AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
                request.setReturnUrl(alipayConfig.getReturnUrl());
                request.setNotifyUrl(alipayConfig.getNotifyUrl());
                AlipayTradePagePayModel model = new AlipayTradePagePayModel();
                model.setOutTradeNo(payParam.getOrderNo());
                model.setTotalAmount(payParam.getPayAmount().toString());
                model.setSubject("在线支付");
                model.setProductCode("FAST_INSTANT_TRADE_PAY");
                model.setTimeExpire(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, payParam.getTimeExpire()));
                request.setBizModel(model);
                try {
                    String body = alipayClient.pageExecute(request).getBody();
                    log.info("订单号：{}，进行支付宝支付：{}", payParam.getOrderNo(), body);
                    return body;
                } catch (AlipayApiException e) {
                    log.error("支付宝支付接口调用异常：", e);
                    throw new HttpServiceException(e.getMessage());
                }
            }
        }
        return null;
    }

    @Override
    public Result<OrderCreateVO> getWechatPayQrCode(String orderNo) {
        SysOrderEntity sysOrderEntity = sysOrderMapper.selectOne(Wrappers.lambdaQuery(SysOrderEntity.class)
                .eq(SysOrderEntity::getOrderNo, orderNo));
        OrderCreateVO orderCreateVO = new OrderCreateVO();
        if (sysOrderEntity != null){
            // 处理在线支付
            if (sysOrderEntity.getOnlinePayAmount() != null && sysOrderEntity.getOnlinePayAmount().compareTo(BigDecimal.ZERO) > 0) {
                orderCreateVO.setOrderNo(sysOrderEntity.getOrderNo());
                orderCreateVO.setOnlinePay(sysOrderEntity.getOnlinePayType());
                OnlinePayParam payParam = new OnlinePayParam();
                payParam.setOrderNo(sysOrderEntity.getOrderNo());
                payParam.setTimeExpire(DateUtils.getDate(DateUtils.getNowDate(), 15, Calendar.MINUTE));
                payParam.setDescription("在线支付");
                payParam.setPayAmount(sysOrderEntity.getOnlinePayAmount());
                payParam.setIsApplet(false);

                log.info("订单号：{}，进行微信支付：{}", payParam.getOrderNo(), JSONObject.toJSONString(payParam));
                NativePayService nativePayService = weChatPayClient.getNativePayService();
                PrepayRequest request = new PrepayRequest();
                request.setAppid(weChatPayConfig.getAppId());
                request.setMchid(weChatPayConfig.getMchId());
                request.setNotifyUrl(weChatPayConfig.getPayNotifyUrl());
                request.setOutTradeNo(payParam.getOrderNo());
                request.setDescription("在线支付");
                Amount amount = new Amount();
                amount.setTotal(AmountUtils.yuanToDivide(payParam.getPayAmount()).intValue());
                request.setAmount(amount);
                PrepayResponse prepay = nativePayService.prepay(request);
                orderCreateVO.setOnlinePayParam(prepay.getCodeUrl());
            }
        }
        return Result.success(orderCreateVO);
    }

    @Override
    public PageResult<SysOrderSourceEntity> getOrderSourceList(PageQuery<Long> query) {
        query.startPage();
        LambdaQueryWrapper<SysOrderSourceEntity> sourceQueryWrapper = Wrappers.lambdaQuery(SysOrderSourceEntity.class)
                .eq(SysOrderSourceEntity::getOrderId, query.getQuery());
        return PageResult.of(sysOrderSourceMapper.selectList(sourceQueryWrapper));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void paySuccess(SysOrderEntity sysOrderEntity, String onlinePaySerialNumber) {
        if (ObjectUtils.isEmpty(sysOrderEntity)) {
            throw new HttpServiceException("订单不存在");
        }
        OrderStatusEnum orderStatus = sysOrderEntity.getOrderStatus();
        if (!orderStatus.equals(OrderStatusEnum.UNPAID)) {
            throw new HttpServiceException("订单状态异常, 无法支付");
        }
        switch (sysOrderEntity.getOrderType()) {
            // 余额充值
            case BALANCE -> {
                sysCustomerService.updateCustomerBalance(sysOrderEntity.getId(),
                        sysOrderEntity.getOrderNo(),
                        sysOrderEntity.getCreateById(),
                        sysOrderEntity.getFinalPayAmount(),
                        SysTransactionType.RECHARGE);
                // 发布账单处理
                cacheQueueService.addDelayQueue(CUSTOMER_BILL_HANDLER_QUEUE_TYPE, sysOrderEntity.getCreateById().toString(), 5);
            }
            // 产品账单处理
            case PRODUCT -> {
                // 发布账单处理 一天后生成(生成包年包月，按天计费另外生成)
                SysOrderSourceEntity orderSource = sysOrderEntity.getOrderSourceList().get(0);
                if (orderSource.getChargeType().equals(POSTPAID_BY_MONTH) || orderSource.getChargeType().equals(POSTPAID_BY_YEAR)){
                    cacheQueueService.addDelayQueue(CUSTOMER_BILL_PRODUCT_QUEUE_TYPE, sysOrderEntity.getId().toString(), 60 * 60 * 24);
                }

            }
            // 新购资源
            case NEW_RESOURCE -> createInstanceOrder(sysOrderEntity, sysOrderEntity.getOrderSourceList());
            case RENEW_RESOURCE -> {
                // todo 续费资源订单
            }
            case RENEW_PRODUCT -> {
                // 续费网络产品订单
                renewProductOrder(sysOrderEntity, sysOrderEntity.getOrderSourceList());
                SysOrderSourceEntity orderSource = sysOrderEntity.getOrderSourceList().get(0);
                if (orderSource.getChargeType().equals(POSTPAID_BY_MONTH) || orderSource.getChargeType().equals(POSTPAID_BY_YEAR)){
                    cacheQueueService.addDelayQueue(CUSTOMER_BILL_PRODUCT_QUEUE_TYPE, sysOrderEntity.getId().toString(), 60 * 60 * 24);
                }
            }
            case UPGRADE_PRODUCT -> {
                // 升级网络产品订单
                upgradeProductOrder(sysOrderEntity);
                SysOrderSourceEntity orderSource = sysOrderEntity.getOrderSourceList().get(0);
                if (orderSource.getChargeType().equals(POSTPAID_BY_MONTH) || orderSource.getChargeType().equals(POSTPAID_BY_YEAR)){
                    cacheQueueService.addDelayQueue(CUSTOMER_BILL_PRODUCT_QUEUE_TYPE, sysOrderEntity.getId().toString(), 60 * 60 * 24);
                }
            }
        }
        log.info("查询充值活动:{}", 1111);
        if (sysOrderEntity.getOrderType().equals(OrderTypeEnum.BALANCE)) {
            // 查询充值活动
            List<SysRechargeActivity> activities = sysRechargeActivityMapper.selectList(
                    new LambdaQueryWrapper<SysRechargeActivity>()
                            .eq(SysRechargeActivity::getStatus, 1)
                            .le(SysRechargeActivity::getRechargeStartTime, new Date())
                            .ge(SysRechargeActivity::getRechargeEndTime, new Date()));
            log.info("查询充值活动:{}", activities);
            if (activities != null && activities.size() > 0) {
                // 查询充值活动的充值金额范围，查询第一个出来记录活动id
                List<Long> collect = activities.stream().map(
                        SysRechargeActivity::getId
                ).collect(Collectors.toList());
                List<SysRechargeActivityRewards> rewards = sysRechargeActivityRewardsMapper.selectList(
                        new LambdaQueryWrapper<SysRechargeActivityRewards>()
                                .in(SysRechargeActivityRewards::getActivityId, collect)
                                .le(SysRechargeActivityRewards::getRechargeAmount, sysOrderEntity.getFinalPayAmount())
                                .orderByDesc(SysRechargeActivityRewards::getRechargeAmount)
                );
                log.info("查询充值活动:{}", activities);
                if (CollectionUtils.isNotEmpty(rewards)) {
                    log.info("查询充值活动 rewards:{}", JSON.toJSONString(rewards, true));
                    SysRechargeActivity sysRechargeActivity = sysRechargeActivityMapper.selectById(rewards.get(0).getActivityId());
                    // 存储到订单中
                    sysOrderEntity.setRewardsType(1);
                    sysOrderEntity.setRechargeId(rewards.get(0).getActivityId());
                    sysOrderEntity.setRechargeName(sysRechargeActivity.getName());
                    sysOrderEntity.setRewardsTime(new Date());
                    sysOrderEntity.setRewardsAmount(rewards.get(0).getCouponAmount());

                    SysCustomerVoucherEntity entity = new SysCustomerVoucherEntity();
                    entity.setVoucherName(sysRechargeActivity.getName());
                    entity.setTotalAmount(rewards.get(0).getCouponAmount());
                    entity.setUseAmount(BigDecimal.ZERO);
                    entity.setStatus(StatusEnum.OK);
                    entity.setUseTimeStart(sysRechargeActivity.getCouponStartTime());
                    entity.setUseTimeEnd(sysRechargeActivity.getCouponEndTime());
                    entity.setCustomerId(sysOrderEntity.getCreateById());

                    sysCustomerVoucherService.addCustomerVoucher(entity);
                }

            }
        }

        sysOrderEntity.setOrderStatus(OrderStatusEnum.PAID);
        sysOrderEntity.setOnlinePaySerialNumber(onlinePaySerialNumber);
        sysOrderEntity.setPayTime(DateUtils.getNowDate());

        if (sysOrderEntity.getNetworkValueId() != null && sysOrderEntity.getOrderType().equals(OrderTypeEnum.PRODUCT)) {
             SysNetworkValueEntity valueEntity = sysNetworkValueMapper.selectById(sysOrderEntity.getNetworkValueId());
//             valueEntity.setPayAmount(sysOrderEntity.getFinalPayAmount());
//             valueEntity.setPayStatus(1);

            //开通飞连账号

            //1、创建客户部门
            SysNetworkProductEntity sysNetworkProductEntity = sysNetworkProductMapper.selectById(valueEntity.getProductId());
            CreateDepartmentVO vo = new CreateDepartmentVO();
            vo.setType(2);
            vo.setParentId(sysNetworkProductEntity.getDepartmentId());//需要查询根部门id ；正式：od_2zdykdKwGxgM 测试：od_qNnYzn1Qe8L1
            vo.setName(valueEntity.getAgiCustomerName());
            AddDepartmentDTO department = flashLianUtils.createDepartment(vo);
            log.info("创建客户部门成功:{}", department);
            String departmentId = department.getId();

            //2、在部门下新增用户
            List<String> emailList = Arrays.stream(valueEntity.getEmail().split(",")).toList();
            List<SysOpenProductUserPwdVO> userPwdList = new ArrayList<>();
            List<SysNetworkProductIpEntity> sysNetworkProductIpEntities = sysNetworkProductIpMapper.selectList(Wrappers.lambdaQuery(SysNetworkProductIpEntity.class)
                    .eq(SysNetworkProductIpEntity::getProductId, valueEntity.getProductId())
                    .eq(SysNetworkProductIpEntity::getStatus, 0));
            int size = sysNetworkProductIpEntities.size();
            //获取VpnId
            List<VpnInfoDTO> vpnList = flashLianUtils.getVpnList();
            VpnInfoDTO vpnInfoDTO = vpnList.get(0);
            Map<String, Integer> nameCountMap = new HashMap<>();
            for (String email : emailList) {

                String[] split = email.split("@");
                String baseName = split[0];

                // 处理重名问题
                String departmentName = baseName;
                if (nameCountMap.containsKey(baseName)) {
                    int count = nameCountMap.get(baseName) + 1;
                    nameCountMap.put(baseName, count);
                    departmentName = baseName + "-" + count;
                } else {
                    nameCountMap.put(baseName, 1);
                }


                //创建账号部门
                CreateDepartmentVO vo4 = new CreateDepartmentVO();
                vo4.setType(2);
                vo4.setParentId(departmentId);
                vo4.setName(departmentName);
                AddDepartmentDTO department2 = flashLianUtils.createDepartment(vo4);
                log.info("创建账号部门成功:{}", department2);
                String departmentId2 = department2.getId();

                CreateUserVO vo2 = new CreateUserVO();
                vo2.setDepartmentId(departmentId2);
                vo2.setEmail(email);
                vo2.setFullName(split[0]);
                vo2.setInviteType(3);
                vo2.setPassword("00000000");//默认密码为邮箱
                vo2.setMobile(valueEntity.getMobile());
                flashLianUtils.createUser(vo2);
                log.info("创建用户成功:{}", email);
                SysOpenProductUserPwdVO userPwdVO = new SysOpenProductUserPwdVO();
                userPwdVO.setEmail(email);
                userPwdVO.setPwd("00000000");
                SysNetworkProductIpEntity sysNetworkProductIpEntity = sysNetworkProductIpEntities.get(size - 1);
                String ip = sysNetworkProductIpEntity.getIp();
                userPwdVO.setIp(ip);
                userPwdVO.setIpAddress(sysNetworkProductIpEntity.getIpAddress());
                userPwdVO.setPublicIp(sysNetworkProductIpEntity.getPublicIp());
                userPwdList.add(userPwdVO);

                //获取飞连用户id
//                JSONObject userInfo = flashLianUtils.getUserInfo(email);
//                String userId = userInfo.get("id").toString();
                AddVpnIpVO vo3 = new AddVpnIpVO();
                vo3.setVpnId(vpnInfoDTO.getId());//需要去查询当前节点的vpnId
                vo3.setFixedIps(new String[]{ip});
                vo3.setDepartmentIds(new String[]{departmentId2});
                log.info("AddVpnIpVO:{}", vo3);
                flashLianUtils.addVpnIp(vo3);
                log.info("添加IP成功:{}", ip);
                sysNetworkProductIpEntity.setStatus(1);
                sysNetworkProductIpEntity.setUserId(valueEntity.getUserId());
                sysNetworkProductIpEntity.setEmail(email);
                sysNetworkProductIpEntity.setUseTime(new Date());
                sysNetworkProductIpMapper.update(sysNetworkProductIpEntity,
                        new LambdaUpdateWrapper<SysNetworkProductIpEntity>()
                                .eq(SysNetworkProductIpEntity::getId, sysNetworkProductIpEntity.getId())
                );
                size--;
            }
            if (size < 3) {
                SysMessage m = new SysMessage();
                m.setMsgType(6);
                m.setText("AGI-C的【" + sysNetworkProductEntity.getName() + "】产品IP地址不足3个，请及时处理！");
                m.setStatus(1);
                m.setUserId(sysOrderEntity.getCreateById());
                sysMessageMapper.insert(m);
            }
            String jsonString = JSONObject.toJSONString(userPwdList);
            Date date = new Date();
            if (valueEntity.getChargeType() == POSTPAID_BY_HOUR){
                sysNetworkValueMapper.update(null,
                        new LambdaUpdateWrapper<SysNetworkValueEntity>()
                                .set(SysNetworkValueEntity::getPayAmount, sysOrderEntity.getFinalPayAmount())
                                .set(SysNetworkValueEntity::getPayStatus, 1)
                                .set(SysNetworkValueEntity::getDepartmentId, departmentId)
                                .set(SysNetworkValueEntity::getActualStatus, 2)
                                .set(SysNetworkValueEntity::getActualAgiOpenTime, date)
                                .set(SysNetworkValueEntity::getActualAgiExpireTime, DateUtils.addDays(date,1))
                                .set(SysNetworkValueEntity::getUserpwdList, jsonString)
                                .eq(SysNetworkValueEntity::getId, valueEntity.getId())

                );
                sysOrderMapper.update(null,
                        new LambdaUpdateWrapper<SysOrderEntity>()
                                .set(SysOrderEntity::getActualStatus, 2)
                                .set(SysOrderEntity::getActualAgiOpenTime, date)
                                .set(SysOrderEntity::getActualAgiExpireTime, DateUtils.addDays(date,1))
                                .set(SysOrderEntity::getBandwidthAll, valueEntity.getBandwidth())
                                .set(SysOrderEntity::getNetworkCountAll, valueEntity.getNetworkCount())
                                .set(SysOrderEntity::getIpCountAll, valueEntity.getIpCount())
                                .eq(SysOrderEntity::getNetworkValueId, valueEntity.getId())

                );
                cacheQueueService.addDelayQueue(CUSTOMER_NEW_DAY_BILL_PRODUCT_QUEUE_TYPE, valueEntity.getId().toString(), 60 * 60 * 24);

            }else {
                LocalDateTime openDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                LocalDateTime expireDateTime;
                if (valueEntity.getChargeType() == POSTPAID_BY_MONTH) {
                    expireDateTime = openDateTime.plusMonths(valueEntity.getDuration());
                } else if (valueEntity.getChargeType() == POSTPAID_BY_YEAR) {
                    expireDateTime = openDateTime.plusYears(valueEntity.getDuration());
                } else {
                    expireDateTime = openDateTime;
                }
                expireDateTime = expireDateTime.withHour(23).withMinute(59).withSecond(59).withNano(0);
                Date expireDate = Date.from(expireDateTime.atZone(ZoneId.systemDefault()).toInstant());

                sysNetworkValueMapper.update(null,
                        new LambdaUpdateWrapper<SysNetworkValueEntity>()
                                .set(SysNetworkValueEntity::getPayAmount, sysOrderEntity.getFinalPayAmount())
                                .set(SysNetworkValueEntity::getPayStatus, 1)
                                .set(SysNetworkValueEntity::getDepartmentId, departmentId)
                                .set(SysNetworkValueEntity::getActualStatus, 2)
                                .set(SysNetworkValueEntity::getActualAgiOpenTime, date)
                                .set(SysNetworkValueEntity::getUserpwdList, jsonString)
                                .set(SysNetworkValueEntity::getActualAgiExpireTime, expireDate)
                                .eq(SysNetworkValueEntity::getId, valueEntity.getId())

                );
                sysOrderMapper.update(null,
                        new LambdaUpdateWrapper<SysOrderEntity>()
                                .set(SysOrderEntity::getActualStatus, 2)
                                .set(SysOrderEntity::getActualAgiOpenTime, date)
                                .set(SysOrderEntity::getActualAgiExpireTime, expireDate)
                                .set(SysOrderEntity::getBandwidthAll, valueEntity.getBandwidth())
                                .set(SysOrderEntity::getNetworkCountAll, valueEntity.getNetworkCount())
                                .set(SysOrderEntity::getIpCountAll, valueEntity.getIpCount())
                                .eq(SysOrderEntity::getNetworkValueId, valueEntity.getId())

                );
            }

            SysMessage m = new SysMessage();
            m.setMsgType(2);
            m.setText("客户：" + valueEntity.getAgiCustomerName() + "新增AGIC产品【" + sysNetworkProductEntity.getName() + "】购买，请查收！");
            m.setStatus(1);
            m.setUserId(sysOrderEntity.getCreateById());
            sysMessageMapper.insert(m);
        }

        // 记录推广大使
        SysExtend one = sysExtendMapper.selectOne(
                new LambdaQueryWrapper<SysExtend>()
                        .eq(SysExtend::getUserId, sysOrderEntity.getCreateById())
        );

        if (one != null) {
            if (one.getParentUserId() != null && !one.getParentUserId().equals(0L)) {
                // 一级推广大使
                SysExtend one1 = sysExtendMapper.selectOne(
                        new LambdaQueryWrapper<SysExtend>()
                                .eq(SysExtend::getUserId, one.getParentUserId())
                                .eq(SysExtend::getType, 1)
                );
                if (one1 != null) {
                    sysOrderEntity.setFirstUserId(one1.getUserId());
                    sysOrderEntity.setFirstRate(one1.getFirstScale());
                    sysOrderEntity.setFirstUserCommission(sysOrderEntity.getFinalPayAmount().multiply(sysOrderEntity.getFirstRate().divide(new BigDecimal("100"))));

                    // 二级推广大使
                    SysExtend one2 = sysExtendMapper.selectOne(
                            new LambdaQueryWrapper<SysExtend>()
                                    .eq(SysExtend::getUserId, one1.getParentUserId())
                                    .eq(SysExtend::getType, 1)
                    );
                    if (one2 != null) {
                        sysOrderEntity.setTwoUserId(one2.getUserId());
                        sysOrderEntity.setTwoRate(one2.getTwoScale());
                        sysOrderEntity.setTwoUserCommission(sysOrderEntity.getFinalPayAmount().multiply(sysOrderEntity.getTwoRate().divide(new BigDecimal("100"))));

                    }


                }
            }
        }

        sysOrderMapper.updateById(sysOrderEntity);
    }

    private void upgradeProductOrder(SysOrderEntity sysOrderEntity) {
        log.info("升级产品订单信息: {}", sysOrderEntity);
        SysNetworkValueEntity sysNetworkValueEntity = sysNetworkValueMapper.selectById(sysOrderEntity.getNetworkValueId());
        if (sysOrderEntity.getNetworkCount() > 0){
            sysNetworkValueEntity.setNetworkCount(sysNetworkValueEntity.getNetworkCount() + sysOrderEntity.getNetworkCount());
            String email2 = sysOrderEntity.getEmail();
            sysNetworkValueEntity.setEmail(sysNetworkValueEntity.getEmail() + "," + email2);
            JSONArray userpwdList = sysNetworkValueEntity.getUserpwdList();
            if (userpwdList != null){
                List<SysOpenProductUserPwdVO> userPwdList = JSON.parseArray(userpwdList.toJSONString(), SysOpenProductUserPwdVO.class);
                List<String> emails = Arrays.stream(email2.split(",")).toList();
                log.info("升级产品的邮箱信息：{}", emails);
                List<SysNetworkProductIpEntity> sysNetworkProductIpEntities = sysNetworkProductIpMapper.selectList(Wrappers.lambdaQuery(SysNetworkProductIpEntity.class)
                        .eq(SysNetworkProductIpEntity::getProductId, sysNetworkValueEntity.getProductId())
                        .eq(SysNetworkProductIpEntity::getStatus, 0));
                int size = sysNetworkProductIpEntities.size();
                //获取VpnId
                List<VpnInfoDTO> vpnList = flashLianUtils.getVpnList();
                VpnInfoDTO vpnInfoDTO = vpnList.get(0);

                // 获取已有用户邮箱信息并初始化 nameCountMap
                List<SysOpenProductUserPwdVO> existingUserPwdList = new ArrayList<>();
                if (sysNetworkValueEntity.getUserpwdList() != null) {
                    existingUserPwdList = JSON.parseArray(sysNetworkValueEntity.getUserpwdList().toJSONString(), SysOpenProductUserPwdVO.class);
                }
                Map<String, Integer> nameCountMap = new HashMap<>(); // 添加这一行

                // 初始化已有用户的计数
                for (SysOpenProductUserPwdVO userPwd : existingUserPwdList) {
                    String[] split = userPwd.getEmail().split("@");
                    String baseName = split[0];
                    nameCountMap.put(baseName, nameCountMap.getOrDefault(baseName, 0) + 1);
                }

                // 处理新增邮箱
                for (String email : emails) {
                    String[] split = email.split("@");
                    String baseName = split[0];

                    // 处理重名问题
                    String departmentName = baseName;
                    if (nameCountMap.containsKey(baseName)) {
                        int count = nameCountMap.get(baseName) + 1;
                        nameCountMap.put(baseName, count);
                        departmentName = baseName + "-" + count;
                    } else {
                        nameCountMap.put(baseName, 1);
                    }
                    //创建账号部门
                    CreateDepartmentVO vo4 = new CreateDepartmentVO();
                    vo4.setType(2);
                    vo4.setParentId(sysNetworkValueEntity.getDepartmentId());
                    vo4.setName(departmentName);
                    AddDepartmentDTO department2 = flashLianUtils.createDepartment(vo4);
                    log.info("创建账号部门成功:{}", department2);
                    String departmentId2 = department2.getId();

                    CreateUserVO vo2 = new CreateUserVO();
                    vo2.setDepartmentId(departmentId2);
                    vo2.setEmail(email);
                    vo2.setFullName(split[0]);
                    vo2.setInviteType(3);
                    vo2.setPassword("00000000");//默认密码为邮箱
                    vo2.setMobile(sysNetworkValueEntity.getMobile());
                    log.info("创建用户:{}", vo2);
                    flashLianUtils.createUser(vo2);
                    log.info("创建用户成功:{}", email);
                    SysOpenProductUserPwdVO userPwdVO = new SysOpenProductUserPwdVO();
                    userPwdVO.setEmail(email);
                    userPwdVO.setPwd("00000000");
                    SysNetworkProductIpEntity sysNetworkProductIpEntity = sysNetworkProductIpEntities.get(size - 1);
                    String ip = sysNetworkProductIpEntity.getIp();
                    userPwdVO.setIp(ip);
                    userPwdVO.setIpAddress(sysNetworkProductIpEntity.getIpAddress());
                    userPwdVO.setPublicIp(sysNetworkProductIpEntity.getPublicIp());
                    userPwdList.add(userPwdVO);

                    //获取飞连用户id
//                    JSONObject userInfo = flashLianUtils.getUserInfo(email);
//                    String userId = userInfo.get("id").toString();
                    AddVpnIpVO vo3 = new AddVpnIpVO();
                    vo3.setVpnId(vpnInfoDTO.getId());//需要去查询当前节点的vpnId
                    vo3.setFixedIps(new String[]{ip});
                    vo3.setDepartmentIds(new String[]{departmentId2});
                    log.info("AddVpnIpVO:{}", vo3);
                    flashLianUtils.addVpnIp(vo3);
                    log.info("添加IP成功:{}", ip);
                    sysNetworkProductIpEntity.setStatus(1);
                    sysNetworkProductIpEntity.setUserId(sysNetworkValueEntity.getUserId());
                    sysNetworkProductIpEntity.setEmail(email);
                    sysNetworkProductIpEntity.setUseTime(new Date());
                    sysNetworkProductIpMapper.update(sysNetworkProductIpEntity,
                            new LambdaUpdateWrapper<SysNetworkProductIpEntity>()
                                    .eq(SysNetworkProductIpEntity::getId, sysNetworkProductIpEntity.getId())
                    );
                    size--;
                }
                if (size < 3) {
                    SysNetworkProductEntity sysNetworkProductEntity = sysNetworkProductMapper.selectById(sysNetworkValueEntity.getProductId());
                    SysMessage m = new SysMessage();
                    m.setMsgType(6);
                    m.setText("AGI-C的【" + sysNetworkProductEntity.getName() + "】产品IP地址不足3个，请及时处理！");
                    m.setStatus(1);
                    m.setUserId(sysOrderEntity.getCreateById());
                    sysMessageMapper.insert(m);
                }
                JSONArray newArray = new JSONArray();
                userPwdList.forEach(vo -> newArray.add(JSON.toJSON(vo)));
                sysNetworkValueEntity.setUserpwdList(newArray);
//                sysNetworkValueEntity.setUserpwdList(JSONArray.of(userPwdList));
            }
        }
        if (sysOrderEntity.getBandwidth() > 0){
            sysNetworkValueEntity.setBandwidth(sysNetworkValueEntity.getBandwidth() + sysOrderEntity.getBandwidth());
        }
        if (sysOrderEntity.getIpCount() > 0){
            sysNetworkValueEntity.setIpCount(sysNetworkValueEntity.getIpCount() + sysOrderEntity.getIpCount());
        }
        sysNetworkValueEntity.setPayAmount(sysNetworkValueEntity.getPayAmount().add(sysOrderEntity.getFinalPayAmount()));
        sysNetworkValueMapper.updateById(sysNetworkValueEntity);

        // 更新订单表中的值为最新的累计值
        sysOrderMapper.update(null,
                new LambdaUpdateWrapper<SysOrderEntity>()
                        .set(SysOrderEntity::getNetworkCountAll, sysNetworkValueEntity.getNetworkCount())
                        .set(SysOrderEntity::getBandwidthAll, sysNetworkValueEntity.getBandwidth())
                        .set(SysOrderEntity::getIpCountAll, sysNetworkValueEntity.getIpCount())
                        .eq(SysOrderEntity::getId, sysOrderEntity.getId())
        );

        SysMessage m = new SysMessage();
        m.setMsgType(9);
        m.setText("客户：" + sysNetworkValueEntity.getAgiCustomerName() + "对AGIC产品进行升级，请查收！");
        m.setStatus(1);
        m.setUserId(sysOrderEntity.getCreateById());
        sysMessageMapper.insert(m);
    }

    private void renewProductOrder(SysOrderEntity sysOrderEntity, List<SysOrderSourceEntity> orderSourceList) {
        log.info("续费产品订单,sysOrderEntity:{},orderSourceList:{}", sysOrderEntity, orderSourceList);
        SysNetworkValueEntity sysNetworkValueEntity = sysNetworkValueMapper.selectById(sysOrderEntity.getNetworkValueId());
        SysOrderSourceEntity sysOrderSourceEntity = orderSourceList.get(0);
        Date actualAgiExpireTime = sysNetworkValueEntity.getActualAgiExpireTime();
        if (actualAgiExpireTime.getTime() < new Date().getTime()){
            LocalDateTime endOfDay = LocalDateTime.now()
                    .withHour(23)
                    .withMinute(59)
                    .withSecond(59)
                    .withNano(0);
            actualAgiExpireTime = Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());
        }
        LocalDate currentDate = actualAgiExpireTime.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        long days = 0;
        Integer duration = sysOrderSourceEntity.getDuration();
        if (sysOrderSourceEntity.getDurationUnit() == YEAR){
            duration = duration * 12;
        }
        LocalDate futureDate = currentDate.plusMonths(duration);
        days = ChronoUnit.DAYS.between(currentDate, futureDate);

        Date date = DateUtils.addDays(actualAgiExpireTime, (int) days);
        //飞连那边添加1天
        Date date2 = DateUtils.addDays(actualAgiExpireTime, (int) days +1);
        // 获取 yyyy-MM-dd 格式的日期字符串
        LocalDate localDate = date2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String dateString = localDate.format(formatter);

        log.info("dateString:{}", dateString);
        //给飞连用户更新时间
        List<SysOpenProductUserPwdVO> userPwdList = com.alibaba.fastjson2.JSON.parseArray(sysNetworkValueEntity.getUserpwdList().toJSONString(), SysOpenProductUserPwdVO.class);
        for (SysOpenProductUserPwdVO sysOpenProductUserPwdVO : userPwdList) {
            String userId = flashLianUtils.getUserInfo(sysOpenProductUserPwdVO.getEmail()).get("id").toString();
            UpdateUserVO vo = new UpdateUserVO();
            vo.setId(userId);
            vo.setExpireDate(dateString);
            flashLianUtils.updateUser(vo);

//            if (sysNetworkValueEntity.getActualAgiExpireTime().before( new Date())){
                UpdateUserStatusVO vo2 = new UpdateUserStatusVO();
                vo2.setId(userId);
                vo2.setStatus("enable");
                flashLianUtils.updateUserStatus(vo2);
//            }
        }

        Integer duration1 = sysNetworkValueEntity.getDuration();
        if (sysNetworkValueEntity.getDurationUnit() == MONTH) {
            duration1 += duration;
        }else if (duration1 == null){
            duration1 = duration;
        }
        sysNetworkValueMapper.update(null,
                new LambdaUpdateWrapper<SysNetworkValueEntity>()
                        .set(SysNetworkValueEntity::getActualAgiExpireTime  , date)
                        .set(SysNetworkValueEntity::getActualStatus, 2)
                        .set(SysNetworkValueEntity::getExpireStatus, 8)
                        .set(SysNetworkValueEntity::getDuration, duration1)
                        .set(SysNetworkValueEntity::getChargeType, sysOrderSourceEntity.getChargeType())
                        .set(SysNetworkValueEntity::getDurationUnit, sysOrderSourceEntity.getDurationUnit())
                        .eq(SysNetworkValueEntity::getId, sysNetworkValueEntity.getId())

        );
        sysOrderMapper.update(null,
                new LambdaUpdateWrapper<SysOrderEntity>()
                        .set(SysOrderEntity::getActualStatus, 2)
                        .set(SysOrderEntity::getActualAgiExpireTime, date)
                        .eq(SysOrderEntity::getNetworkValueId, sysNetworkValueEntity.getId())

        );

        SysMessage m = new SysMessage();
        m.setMsgType(8);
        m.setText("客户：" + sysNetworkValueEntity.getAgiCustomerName() + "对AGIC产品进行续费，请查收！");
        m.setStatus(1);
        m.setUserId(sysOrderEntity.getCreateById());
        sysMessageMapper.insert(m);
    }

    public static void main(String[] args) {
        System.out.println(BigDecimal.valueOf(100).multiply(BigDecimal.valueOf(20).divide(new BigDecimal("100"))));
    }

    @Override
    public SysOrderEntity getOrderDetailByOrderNo(String orderNo) {
        SysOrderEntity orderEntity = sysOrderMapper.selectOne(Wrappers.lambdaQuery(SysOrderEntity.class)
                .eq(SysOrderEntity::getOrderNo, orderNo));
        if (ObjectUtils.isEmpty(orderEntity)) {
            throw new HttpServiceException("订单不存在");
        }
        orderEntity.setOrderSourceList(sysOrderSourceMapper.selectList(Wrappers.lambdaQuery(SysOrderSourceEntity.class)
                .eq(SysOrderSourceEntity::getOrderId, orderEntity.getId())));
        return orderEntity;
    }

    @Override
    public Result<PageResult<ExtendOrderVO>> getExtendPage(PageQuery<SysExtendOrderQuery> query) {
        query.startPage();
        SysExtendOrderQuery orderQuery = query.getQuery();
        return Result.success(Optional.of(sysOrderMapper.getExtendList(orderQuery))
                .flatMap(entities -> {
                        entities.forEach(e -> {
                            // 判断一级和二级用户id 有就查询并填充
                            if (ObjectUtils.isNotEmpty(e.getFirstUserId())) {
                                SysCustomerEntity sysCustomerEntity = sysCustomerMapper.selectById(e.getFirstUserId());
                                e.setFirstAvatar(sysCustomerEntity.getAvatar());
                                e.setFirstUserName(sysCustomerEntity.getCustomerName());
                                e.setFirstUserPhone(sysCustomerEntity.getPhone());
                            }
                            if (ObjectUtils.isNotEmpty(e.getTwoUserId())) {
                                SysCustomerEntity sysCustomerEntity = sysCustomerMapper.selectById(e.getTwoUserId());
                                e.setTwoAvatar(sysCustomerEntity.getCustomerName());
                                e.setTwoUserName(sysCustomerEntity.getCustomerName());
                                e.setTwoUserPhone(sysCustomerEntity.getPhone());
                            }
                            // 获取order_souce 的列表 根据订单id 并回填数据
                             List<SysOrderSourceEntity> sysOrderSourceEntities = sysOrderSourceMapper.selectList(Wrappers.lambdaQuery(SysOrderSourceEntity.class)
                                    .eq(SysOrderSourceEntity::getOrderId, e.getId()));

                            e.setOrders(sysOrderSourceEntities);
                        });
                    return PageResult.of(entities);
                })
                .orElseGet(() -> PageResult.of(List.of())));
    }


    private void createInstanceOrder(SysOrderEntity sysOrderEntity, List<SysOrderSourceEntity> orderSourceList) {
        if (ObjectUtils.isEmpty(orderSourceList)) {
            throw new HttpServiceException("资源列表不能为空");
        }
        Map<String, List<SysOrderSourceEntity>> uidMap = orderSourceList.stream().collect(Collectors.groupingBy(SysOrderSourceEntity::getUid));
        List<SysCustomerInstancesEntity> customerInstancesEntities = new ArrayList<>(orderSourceList.size());
        List<SysCustomerEipEntity> customerEipEntities = new ArrayList<>(orderSourceList.size());
        List<SysCustomerVolumeEntity> customerVolumeEntityList = new ArrayList<>(orderSourceList.size());
        List<SysCustomerContainerEntity> customerContainerEntityList = new ArrayList<>(orderSourceList.size());
        List<SysCustomerImageRepositoryEntity> customerImageRepositoryEntityList = new ArrayList<>(orderSourceList.size());
        Long customerId = sysOrderEntity.getCreateById();
        boolean tag = false;
        for (Map.Entry<String, List<SysOrderSourceEntity>> entry : uidMap.entrySet()) {
            for (SysOrderSourceEntity sourceEntity : entry.getValue()) {
                JSONObject configDetail = sourceEntity.getConfigDetail();
                switch (sourceEntity.getSourceType()) {
                    case ECS -> {
                        SysCustomerInstancesEntity entity = new SysCustomerInstancesEntity();
                        entity.setOrderId(sysOrderEntity.getId());
                        entity.setOrderSourceUid(entry.getKey());
                        entity.setCustomerId(customerId);
                        entity.setEcsScale(configDetail.getString("ecsScale"));
                        entity.setCpuNumber(configDetail.getLong("cpuNumber"));
                        entity.setCpuModel(configDetail.getString("cpuModel"));
                        entity.setMemorySize(configDetail.getLong("memorySize"));
                        entity.setGpuMemory(configDetail.getString("gpuMemory"));
                        entity.setGpuModel(configDetail.getString("gpuModel"));
                        entity.setRegion(sourceEntity.getRegionsId());
                        entity.setDescription(configDetail.getString("description"));
                        entity.setInstanceName(ObjectUtils.defaultIfNull(configDetail.getString("instanceName"), IdUtils.randomLetterString(36)));
                        entity.setHostName(configDetail.getString("hostname"));
                        entity.setImageId(configDetail.getString("imageId"));
                        entity.setCommandAgent(configDetail.getBoolean("installRunCommandAgent"));
                        entity.setChargeType(sourceEntity.getChargeType());
                        entity.setPassword(configDetail.getString("password"));
                        entity.setDuration(sourceEntity.getDuration());
                        entity.setDurationUnit(sourceEntity.getDurationUnit());
                        entity.setStatus(EcsStatusEnum.CREATING);
                        entity.setZoneId(configDetail.getString("zoneId"));
                        entity.setProductType(sourceEntity.getProductType());
                        entity.setEcsType(sourceEntity.getEcsType());
                        entity.setBandwidth(sysOrderEntity.getBandwidth());
                        entity.setStartTime(new Date());
                        LocalDate currentDate = LocalDate.now();
                        Date date = new Date();
                        if (sourceEntity.getProductType() == 2){
                            if (sourceEntity.getChargeType() == POSTPAID_BY_HOUR){
                                entity.setExpireTime(DateUtils.addDays(date, sourceEntity.getDuration()));
                            }else if (sourceEntity.getChargeType() == POSTPAID_BY_MONTH){
                                LocalDate futureDate = currentDate.plusMonths(sourceEntity.getDuration());
                                long days = ChronoUnit.DAYS.between(currentDate, futureDate);
                                entity.setExpireTime(DateUtils.addDays(date, (int) days));
                            }else if (sourceEntity.getChargeType() == POSTPAID_BY_YEAR){
                                LocalDate futureDate = currentDate.plusYears(sourceEntity.getDuration());
                                long days = ChronoUnit.DAYS.between(currentDate, futureDate);
                                entity.setExpireTime(DateUtils.addDays(date, (int) days));
                            }
                        }
                        customerInstancesEntities.add(entity);
                    }
                    case CLOUD_NETWORK -> {
                        SysCustomerEipEntity eipEntity = new SysCustomerEipEntity();
                        eipEntity.setOrderId(sysOrderEntity.getId());
                        eipEntity.setOrderSourceUid(entry.getKey());
                        eipEntity.setCustomerId(customerId);
                        EipAddressConfigModel configModel = configDetail.toJavaObject(EipAddressConfigModel.class);
                        eipEntity.setBandwidth(configModel.getBandwidthMbps());
                        eipEntity.setBillingType(configModel.getChargeType());
                        eipEntity.setIsp(configModel.getIsp());
                        eipEntity.setEipName(IdUtils.fastSimpleUUID());
                        eipEntity.setEipDesc(eipEntity.getEipName());
                        customerEipEntities.add(eipEntity);
                    }
                    case CLOUD_STORAGE -> {
                        SysCustomerVolumeEntity volumeEntity = new SysCustomerVolumeEntity();
                        String kind = configDetail.getString("kind");
                        volumeEntity.setOrderId(sysOrderEntity.getId());
                        volumeEntity.setOrderSourceUid(entry.getKey());
                        volumeEntity.setRegionId(sourceEntity.getRegionsId());
                        volumeEntity.setCustomerId(customerId);
                        volumeEntity.setChargeType(sourceEntity.getChargeType());
                        if ("system".equals(kind)) {
                            EcsSystemVolumeConfigModel configModel = configDetail.toJavaObject(EcsSystemVolumeConfigModel.class);
                            volumeEntity.setKind(kind);
                            volumeEntity.setVolumeType(configModel.getVolumeType());
                            volumeEntity.setSize(configModel.getSize());
                        } else {
                            SysVolumeEntity sysVolumeEntity = configDetail.toJavaObject(SysVolumeEntity.class);
                            volumeEntity.setKind(kind);
                            volumeEntity.setVolumeType(sysVolumeEntity.getVolumeType());
                            volumeEntity.setSize(sysVolumeEntity.getVolumeCapacity());
                        }
                        customerVolumeEntityList.add(volumeEntity);
                    }
                    case VKE ->{
                        SysCustomerContainerEntity sysCustomerContainerEntity = new SysCustomerContainerEntity();
                        sysCustomerContainerEntity.setOrderId(sysOrderEntity.getId());
                        sysCustomerContainerEntity.setOrderSourceUid(entry.getKey());
                        sysCustomerContainerEntity.setCustomerId(customerId);
                        sysCustomerContainerEntity.setClusterName(configDetail.getString("clusterName"));
                        sysCustomerContainerEntity.setKubernetesVersion(configDetail.getString("kubernetesVersion"));
                        sysCustomerContainerEntity.setResourcePublicAccessDefaultEnabled(configDetail.getBoolean("resourcePublicAccessDefaultEnabled"));
                        sysCustomerContainerEntity.setApiServerPublicAccessEnabled(configDetail.getBoolean("apiServerPublicAccessEnabled"));
                        sysCustomerContainerEntity.setNodePoolName(configDetail.getString("nodePoolName"));
                        sysCustomerContainerEntity.setNodePoolNumber(configDetail.getInteger("nodePoolNumber"));
                        sysCustomerContainerEntity.setIsOpenSecurityHardening(configDetail.getBoolean("isOpenSecurityHardening"));
                        sysCustomerContainerEntity.setStatus(ContainerStatusEnum.CREATING);
                        customerContainerEntityList.add(sysCustomerContainerEntity);
                        tag = true;
                    }
                    case CR -> {
                        SysCustomerImageRepositoryEntity sysCustomerImageEntity = new SysCustomerImageRepositoryEntity();
                        sysCustomerImageEntity.setOrderId(sysOrderEntity.getId());
                        sysCustomerImageEntity.setOrderSourceUid(entry.getKey());
                        sysCustomerImageEntity.setCustomerId(customerId);
                        sysCustomerImageEntity.setInstanceName(configDetail.getString("instanceName"));
                        sysCustomerImageEntity.setDomain(sysCustomerImageEntity.getInstanceName() + "-cn-beijing.cr.volces.com");
                        sysCustomerImageEntity.setStatus(ContainerStatusEnum.CREATING);
                        sysCustomerImageEntity.setChargeType(POSTPAID_BY_HOUR);
                        customerImageRepositoryEntityList.add(sysCustomerImageEntity);
                    }
                }
            }
        }
        sysCustomerInstancesMapper.batchInsert(customerInstancesEntities);
        sysCustomerEipMapper.batchInsert(customerEipEntities);
        sysCustomerVolumeMapper.batchInsert(customerVolumeEntityList);
        if (ObjectUtils.isNotEmpty(customerContainerEntityList)){{
            List<SysCustomerContainerEntity> list = customerContainerEntityList.stream().distinct().toList();
            SysCustomerInstancesEntity sysCustomerInstancesEntity = sysCustomerInstancesMapper.selectOne(Wrappers.lambdaQuery(SysCustomerInstancesEntity.class)
                    .eq(SysCustomerInstancesEntity::getOrderSourceUid, list.get(0).getOrderSourceUid()));
            if (sysCustomerInstancesEntity != null){
                list.forEach(item -> {
                    item.setCustomerInstancesId(sysCustomerInstancesEntity.getId());
                });
            }
            sysCustomerContainerMapper.batchInsert(list);
        }}
        List<SysCustomerImageRepositoryEntity> customerImageRepositoryEntities = customerImageRepositoryEntityList.stream().distinct().toList();
        sysImageRepositoryMapper.batchInsert(customerImageRepositoryEntities);
        boolean workCreated = false; // 标记是否已生成工单
        for (SysCustomerInstancesEntity customerInstancesEntity : customerInstancesEntities) {
            if (!tag){
                if (customerInstancesEntity.getProductType()  == 1){
                    log.info("创建VolcEngine ECS实例");
                    cacheQueueService.addDelayQueue(CacheQueueConstant.CUSTOMER_INSTANCE_CREATE_QUERY_QUEUE_TYPE, customerInstancesEntity.getOrderSourceUid(), 5);
                }else if (!workCreated) {
                    //todo 自建服务器生成工单
                    SysCustomerEcsWorkEntity entity = new SysCustomerEcsWorkEntity();
                    // 获取当前时间（格式：yyyyMMddHHmmss）
                    String timestamp = LocalDateTime.now()
                            .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
                    // 生成5位随机数（范围：10000-99999）
                    int randomNum = ThreadLocalRandom.current().nextInt(10000, 100000);
                    // 拼接成19位字符串
                    String result = timestamp + randomNum;
                    entity.setCustomerId(customerId);
                    entity.setWorkNo(result);
                    entity.setOrderId(sysOrderEntity.getId());
                    entity.setNumber((int)sysOrderEntity.getOrderSourceList().stream().filter(item -> item.getSourceType().equals(ECS)).count());
                    sysCustomerEcsWorkMapper.insert(entity);
                    workCreated = true; // 标记已生成
                    if (customerInstancesEntity.getChargeType().equals(POSTPAID_BY_HOUR)) {
                        cacheQueueService.addDelayQueue(CacheQueueConstant.CUSTOMER_SELF_BUILD_SERVER_HOURLY_CHECK_QUEUE_TYPE, customerInstancesEntity.getId().toString(), 60 * 60 * 24);
                    }else {
                        cacheQueueService.addDelayQueue(CacheQueueConstant.CUSTOMER_BILL_SELF_BUILD_QUEUE_TYPE, sysOrderEntity.getId().toString(), 60 * 60 * 24);
                    }
                    SysMessage m = new SysMessage();
                    m.setMsgType(7);
                    m.setText("新增购买自建服务器工单，请及时处理！");
                    m.setStatus(1);
                    m.setUserId(customerId);
                    sysMessageMapper.insert(m);
                }
            }
        }
        for (SysCustomerContainerEntity sysCustomerContainerEntity : customerContainerEntityList) {
            cacheQueueService.addDelayQueue(CacheQueueConstant.CUSTOMER_CONTAINER_CREATE_QUERY_QUEUE_TYPE, sysCustomerContainerEntity.getOrderSourceUid(), 5);
        }
        for (SysCustomerImageRepositoryEntity sysCustomerImageRepositoryEntity : customerImageRepositoryEntityList) {
            cacheQueueService.addDelayQueue(CacheQueueConstant.CUSTOMER_IMAGE_REPOSITORY_QUERY_QUEUE_TYPE, sysCustomerImageRepositoryEntity.getOrderSourceUid(), 5);
        }
    }

    // 创建充值订单
    private List<SysOrderSourceEntity> createBalanceOrder(SysOrderCreateDTO createDTO) {
        if (createDTO.getAmount() == null || createDTO.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new HttpServiceException("金额不能小于0");
        }
        SysOrderSourceEntity entity = new SysOrderSourceEntity();
        entity.setUnitPrice(createDTO.getAmount());
        entity.setPremiumPrice(BigDecimal.ZERO);
        entity.setUserDiscountAmount(BigDecimal.ZERO);
        entity.setVoucherDiscountAmount(BigDecimal.ZERO);
        entity.setCouponDiscountAmount(BigDecimal.ZERO);
        entity.setFinalUnitPrice(createDTO.getAmount());
        entity.setNumber(1);
        createDTO.setBalancePay(false);
        createDTO.setCouponId(null);
        createDTO.setVoucherPay(false);
        return List.of(entity);
    }

    // 创建资源订单
    private List<SysOrderSourceEntity> createResourceOrder(SysOrderCreateDTO createDTO) {
        List<SysOrderSourceCreateDTO> sourceList = createDTO.getOrderSource();
        if (ObjectUtils.isEmpty(sourceList)) {
            throw new HttpServiceException("sourceList is empty");
        }
        List<SysOrderSourceCreateDTO> escList = new ArrayList<>(sourceList.size());
        List<SysOrderSourceCreateDTO> volumeList = new ArrayList<>(sourceList.size());
        List<SysOrderSourceCreateDTO> containerList = new ArrayList<>(sourceList.size());
        List<SysOrderSourceCreateDTO> imageRepositoryList = new ArrayList<>(sourceList.size());
        List<SysOrderSourceEntity> orderSourceList = new ArrayList<>(sourceList.size());
        for (SysOrderSourceCreateDTO orderSource : sourceList) {
            if (Objects.requireNonNull(orderSource.getSourceType()) == ECS) {
                escList.add(orderSource);
            }
            if (Objects.requireNonNull(orderSource.getSourceType()) == SourceTypeEnum.CLOUD_STORAGE) {
                volumeList.add(orderSource);
            }
            if (Objects.requireNonNull(orderSource.getSourceType()) == SourceTypeEnum.CONTAINER){
                containerList.add(orderSource);
            }
            if (Objects.requireNonNull(orderSource.getSourceType()) == SourceTypeEnum.CR){
                imageRepositoryList.add(orderSource);
            }

        }
        handlerEcsOrder(createDTO,orderSourceList, escList);
        handlerVolumeOrder(orderSourceList, volumeList);
        handlerContainerOrder(orderSourceList, containerList);
        handlerImageRepositoryOrder(orderSourceList, imageRepositoryList);
        // 处理平台溢价
        Map<SourceTypeEnum, BigDecimal> sourceDiscount = Optional.of(sysCustomerDiscountMapper.selectList(Wrappers.lambdaQuery(SysCustomerDiscountEntity.class)
                        .eq(SysCustomerDiscountEntity::getCustomerId, SecurityContext.getUserInfo().getUserId())))
                .flatMap(list -> list.stream().collect(Collectors.toMap(SysCustomerDiscountEntity::getSourceType, SysCustomerDiscountEntity::getDiscountRation)))
                .orElse(new HashMap<>(0));
        BigDecimal sellPriceRatio = sysConfigService.getConfig(SystemConfigEnum.SELL_PRICE_RATIO).getValue(SystemPriceRationConfigModel.class).getPriceRatio();
        orderSourceList.forEach(e -> {
            // 处理用户溢价
            e.setPremiumPrice(e.getUnitPrice().multiply(sellPriceRatio).setScale(2, RoundingMode.UP));
            log.info("处理用户议价:{} sellPriceRatio:{}", e.getPremiumPrice(), sellPriceRatio);
            BigDecimal orDefault = sourceDiscount.get(e.getSourceType());
            log.info("处理用户议价 getSourceType：{} orDefault :{}",  e.getSourceType(), orDefault);
            if (orDefault == null || orDefault.compareTo(BigDecimal.ONE) == 0) {
                e.setUserDiscountAmount(BigDecimal.ZERO);
                e.setFinalUnitPrice(e.getPremiumPrice().setScale(2, RoundingMode.UP));
                log.info("123处理用户议价 e.getPremiumPrice():{}",  e.getPremiumPrice());
            } else {
                e.setUserDiscountAmount(e.getPremiumPrice().multiply(orDefault).setScale(2, RoundingMode.UP));
                e.setFinalUnitPrice(e.getUserDiscountAmount().setScale(2, RoundingMode.UP));
                log.info("456处理用户议价 getSourceType：{} orDefault :{}",  e.getSourceType(), orDefault);
            }
        });
        return orderSourceList;
    }

    private void handlerImageRepositoryOrder(List<SysOrderSourceEntity> orderSourceList, List<SysOrderSourceCreateDTO> imageRepositoryList) {
        if (ObjectUtils.isEmpty(imageRepositoryList)) {
            return;
        }
        for (SysOrderSourceCreateDTO source : imageRepositoryList) {
            String uid = ObjectUtils.defaultIfNull(source.getUid(), IdUtils.randomUUID());
            SysOrderSourceEntity ecsOrderSourceEntity = BeanUtils.copyBean(source, SysOrderSourceEntity.class);
            ecsOrderSourceEntity.setSourceType(SourceTypeEnum.CR);
            ecsOrderSourceEntity.setUid(uid);
            ecsOrderSourceEntity.setProductName("实例费用");
            ecsOrderSourceEntity.setUnitPrice(new BigDecimal("1.97"));

            SysOrderSourceEntity networkSourceEntity = BeanUtils.copyBean(source, SysOrderSourceEntity.class);
            networkSourceEntity.setSourceType(SourceTypeEnum.CR);//暂时设置为CR，具体值看账单返回得产品名称
            networkSourceEntity.setUid(uid);
            networkSourceEntity.setProductName("公网流出流量费用");
            networkSourceEntity.setUnitPrice(new BigDecimal("0.5"));

            SysOrderSourceEntity storageCapacitySourceEntity = BeanUtils.copyBean(source, SysOrderSourceEntity.class);
            networkSourceEntity.setSourceType(SourceTypeEnum.CR);//暂时设置为CR，具体值看账单返回得产品名称
            storageCapacitySourceEntity.setUid(uid);
            storageCapacitySourceEntity.setProductName("存储容量费用");
            storageCapacitySourceEntity.setUnitPrice(new BigDecimal("0.099"));
            orderSourceList.addAll(List.of(ecsOrderSourceEntity, networkSourceEntity, storageCapacitySourceEntity));
        }
    }

    private void handlerContainerOrder(List<SysOrderSourceEntity> orderSourceList, List<SysOrderSourceCreateDTO> containerList) {
        if (ObjectUtils.isEmpty(containerList)) {
            return;
        }

        for (SysOrderSourceCreateDTO source : containerList) {
            String uid = ObjectUtils.defaultIfNull(source.getUid(), IdUtils.randomUUID());
            SysOrderSourceEntity balancingOrderSourceEntity = BeanUtils.copyBean(source, SysOrderSourceEntity.class);
            balancingOrderSourceEntity.setSourceType(SourceTypeEnum.CLB);
            balancingOrderSourceEntity.setUid(uid);
            balancingOrderSourceEntity.setProductName("负载均衡资源费用");
            balancingOrderSourceEntity.setUnitPrice(new BigDecimal("0.3500"));//取火山默认值
            SysOrderSourceEntity NATOrderSourceEntity = BeanUtils.copyBean(source, SysOrderSourceEntity.class);
            NATOrderSourceEntity.setSourceType(SourceTypeEnum.NAT_GATEWAY);
            NATOrderSourceEntity.setUid(uid);
            NATOrderSourceEntity.setProductName("NAT 网关资源费用");
            NATOrderSourceEntity.setUnitPrice(new BigDecimal("0.9600"));
            SysOrderSourceEntity  hostingFeesOrderSourceEntity = BeanUtils.copyBean(source, SysOrderSourceEntity.class);
            hostingFeesOrderSourceEntity.setSourceType(SourceTypeEnum.VKE);
            hostingFeesOrderSourceEntity.setUid(uid);
            hostingFeesOrderSourceEntity.setProductName("容器服务托管费用");
            hostingFeesOrderSourceEntity.setUnitPrice(new BigDecimal("0.8417"));
            orderSourceList.add(NATOrderSourceEntity);
            orderSourceList.add(hostingFeesOrderSourceEntity);
            orderSourceList.add(balancingOrderSourceEntity);
        }
    }

    private void handlerVolumeOrder(List<SysOrderSourceEntity> orderSourceList, List<SysOrderSourceCreateDTO> volumeList) {
        if (ObjectUtils.isEmpty(volumeList)) {
            return;
        }
        List<SysVolumeEntity> volumeEntity = sysVolumeMapper.selectList(Wrappers.lambdaQuery(SysVolumeEntity.class)
                .in(SysVolumeEntity::getId, volumeList.stream().map(e -> e.getConfigDetail().getLong("id")).toList())
                .eq(SysVolumeEntity::getKind, "data"));
        if (ObjectUtils.isEmpty(volumeEntity)) {
            throw new HttpServiceException("数据盘不存在，请联系管理员同步");
        }
        Map<Long, SysVolumeEntity> volumeIdMap = volumeEntity.stream().collect(Collectors.toMap(SysVolumeEntity::getId, e -> e));
        for (SysOrderSourceCreateDTO source : volumeList) {
            SysVolumeEntity sysVolumeEntity = volumeIdMap.get(source.getConfigDetail().getLong("id"));
            if (sysVolumeEntity == null) {
                continue;
            }
            SysOrderSourceEntity entity = BeanUtils.copyBean(source, SysOrderSourceEntity.class);
            entity.setUid(ObjectUtils.defaultIfNull(source.getUid(), IdUtils.randomUUID()));
            entity.setProductName("数据盘");
            entity.setSourceName(sysVolumeEntity.getVolumeScale());
            entity.setConfigDetail(JSONObject.parseObject(JSONObject.toJSONString(sysVolumeEntity)));
            switch (entity.getChargeType()) {
                case POSTPAID_BY_HOUR -> entity.setUnitPrice(sysVolumeEntity.getHoursPrice());
                case POSTPAID_BY_MONTH -> entity.setUnitPrice(sysVolumeEntity.getMonthPrice());
                case POSTPAID_BY_YEAR -> entity.setUnitPrice(calculatePrice(entity.getDuration(), sysVolumeEntity::getThreeYearPrice, sysVolumeEntity::getTwoYearPrice, sysVolumeEntity::getOneYearPrice));
            }
            orderSourceList.add(entity);
        }
    }


    private void handlerEcsOrder(SysOrderCreateDTO createDTO,List<SysOrderSourceEntity> orderSourceList, List<SysOrderSourceCreateDTO> escList) {
        if (ObjectUtils.isEmpty(escList)) {
            return;
        }
        EcsSystemVolumeConfigModel systemVolumeConfigModel = sysConfigService.getConfig(SystemConfigEnum.ECS_SYSTEM_VOLUME).getValue(EcsSystemVolumeConfigModel.class);
        EipAddressConfigModel eipAddressConfigModel = sysConfigService.getConfig(SystemConfigEnum.EIP_ADDRESS).getValue(EipAddressConfigModel.class);
        List<Long> ecsIdList = escList.stream()
                .flatMap(orderSource -> Stream.of(orderSource.getConfigDetail().toJavaObject(SysEcsEntity.class)))
                .map(BaseEntity::getId)
                .toList();
        LambdaQueryWrapper<SysEcsEntity> wrapper = Wrappers.lambdaQuery(SysEcsEntity.class);
        wrapper.in(SysEcsEntity::getId, ecsIdList);
        List<SysEcsEntity> ecsList = sysEcsMapper.selectList(wrapper);
        if (ObjectUtils.isEmpty(ecsList)) {
            throw new HttpServiceException("云服务器不存在，请联系管理员同步服务器");
        }
        Map<Long, SysEcsEntity> sysEcsMap = ecsList.stream().collect(Collectors.toMap(BaseEntity::getId, ecsEntity -> ecsEntity));
        // 处理价格
        for (SysOrderSourceCreateDTO ecsCreate : escList) {
            String uid = ObjectUtils.defaultIfNull(ecsCreate.getUid(), IdUtils.randomUUID());
            SysOrderSourceEntity ecsOrderSource = BeanUtils.copyBean(ecsCreate, SysOrderSourceEntity.class);
            ecsOrderSource.setUid(uid);
            SysOrderSourceEntity volumeOrderSource = EcsSystemVolumeConfigModel.buildOrderSource(ecsOrderSource, systemVolumeConfigModel);
            volumeOrderSource.setUid(uid);
            SysOrderSourceEntity eipAddressOrderSource = EipAddressConfigModel.buildIpOrderSource(createDTO,ecsOrderSource, eipAddressConfigModel);
            eipAddressOrderSource.setUid(uid);
            SysEcsEntity ecsConfig = ecsOrderSource.getConfigDetail().toJavaObject(SysEcsEntity.class);
            SysEcsEntity sysEcsEntity = sysEcsMap.get(ecsConfig.getId());
            ecsOrderSource.setProductName("云服务器");
            ecsOrderSource.setSourceName(sysEcsEntity.getEcsScale());
            ecsOrderSource.setEcsType(sysEcsEntity.getEcsType());
            eipAddressOrderSource.setChargeType(ecsOrderSource.getChargeType());
            switch (ecsOrderSource.getChargeType()) {
                case POSTPAID_BY_HOUR -> {
                    volumeOrderSource.setUnitPrice(systemVolumeConfigModel.getHoursPrice());
                    ecsOrderSource.setUnitPrice(sysEcsEntity.getHoursPrice());
                    if (ecsCreate.getProductType() == 2){
                        eipAddressOrderSource.setUnitPrice(sysEcsEntity.getIpPrice().multiply(new BigDecimal(createDTO.getBandwidth())));
                        eipAddressOrderSource.setDurationUnit(HOUR);
                    }
                }
                case POSTPAID_BY_MONTH -> {
                    Integer duration = ecsOrderSource.getDuration();
                    if (duration == null) {
                        duration = 1;
                    }
                    volumeOrderSource.setUnitPrice(systemVolumeConfigModel.getMonthPrice().multiply(BigDecimal.valueOf(duration)));
                    ecsOrderSource.setUnitPrice(sysEcsEntity.getMonthPrice().multiply(BigDecimal.valueOf(duration)));
                    if (ecsCreate.getProductType() == 2){
                        eipAddressOrderSource.setDurationUnit(MONTH);
                        eipAddressOrderSource.setUnitPrice(sysEcsEntity.getIpPrice().multiply(new BigDecimal(24 * 30)).multiply(BigDecimal.valueOf(duration)).multiply(new BigDecimal(createDTO.getBandwidth())));
                    }
                }
                case POSTPAID_BY_YEAR -> {
                    Integer duration = ecsOrderSource.getDuration();
                    if (duration == null) {
                        duration = 1;
                    }
                    if (duration <= 0) {
                        throw new HttpServiceException("时长必须大于0");
                    }
                    BigDecimal ecsPrice = calculatePrice(duration, sysEcsEntity::getThreeYearPrice, sysEcsEntity::getTwoYearPrice, sysEcsEntity::getOneYearPrice);
                    BigDecimal volumePrice = calculatePrice(duration, systemVolumeConfigModel::getThreeYearPrice, systemVolumeConfigModel::getTwoYearPrice, systemVolumeConfigModel::getOneYearPrice);
                    ecsOrderSource.setUnitPrice(ecsPrice);
                    volumeOrderSource.setUnitPrice(volumePrice);
                    if (ecsCreate.getProductType() == 2){
                        eipAddressOrderSource.setDurationUnit(YEAR);
                        eipAddressOrderSource.setUnitPrice(sysEcsEntity.getIpPrice().multiply(new BigDecimal(24 * 30)).multiply(BigDecimal.valueOf(duration * 12)).multiply(new BigDecimal(createDTO.getBandwidth())));
                    }
                }
            }
            orderSourceList.add(ecsOrderSource);
            orderSourceList.add(volumeOrderSource);
            orderSourceList.add(eipAddressOrderSource);
        }
    }

    private BigDecimal calculatePrice(int duration, Supplier<BigDecimal> threeYearPriceSupplier, Supplier<BigDecimal> twoYearPriceSupplier, Supplier<BigDecimal> oneYearPriceSupplier) {
        BigDecimal price = BigDecimal.ZERO;
        if (duration >= 3) {
            int index = duration / 3;
            price = price.add(threeYearPriceSupplier.get().multiply(BigDecimal.valueOf(index)));
            duration -= index * 3;
        }
        if (duration >= 2) {
            int index = duration / 2;
            price = price.add(twoYearPriceSupplier.get().multiply(BigDecimal.valueOf(index)));
            duration -= index * 2;
        }
        if (duration >= 1) {
            price = price.add(oneYearPriceSupplier.get().multiply(BigDecimal.valueOf(duration)));
        }
        return price;
    }

    // 续费订单
    private List<SysOrderSourceEntity> createRenewResourceOrder(String orderNo) {
        return emptyList();
    }

    @Override
    public SysEcsWorkPageDTO getOrderDetail(String orderNo) {
        return null;
    }

    @Override
    public Boolean updateAgiCustomerName(PcUpdateCustomerNameVO vo) {

        List<SysNetworkValueEntity> sysNetworkValueEntityList = sysNetworkValueMapper.selectList(Wrappers.lambdaQuery(SysNetworkValueEntity.class)
                .eq(SysNetworkValueEntity::getActualStatus, 2));

        if (vo.getAgiCustomerName() != null) {
            // 检查 agiCustomerName 是否已经存在于数据库中
            boolean exists = sysNetworkValueEntityList.stream()
                    .anyMatch(entity -> vo.getAgiCustomerName().equals(entity.getAgiCustomerName()));

            if (exists) {
                throw new HttpServiceException("客户名称已存在，请重新输入");
            }
        }

        SysNetworkValueEntity sysNetworkValueEntity = sysNetworkValueMapper.selectById(vo.getNetworkValueId());

        //修改飞连部门名称
        UpdateDepartmentVO vo1 = new UpdateDepartmentVO();
        vo1.setId(sysNetworkValueEntity.getDepartmentId());
        vo1.setName(vo.getAgiCustomerName());
        vo1.setParentId(parentDepartmentId);//需要查询根部门id ；正式：od_2zdykdKwGxgM 测试：od_qNnYzn1Qe8L1
        flashLianUtils.updateDepartment(vo1);

        return sysNetworkValueMapper.update(null,new LambdaUpdateWrapper<SysNetworkValueEntity>()
                .set(SysNetworkValueEntity::getAgiCustomerName, vo.getAgiCustomerName())
                .eq(SysNetworkValueEntity::getId, vo.getNetworkValueId())) > 0;
    }

    @Override
    public Boolean updateAgiIsAutoRenew(PcUpdateAutoRenewVO vo) {
        return sysNetworkValueMapper.update(null,new LambdaUpdateWrapper<SysNetworkValueEntity>()
                .set(SysNetworkValueEntity::getIsAutoRenew, vo.getIsAutoRenew())
                .eq(SysNetworkValueEntity::getId, vo.getNetworkValueId())) > 0;
    }

    @Override
    public List<String> isEmailExist(PcIsEmailExistVO vo) {
        List<String> list = new ArrayList<>();
        for (String e : vo.getEmails()) {
            JSONObject userInfo = flashLianUtils.getUserInfo2(e);
            if (userInfo.getInteger("code") == 0) {
                // 处理已有数据进行处理
                list.add(e);
            }
        }
        return list;
    }
}