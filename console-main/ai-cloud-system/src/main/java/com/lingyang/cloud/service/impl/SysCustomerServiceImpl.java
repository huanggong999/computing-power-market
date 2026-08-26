package com.lingyang.cloud.service.impl;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.api.model.dto.PcRealNameDTO;
import com.lingyang.cloud.api.model.vo.PcRealNameVO;
import com.lingyang.cloud.entity.*;
import com.lingyang.cloud.enums.customer.SysCustomerAmountType;
import com.lingyang.cloud.enums.customer.SysTransactionType;
import com.lingyang.cloud.enums.order.OrderStatusEnum;
import com.lingyang.cloud.enums.source.EcsStatusEnum;
import com.lingyang.cloud.enums.source.SourceTypeEnum;
import com.lingyang.cloud.enums.system.StatusEnum;
import com.lingyang.cloud.mapper.*;
import com.lingyang.cloud.model.query.customer.SysCompanyQuery;
import com.lingyang.cloud.model.query.customer.SysCustomerQuery;
import com.lingyang.cloud.model.vo.customer.SysCustomerDiscountVO;
import com.lingyang.cloud.model.vo.customer.SysCustomerListVO;
import com.lingyang.cloud.service.SysCustomerCreditLineService;
import com.lingyang.cloud.service.SysCustomerService;
import com.lingyang.cloud.service.SysCustomerVoucherService;
import com.lingyang.cloud.utils.BaiDuCertUtils;
import com.lingyang.cloud.utils.TencentUtils;
import com.lingyang.common.core.exception.http.HttpServiceException;
import com.lingyang.common.core.lock.DistributedLock;
import com.lingyang.common.core.lock.distributed.DistributedExecute;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.security.SecurityContext;
import com.lingyang.common.core.utils.DateUtils;
import com.lingyang.common.core.utils.Optional;
import com.lingyang.common.core.utils.StringUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/10/21 17:17
 */
@Service
@Slf4j
public class SysCustomerServiceImpl implements SysCustomerService {
    @Resource
    private DistributedLock distributedLock;
    @Resource
    private SysCustomerMapper sysCustomerMapper;
    @Resource
    private SysCustomerVoucherMapper sysCustomerVoucherMapper;

    @Resource
    private SysCustomerDiscountMapper sysCustomerDiscountMapper;
    @Autowired
    private SysExtendMapper sysExtendMapper;

    @Resource
    private SysCustomerAmountLogMapper sysCustomerAmountLogMapper;
    @Resource
    private SysOrderMapper sysOrderMapper;
    @Resource
    private SysCustomerInstancesMapper sysCustomerInstancesMapper;

    @Resource
    private SysCustomerVoucherService sysCustomerVoucherService;
    @Resource
    private SysCustomerCreditLineService sysCustomerCreditLineService;

    @Resource
    private BaiDuCertUtils bizDuCertUtils;

    @Resource
    private TencentUtils tenantUtils;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private SysCustomerNetworkMapper sysCustomerNetworkMapper;
    @Autowired
    private SysNetworkProductMapper sysNetworkProductMapper;

    @Resource
    private SysCustomerCertLogMapper sysCustomerCertLogMapper;
    @Override
    public boolean updateById(SysCustomerEntity entity) {
        return sysCustomerMapper.updateById(entity) > 0;
    }

    @Override
    public boolean insert(SysCustomerEntity entity) {
        return sysCustomerMapper.insert(entity) > 0;
    }

    @Override
    public SysCustomerEntity getByEntity(SysCustomerEntity entity) {
        return sysCustomerMapper.selectOne(Wrappers.lambdaQuery(SysCustomerEntity.class)
                .eq(entity.getId() != null && entity.getId() != 0, SysCustomerEntity::getId, entity.getId())
                .eq(StringUtils.isNotEmpty(entity.getPhone()), SysCustomerEntity::getPhone, entity.getPhone()));
    }

