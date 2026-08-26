package com.lingyang.cloud.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.entity.SysEcsEntity;
import com.lingyang.cloud.entity.SysNetworkFormEntity;
import com.lingyang.cloud.entity.SysNetworkFormEntity;
import com.lingyang.cloud.mapper.SysNetworkFormMapper;
import com.lingyang.cloud.model.query.home.SysHomeEcsQuery;
import com.lingyang.cloud.service.SysNetworkFormService;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.utils.Optional;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysNetworkFormServiceImpl implements SysNetworkFormService {
    @Autowired
    private SysNetworkFormMapper sysNetworkFormMapper;


    @Override
    public Result<PageResult<SysNetworkFormEntity>> getPage(PageQuery<SysHomeEcsQuery> pageQuery) {
        SysHomeEcsQuery query = pageQuery.getQuery();
        pageQuery.startPage();
        LambdaQueryWrapper<SysNetworkFormEntity> queryWrapper = Wrappers.lambdaQuery(SysNetworkFormEntity.class)
                .eq(query.getStatus() != null, SysNetworkFormEntity::getStatus, query.getStatus())
                .eq(query.getFormType() != null, SysNetworkFormEntity::getFormType, query.getFormType())
                .like(StringUtils.isNotBlank(query.getName()), SysNetworkFormEntity::getName, query.getName())
                .orderByDesc(SysNetworkFormEntity::getCreateTime);
        List<SysNetworkFormEntity> value = sysNetworkFormMapper.selectList(queryWrapper);
        return Result.success(Optional.of(value)
                .flatMap(entities -> {
                    PageResult<SysNetworkFormEntity> result = PageResult.of(entities);
                    return result;
                })
                .orElseGet(() -> {
                    return PageResult.of(List.of());
                }));
    }

    @Override
    public SysNetworkFormEntity getById(Long id) {
        return sysNetworkFormMapper.selectById(id);
    }

    @Override
    public void save(SysNetworkFormEntity entity) {
        sysNetworkFormMapper.insert(entity);
    }

    @Override
    public void update(SysNetworkFormEntity entity) {
        sysNetworkFormMapper.updateById(entity);
    }
}
