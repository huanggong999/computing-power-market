package com.lingyang.cloud.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.entity.GpuResourceEntity;
import com.lingyang.cloud.entity.GpuResourceSpecEntity;
import com.lingyang.cloud.entity.GpuResourceStockEntity;
import com.lingyang.cloud.mapper.GpuResourceMapper;
import com.lingyang.cloud.mapper.GpuResourceSpecMapper;
import com.lingyang.cloud.mapper.GpuResourceStockMapper;
import com.lingyang.cloud.model.edit.gpu.GpuSpecEdit;
import com.lingyang.cloud.model.vo.pc.GpuModelStatVO;
import com.lingyang.cloud.model.vo.system.GpuSpecVO;
import com.lingyang.cloud.service.GpuResourceSpecService;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * GPU规格服务实现
 * @author Claude
 * @Date: 2025/05/13
 */
@Service
@Slf4j
public class GpuResourceSpecServiceImpl implements GpuResourceSpecService {

    @Resource
    private GpuResourceSpecMapper gpuResourceSpecMapper;

    @Resource
    private GpuResourceMapper gpuResourceMapper;

    @Resource
    private GpuResourceStockMapper gpuResourceStockMapper;

    @Override
    public PageResult<GpuSpecVO> getSpecPage(PageQuery pageQuery, String model, Integer status) {
        pageQuery.startPage();
        LambdaQueryWrapper<GpuResourceSpecEntity> wrapper = Wrappers.lambdaQuery(GpuResourceSpecEntity.class)
                .eq(GpuResourceSpecEntity::getDelFlag, 0)
                .orderByAsc(GpuResourceSpecEntity::getSortOrder);
        if (ObjectUtils.isNotEmpty(model)) {
            wrapper.like(GpuResourceSpecEntity::getModel, model);
        }
        if (status != null) {
            wrapper.eq(GpuResourceSpecEntity::getStatus, status);
        }
        List<GpuResourceSpecEntity> list = gpuResourceSpecMapper.selectList(wrapper);
        return PageResult.of(convertToSpecVO(list));
    }

    @Override
    public List<GpuResourceSpecEntity> listAllEnabled() {
        LambdaQueryWrapper<GpuResourceSpecEntity> wrapper = Wrappers.lambdaQuery(GpuResourceSpecEntity.class)
                .eq(GpuResourceSpecEntity::getStatus, 1)
                .eq(GpuResourceSpecEntity::getDelFlag, 0)
                .orderByAsc(GpuResourceSpecEntity::getSortOrder);
        return gpuResourceSpecMapper.selectList(wrapper);
    }

    @Override
    public GpuResourceSpecEntity getById(Long id) {
        return gpuResourceSpecMapper.selectById(id);
    }

    @Override
    public GpuResourceSpecEntity getByModel(String model) {
        LambdaQueryWrapper<GpuResourceSpecEntity> wrapper = Wrappers.lambdaQuery(GpuResourceSpecEntity.class)
                .eq(GpuResourceSpecEntity::getModel, model);
        return gpuResourceSpecMapper.selectOne(wrapper);
    }

    @Override
    public void saveOrUpdate(GpuSpecEdit edit) {
        GpuResourceSpecEntity entity = new GpuResourceSpecEntity();
        BeanUtils.copyProperties(edit, entity);
        if (edit.getId() == null) {
            gpuResourceSpecMapper.insert(entity);
        } else {
            gpuResourceSpecMapper.updateById(entity);
        }
    }

    @Override
    public void deleteById(Long id) {
        gpuResourceSpecMapper.deleteById(id);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        GpuResourceSpecEntity entity = new GpuResourceSpecEntity();
        entity.setId(id);
        entity.setStatus(status);
        gpuResourceSpecMapper.updateById(entity);
    }

    @Override
    public List<GpuModelStatVO> getModelStats() {
        List<GpuResourceSpecEntity> specs = listAllEnabled();
        if (ObjectUtils.isEmpty(specs)) {
            return new ArrayList<>();
        }

        List<Long> specIds = specs.stream().map(GpuResourceSpecEntity::getId).toList();
        LambdaQueryWrapper<GpuResourceEntity> wrapper = Wrappers.lambdaQuery(GpuResourceEntity.class)
                .in(GpuResourceEntity::getSpecId, specIds)
                .eq(GpuResourceEntity::getStatus, 1)
                .eq(GpuResourceEntity::getDelFlag, 0);
        List<GpuResourceEntity> resources = gpuResourceMapper.selectList(wrapper);

        Map<Long, List<GpuResourceEntity>> specResourceMap = resources.stream()
                .collect(Collectors.groupingBy(GpuResourceEntity::getSpecId));

        return specs.stream()
                .filter(spec -> specResourceMap.containsKey(spec.getId()))
                .map(spec -> {
                    GpuModelStatVO vo = new GpuModelStatVO();
                    vo.setModel(spec.getModel());

                    List<GpuResourceEntity> specResources = specResourceMap.get(spec.getId());
                    int availableCount = 0;
                    int totalCount = 0;
                    for (GpuResourceEntity resource : specResources) {
                        GpuResourceStockEntity stock = gpuResourceStockMapper.selectByResourceId(resource.getId());
                        if (stock != null) {
                            availableCount += stock.getAvailableCount() == null ? 0 : stock.getAvailableCount();
                            totalCount += stock.getTotalCount() == null ? 0 : stock.getTotalCount();
                        }
                    }
                    vo.setAvailableCount(availableCount);
                    vo.setTotalCount(totalCount);

                    return vo;
                })
                .filter(stat -> stat.getTotalCount() != null && stat.getTotalCount() > 0)
                .toList();
    }

    private List<GpuSpecVO> convertToSpecVO(List<GpuResourceSpecEntity> list) {
        if (ObjectUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        return list.stream().map(entity -> {
            GpuSpecVO vo = new GpuSpecVO();
            BeanUtils.copyProperties(entity, vo);
            if (ObjectUtils.isNotEmpty(entity.getTags())) {
                vo.setTags(JSON.parseArray(entity.getTags(), String.class));
            }
            return vo;
        }).toList();
    }
}
