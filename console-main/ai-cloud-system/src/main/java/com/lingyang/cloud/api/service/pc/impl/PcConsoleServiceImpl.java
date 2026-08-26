package com.lingyang.cloud.api.service.pc.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.api.model.edit.PcInstancesStatusHandlerEdit;
import com.lingyang.cloud.api.model.query.PcConsoleEcsQuery;
import com.lingyang.cloud.api.model.vo.*;
import com.lingyang.cloud.api.service.pc.PcConsoleService;
import com.lingyang.cloud.common.constant.CacheQueueConstant;
import com.lingyang.cloud.entity.*;
import com.lingyang.cloud.enums.customer.SysTransactionType;
import com.lingyang.cloud.enums.order.OrderStatusEnum;
import com.lingyang.cloud.enums.source.EcsStatusEnum;
import com.lingyang.cloud.enums.source.SourceChargeTypeEnum;
import com.lingyang.cloud.enums.source.SourceRegionsEnum;
import com.lingyang.cloud.enums.source.SourceTypeEnum;
import com.lingyang.cloud.enums.system.SystemConfigEnum;
import com.lingyang.cloud.mapper.*;
import com.lingyang.cloud.model.config.SystemPriceRationConfigModel;
import com.lingyang.cloud.model.dto.SysCustomerEcsWorkEipDTO;
import com.lingyang.cloud.model.vo.applets.AppletsEcsWorkOperationVO;
import com.lingyang.cloud.model.vo.regions.SourceRegionZoneVO;
import com.lingyang.cloud.service.*;
import com.lingyang.cloud.voengine.client.VoEngineApiClient;
import com.lingyang.common.cache.CacheQueueService;
import com.lingyang.common.core.exception.http.HttpServiceException;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.security.SecurityContext;
import com.lingyang.common.core.thread.ThreadLocalContext;
import com.lingyang.common.core.utils.*;
import com.lingyang.common.core.utils.Optional;
import com.volcengine.ApiException;
import com.volcengine.ecs.EcsApi;
import com.volcengine.ecs.model.*;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import static com.lingyang.cloud.enums.source.EcsStatusEnum.*;
import static com.lingyang.cloud.enums.source.SourceChargeTypeEnum.POSTPAID_BY_HOUR;
import static com.lingyang.cloud.enums.source.SourceTypeEnum.CLOUD_STORAGE;
import static com.lingyang.cloud.enums.source.SourceTypeEnum.ECS;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/19 18:06
 */
@Service
public class PcConsoleServiceImpl implements PcConsoleService {
    private static final Logger log = LoggerFactory.getLogger(PcConsoleServiceImpl.class);
    @Resource
    private SysCustomerInstancesMapper sysCustomerInstancesMapper;
    @Resource
    private CacheQueueService cacheQueueService;
    @Resource
    private SysCustomerVolumeMapper sysCustomerVolumeMapper;
    @Resource
    private SysCustomerBillMapper sysCustomerBillMapper;
    @Resource
    private SysCustomerEcsWorkMapper sysCustomerEcsWorkMapper;
    @Resource
    private SysCustomerEcsWorkEipMapper sysCustomerEcsWorkEipMapper;

    @Resource
    private SysCustomerBillService sysCustomerBillService;

    @Resource
    private SysOrderSourceMapper sysOrderSourceMapper;
    @Resource
    private SysEcsMapper sysEcsMapper;

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

    @Resource
    private SysMessageMapper sysMessageMapper;


    @Override
    public PcConsoleHomeVO getHome(Long userId) {
        PcConsoleHomeVO home = new PcConsoleHomeVO();
        Long ecsCount = sysCustomerInstancesMapper.selectCount(Wrappers.lambdaQuery(SysCustomerInstancesEntity.class)
                .eq(SysCustomerInstancesEntity::getCustomerId, userId)
                .ne(SysCustomerInstancesEntity::getStatus, ERROR));
        home.setEcsNumber(ecsCount);
        return home;
    }

