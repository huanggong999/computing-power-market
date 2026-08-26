package com.lingyang.cloud.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.entity.GpuZoneEntity;
import com.lingyang.cloud.mapper.GpuZoneMapper;
import com.lingyang.cloud.model.edit.gpu.GpuZoneEdit;
import com.lingyang.cloud.model.vo.system.GpuZoneVO;
import com.lingyang.cloud.service.GpuZoneService;
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
 * GPU专区服务实现
 * @author Claude
 * @Date: 2025/05/13
 */
@Service
@Slf4j
public class GpuZoneServiceImpl implements GpuZoneService {

    @Resource
    private GpuZoneMapper gpuZoneMapper;

    @Override
    public PageResult<GpuZoneVO> getZonePage(PageQuery pageQuery, Integer status, String zoneCode, String zoneName) {
        pageQuery.startPage();
        LambdaQueryWrapper<GpuZoneEntity> wrapper = Wrappers.lambdaQuery(GpuZoneEntity.class)
                .eq(status != null, GpuZoneEntity::getStatus, status)
                .like(ObjectUtils.isNotEmpty(zoneCode), GpuZoneEntity::getZoneCode, zoneCode)
                .like(ObjectUtils.isNotEmpty(zoneName), GpuZoneEntity::getZoneName, zoneName)
                .orderByAsc(GpuZoneEntity::getSortOrder);
        List<GpuZoneEntity> list = gpuZoneMapper.selectList(wrapper);
        if (ObjectUtils.isEmpty(list)) {
            return PageResult.of(new ArrayList<>());
        }
        List<GpuZoneVO> voList = list.stream().map(entity -> {
            GpuZoneVO vo = new GpuZoneVO();
            BeanUtils.copyProperties(entity, vo);
            return vo;
        }).toList();
        return PageResult.of(voList);
    }

    @Override
    public List<GpuZoneVO> listAllEnabled() {
        LambdaQueryWrapper<GpuZoneEntity> wrapper = Wrappers.lambdaQuery(GpuZoneEntity.class)
                .eq(GpuZoneEntity::getStatus, 1)
                .eq(GpuZoneEntity::getDelFlag, 0)
                .orderByAsc(GpuZoneEntity::getSortOrder);
        List<GpuZoneEntity> list = gpuZoneMapper.selectList(wrapper);
        if (ObjectUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        return list.stream().map(entity -> {
            GpuZoneVO vo = new GpuZoneVO();
            BeanUtils.copyProperties(entity, vo);
            return vo;
        }).toList();
    }

    @Override
    public GpuZoneEntity getByCode(String zoneCode) {
        LambdaQueryWrapper<GpuZoneEntity> wrapper = Wrappers.lambdaQuery(GpuZoneEntity.class)
                .eq(GpuZoneEntity::getZoneCode, zoneCode);
        return gpuZoneMapper.selectOne(wrapper);
    }

    @Override
    public GpuZoneEntity getById(Long id) {
        return gpuZoneMapper.selectById(id);
    }

    @Override
    public void saveOrUpdate(GpuZoneEdit edit) {
        GpuZoneEntity entity = new GpuZoneEntity();
        BeanUtils.copyProperties(edit, entity);
        if (edit.getId() == null) {
            gpuZoneMapper.insert(entity);
        } else {
            gpuZoneMapper.updateById(entity);
        }
    }

    @Override
    public void deleteById(Long id) {
        gpuZoneMapper.deleteById(id);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        GpuZoneEntity entity = new GpuZoneEntity();
        entity.setId(id);
        entity.setStatus(status);
        gpuZoneMapper.updateById(entity);
    }
}