    @Override
    public PageResult<SysCustomerListVO> getPage(PageQuery<SysCustomerQuery> pageQuery) {
        pageQuery.startPage();
        SysCustomerQuery query = pageQuery.getQuery();
        LambdaQueryWrapper<SysCustomerEntity> customerQueryWrapper = Wrappers.lambdaQuery(SysCustomerEntity.class)
                .eq(query.getStatus() != null, SysCustomerEntity::getStatus, query.getStatus())
                .and(StringUtils.isNotEmpty(query.getNamePhoneSearchKey()), w ->
                        w.like(StringUtils.isNotEmpty(query.getNamePhoneSearchKey()), SysCustomerEntity::getCustomerName, query.getNamePhoneSearchKey())
                                .or()
                                .like(StringUtils.isNotEmpty(query.getNamePhoneSearchKey()), SysCustomerEntity::getPhone, query.getNamePhoneSearchKey())
                );
        return Optional.of(sysCustomerMapper.selectList(customerQueryWrapper))
                .flatMap(customerEntityList -> {
                    PageResult<SysCustomerListVO> result = PageResult.of(customerEntityList, SysCustomerListVO.class);
                    List<Long> customerIdList = result.getList().stream().map(SysCustomerListVO::getId).toList();
                    Map<Long, BigDecimal> customerVoucherAmountMap = getCustomerVoucherAmountMap(customerIdList);
                    LambdaQueryWrapper<SysCustomerDiscountEntity> customerDiscountQueryWrapper = Wrappers.lambdaQuery(SysCustomerDiscountEntity.class)
                            .in(SysCustomerDiscountEntity::getCustomerId, customerIdList);
                    Map<Long, List<SysCustomerDiscountEntity>> customerDiscountEntityMap = Optional.of(sysCustomerDiscountMapper.selectList(customerDiscountQueryWrapper))
                            .flatMap(customerDiscountList -> customerDiscountList.stream().collect(Collectors.groupingBy(SysCustomerDiscountEntity::getCustomerId)))
                            .orElse(new HashMap<>(0));
                    List<SysOrderEntity> sysOrderEntities = sysOrderMapper.selectList(Wrappers.lambdaQuery(SysOrderEntity.class)
                            .in(SysOrderEntity::getCreateById, customerIdList)
                            .eq(SysOrderEntity::getOrderStatus, OrderStatusEnum.PAID)
                            .isNotNull(SysOrderEntity::getOnlinePayAmount)
                            .ne(SysOrderEntity::getOnlinePayAmount, BigDecimal.ZERO));
                    Map<Long, BigDecimal> orderAmountMap = new HashMap<>(0);
                    if (ObjectUtils.isNotEmpty(sysOrderEntities)) {
                        orderAmountMap = sysOrderEntities.stream()
                                .collect(Collectors.groupingBy(SysOrderEntity::getCreateById, Collectors.reducing(BigDecimal.ZERO, SysOrderEntity::getOnlinePayAmount, BigDecimal::add)));
                    }
                    List<SysCustomerInstancesEntity> customerInstancesList = sysCustomerInstancesMapper.selectList(Wrappers.lambdaQuery(SysCustomerInstancesEntity.class)
                            .in(SysCustomerInstancesEntity::getCustomerId, customerIdList));
                    if (ObjectUtils.isEmpty(customerInstancesList)) {
                        customerInstancesList = new ArrayList<>(0);
                    }
                    SourceTypeEnum[] sourceTypeArr = SourceTypeEnum.values();
                    List<SysCustomerDiscountEntity> insertList = new ArrayList<>();
                    Map<Long, BigDecimal> finalOrderAmountMap = orderAmountMap;

                    List<SysCustomerInstancesEntity> finalCustomerInstancesList = customerInstancesList;
                    result.getList().forEach(list -> {
//                        list.setVoucherAmount(customerVoucherAmountMap.getOrDefault(list.getId(), BigDecimal.ZERO));
                        list.setVoucherAmount(sysCustomerVoucherService.getUserVoucherBalance(list.getId()));
                        list.setCreditAmount(sysCustomerCreditLineService.getCreditAmount(list.getId()));
                        list.setInstanceTotal(finalCustomerInstancesList.stream().filter(e -> e.getCustomerId().equals(list.getId())).count());
                        list.setInstanceRunNumber(finalCustomerInstancesList.stream().filter(e -> e.getCustomerId().equals(list.getId()) && e.getStatus().equals(EcsStatusEnum.RUNNING)).count());
                        list.setTotalConsumeAmount(finalOrderAmountMap.getOrDefault(list.getId(), BigDecimal.ZERO));
                        List<SysCustomerDiscountEntity> discountEntities = customerDiscountEntityMap.get(list.getId());
                        if (ObjectUtils.isEmpty(discountEntities)) {
                            discountEntities = new ArrayList<>(sourceTypeArr.length);
                            for (SourceTypeEnum sourceTypeEnum : sourceTypeArr) {
                                SysCustomerDiscountEntity entity = new SysCustomerDiscountEntity();
                                entity.setCustomerId(list.getId());
                                entity.setDiscountRation(BigDecimal.ONE);
                                entity.setSourceType(sourceTypeEnum);
                                discountEntities.add(entity);
                                insertList.add(entity);
                            }
                        }
                        list.setDiscountList(discountEntities);



                        // 获取AGIC的折扣
                        List<SysCustomerNetwork> networks = sysCustomerNetworkMapper.selectList(
                                new LambdaQueryWrapper<SysCustomerNetwork>()
                                        .eq(SysCustomerNetwork::getUserId, list.getId())
                        );
                        List<SysCustomerNetwork> nessssss = new ArrayList<>();
                        if (CollectionUtils.isNotEmpty(networks)) {
                            List<SysNetworkProductEntity> entities = sysNetworkProductMapper.selectList(
                                    new LambdaQueryWrapper<SysNetworkProductEntity>()
                                            .eq(SysNetworkProductEntity::getStatus, 1)
                                            .orderByDesc(SysNetworkProductEntity::getCreateTime)
                            );
                            if (CollectionUtils.isNotEmpty(entities)) {
                                for (SysNetworkProductEntity entity : entities) {
                                    long count = networks.stream().filter(s -> {
                                        return s.getNetworkId().equals(entity.getId());
                                    }).count();
                                    if (count == 0) {
                                        SysCustomerNetwork n = new SysCustomerNetwork();
                                        n.setUserId(list.getId());
                                        n.setNetworkId(entity.getId());
                                        n.setDiscountRation(null);
                                        n.setNetworkName(entity.getName());
                                        nessssss.add(n);
                                    } else {
                                        for (SysCustomerNetwork network : networks) {
                                            if (network.getNetworkId().equals(entity.getId())) {
                                                SysCustomerNetwork n = new SysCustomerNetwork();
                                                n.setUserId(list.getId());
                                                n.setNetworkId(entity.getId());
                                                n.setDiscountRation(network.getDiscountRation());
                                                n.setNetworkName(entity.getName());
                                                nessssss.add(n);
                                            }
                                        }
                                    }


                                }
                            }
                        } else {
                            List<SysNetworkProductEntity> entities = sysNetworkProductMapper.selectList(
                                    new LambdaQueryWrapper<SysNetworkProductEntity>()
                                            .eq(SysNetworkProductEntity::getStatus, 1)
                                            .orderByDesc(SysNetworkProductEntity::getCreateTime)
                            );
                            if (CollectionUtils.isNotEmpty(entities)) {
                                for (SysNetworkProductEntity entity : entities) {
                                    SysCustomerNetwork n = new SysCustomerNetwork();
                                    n.setUserId(list.getId());
                                    n.setNetworkId(entity.getId());
                                    n.setDiscountRation(null);
                                    n.setNetworkName(entity.getName());
                                    nessssss.add(n);
                                }
                            }
                        }

                        list.setCustomerNetworkList(nessssss);

                        SysExtend e = sysExtendMapper.selectOne(
                                new LambdaQueryWrapper<SysExtend>()
                                        .eq(SysExtend::getUserId, list.getId())
                        );

                        if (e != null) {
                            list.setExtend(true);
                            if (e.getParentUserId() != null && e.getParentUserId() != 0){
                                SysCustomerEntity sysCustomerEntity = sysCustomerMapper.selectOne(
                                        new LambdaQueryWrapper<SysCustomerEntity>()
                                                .eq(SysCustomerEntity::getId, e.getParentUserId())
                                );
                                list.setPName(sysCustomerEntity.getCustomerName());
                                list.setPPhone(sysCustomerEntity.getPhone());
                            }
                        }
                    });
                    if (!insertList.isEmpty()) {
                        sysCustomerDiscountMapper.batchInsert(insertList);
                    }




                    return result;
                })
                .orElseGet(() -> PageResult.of(List.of()));
    }