    @Override
    public PcConsoleSourceInfoVO getSourceInfo(Long userId) {
        LambdaQueryWrapper<SysCustomerInstancesEntity> queryWrapper = Wrappers.lambdaQuery(SysCustomerInstancesEntity.class)
                .eq(SysCustomerInstancesEntity::getCustomerId, userId);
        return Optional.of(sysCustomerInstancesMapper.selectList(queryWrapper))
                .flatMap(instancesList -> {
                    PcConsoleSourceInfoVO sourceInfo = new PcConsoleSourceInfoVO();
                    // 调用火山获取实例状态
                    Map<SourceRegionsEnum, List<SysCustomerInstancesEntity>> reginosMap = instancesList.stream().collect(Collectors.groupingBy(SysCustomerInstancesEntity::getRegion));
                    List<PcSourceRegionsNumberInfoVO> regionsNumberInfoList = new ArrayList<>(reginosMap.size());
                    long nowTime = DateUtils.getNowDate().getTime();
                    for (Map.Entry<SourceRegionsEnum, List<SysCustomerInstancesEntity>> entry : reginosMap.entrySet()) {
                        List<SysCustomerInstancesEntity> value = entry.getValue().stream().filter(i -> i.getProductType().equals(1)).toList();
                        int size = value.size();
                        int index = 1;
                        if (size > 100) {
                            index = size / 100 + 1;
                        }
                        EcsApi ecsApi = VoEngineApiClient.getEcsApi(entry.getKey().getId());
                        List<InstanceForDescribeInstancesOutput> instanceList = new ArrayList<>();
                        for (int i = 0; i < index; i++) {
                            List<String> instanceIdList = value.subList(i * 100, i == index - 1 ? value.size() : (i + 1) * 100)
                                    .stream().map(SysCustomerInstancesEntity::getInstanceId).filter(Objects::nonNull).toList();
                            log.info("instanceIdList:{}", instanceIdList);
                            if (!ObjectUtils.isEmpty(instanceIdList)){
                                DescribeInstancesRequest request = new DescribeInstancesRequest();
                                request.setInstanceIds(instanceIdList);
                                request.setMaxResults(instancesList.size());
                                try {
                                    instanceList.addAll(ecsApi.describeInstances(request).getInstances());
                                } catch (Exception e) {
                                    log.error("获取火山服务器资源失败: ", e);
                                }
                            }
                        }
                        log.info("获取火山服务器资源: {}", instanceList);
                        long runningNumber = instanceList.stream().filter(instance -> Objects.equals(instance.getStatus(), RUNNING.name())).count();
                        log.info("runningNumber:{}", runningNumber);
                        long stoppedNumber = instanceList.stream().filter(instance -> Objects.equals(instance.getStatus(), STOPPED.name())).count();
                        log.info("stoppedNumber:{}", stoppedNumber);
                        long expiredNumber = instanceList.stream().filter(instance -> {
                            Date expireTime = DateUtils.toDate(instance.getExpiredAt(), "yyyy-MM-dd'T'HH:mm:ssXXX");
                            return expireTime != null && expireTime.getTime() <= nowTime;
                        }).count();
                        log.info("expiredNumber:{}", expiredNumber);
                        long expiringNumber = instanceList.stream().filter(instance -> {
                            Date expireTime = DateUtils.toDate(instance.getExpiredAt(), "yyyy-MM-dd'T'HH:mm:ssXXX");
                            return expireTime != null && expireTime.getTime() > nowTime && expireTime.getTime() <= nowTime + (30L * 24 * 60 * 60 * 1000);
                        }).count();
                        log.info("expiringNumber:{}", expiringNumber);
                        sourceInfo.setEcsRunningNumber(sourceInfo.getEcsRunningNumber() == null ? runningNumber : sourceInfo.getEcsRunningNumber() + runningNumber);
                        sourceInfo.setEcsStoppedNumber(sourceInfo.getEcsStoppedNumber() == null ? stoppedNumber : sourceInfo.getEcsStoppedNumber() + stoppedNumber);
                        sourceInfo.setExpireNumber(sourceInfo.getExpireNumber() == null ?  expiredNumber : sourceInfo.getExpireNumber() + expiredNumber);
                        sourceInfo.setExpiringNumber(sourceInfo.getExpiringNumber() == null ? expiringNumber : sourceInfo.getExpiringNumber() + expiringNumber);
                        //查询自建服务器数量
                        List<SysCustomerInstancesEntity> sysCustomerInstancesEntities = entry.getValue().stream().filter(i -> i.getProductType().equals(2)).toList();
                        log.info("查询自建服务器资源: {}", sysCustomerInstancesEntities);
                        long ownNumber = 0;
                        long ownRunningNumber = 0;
                        long ownStoppedNumber = 0;
                        long ownExpiredNumber = 0;
                        if (CollectionUtils.isNotEmpty(sysCustomerInstancesEntities)){
                            List<Long> list = sysCustomerInstancesEntities.stream().map(SysCustomerInstancesEntity::getId).toList();
                            for (Long instanceId : list) {
                                SysCustomerEcsWorkEipEntity sysCustomerEcsWorkEipEntity = sysCustomerEcsWorkEipMapper.selectOne(Wrappers.lambdaQuery(SysCustomerEcsWorkEipEntity.class)
                                        .eq(SysCustomerEcsWorkEipEntity::getInstanceId, instanceId));
                                if (sysCustomerEcsWorkEipEntity != null){
                                    ownNumber += 1;
                                    Integer operationStatus = sysCustomerEcsWorkEipEntity.getOperationStatus();
                                    switch (operationStatus) {
                                        case 2: ownRunningNumber += 1;
                                            break;
                                        case 4: ownStoppedNumber += 1;
                                            break;
                                        case 5: ownStoppedNumber += 1;
                                            break;
                                        case 6: ownExpiredNumber += 1;
                                            break;
                                    }
                                }
                            }
                            sourceInfo.setEcsRunningNumber( sourceInfo.getEcsRunningNumber() + ownRunningNumber);
                            sourceInfo.setEcsStoppedNumber( sourceInfo.getEcsStoppedNumber() + ownStoppedNumber);
                            sourceInfo.setExpireNumber( sourceInfo.getExpireNumber() + ownExpiredNumber);

                        }
                        log.info("ownRunningNumber:" + ownRunningNumber);
                        log.info("ownStoppedNumber:" + ownStoppedNumber);
                        log.info("ownExpiredNumber:" + ownExpiredNumber);
                        PcSourceRegionsNumberInfoVO regionsNumberInfo = new PcSourceRegionsNumberInfoVO();
                        regionsNumberInfo.setRegion(entry.getKey());
                        regionsNumberInfo.setEcsNumber((long) instanceList.size() + ownNumber);
                        regionsNumberInfo.setEcsRunningNumber(runningNumber + ownRunningNumber);
                        regionsNumberInfo.setEcsStoppedNumber(stoppedNumber + ownStoppedNumber + ownExpiredNumber);
                        log.info("regionsNumberInfo:" + regionsNumberInfo);
                        regionsNumberInfoList.add(regionsNumberInfo);

                        // 重置计数器
                        ownNumber = 0;
                        ownRunningNumber = 0;
                        ownStoppedNumber = 0;
                        ownExpiredNumber = 0;
                        log.info("sourceInfo:{}",sourceInfo);
                    }
                    sourceInfo.setRegionsNumberList(regionsNumberInfoList);
                    return sourceInfo;
                })
                .orElse(new PcConsoleSourceInfoVO());
    }

