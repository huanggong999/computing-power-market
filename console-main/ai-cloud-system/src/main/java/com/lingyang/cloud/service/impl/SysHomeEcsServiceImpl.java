package com.lingyang.cloud.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.entity.SysEcsEntity;
import com.lingyang.cloud.entity.SysHomeEcsEntity;
import com.lingyang.cloud.mapper.SysEcsMapper;
import com.lingyang.cloud.mapper.SysHomeEcsMapper;
import com.lingyang.cloud.model.query.home.SysHomeEcsQuery;
import com.lingyang.cloud.model.vo.regions.SourceRegionZoneVO;
import com.lingyang.cloud.service.SysHomeEcsService;
import com.lingyang.cloud.voengine.client.VoEngineApiClient;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.utils.Optional;
import com.volcengine.ApiException;
import com.volcengine.ecs.EcsApi;
import com.volcengine.ecs.model.AvailableZoneForDescribeAvailableResourceOutput;
import com.volcengine.ecs.model.DescribeAvailableResourceRequest;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class SysHomeEcsServiceImpl  implements SysHomeEcsService {

    @Autowired
    private SysHomeEcsMapper sysHomeEcsMapper;

    @Autowired
    private SysEcsMapper sysEcsMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public SysHomeEcsEntity getById(Long id) {
        SysHomeEcsEntity sysHomeEcsEntity = sysHomeEcsMapper.selectById(id);
        SysEcsEntity sysEcsEntity = sysEcsMapper.selectById(sysHomeEcsEntity.getEcsId());
        if (sysEcsEntity != null) {
            sysHomeEcsEntity.setCpuNumber(sysEcsEntity.getCpuNumber());
            sysHomeEcsEntity.setMemorySize(sysEcsEntity.getMemorySize());
            sysHomeEcsEntity.setGpuModel(sysEcsEntity.getGpuModel());
            sysHomeEcsEntity.setEcsScale(sysEcsEntity.getEcsScale());
        }
        return sysHomeEcsEntity;
    }

    @Override
    public void save(SysHomeEcsEntity entity) {
        sysHomeEcsMapper.insert(entity);
    }

    @Override
    public void update(SysHomeEcsEntity entity) {
        sysHomeEcsMapper.updateById(entity);
    }

    @Override
    public Result<PageResult<SysHomeEcsEntity>> getPage(PageQuery<SysHomeEcsQuery> pageQuery) {
        SysHomeEcsQuery query = pageQuery.getQuery();
        pageQuery.startPage();
        LambdaQueryWrapper<SysHomeEcsEntity> queryWrapper = Wrappers.lambdaQuery(SysHomeEcsEntity.class)
                .eq(query.getStatus() != null, SysHomeEcsEntity::getStatus, query.getStatus())
                .eq(query.getProductType() != null, SysHomeEcsEntity::getProductType, query.getProductType())
                .like(StringUtils.isNotBlank(query.getName()), SysHomeEcsEntity::getName, query.getName())
                .orderByDesc(SysHomeEcsEntity::getCreateTime);
        List<SysHomeEcsEntity> value = sysHomeEcsMapper.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(value)) {
            for (SysHomeEcsEntity sysHomeEcsEntity : value) {
                SysEcsEntity sysEcsEntity = sysEcsMapper.selectById(sysHomeEcsEntity.getEcsId());
                if (sysEcsEntity != null) {
                    sysHomeEcsEntity.setCpuNumber(sysEcsEntity.getCpuNumber());
                    sysHomeEcsEntity.setMemorySize(sysEcsEntity.getMemorySize());
                    sysHomeEcsEntity.setGpuModel(sysEcsEntity.getGpuModel());
                    sysHomeEcsEntity.setEcsScale(sysEcsEntity.getEcsScale());
                    sysHomeEcsEntity.setHoursPrice(sysEcsEntity.getHoursPrice());
                    sysHomeEcsEntity.setOneYearPrice(sysEcsEntity.getOneYearPrice());
                    sysHomeEcsEntity.setTwoYearPrice(sysEcsEntity.getTwoYearPrice());
                    sysHomeEcsEntity.setThreeYearPrice(sysEcsEntity.getThreeYearPrice());
                    sysHomeEcsEntity.setMonthPrice(sysEcsEntity.getMonthPrice());
                }
            }
        }
        return Result.success(Optional.of(value)
                .flatMap(entities -> {

                    PageResult<SysHomeEcsEntity> result = PageResult.of(entities);

                    return result;
                })
                .orElseGet(() -> {
                    return PageResult.of(List.of());
                }));
    }

    @Override
    public Result<PageResult<SysHomeEcsEntity>> getHomePage(PageQuery<SysHomeEcsQuery> pageQuery) {
        pageQuery.setPageSize(1000);
        SysHomeEcsQuery query = pageQuery.getQuery();
        pageQuery.startPage();
        List<SysHomeEcsEntity> value = sysHomeEcsMapper.selectHomeEcsWithDetails(query);
        if (CollectionUtils.isNotEmpty(value)) {
            for (SysHomeEcsEntity sysHomeEcsEntity : value) {
                List<SourceRegionZoneVO> regionZones = new ArrayList<>();
                if (sysHomeEcsEntity.getProductType() == 1 && sysHomeEcsEntity.getRegionsZones() != null){

                    Object o = redisTemplate.opsForValue().get("home_ecs_regions_" + sysHomeEcsEntity.getId());
                    if (o !=  null){
                        if (o instanceof List<?> list2) {
                            for (Object item : list2) {
                                if (item instanceof SourceRegionZoneVO) {
                                    regionZones.add((SourceRegionZoneVO) item);
                                }
                            }
                        }
                    }else {
                        // 查询库存
                        EcsApi ecsApi = VoEngineApiClient.getEcsApi(sysHomeEcsEntity.getRegionsZones().getId());
                        DescribeAvailableResourceRequest request = new DescribeAvailableResourceRequest();
                        request.setDestinationResource("InstanceType");
                        request.setInstanceTypeId(sysHomeEcsEntity.getEcsScale());

                        try {
                            Optional.of(ecsApi.describeAvailableResource(request).getAvailableZones())
                                    .ifPresent(availableZones -> {
                                        for (AvailableZoneForDescribeAvailableResourceOutput availableZone : availableZones) {
                                            if ("Available".equals(availableZone.getStatus())) {
                                                regionZones.add(new SourceRegionZoneVO(availableZone.getZoneId()));

                                            }
                                        }
                                    });
                        } catch (ApiException e) {
                            log.error("获取云服务器可用资源区失败", e);
                        }
                        redisTemplate.opsForValue().set("home_ecs_regions_" + sysHomeEcsEntity.getId(), regionZones);
                    }


                    if (CollectionUtils.isNotEmpty(regionZones)){
                        sysHomeEcsEntity.setZoneList(regionZones);
                        // 标记有资源
                        sysHomeEcsEntity.setHasResource(true);
                    } else {
                        sysHomeEcsEntity.setHasResource(false);
                    }
                }
            }
            // 按是否有资源排序，有资源的在前面
            value.sort((entity1, entity2) -> {
                boolean hasResource1 = Boolean.TRUE.equals(entity1.getHasResource());
                boolean hasResource2 = Boolean.TRUE.equals(entity2.getHasResource());
                if (hasResource1 && !hasResource2) {
                    return -1; // entity1 排在前面
                } else if (!hasResource1 && hasResource2) {
                    return 1;  // entity2 排在前面
                } else {
                    return 0;  // 保持原有顺序
                }
            });
        }
        return Result.success(Optional.of(value)
                .flatMap(entities -> {

                    PageResult<SysHomeEcsEntity> result = PageResult.of(entities);

                    return result;
                })
                .orElseGet(() -> {
                    return PageResult.of(List.of());
                }));
    }
}