    @Override
    public void updateCustomerDiscount(SysCustomerDiscountVO updateEntity) {
        List<SourceTypeEnum> sourceTypeList = updateEntity.getSourceTypeList();
        for (SourceTypeEnum sourceTypeEnum : sourceTypeList) {
            LambdaQueryWrapper<SysCustomerDiscountEntity> queryWrapper = Wrappers.lambdaQuery(SysCustomerDiscountEntity.class)
                    .eq(SysCustomerDiscountEntity::getCustomerId, updateEntity.getCustomerId())
                    .eq(SysCustomerDiscountEntity::getSourceType, sourceTypeEnum.getId());

            Optional.of(sysCustomerDiscountMapper.selectOne(queryWrapper))
                    .ifPresentOrElse(entity -> {
                        entity.setDiscountRation(updateEntity.getDiscountRation());
                        sysCustomerDiscountMapper.updateById(entity);
                    }, () -> {
                        SysCustomerDiscountEntity newEntity = new SysCustomerDiscountEntity();
                        newEntity.setCustomerId(updateEntity.getCustomerId());
                        newEntity.setSourceType(sourceTypeEnum);
                        newEntity.setDiscountRation(updateEntity.getDiscountRation());
                        sysCustomerDiscountMapper.insert(newEntity);
                    });
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void updateCustomerBalance(Long orderId, String orderNo, Long userId, BigDecimal balancePayAmount, SysTransactionType sysTransactionType) {
        if (balancePayAmount.compareTo(BigDecimal.ZERO) < 1) {
            return;
        }
        DistributedExecute.of(distributedLock)
                .lock("updateCustomerBalance_" + userId.toString())
                .onSuccessFunction((o) -> {
                    SysCustomerAmountLogEntity logEntity = new SysCustomerAmountLogEntity();
                    logEntity.setOrderId(orderId);
                    logEntity.setOrderNo(orderNo);
                    logEntity.setCustomerId(userId);
                    logEntity.setAmountType(SysCustomerAmountType.BALANCE);
                    logEntity.setTransactionType(sysTransactionType);
                    SysCustomerEntity entity = sysCustomerMapper.selectById(userId);
                    BigDecimal balance = entity.getBalance();
                    logEntity.setBeforeAmount(balance);
                    logEntity.setAmount(balancePayAmount);
                    if (sysTransactionType.isPay()) {
                        if (balance.compareTo(balancePayAmount) < 0) {
                            throw new HttpServiceException("余额不足");
                        }
                        balance = balance.subtract(balancePayAmount);
                    } else {
                        balance = balance.add(balancePayAmount);
                    }
                    entity.setBalance(balance);
                    logEntity.setAfterAmount(balance);
                    logEntity.setCreateTime(DateUtils.getNowDate());
                    sysCustomerMapper.updateById(entity);
                    sysCustomerAmountLogMapper.insert(logEntity);
                });
    }

    @Override
    public boolean updateByPhone(SysCustomerEntity entity) {
        return sysCustomerMapper.update(entity, Wrappers.lambdaUpdate(SysCustomerEntity.class)
                .eq(SysCustomerEntity::getPhone, entity.getPhone())) > 0;
    }

    @Override
    public PageResult<SysCustomerEntity> getCompanyPage(PageQuery<SysCompanyQuery> pageQuery) {
        pageQuery.startPage();
        SysCompanyQuery query = pageQuery.getQuery();
        LambdaQueryWrapper<SysCustomerEntity> customerQueryWrapper = Wrappers.lambdaQuery(SysCustomerEntity.class)
                .eq(query.getCompanyStatus() != null, SysCustomerEntity::getCompanyStatus, query.getCompanyStatus())
                .ne(query.getCompanyStatus() == null, SysCustomerEntity::getCompanyStatus, 1)
                .like(StringUtils.isNotEmpty(query.getCompanyName()), SysCustomerEntity::getCompanyName, query.getCompanyName());
        return Optional.of(sysCustomerMapper.selectList(customerQueryWrapper))
                .flatMap(customerEntityList -> {
                    PageResult<SysCustomerEntity> result = PageResult.of(customerEntityList);
                    return result;
                })
                .orElseGet(() -> PageResult.of(List.of()));
    }


    private Map<Long, BigDecimal> getCustomerVoucherAmountMap(List<Long> customerIdList) {
        Date nowDate = DateUtils.getNowDate();
        LambdaQueryWrapper<SysCustomerVoucherEntity> customerVoucherQueryWrapper = Wrappers.lambdaQuery(SysCustomerVoucherEntity.class)
                .in(SysCustomerVoucherEntity::getCustomerId, customerIdList)
                .eq(SysCustomerVoucherEntity::getStatus, StatusEnum.OK)
                .ge(SysCustomerVoucherEntity::getUseTimeEnd, nowDate);
        return Optional.of(sysCustomerVoucherMapper.selectList(customerVoucherQueryWrapper))
                .flatMap(customerVoucherList -> {
                    Map<Long, BigDecimal> resultMap = new HashMap<>(customerVoucherList.size());
                    for (SysCustomerVoucherEntity voucherEntity : customerVoucherList) {
                        BigDecimal amount = voucherEntity.getTotalAmount().subtract(voucherEntity.getUseAmount());
                        resultMap.putIfAbsent(voucherEntity.getCustomerId(), amount.add(resultMap.getOrDefault(voucherEntity.getCustomerId(), BigDecimal.ZERO)));
                    }
                    return resultMap;
                })
                .orElse(new HashMap<>(0));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PcRealNameDTO realNameVerify(PcRealNameVO vo) {
        log.info("实名认证入参：{}",  vo);
        Long userId = SecurityContext.getUserInfo().getUserId();
        SysCustomerEntity sysCustomerEntity = sysCustomerMapper.selectById(userId);
        sysCustomerEntity.setCertType(vo.getCertType());
        SysCustomerCertLogEntity sysCustomerCertLogEntity = new SysCustomerCertLogEntity();
        sysCustomerCertLogEntity.setCertType(vo.getCertType());
        sysCustomerCertLogEntity.setCreateTime(DateUtils.getNowDate());
        sysCustomerCertLogEntity.setCustomerId(userId);
        sysCustomerCertLogEntity.setName(vo.getName());
        sysCustomerCertLogEntity.setIdCard(vo.getIdCard());
        sysCustomerCertLogEntity.setCompanyAddress(vo.getCompanyAddress());
        sysCustomerCertLogEntity.setCompanyName(vo.getCompanyName());
        sysCustomerCertLogEntity.setCompanyImg(vo.getCompanyImg());
        sysCustomerCertLogEntity.setCompanyCode(vo.getCompanyCode());
        PcRealNameDTO dto = new PcRealNameDTO();
        try {
            Integer certType = vo.getCertType();
            if (certType == 1) {
                Response response = bizDuCertUtils.idMatch(vo.getIdCard(), vo.getName());
                ResponseBody body = response.body();
                assert body != null;
                String content = body.string();
                JSONObject json = new JSONObject(content);
                Integer errorCode = json.getInt("error_code");
                if (errorCode == 0){
                    if (vo.getIsFace()){
                        //todo 等腾讯云人脸识别
                        extracted(vo, dto);
//                        sysCustomerEntity.setCertStatus(2);
//                        sysCustomerCertLogEntity.setCertStatus(2);
                    }else {
                        dto.setSuccess(true);
                        sysCustomerEntity.setCertStatus(3);
                        sysCustomerEntity.setCertTime(DateUtils.getNowDate());
                        sysCustomerCertLogEntity.setCertStatus(3);
                    }
                }else {
                    dto.setSuccess(false);
                    sysCustomerCertLogEntity.setCertStatus(4);
                    sysCustomerEntity.setCertStatus(4);
                    dto.setFailReason("身份证号码与名字不匹配，请重新输入！");
                }
            }else {
                Response response = bizDuCertUtils.threeFactorsVerification(vo.getName(), vo.getCompanyName(), vo.getCompanyCode());
                ResponseBody body = response.body();
                assert body != null;
                String content = body.string();
                JSONObject json = new JSONObject(content);
                JSONObject wordsResult = json.getJSONObject("words_result");
                String verifyResult = wordsResult.getStr("verifyresult");
                if ("1".equals(verifyResult)){
                    Response response2 = bizDuCertUtils.idMatch(vo.getIdCard(), vo.getName());
                    ResponseBody body2 = response2.body();
                    assert body2 != null;
                    String content2 = body2.string();
                    JSONObject json2 = new JSONObject(content2);
                    Integer errorCode = json2.getInt("error_code");
                    if (errorCode == 0){
                        if (vo.getIsFace()){
                            //todo 等腾讯云人脸识别
                            extracted(vo, dto);
//                            sysCustomerEntity.setCertStatus(2);
//                            sysCustomerCertLogEntity.setCertStatus(2);
                        }else {
                            dto.setSuccess(true);
                            sysCustomerEntity.setCertStatus(3);
                            sysCustomerEntity.setCertTime(DateUtils.getNowDate());
                            sysCustomerEntity.setCompanyName(vo.getCompanyName());
                            sysCustomerEntity.setCompanyCode(vo.getCompanyCode());
                            sysCustomerEntity.setCompanyAddress(vo.getCompanyAddress());
                            sysCustomerEntity.setCompanyImg(vo.getCompanyImg());
                            sysCustomerCertLogEntity.setCertStatus(3);
                        }
                    }else {
                        dto.setSuccess(false);
                        sysCustomerCertLogEntity.setCertStatus(4);
                        sysCustomerEntity.setCertStatus(4);
                        dto.setFailReason("身份证号码与名字不匹配，请重新输入！");
                    }
                    dto.setLogId(sysCustomerCertLogEntity.getId());
                }else {
                    dto.setSuccess(false);
                    sysCustomerCertLogEntity.setCertStatus(4);
                    sysCustomerEntity.setCertStatus(4);
                    String nameMatch = wordsResult.getStr("namematch");
                    String companyMatch = wordsResult.getStr("companymatch");
                    String regNumMatch	 = wordsResult.getStr("regnummatch");
                    if (!"1".equals(nameMatch)){
                        dto.setFailReason("法人姓名不匹配，请重新输入！");
                    }
                    if (!"1".equals(companyMatch)){
                        dto.setFailReason("企业名称不匹配，请重新输入！");
                    }
                    if (!"1".equals(regNumMatch)){
                        dto.setFailReason("统一社会信用代码不匹配，请重新输入！");
                    }

                }
            }
            sysCustomerCertLogMapper.insert(sysCustomerCertLogEntity);
            sysCustomerMapper.updateById(sysCustomerEntity);
            log.info("认证成功记录id！:{}", sysCustomerCertLogEntity.getId());
            dto.setLogId(sysCustomerCertLogEntity.getId());
        }catch (Exception e){
            log.error("认证失败！:{}", e.getMessage(),e);
            throw new HttpServiceException("实名认证失败！");
        }
        return dto;
    }

    private void extracted(PcRealNameVO vo, PcRealNameDTO dto) {
        String token = (String) redisTemplate.opsForValue().get("eid_token_" + vo.getName());
        if (StringUtils.isBlank(token)){
            String response2 = tenantUtils.getEidToken(vo.getIdCard(), vo.getName());
            JSONObject jsonObject = new JSONObject(response2);
            String eidToken = jsonObject.getStr("EidToken");
            if (StringUtils.isBlank(eidToken)){
                dto.setSuccess(false);
                JSONObject error = jsonObject.getJSONObject("Error");
                if (error != null){
                    String message = error.getStr("Message");
                    dto.setFailReason(message);
                }
            }else {
                dto.setSuccess(true);
                dto.setEidToken(eidToken);
                redisTemplate.opsForValue().set("eid_token_" + vo.getName(), eidToken, 9, java.util.concurrent.TimeUnit.MINUTES);
            }
        }else {
            dto.setSuccess(true);
            dto.setEidToken(token);
        }
    }

    @Override
    public String getEidToken(PcRealNameVO vo) {
        PcRealNameDTO dto = new PcRealNameDTO();
        extracted(vo, dto);
        return dto.getEidToken();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> getEidResult(Long logId) {
        log.info("开始获取eidToken结果----------");
        SysCustomerCertLogEntity sysCustomerCertLogEntity1 = sysCustomerCertLogMapper.selectById(logId);
        String eidToken = (String) redisTemplate.opsForValue().get("eid_token_" + sysCustomerCertLogEntity1.getName());
        if (StringUtils.isBlank(eidToken)){
            log.error("未获取到eidToken！");
            return Result.success(false);
        }
        try {
            String response = tenantUtils.getEidResult(eidToken);
            if (StringUtils.isNotBlank(response)){
                JSONObject jsonObject = new JSONObject(response);
                JSONObject text = jsonObject.getJSONObject("Text");
                log.info("eidToken结果:{}", text);
                Integer errCode = text.getInt("ErrCode");
                if (errCode == 0){
                    SysCustomerEntity sysCustomerEntity = sysCustomerMapper.selectById(sysCustomerCertLogEntity1.getCustomerId());
                    SysCustomerCertLogEntity sysCustomerCertLogEntity = sysCustomerCertLogMapper.selectById(logId);
                    if (sysCustomerCertLogEntity != null){
                        sysCustomerCertLogEntity.setCertStatus(3);
                        sysCustomerCertLogEntity.setUpdateTime(DateUtils.getNowDate());
                        sysCustomerCertLogMapper.updateById(sysCustomerCertLogEntity);
                        if (sysCustomerCertLogEntity.getCertType() == 2){
                            sysCustomerEntity.setCompanyName(sysCustomerCertLogEntity.getCompanyName());
                            sysCustomerEntity.setCompanyCode(sysCustomerCertLogEntity.getCompanyCode());
                            sysCustomerEntity.setCompanyAddress(sysCustomerCertLogEntity.getCompanyAddress());
                            sysCustomerEntity.setCompanyImg(sysCustomerCertLogEntity.getCompanyImg());
                        }
                        sysCustomerEntity.setCertStatus(3);
                        sysCustomerEntity.setCertTime(DateUtils.getNowDate());
                        sysCustomerEntity.setCertType(sysCustomerCertLogEntity.getCertType());
                        sysCustomerMapper.updateById(sysCustomerEntity);
                    }
                    return Result.success(true);
                }
            }
        }catch (Exception e){
            log.error("获取eid结果失败！:{}", e.getMessage(),e);
            throw new HttpServiceException("获取eid结果失败！");
        }
        return Result.success(false);
    }

    @Override
    public Result<Boolean> remove(Long customerId) {
        return Result.success(sysCustomerMapper.deleteById(customerId) > 0);
    }
}