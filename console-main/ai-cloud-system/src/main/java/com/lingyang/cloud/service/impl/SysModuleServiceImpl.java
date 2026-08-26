package com.lingyang.cloud.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.entity.SysApplyEntity;
import com.lingyang.cloud.entity.SysApplyTypeEntity;
import com.lingyang.cloud.entity.SysModuleEntity;
import com.lingyang.cloud.mapper.SysModuleMapper;
import com.lingyang.cloud.model.query.apply.SysApplyQuery;
import com.lingyang.cloud.model.query.module.SysModuleQuery;
import com.lingyang.cloud.service.SysModuleService;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.utils.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class SysModuleServiceImpl implements SysModuleService {


    @Autowired
    private SysModuleMapper sysModuleMapper;


    @Override
    public Result<PageResult<SysModuleEntity>> getPage(PageQuery<SysModuleQuery> pageQuery) {
        SysModuleQuery query = pageQuery.getQuery();
        pageQuery.startPage();
        LambdaQueryWrapper<SysModuleEntity> queryWrapper = Wrappers.lambdaQuery(SysModuleEntity.class)
                .eq(query.getType() != null, SysModuleEntity::getType, query.getType())
                .eq(query.getStatus() != null, SysModuleEntity::getStatus, query.getStatus())
                .like(StringUtils.isNotBlank(query.getName()), SysModuleEntity::getName, query.getName())
                .like(StringUtils.isNotBlank(query.getPublishUserName()), SysModuleEntity::getPublishUserName, query.getPublishUserName())
                .orderByAsc(SysModuleEntity::getSort);
        return Result.success(Optional.of(sysModuleMapper.selectList(queryWrapper))
                .flatMap(entities -> {

                    PageResult<SysModuleEntity> result = PageResult.of(entities);

                    return result;
                })
                .orElseGet(() -> {
                    return PageResult.of(List.of());
                }));
    }

    @Override
    public SysModuleEntity getById(Long id) {
        SysModuleEntity entity = sysModuleMapper.selectById(id);
        return entity;
    }

    @Override
    public Boolean save(SysModuleEntity entity) {
        return sysModuleMapper.insert(entity) > 0;
    }

    @Override
    public Boolean update(SysModuleEntity entity) {
        return sysModuleMapper.updateById(entity) > 0;
    }
}
