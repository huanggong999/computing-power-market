package com.lingyang.cloud.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.entity.SysVolumeEntity;
import com.lingyang.cloud.mapper.SysVolumeMapper;
import com.lingyang.cloud.model.query.volume.SysVolumeQuery;
import com.lingyang.cloud.service.SysVolumeService;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.utils.Optional;
import com.lingyang.common.core.utils.StringUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/5 17:32
 */
@Service
public class SysVolumeServiceImpl implements SysVolumeService {
    @Resource
    private SysVolumeMapper sysVolumeMapper;

    @Override
    public PageResult<SysVolumeEntity> getPageList(PageQuery<SysVolumeQuery> pageQuery) {
        pageQuery.startPage();
        SysVolumeQuery query = pageQuery.getQuery();
        LambdaQueryWrapper<SysVolumeEntity> queryWrapper = Wrappers.lambdaQuery(SysVolumeEntity.class)
                .eq(SysVolumeEntity::getKind, "data")
                .eq(ObjectUtils.isNotEmpty(query.getRegion()), SysVolumeEntity::getRegionsZones, query.getRegion())
                .eq(StringUtils.isNotEmpty(query.getVolumeType()), SysVolumeEntity::getVolumeType, query.getVolumeType())
                .orderByDesc(SysVolumeEntity::getUpdateTime);
        List<SysVolumeEntity> sysVolumeEntities = Optional.of(sysVolumeMapper.selectList(queryWrapper))
                .flatMap(list -> {
                    Integer capacity = query.getCapacity();
                    if (ObjectUtils.isNotEmpty(capacity)) {
                        list.forEach(l -> {
                            if (!Objects.equals(l.getVolumeCapacity(), capacity)) {
                                int newCapacity = capacity - l.getVolumeCapacity();
                                BigDecimal ioPsStepSize = l.getIoPsStepSize();
                                if (ioPsStepSize != null) {
                                    l.setIoPs(BigDecimal.valueOf(l.getIoPs()).add(ioPsStepSize.multiply(BigDecimal.valueOf(newCapacity))).intValue());
                                }
                                BigDecimal throughputStepSize = l.getThroughputStepSize();
                                if (throughputStepSize != null ) {
                                    l.setThroughput(l.getThroughput().add(throughputStepSize.multiply(BigDecimal.valueOf(newCapacity))));
                                }
                            }
                        });
                    }
                    return list;
                })
                .orElseGet(List::of);
        return PageResult.of(sysVolumeEntities);
    }
}