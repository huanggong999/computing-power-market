package com.lingyang.cloud.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.entity.SysApplyEntity;
import com.lingyang.cloud.entity.SysApplyTypeEntity;
import com.lingyang.cloud.mapper.SysApplyMapper;
import com.lingyang.cloud.mapper.SysApplyTypeMapper;
import com.lingyang.cloud.model.query.apply.SysApplyQuery;
import com.lingyang.cloud.service.SysApplyService;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.utils.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class SysApplyServiceImpl implements SysApplyService {

    @Autowired
    private SysApplyMapper sysApplyMapper;

    @Autowired
    private SysApplyTypeMapper sysApplyTypeMapper;

    @Override
    public Result<PageResult<SysApplyEntity>> getPage(PageQuery<SysApplyQuery> pageQuery) {
        List<SysApplyTypeEntity> typeEntities = sysApplyTypeMapper.selectList(new LambdaQueryWrapper<>());
        SysApplyQuery query = pageQuery.getQuery();
        pageQuery.startPage();
        LambdaQueryWrapper<SysApplyEntity> queryWrapper = Wrappers.lambdaQuery(SysApplyEntity.class)
                .eq(query.getTypeId() != null, SysApplyEntity::getTypeId, query.getTypeId())
                .eq(query.getStatus() != null, SysApplyEntity::getStatus, query.getStatus())
                .like(StringUtils.isNotBlank(query.getName()), SysApplyEntity::getName, query.getName())
                .like(StringUtils.isNotBlank(query.getPublishUserName()), SysApplyEntity::getPublishUserName, query.getPublishUserName())
                .orderByAsc(SysApplyEntity::getSort);
        return Result.success(Optional.of(sysApplyMapper.selectList(queryWrapper))
                .flatMap(entities -> {

                    PageResult<SysApplyEntity> result = PageResult.of(entities);
                    for (SysApplyEntity sysApply : result.getList()) {
                        sysApply.setTypeName(typeEntities.stream()
                                .filter(type -> type.getId().equals(sysApply.getTypeId()))
                                .map(SysApplyTypeEntity::getName)
                                .findFirst()
                                .orElse("")
                        );
                    }
                    return result;
                })
                .orElseGet(() -> {
                    return PageResult.of(List.of());
                }));
    }

    @Override
    public Boolean save(SysApplyEntity entity) {
        return sysApplyMapper.insert(entity) > 0;
    }

    @Override
    public Boolean update(SysApplyEntity entity) {
        return sysApplyMapper.updateById(entity) > 0;
    }

    @Override
    public SysApplyEntity getById(Long id) {
        SysApplyEntity sysApply = sysApplyMapper.selectById(id);
        SysApplyTypeEntity type = sysApplyTypeMapper.selectById(sysApply.getTypeId());
        sysApply.setTypeTypes(Arrays.asList(type.getParentId(), type.getId()));
        return sysApply;
    }


}
