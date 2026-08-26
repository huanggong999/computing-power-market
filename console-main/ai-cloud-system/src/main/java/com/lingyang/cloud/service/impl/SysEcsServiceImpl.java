package com.lingyang.cloud.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.entity.SysEcsEntity;
import com.lingyang.cloud.enums.system.SystemConfigEnum;
import com.lingyang.cloud.mapper.SysEcsMapper;
import com.lingyang.cloud.model.config.SystemPriceRationConfigModel;
import com.lingyang.cloud.model.query.esc.PcEcsQuery;
import com.lingyang.cloud.model.vo.regions.SourceRegionZoneVO;
import com.lingyang.cloud.service.SysConfigService;
import com.lingyang.cloud.service.SysEcsService;
import com.lingyang.cloud.voengine.client.VoEngineApiClient;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.utils.Optional;
import com.volcengine.ApiException;
import com.volcengine.ecs.EcsApi;
import com.volcengine.ecs.model.AvailableZoneForDescribeAvailableResourceOutput;
import com.volcengine.ecs.model.DescribeAvailableResourceRequest;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/1 16:06
 */
@Service
@Slf4j
public class SysEcsServiceImpl implements SysEcsService {

    @Resource
    private SysEcsMapper sysEcsMapper;

    @Resource
    private SysConfigService configService;


    @Override
    public PageResult<SysEcsEntity> getEcsPage(PageQuery<PcEcsQuery> pageQuery) {
        pageQuery.startPage();
        PcEcsQuery query = pageQuery.getQuery();
        LambdaQueryWrapper<SysEcsEntity> queryWrapper = Wrappers.lambdaQuery(SysEcsEntity.class)
                .eq(ObjectUtils.isNotEmpty(query.getEcsTypeEnum()), SysEcsEntity::getEcsType, query.getEcsTypeEnum())
                .eq(ObjectUtils.isNotEmpty(query.getSourceRegions()), SysEcsEntity::getRegionsZones, query.getSourceRegions())
                .eq(ObjectUtils.isNotEmpty(query.getVCpuNumber()), SysEcsEntity::getCpuNumber, query.getVCpuNumber())
                .eq(ObjectUtils.isNotEmpty(query.getMemorySize()), SysEcsEntity::getMemorySize, query.getMemorySize())
                .eq(ObjectUtils.isNotEmpty(query.getProductType()), SysEcsEntity::getProductType, query.getProductType())
                .like(ObjectUtils.isNotEmpty(query.getEcsScale()), SysEcsEntity::getEcsScale, query.getEcsScale())
                .like(ObjectUtils.isNotEmpty(query.getGpuMemory()), SysEcsEntity::getGpuMemory, query.getGpuMemory())
                .like(ObjectUtils.isNotEmpty(query.getGpuModel()), SysEcsEntity::getGpuModel, query.getGpuModel())
                // .orderByDesc(SysEcsEntity::getCreateTime)
                .last(query.getEcsId() == null, " order by create_time desc ")
                .last(query.getEcsId() != null, " order by CASE WHEN id in ("+ query.getEcsId() +") THEN 0 ELSE 1 END ");
        List<SysEcsEntity> sysEcsEntities = sysEcsMapper.selectList(queryWrapper);
        if (ObjectUtils.isNotEmpty(sysEcsEntities) && query.getSourceRegions() != null) {
            // 处理溢价
            SystemPriceRationConfigModel priceRation = configService.getConfig(SystemConfigEnum.SELL_PRICE_RATIO).getValue(SystemPriceRationConfigModel.class);
            sysEcsEntities.forEach(priceRation::calculatePremium);
            // 查询库存
            EcsApi ecsApi = VoEngineApiClient.getEcsApi(query.getSourceRegions().getId());
            Map<String, List<SourceRegionZoneVO>> regionZones = new HashMap<>(sysEcsEntities.size());
            sysEcsEntities.stream().filter(e -> e.getProductType() == 1).map(SysEcsEntity::getEcsScale)
                    .toList().forEach(ecsScale -> {
                DescribeAvailableResourceRequest request = new DescribeAvailableResourceRequest();
                request.setDestinationResource("InstanceType");
                request.setInstanceTypeId(ecsScale);
                try {
                    Optional.of(ecsApi.describeAvailableResource(request).getAvailableZones())
                            .ifPresent(availableZones -> {
                                for (AvailableZoneForDescribeAvailableResourceOutput availableZone : availableZones) {
                                    if ("Available".equals(availableZone.getStatus())) {
                                        List<SourceRegionZoneVO> regionZoneList = regionZones.get(ecsScale);
                                        if (ObjectUtils.isEmpty(regionZoneList)) {
                                            regionZoneList = new ArrayList<>(4);
                                        }
                                        regionZoneList.add(new SourceRegionZoneVO(availableZone.getZoneId()));
                                        regionZones.put(ecsScale, regionZoneList);
                                    }
                                }
                            });
                } catch (ApiException e) {
                    log.error("获取云服务器可用资源区失败", e);
                }
            });
            sysEcsEntities.forEach(e -> e.setZoneList(regionZones.get(e.getEcsScale())));
        }
        return PageResult.of(sysEcsEntities);
    }

    @Override
    public void clearEcs() {
        sysEcsMapper.clearEcs();
    }


}