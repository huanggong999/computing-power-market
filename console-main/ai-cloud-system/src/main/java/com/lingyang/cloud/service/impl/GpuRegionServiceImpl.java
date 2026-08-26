package com.lingyang.cloud.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.entity.GpuRegionEntity;
import com.lingyang.cloud.mapper.GpuRegionMapper;
import com.lingyang.cloud.model.edit.gpu.GpuRegionEdit;
import com.lingyang.cloud.model.vo.system.GpuRegionVO;
import com.lingyang.cloud.service.GpuRegionService;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * GPU地区服务实现
 * @author Claude
 * @Date: 2025/05/13
 */
@Service
@Slf4j
public class GpuRegionServiceImpl implements GpuRegionService {

    @Resource
    private GpuRegionMapper gpuRegionMapper;

    @Override
    public PageResult<GpuRegionVO> getRegionPage(PageQuery pageQuery, Integer status, String regionCode, String regionName) {
        pageQuery.startPage();
        LambdaQueryWrapper<GpuRegionEntity> wrapper = Wrappers.lambdaQuery(GpuRegionEntity.class)
                .eq(status != null, GpuRegionEntity::getStatus, status)
                .like(ObjectUtils.isNotEmpty(regionCode), GpuRegionEntity::getRegionCode, regionCode)
                .like(ObjectUtils.isNotEmpty(regionName), GpuRegionEntity::getRegionName, regionName)
                .orderByAsc(GpuRegionEntity::getSortOrder);
        List<GpuRegionEntity> list = gpuRegionMapper.selectList(wrapper);
        if (ObjectUtils.isEmpty(list)) {
            return PageResult.of(new ArrayList<>());
        }
        List<GpuRegionVO> voList = list.stream().map(entity -> {
            GpuRegionVO vo = new GpuRegionVO();
            BeanUtils.copyProperties(entity, vo);
            return vo;
        }).toList();
        return PageResult.of(voList);
    }

    @Override
    public List<GpuRegionVO> listAllEnabled() {
        LambdaQueryWrapper<GpuRegionEntity> wrapper = Wrappers.lambdaQuery(GpuRegionEntity.class)
                .eq(GpuRegionEntity::getStatus, 1)
                .eq(GpuRegionEntity::getDelFlag, 0)
                .orderByAsc(GpuRegionEntity::getSortOrder);
        List<GpuRegionEntity> list = gpuRegionMapper.selectList(wrapper);
        if (ObjectUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        return list.stream().map(entity -> {
            GpuRegionVO vo = new GpuRegionVO();
            BeanUtils.copyProperties(entity, vo);
            return vo;
        }).toList();
    }

    @Override
    public GpuRegionEntity getByCode(String regionCode) {
        LambdaQueryWrapper<GpuRegionEntity> wrapper = Wrappers.lambdaQuery(GpuRegionEntity.class)
                .eq(GpuRegionEntity::getRegionCode, regionCode);
        return gpuRegionMapper.selectOne(wrapper);
    }

    @Override
    public GpuRegionEntity getById(Long id) {
        return gpuRegionMapper.selectById(id);
    }

    @Override
    public void saveOrUpdate(GpuRegionEdit edit) {
        GpuRegionEntity entity = new GpuRegionEntity();
        BeanUtils.copyProperties(edit, entity);
        if (edit.getId() == null) {
            gpuRegionMapper.insert(entity);
        } else {
            gpuRegionMapper.updateById(entity);
        }
    }

    @Override
    public void deleteById(Long id) {
        gpuRegionMapper.deleteById(id);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        GpuRegionEntity entity = new GpuRegionEntity();
        entity.setId(id);
        entity.setStatus(status);
        gpuRegionMapper.updateById(entity);
    }
}