    @Override
    public PageResult<PcConsoleEcsListVO> getInstancePageList(PageQuery<PcConsoleEcsQuery> pageQuery) {
        pageQuery.startPage();
        PcConsoleEcsQuery query = pageQuery.getQuery();
        LambdaQueryWrapper<SysCustomerInstancesEntity> queryWrapper = Wrappers.lambdaQuery(SysCustomerInstancesEntity.class)
                .eq(SysCustomerInstancesEntity::getCustomerId, query.getCustomerId())
//                .eq(SysCustomerInstancesEntity::getProductType,1)
                .eq(query.getType() != null,
                        SysCustomerInstancesEntity::getRegion,
                        query.getType() != null ? (query.getType() == 1 ? SourceRegionsEnum.CN_BEIJING : SourceRegionsEnum.CN_SHANGHAI) : null)
                .eq(ObjectUtils.isNotEmpty(query.getChargeType()), SysCustomerInstancesEntity::getChargeType, query.getChargeType())
                .like(ObjectUtils.isNotEmpty(query.getInstanceName()), SysCustomerInstancesEntity::getInstanceName, query.getInstanceName())
//                .in(SysCustomerInstancesEntity::getStatus,
//                        EcsStatusEnum.CREATING.getCode(),
//                        EcsStatusEnum.RUNNING.getCode(),
//                        EcsStatusEnum.STOPPING.getCode(),
//                        EcsStatusEnum.STOPPED.getCode(),
//                        EcsStatusEnum.REBOOTING.getCode(),
//                        EcsStatusEnum.STARTING.getCode(),
//                        EcsStatusEnum.REBUILDING.getCode(),
//                        EcsStatusEnum.RESIZING.getCode(),
//                        EcsStatusEnum.DELETING.getCode()
//                )
                .orderByDesc(SysCustomerInstancesEntity::getUpdateTime);
        log.info("查询实例列表: {}", queryWrapper);
        return PageResult.of(Optional.of(sysCustomerInstancesMapper.selectList(queryWrapper))
                .flatMap(instancesList -> {
                    List<String> orderSourceUid = instancesList.stream().map(SysCustomerInstancesEntity::getOrderSourceUid)
                            .toList();
                    // 获取云盘信息
                    List<SysCustomerVolumeEntity> volumeEntityList = sysCustomerVolumeMapper.selectList(Wrappers.lambdaQuery(SysCustomerVolumeEntity.class)
                            .in(SysCustomerVolumeEntity::getOrderSourceUid, orderSourceUid));
                    Map<String, List<SysCustomerVolumeEntity>> instancesVolumeMap = new HashMap<>(0);
                    if (ObjectUtils.isNotEmpty(volumeEntityList)) {
                        instancesVolumeMap = volumeEntityList.stream().collect(Collectors.groupingBy(SysCustomerVolumeEntity::getOrderSourceUid));
                    }
                    Map<String, InstanceForDescribeInstancesOutput> voEcsMap = new HashMap<>(0);
                    EcsApi ecsApi = VoEngineApiClient.getEcsApi(query.getRegions() == null ? SourceRegionsEnum.CN_BEIJING.getId() : query.getRegions().getId());
                    List<PcConsoleEcsListVO> ecsList = BeanUtils.copyList(instancesList, PcConsoleEcsListVO.class);
                    // 调用 火山实列接口，获取数据
                    List<String> instanceIdList = instancesList.stream().map(SysCustomerInstancesEntity::getInstanceId)
                            .filter(ObjectUtils::isNotEmpty)
                            .toList();
                    if (!instanceIdList.isEmpty()) {
                        DescribeInstancesRequest request = new DescribeInstancesRequest();
                        request.setInstanceIds(instanceIdList);
                        request.setMaxResults(instancesList.size());
                        try {
                            voEcsMap = ecsApi.describeInstances(request).getInstances().stream().collect(Collectors.toMap(InstanceForDescribeInstancesOutput::getInstanceId, i -> i));
                        } catch (Exception e) {
                            log.error("获取火山服务器资源失败: ", e);
                            voEcsMap = new HashMap<>(0);
                        }
                    }
                    Map<String, InstanceForDescribeInstancesOutput> finalVoEcsMap = voEcsMap;
                    Map<String, List<SysCustomerVolumeEntity>> finalInstancesVolumeMap = instancesVolumeMap;
                    ecsList.forEach(vo -> {
                        if (vo.getProductType() == 1){
                            InstanceForDescribeInstancesOutput output = finalVoEcsMap.get(vo.getInstanceId());
                            if (output != null) {
                                vo.setStatus(EcsStatusEnum.valueOf(output.getStatus()));
                                vo.setInstanceName(output.getInstanceName());
                                vo.setNetworkList(output.getNetworkInterfaces());
                                vo.setEipAddress(output.getEipAddress());
                                if (StringUtils.isNotBlank(output.getZoneId())){
                                    vo.setZone(new SourceRegionZoneVO(output.getZoneId()));
                                }
                                vo.setOsName(output.getOsName());
                                vo.setCale(output.getInstanceTypeId() + "|" + output.getCpus() + "核|" + output.getMemorySize() / 1024 + "G");
                                vo.setChargeType(SourceChargeTypeEnum.getTypeByVolcengineDesc(output.getInstanceChargeType()));
                                if (!vo.getChargeType().equals(SourceChargeTypeEnum.POSTPAID_BY_HOUR)) {
                                    Optional.of(output.getExpiredAt())
                                            .ifPresent(expiredAt -> vo.setExpiredTime(DateUtils.toDate(expiredAt, "yyyy-MM-dd'T'HH:mm:ssXXX")));
                                }
                                List<SysCustomerVolumeEntity> customerVolumeEntityList = finalInstancesVolumeMap.get(vo.getOrderSourceUid());
                                if (customerVolumeEntityList != null) {
                                    vo.setSystemVolume(customerVolumeEntityList.stream()
                                            .filter(volume -> "system".equals(volume.getKind()))
                                            .findFirst()
                                            .orElse(null));
                                    vo.setVolumeList(customerVolumeEntityList.stream()
                                            .filter(volume -> !"system".equals(volume.getKind()))
                                            .toList());
                                }
                            } else {
                                // 创建中的也展示数据
                                if (vo.getStatus().equals(EcsStatusEnum.CREATING)) {
                                    SysCustomerInstancesEntity sysCustomerInstancesEntity = instancesList.get(0);
                                    log.info("实例查询11111111111111: {}", sysCustomerInstancesEntity);
                                    SourceRegionZoneVO zv = new SourceRegionZoneVO(sysCustomerInstancesEntity.getZoneId());
                                    zv.setName(zv.getName());
                                    vo.setZone(zv);

                                    List<SysCustomerVolumeEntity> customerVolumeEntityList = finalInstancesVolumeMap.get(vo.getOrderSourceUid());
                                    if (customerVolumeEntityList != null) {
                                        vo.setSystemVolume(customerVolumeEntityList.stream()
                                                .filter(volume -> "system".equals(volume.getKind()))
                                                .findFirst()
                                                .orElse(null));
                                        vo.setVolumeList(customerVolumeEntityList.stream()
                                                .filter(volume -> !"system".equals(volume.getKind()))
                                                .toList());
                                    }

                                    vo.setCale(sysCustomerInstancesEntity.getEcsScale() + "|" + sysCustomerInstancesEntity.getCpuNumber() + "核");


                                }
                            }
                        }else {
                            SysCustomerInstancesEntity sysCustomerInstancesEntity = sysCustomerInstancesMapper.selectById(vo.getId());
                            SysCustomerEcsWorkEipEntity sysCustomerEcsWorkEipEntity = sysCustomerEcsWorkEipMapper.selectOne(Wrappers.lambdaQuery(SysCustomerEcsWorkEipEntity.class)
                                    .eq(SysCustomerEcsWorkEipEntity::getInstanceId, vo.getId()));
                            if (sysCustomerEcsWorkEipEntity != null){
                                switch (sysCustomerEcsWorkEipEntity.getOperationStatus()) {
                                    case 1:
                                        vo.setStatus(HAVE_NOT_OPENED);
                                        break;
                                    case 2:
                                        vo.setStatus(HAVE_OPENED);
                                        break;
                                    case 3:
                                        vo.setStatus(CALCULATING);
                                        break;
                                    case 4:
                                        vo.setStatus(SHUT_DOWN);
                                        break;
                                    case 5:
                                        vo.setStatus(REFUNDED);
                                        break;
                                    case 6:
                                        vo.setStatus(EXPIRE);
                                }
                            }

                            try {

                                DescribeImagesResponse describeImagesResponse = describeImages(sysCustomerInstancesEntity.getImageId());
                                if (com.baomidou.mybatisplus.core.toolkit.ObjectUtils.isNotEmpty(describeImagesResponse)){
                                    describeImagesResponse.getImages().forEach(image -> {
                                        vo.setOsName(image.getImageName());
                                    });
                                }

                            } catch (Exception e) {
                               log.error("查询镜像信息失败", e);
                            }
                            SourceRegionZoneVO zv = new SourceRegionZoneVO(sysCustomerInstancesEntity.getZoneId());
                            zv.setName(zv.getName());
                            vo.setZone(zv);
                            List<SysCustomerVolumeEntity> customerVolumeEntityList = finalInstancesVolumeMap.get(vo.getOrderSourceUid());
                            if (customerVolumeEntityList != null) {
                                vo.setSystemVolume(customerVolumeEntityList.stream()
                                        .filter(volume -> "system".equals(volume.getKind()))
                                        .findFirst()
                                        .orElse(null));
                                vo.setVolumeList(customerVolumeEntityList.stream()
                                        .filter(volume -> !"system".equals(volume.getKind()))
                                        .toList());
                            }
                            vo.setCale(sysCustomerInstancesEntity.getEcsScale() + "|" + sysCustomerInstancesEntity.getCpuNumber() + "核");
                        }

                    });
                    return ecsList;
                }).orElseGet(List::of));
    }

