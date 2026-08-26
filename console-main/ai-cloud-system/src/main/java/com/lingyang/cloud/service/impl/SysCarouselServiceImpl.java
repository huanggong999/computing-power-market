package com.lingyang.cloud.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.entity.SysApplyEntity;
import com.lingyang.cloud.entity.SysApplyTypeEntity;
import com.lingyang.cloud.entity.SysCarouselEntity;
import com.lingyang.cloud.enums.system.StatusEnum;
import com.lingyang.cloud.mapper.SysCarouselMapper;
import com.lingyang.cloud.model.query.apply.SysApplyQuery;
import com.lingyang.cloud.model.query.carousel.SysCarouselQuery;
import com.lingyang.cloud.service.SysCarouselService;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.utils.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysCarouselServiceImpl implements SysCarouselService {

    @Autowired
    private SysCarouselMapper sysCarouselMapper;

    @Override
    public Result<PageResult<SysCarouselEntity>> getPage(PageQuery<SysCarouselQuery> pageQuery) {
        SysCarouselQuery query = pageQuery.getQuery();
        pageQuery.startPage();
        LambdaQueryWrapper<SysCarouselEntity> queryWrapper = Wrappers.lambdaQuery(SysCarouselEntity.class)
                .eq(query.getEquipmentType() != null, SysCarouselEntity::getEquipmentType, query.getEquipmentType())
                .like(StringUtils.isNotBlank(query.getName()), SysCarouselEntity::getName, query.getName())
                .orderByAsc(SysCarouselEntity::getSort);
        return Result.success(Optional.of(sysCarouselMapper.selectList(queryWrapper))
                .flatMap(entities -> {
                    PageResult<SysCarouselEntity> result = PageResult.of(entities);
                    return result;
                })
                .orElseGet(() -> {
                    return PageResult.of(List.of());
                }));
    }

    @Override
    public SysCarouselEntity getById(Long id) {
        return sysCarouselMapper.selectById(id);
    }

    @Override
    public Boolean save(SysCarouselEntity entity) {
        return sysCarouselMapper.insert(entity) > 0;
    }

    @Override
    public Boolean update(SysCarouselEntity entity) {
        return sysCarouselMapper.updateById(entity) > 0;
    }

    @Override
    public List<SysCarouselEntity> list(Integer type) {
        return sysCarouselMapper.selectList(
                new LambdaQueryWrapper<SysCarouselEntity>()
                        .eq(SysCarouselEntity::getEquipmentType, 1)
                        .eq(SysCarouselEntity::getType, type)
                        .eq(SysCarouselEntity::getStatus, StatusEnum.OK.getCode())
                        .orderByAsc(SysCarouselEntity::getSort)
        );
    }
}
