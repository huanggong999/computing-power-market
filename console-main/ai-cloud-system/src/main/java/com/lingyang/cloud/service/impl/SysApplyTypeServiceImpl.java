package com.lingyang.cloud.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.entity.SysApplyTypeEntity;
import com.lingyang.cloud.entity.SysCustomerDiscountEntity;
import com.lingyang.cloud.entity.SysCustomerEntity;
import com.lingyang.cloud.enums.source.SourceTypeEnum;
import com.lingyang.cloud.enums.system.StatusEnum;
import com.lingyang.cloud.mapper.SysApplyTypeMapper;
import com.lingyang.cloud.model.query.customer.SysCustomerQuery;
import com.lingyang.cloud.model.vo.apply.SysApplyTypeListVO;
import com.lingyang.cloud.model.vo.customer.SysCustomerListVO;
import com.lingyang.cloud.service.SysApplyTypeService;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.utils.Optional;
import com.lingyang.common.core.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SysApplyTypeServiceImpl implements SysApplyTypeService {

    @Autowired
    private SysApplyTypeMapper sysApplyTypeMapper;

    @Override
    public Result<PageResult<SysApplyTypeEntity>> getPage(PageQuery<Object> pageQuery) {
        pageQuery.startPage();
        LambdaQueryWrapper<SysApplyTypeEntity> queryWrapper = Wrappers.lambdaQuery(SysApplyTypeEntity.class)
                .eq(SysApplyTypeEntity::getParentId, 0L)
                .orderByAsc(SysApplyTypeEntity::getSort);
        return Result.success(Optional.of(sysApplyTypeMapper.selectList(queryWrapper))
                .flatMap(entities -> {
                    PageResult<SysApplyTypeEntity> result = PageResult.of(entities);
                    for (SysApplyTypeEntity sysApplyTypeEntity : result.getList()) {
                        List<SysApplyTypeEntity> childrenList = sysApplyTypeMapper.selectList(
                                Wrappers.lambdaQuery(SysApplyTypeEntity.class)
                                        .eq(SysApplyTypeEntity::getParentId, sysApplyTypeEntity.getId())
                                        .orderByAsc(SysApplyTypeEntity::getSort)
                        );
                        sysApplyTypeEntity.setChildren(childrenList);
                    }
                    return result;
                })
                .orElseGet(() -> {
                    return PageResult.of(List.of());
                }));
    }

    @Override
    public Boolean save(SysApplyTypeEntity entity) {
        return sysApplyTypeMapper.insert(entity) > 0;
    }

    @Override
    public Boolean update(SysApplyTypeEntity entity) {
        return sysApplyTypeMapper.updateById(entity) > 0;
    }

    @Override
    public List<SysApplyTypeEntity> firstList() {
        return sysApplyTypeMapper.selectList(
                new LambdaQueryWrapper<SysApplyTypeEntity>()
                        .eq(SysApplyTypeEntity::getParentId, 0L)
                        .orderByAsc(SysApplyTypeEntity::getSort)
        );
    }

    @Override
    public List<SysApplyTypeListVO> getAll() {
        List<SysApplyTypeEntity> list = sysApplyTypeMapper.selectList(
                new LambdaQueryWrapper<SysApplyTypeEntity>()
                        .eq(SysApplyTypeEntity::getStatus, StatusEnum.OK.getCode())
                        .orderByAsc(SysApplyTypeEntity::getSort)
        );
        List<SysApplyTypeListVO> listVOS;
        if (CollectionUtils.isNotEmpty(list)) {
            listVOS = list.stream().filter(s -> {
                return s.getParentId().equals(0L);
            }).map(SysApplyTypeListVO::build).collect(Collectors.toList());
            for (SysApplyTypeListVO listVO : listVOS) {
                listVO.setChildrenList(list.stream().filter(s -> {
                    return s.getParentId().equals(listVO.getId());
                }).map(SysApplyTypeListVO::build).collect(Collectors.toList()));
            }
        } else {
            listVOS = new ArrayList<>(0);
        }
        return listVOS;
    }
}