    public DescribeImagesResponse describeImages(String imageId) throws Exception{
        EcsApi ecsApi = VoEngineApiClient.getEcsApi();
        try {
            DescribeImagesRequest request = new DescribeImagesRequest();
            request.setImageIds(Collections.singletonList(imageId));
            return ecsApi.describeImages(request);
        } catch (ApiException e) {
            return new DescribeImagesResponse();
        }
    }

    @Override
    public PcConsoleEcsDetailVO getInstanceDetail(String id) {
        SysCustomerInstancesEntity entity = sysCustomerInstancesMapper.selectById(id);
        if (ObjectUtils.isEmpty(entity)) {
            throw new HttpServiceException("实列不存在");
        }
        String instanceId = entity.getInstanceId();
        if (StringUtils.isNotBlank(instanceId)) {
            // 获取云盘信息
            List<SysCustomerVolumeEntity> volumeEntityList = sysCustomerVolumeMapper.selectList(Wrappers.lambdaQuery(SysCustomerVolumeEntity.class)
                    .in(SysCustomerVolumeEntity::getOrderSourceUid, entity.getOrderSourceUid()));

            PcConsoleEcsDetailVO detailVO = BeanUtils.copyBean(entity, PcConsoleEcsDetailVO.class);
            EcsApi ecsApi = VoEngineApiClient.getEcsApi(entity.getRegion().getId());
            // 实列信息
            DescribeInstancesRequest request = new DescribeInstancesRequest();
            request.setInstanceIds(List.of(instanceId));
            request.setMaxResults(1);
            try {
                List<InstanceForDescribeInstancesOutput> instances = ecsApi.describeInstances(request).getInstances();
                if (ObjectUtils.isNotEmpty(instances)){
                    InstanceForDescribeInstancesOutput instancesOutput = instances.get(0);
                    detailVO.setOsName(instancesOutput.getOsName());
                    detailVO.setOsType(instancesOutput.getOsType());
                    detailVO.setInstanceName(instancesOutput.getInstanceName());
                    detailVO.setStatus(EcsStatusEnum.valueOf(instancesOutput.getStatus()));
                    if (StringUtils.isNotBlank(instancesOutput.getZoneId())){
                        detailVO.setZone(new SourceRegionZoneVO(instancesOutput.getZoneId()));
                    }
                    detailVO.setNetworkList(instancesOutput.getNetworkInterfaces());
                    detailVO.setEipAddress(instancesOutput.getEipAddress());
                }

                if (volumeEntityList != null) {
                    detailVO.setSysVolume(volumeEntityList.stream()
                            .filter(volume -> "system".equals(volume.getKind()))
                            .findFirst()
                            .orElse(null));
                    detailVO.setVolumeList(volumeEntityList.stream()
                            .filter(volume -> !"system".equals(volume.getKind()))
                            .toList());
                }
            } catch (ApiException e) {
                log.error("获取火山服务器资源失败: ", e);
            }
            return detailVO;
        } else {
            // 获取云盘信息
            List<SysCustomerVolumeEntity> volumeEntityList = sysCustomerVolumeMapper.selectList(Wrappers.lambdaQuery(SysCustomerVolumeEntity.class)
                    .in(SysCustomerVolumeEntity::getOrderSourceUid, entity.getOrderSourceUid()));
            PcConsoleEcsDetailVO detailVO = BeanUtils.copyBean(entity, PcConsoleEcsDetailVO.class);
            if (volumeEntityList != null) {
                detailVO.setSysVolume(volumeEntityList.stream()
                        .filter(volume -> "system".equals(volume.getKind()))
                        .findFirst()
                        .orElse(null));
                detailVO.setVolumeList(volumeEntityList.stream()
                        .filter(volume -> !"system".equals(volume.getKind()))
                        .toList());
            }
            //自建服务器
            if (entity.getProductType() == 2) {
                SysCustomerEcsWorkEipEntity sysCustomerEcsWorkEipEntity = sysCustomerEcsWorkEipMapper.selectOne(Wrappers.lambdaQuery(SysCustomerEcsWorkEipEntity.class)
                        .eq(SysCustomerEcsWorkEipEntity::getInstanceId, id));
                if (ObjectUtils.isNotEmpty(sysCustomerEcsWorkEipEntity)){
                    detailVO.setWorkId(sysCustomerEcsWorkEipEntity.getEcsWorkId());
                        detailVO.setPublicInfos(List.of(BeanUtils.copyBean(sysCustomerEcsWorkEipEntity, SysCustomerEcsWorkEipDTO.class)));

                    switch (sysCustomerEcsWorkEipEntity.getOperationStatus()) {
                        case 1:
                            detailVO.setStatus(HAVE_NOT_OPENED);
                            break;
                        case 2:
                            detailVO.setStatus(HAVE_OPENED);
                            break;
                        case 3:
                            detailVO.setStatus(CALCULATING);
                            break;
                        case 4:
                            detailVO.setStatus(SHUT_DOWN);
                            break;
                        case 5:
                            detailVO.setStatus(REFUNDED);
                            break;
                        case 6:
                            detailVO.setStatus(EXPIRE);
                    }

                }
                try {

                    DescribeImagesResponse describeImagesResponse = describeImages(entity.getImageId());
                    if (com.baomidou.mybatisplus.core.toolkit.ObjectUtils.isNotEmpty(describeImagesResponse)){
                        describeImagesResponse.getImages().forEach(image -> {
                            detailVO.setOsName(image.getImageName());
                            detailVO.setOsType(image.getVisibility());
                        });
                    }

                } catch (Exception e) {
                    log.error("查询镜像信息失败", e);
                }
            }

            return detailVO;
        }
    }
//    @Override
//    public CompletableFuture<PcConsoleEcsDetailVO> getInstanceDetailAsync(String id) {
//        CompletableFuture<PcConsoleEcsDetailVO> future = CompletableFuture.supplyAsync(() -> {
//            SysCustomerInstancesEntity entity = sysCustomerInstancesMapper.selectById(id);
//            if (ObjectUtils.isEmpty(entity)) {
//                throw new HttpServiceException("实列不存在");
//            }
//            return BeanUtils.copyBean(entity, PcConsoleEcsDetailVO.class);
//        });
//        CompletableFuture<InstanceForDescribeInstancesOutput> instancesOutputCompletableFuture = future.thenApplyAsync(detailVO -> {
//            log.info("调用火山接口");
//            InstanceForDescribeInstancesOutput instancesOutput = null;
//            if (StringUtils.isNotEmpty(detailVO.getInstanceId())) {
//                EcsApi ecsApi = VoEngineApiClient.getEcsApi(detailVO.getRegion().getId());
//                // 实列信息
//                DescribeInstancesRequest request = new DescribeInstancesRequest();
//                request.setInstanceIds(List.of(detailVO.getInstanceId()));
//                request.setMaxResults(1);
//                try {
//                    List<InstanceForDescribeInstancesOutput> instances = ecsApi.describeInstances(request).getInstances();
//                    instancesOutput = instances.get(0);
//                } catch (ApiException e) {
//                    log.error("获取火山服务器资源失败: ", e);
//                }
//            }
//            return instancesOutput;
//        });
//        // 获取云盘信息
//        CompletableFuture<List<SysCustomerVolumeEntity>> volumeFuture = future.thenApplyAsync(detailVO ->
//                sysCustomerVolumeMapper.selectList(Wrappers.lambdaQuery(SysCustomerVolumeEntity.class)
//                .in(SysCustomerVolumeEntity::getOrderSourceUid, detailVO.getOrderSourceUid())));
//        return future.thenCombine(instancesOutputCompletableFuture, (detailVO, instancesOutput) -> {
//            if (instancesOutput != null) {
//                detailVO.setOsName(instancesOutput.getOsName());
//                detailVO.setOsType(instancesOutput.getOsType());
//                detailVO.setInstanceName(instancesOutput.getInstanceName());
//                detailVO.setStatus(EcsStatusEnum.valueOf(instancesOutput.getStatus()));
//                detailVO.setZone(new SourceRegionZoneVO(instancesOutput.getZoneId()));
//                detailVO.setNetworkList(instancesOutput.getNetworkInterfaces());
//                detailVO.setEipAddress(instancesOutput.getEipAddress());
//            }
//            return detailVO;
//        }).thenCombine(volumeFuture, (detailVO, volumeEntityList) -> {
//            if (volumeEntityList != null) {
//                detailVO.setSysVolume(volumeEntityList.stream()
//                        .filter(volume -> "system".equals(volume.getKind()))
//                        .findFirst()
//                        .orElse(null));
//                detailVO.setVolumeList(volumeEntityList.stream()
//                        .filter(volume -> !"system".equals(volume.getKind()))
//                        .toList());
//            }
//            return detailVO;
//        });
//    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handlerInstancesStatus(PcInstancesStatusHandlerEdit edit, SourceRegionsEnum sourceRegionsEnum) {
        List<SysCustomerInstancesEntity> instancesList = sysCustomerInstancesMapper.selectList(Wrappers.lambdaQuery(SysCustomerInstancesEntity.class)
                .in(SysCustomerInstancesEntity::getId, edit.getIdList())
                .eq(SysCustomerInstancesEntity::getRegion, sourceRegionsEnum));
        if (ObjectUtils.isEmpty(instancesList)) {
            throw new HttpServiceException("实列不存在");
        }
        List<String> instanceIdList = instancesList.stream()
                .map(SysCustomerInstancesEntity::getInstanceId)
                .filter(ObjectUtils::isNotEmpty)
                .toList();
        EcsApi ecsApi = VoEngineApiClient.getEcsApi(sourceRegionsEnum.getId());
        List<String> successInstanceIds = new ArrayList<>(instanceIdList.size());
        EcsStatusEnum status = edit.getStatusEnum();
        try {
            switch (status) {
                case STARTING -> {
                    Long count = sysCustomerBillMapper.selectCount(Wrappers.lambdaQuery(SysCustomerBillEntity.class)
                            .in(SysCustomerBillEntity::getInstanceId, instanceIdList)
                            .eq(SysCustomerBillEntity::getPayStatus, OrderStatusEnum.UNPAID));
                    if (count != null && count > 0) {
                        throw new HttpServiceException("实列存在未支付账单，请先充值余额");
                    }
                    StartInstancesRequest request = new StartInstancesRequest();
                    request.setInstanceIds(instanceIdList);
                    request.setClientToken(ThreadLocalContext.getRequestLogId().toString());
                    List<OperationDetailForStartInstancesOutput> operationDetails = ecsApi.startInstances(request).getOperationDetails();
                    for (OperationDetailForStartInstancesOutput operationDetail : operationDetails) {
                        ErrorForStartInstancesOutput error = operationDetail.getError();
                        if (error == null || StringUtils.isEmpty(error.getCode())) {
                            successInstanceIds.add(operationDetail.getInstanceId());
                        } else {
                            log.error("启动实列失败：{}, msg: {}", error.getCode(), error.getMessage());
                        }
                    }
                }
                case STOPPING -> {
                    StopInstancesRequest request = new StopInstancesRequest();
                    request.setInstanceIds(instanceIdList);
                    request.setStoppedMode(StringUtils.isEmpty(edit.getStoppedMode()) ? "KeepCharging" : edit.getStoppedMode());
                    request.setClientToken(ThreadLocalContext.getRequestLogId().toString());
                    List<OperationDetailForStopInstancesOutput> operationDetails = ecsApi.stopInstances(request).getOperationDetails();
                    for (OperationDetailForStopInstancesOutput operationDetail : operationDetails) {
                        ErrorForStopInstancesOutput error = operationDetail.getError();
                        if (error == null || StringUtils.isEmpty(error.getCode())) {
                            successInstanceIds.add(operationDetail.getInstanceId());
                        } else {
                            log.error("停止实列失败：{}, msg: {}", error.getCode(), error.getMessage());
                        }
                    }
                }
                case REBOOTING -> {
                    RebootInstancesRequest request = new RebootInstancesRequest();
                    request.setInstanceIds(instanceIdList);
                    request.setClientToken(ThreadLocalContext.getRequestLogId().toString());
                    List<OperationDetailForRebootInstancesOutput> operationDetails = ecsApi.rebootInstances(request).getOperationDetails();
                    for (OperationDetailForRebootInstancesOutput operationDetail : operationDetails) {
                        ErrorForRebootInstancesOutput error = operationDetail.getError();
                        if (error == null || StringUtils.isEmpty(error.getCode())) {
                            successInstanceIds.add(operationDetail.getInstanceId());
                        } else {
                            log.error("重启实列失败：{}, msg: {}", error.getCode(), error.getMessage());
                        }
                    }
                }
                case DELETING -> {
                    sysCustomerInstancesMapper.deleteBatchIds(edit.getIdList());
                    DescribeInstancesRequest request2 = new DescribeInstancesRequest();
                    request2.setInstanceIds(instanceIdList);
                    request2.setMaxResults(instancesList.size());
                    try {
                        List<InstanceForDescribeInstancesOutput> instances = ecsApi.describeInstances(request2).getInstances();
                        if (ObjectUtils.isNotEmpty(instances)){
                            List<String> ids = instances.stream().map(InstanceForDescribeInstancesOutput::getInstanceId).toList();
                            DeleteInstancesRequest request = new DeleteInstancesRequest();
                            request.setInstanceIds(ids);
                            request.setClientToken(ThreadLocalContext.getRequestLogId().toString());
                            List<OperationDetailForDeleteInstancesOutput> operationDetails = ecsApi.deleteInstances(request).getOperationDetails();
                            for (OperationDetailForDeleteInstancesOutput operationDetail : operationDetails) {
                                ErrorForDeleteInstancesOutput error = operationDetail.getError();
                                if (error == null || StringUtils.isEmpty(error.getCode())) {
                                    successInstanceIds.add(operationDetail.getInstanceId());
                                } else {
                                    log.error("删除实列失败：{}, msg: {}", error.getCode(), error.getMessage());
                                }
                            }
                        }
                    } catch (Exception e) {
                        log.error("获取火山服务器资源失败: ", e);
                    }

                }
            }
        } catch (ApiException e) {
            log.error("批量{}实列失败 ", status.getDesc(), e);
            throw new HttpServiceException("实列操作失败");
        }
        if (!successInstanceIds.isEmpty()) {
            List<SysCustomerInstancesEntity> list = instancesList.stream()
                    .filter(e -> successInstanceIds.contains(e.getInstanceId()))
                    .peek(e -> e.setStatus(status))
                    .toList();
            sysCustomerInstancesMapper.batchUpdateById(list);
            edit.setRegionsEnum(sourceRegionsEnum);
            edit.setIdList(list.stream().map(SysCustomerInstancesEntity::getId).toList());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean serverApply(AppletsEcsWorkOperationVO vo) {
        SysCustomerEcsWorkEipEntity sysCustomerEcsWorkEipEntity = sysCustomerEcsWorkEipMapper.selectOne(Wrappers.lambdaQuery(SysCustomerEcsWorkEipEntity.class)
                .eq(SysCustomerEcsWorkEipEntity::getInstanceId, vo.getInstanceId()));
        if (sysCustomerEcsWorkEipEntity != null) {
            SysCustomerInstancesEntity sysCustomerInstancesEntity = sysCustomerInstancesMapper.selectById(vo.getInstanceId());
            SourceChargeTypeEnum chargeType = sysCustomerInstancesEntity.getChargeType();
            int hours;
            Long userId;
            try {
                userId = SecurityContext.getUserInfo().getUserId();
            }catch (Exception ignored){
                log.info("从队列进来的请求");
                userId = sysCustomerInstancesEntity.getCustomerId();
            }
            SysMessage m = new SysMessage();
            m.setMsgType(7);
            m.setStatus(1);
            m.setUserId(userId);
            SysCustomerEntity sysCustomerEntity = sysCustomerMapper.selectById(userId);

            switch (vo.getOperationType()) {
                case 1:
                    if (sysCustomerEcsWorkEipEntity.getUserStatus() == 2 && sysCustomerEcsWorkEipEntity.getOperationStatus() == 2){
                        throw new HttpServiceException("实例已申请停机,请勿重复提交");
                    }
                    sysCustomerEcsWorkEipEntity.setUserStatus(2);
                    if (chargeType == SourceChargeTypeEnum.POSTPAID_BY_HOUR) {
                        hours = calculateUsageHours(sysCustomerInstancesEntity.getStartTime(), new Date());
                        handlerInstancesBill(hours,sysCustomerInstancesEntity);
                    }
                    m.setText("用户【" + sysCustomerEntity.getCustomerName() + "】对服务器【" + sysCustomerInstancesEntity.getInstanceName()+ "】申请停机，请及时处理！");
                    break;
                case 2:
                    if (sysCustomerEcsWorkEipEntity.getUserStatus() == 5 && sysCustomerEcsWorkEipEntity.getOperationStatus() != 5){
                        throw new HttpServiceException("实例已申请销毁,请勿重复提交");
                    }
                    sysCustomerEcsWorkEipEntity.setUserStatus(5);
                    if (chargeType == SourceChargeTypeEnum.POSTPAID_BY_HOUR) {
                        hours = calculateUsageHours(sysCustomerInstancesEntity.getStartTime(), new Date());
                        handlerInstancesBill(hours,sysCustomerInstancesEntity);
                    }
                    m.setText("用户【" + sysCustomerEntity.getCustomerName() + "】对服务器【" + sysCustomerInstancesEntity.getInstanceName()+ "】申请销毁，请及时处理！");
                    break;
                case 3:
                    if (sysCustomerEcsWorkEipEntity.getUserStatus() == 3 && sysCustomerEcsWorkEipEntity.getOperationStatus() != 2){
                        throw new HttpServiceException("实例已申请重启,请勿重复提交");
                    }
                    sysCustomerEcsWorkEipEntity.setUserStatus(3);
                    sysCustomerInstancesEntity.setStartTime(new Date());
                    if (chargeType == SourceChargeTypeEnum.POSTPAID_BY_HOUR) {
                        BigDecimal arrearsAmount = sysCustomerBillService.getArrearsAmount(sysCustomerInstancesEntity.getCustomerId());
                        if (arrearsAmount.compareTo(BigDecimal.ZERO) > 0) {
                            throw new HttpServiceException("当前账户有未缴清的账单，请先缴清后再启动");
                        }
                    }

                    m.setText("用户【" + sysCustomerEntity.getCustomerName() + "】对服务器【" + sysCustomerInstancesEntity.getInstanceName()+ "】申请重启，请及时处理！");

                    break;
                case 4: //  续费（暂不处理）
                    break;
            }
            sysMessageMapper.insert(m);
            return sysCustomerEcsWorkEipMapper.updateById(sysCustomerEcsWorkEipEntity)>0;
        }
        return false;
    }

    // 新增方法：计算使用小时数（向上取整）
    private int calculateUsageHours(Date startTime, Date endTime) {
        if (startTime == null || endTime == null) {
            return 0;
        }
        long diffInMillis = endTime.getTime() - startTime.getTime();
        return (int) Math.ceil(diffInMillis / (1000.0 * 60 * 60));
    }

    public void handlerInstancesBill(int hours,SysCustomerInstancesEntity sysCustomerInstancesEntity) {
        log.info("开始计算按时付费的自建服务器,hours:{},sysCustomerInstancesEntity:{}", hours, sysCustomerInstancesEntity);
        SysCustomerBillEntity entity = getSysCustomerBillEntity(hours, sysCustomerInstancesEntity);
        log.info("账单信息：{}", entity);
        // 处理代金卷
        Map<Long, BigDecimal> customerVoucherBalanceMap = sysCustomerVoucherService.batchUserVoucherBalance(List.of(entity.getCustomerId()));
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
        // 处理授信额
        Map<Long, BigDecimal> customerCreditLineMap = sysCustomerCreditLineService.batchUserCreditLine(Set.of(entity.getCustomerId()));
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
        // 处理余额
        List<SysCustomerEntity> customerList = sysCustomerMapper.selectList(Wrappers.lambdaQuery(SysCustomerEntity.class)
                .in(SysCustomerEntity::getId, entity.getCustomerId()));
        Map<Long, BigDecimal> customerBalanceMap = customerList.stream().collect(Collectors.toMap(SysCustomerEntity::getId, SysCustomerEntity::getBalance));
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
        log.info("最后账单信息：{}", entity);
        // 入库
        sysCustomerBillMapper.insert(entity);
        log.info("自建服务器按时计费账单处理完成");
    }

    private SysCustomerBillEntity getSysCustomerBillEntity(int hours, SysCustomerInstancesEntity sysCustomerInstancesEntity) {
        BigDecimal price = BigDecimal.ZERO;
        String orderSourceUid = sysCustomerInstancesEntity.getOrderSourceUid();
        List<SysOrderSourceEntity> sysOrderSourceEntityList = sysOrderSourceMapper.selectList(Wrappers.lambdaQuery(SysOrderSourceEntity.class)
                .eq(SysOrderSourceEntity::getUid, orderSourceUid));
        for (SysOrderSourceEntity sysOrderSourceEntity : sysOrderSourceEntityList) {
            if (sysOrderSourceEntity.getSourceType().equals(ECS)) {
                JSONObject configDetail = sysOrderSourceEntity.getConfigDetail();
                Long id = configDetail.getLong("id");
                SysEcsEntity sysEcsEntity = sysEcsMapper.selectById(id);
                log.info("ecs:{}", sysEcsEntity);
                BigDecimal ipPrice = sysEcsEntity.getIpPrice().multiply(new BigDecimal(sysCustomerInstancesEntity.getBandwidth()));
                price = price.add(sysEcsEntity.getHoursPrice()).add(ipPrice); // 重新赋值
            } else if (sysOrderSourceEntity.getSourceType().equals(CLOUD_STORAGE)) {
                JSONObject configDetail = sysOrderSourceEntity.getConfigDetail();
                BigDecimal hoursPrice = configDetail.getBigDecimal("hoursPrice");
                price = price.add(hoursPrice); // 重新赋值
            }
        }


        log.info("price:{},hours:{}", price,hours);
        SysCustomerBillEntity entity = Builder.of(SysCustomerBillEntity::new)
                .set(SysCustomerBillEntity::setId, IdUtils.nextId())
                .set(SysCustomerBillEntity::setCustomerId, sysCustomerInstancesEntity.getCustomerId())
                .set(SysCustomerBillEntity::setSourceId, sysCustomerInstancesEntity.getId())
                .set(SysCustomerBillEntity::setBill, DateUtils.parseDateToStr("yyyy-MM", new Date()))
                .set(SysCustomerBillEntity::setBillNo, IdUtils.simpleUUID())
                .set(SysCustomerBillEntity::setBillDate, new Date())
                .set(SysCustomerBillEntity::setSourceType, ECS)
                .set(SysCustomerBillEntity::setChargeType, sysCustomerInstancesEntity.getChargeType())
                .set(SysCustomerBillEntity::setDuration, hours)
                .set(SysCustomerBillEntity::setChargeUnit,"小时")
                .set(SysCustomerBillEntity::setBillType, "消费-使用")
                .set(SysCustomerBillEntity::setSettleType, "结算")
                .set(SysCustomerBillEntity::setInstanceId, sysCustomerInstancesEntity.getInstanceId())
                .set(SysCustomerBillEntity::setInstanceName, "云服务器")
                .set(SysCustomerBillEntity::setUnitId, "")
                .set(SysCustomerBillEntity::setRegion, sysCustomerInstancesEntity.getRegion().getName())
                .set(SysCustomerBillEntity::setZone, sysCustomerInstancesEntity.getZoneId())
                .set(SysCustomerBillEntity::setPriceType, "按时计费")
                .set(SysCustomerBillEntity::setPrice, price.multiply(BigDecimal.valueOf(hours)))
                .set(SysCustomerBillEntity::setUsage, new BigDecimal(1))
                .set(SysCustomerBillEntity::setUsageUnit, "台")
                .set(SysCustomerBillEntity::setOriginalPrice, price.multiply(BigDecimal.valueOf(hours)))
                .set(SysCustomerBillEntity::setBillStartTime, sysCustomerInstancesEntity.getStartTime())
                .set(SysCustomerBillEntity::setBillEndTime,new Date())
                .build();
                log.info("自建服务器按时计费账单:{}", entity);
        Map<Long, Map<SourceTypeEnum, BigDecimal>> customerDiscountMap = Optional.of(sysCustomerDiscountMapper.selectList(Wrappers.lambdaQuery(SysCustomerDiscountEntity.class)
                        .eq(SysCustomerDiscountEntity::getCustomerId, entity.getCustomerId())))
                .flatMap(customerDiscountList -> {
                    Map<Long, Map<SourceTypeEnum, BigDecimal>> resultMap = new HashMap<>(customerDiscountList.size());
                    for (SysCustomerDiscountEntity entity2 : customerDiscountList) {
                        Map<SourceTypeEnum, BigDecimal> map = resultMap.get(entity2.getCustomerId());
                        if (map == null) {
                            map = new HashMap<>(SourceTypeEnum.values().length);
                        }
                        map.put(entity2.getSourceType(), entity2.getDiscountRation());
                        resultMap.put(entity2.getCustomerId(), map);
                    }
                    return resultMap;
                })
                .orElse(new HashMap<>(0));
        log.info("customerDiscountMap:{}", customerDiscountMap);
        // 计算折扣
        SystemPriceRationConfigModel sellPriceRatioConfig = sysConfigService.getConfig(SystemConfigEnum.SELL_PRICE_RATIO).getConfigValue().to(SystemPriceRationConfigModel.class);
        // 计算账单价格
        SourceTypeEnum sourceType = entity.getSourceType();
        BigDecimal price2 = entity.getPrice();
        if (price2.compareTo(BigDecimal.ZERO) != 0) {
            Map<SourceTypeEnum, BigDecimal> map = customerDiscountMap.get(entity.getCustomerId());
            BigDecimal discount;
            if (map == null) {
                discount = BigDecimal.ONE;
            } else {
                discount = map.get(sourceType);
            }
            if (discount == null) {
                discount = BigDecimal.ONE;
            }
            log.info("discount:{}", discount);
            BigDecimal originalPrice;
            if (!entity.getChargeType().equals(POSTPAID_BY_HOUR)) {
                originalPrice = entity.getPrice();
                entity.setOriginalPrice(originalPrice.setScale(8, RoundingMode.UP));
                entity.setPremiumPrice(sellPriceRatioConfig.calculatePremium(entity.getOriginalPrice()));
                entity.setUserDiscountAmount(entity.getPremiumPrice().multiply(discount).setScale(8, RoundingMode.UP));
                entity.setPayPrice(entity.getUserDiscountAmount().setScale(2, RoundingMode.UP));
                entity.setPayStatus(OrderStatusEnum.PAID);
            }else {
                if (entity.getSourceType().equals(SourceTypeEnum.CLOUD_NETWORK)) {
                    originalPrice = entity.getUsage().multiply(entity.getPrice()).setScale(8, RoundingMode.UP);
                } else {
                    originalPrice = BigDecimal.valueOf(entity.getDuration()).multiply(entity.getPrice());
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
        return entity;
    }

    @Override
    public void handlerCustomerBillSelfBuildHourCheckout(Long valueOf) {
        SysCustomerInstancesEntity sysCustomerInstancesEntity = sysCustomerInstancesMapper.selectById(valueOf);
        if (sysCustomerInstancesEntity != null) {
            SysCustomerBillEntity sysCustomerBillEntity = this.getSysCustomerBillEntity(1, sysCustomerInstancesEntity);
            BigDecimal payPrice = sysCustomerBillEntity.getPayPrice();
            SysCustomerEntity sysCustomerEntity = sysCustomerMapper.selectById(sysCustomerInstancesEntity.getCustomerId());
            BigDecimal creditAmount = sysCustomerCreditLineService.getCreditAmount(sysCustomerEntity.getId());
            BigDecimal userVoucherBalance = sysCustomerVoucherService.getUserVoucherBalance(sysCustomerEntity.getId());
            BigDecimal balance = sysCustomerEntity.getBalance();
            if (creditAmount.add( balance).add(userVoucherBalance).compareTo(payPrice) >= 0) {
                cacheQueueService.addDelayQueue(CacheQueueConstant.CUSTOMER_SELF_BUILD_SERVER_HOURLY_CHECK_QUEUE_TYPE,valueOf.toString(), 60 * 60 * 24);
            } else {
                AppletsEcsWorkOperationVO vo = new AppletsEcsWorkOperationVO();
                vo.setInstanceId(sysCustomerInstancesEntity.getId());
                vo.setOperationType(1);
                serverApply(vo);
            }
        }

    }
}
