package com.lingyang.cloud.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.entity.SysDocumentType;
import com.lingyang.cloud.mapper.SysDocumentTypeMapper;
import com.lingyang.cloud.service.SysDocumentTypeService;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysDocumentTypeServiceImpl implements SysDocumentTypeService {

    @Autowired
    private SysDocumentTypeMapper sysDocumentTypeMapper;


    @Override
    public Result<PageResult<SysDocumentType>> getPage(PageQuery<Object> pageQuery) {
        pageQuery.startPage();
        List<SysDocumentType> rootEntities = getSysDocumentTypes(0);
        PageResult<SysDocumentType> result = PageResult.of(rootEntities);
        return Result.success(result);
    }

    private List<SysDocumentType> getSysDocumentTypes(long val) {
        LambdaQueryWrapper<SysDocumentType> queryWrapper = Wrappers.lambdaQuery(SysDocumentType.class)
                .eq(SysDocumentType::getParentId, val)
                .orderByAsc(SysDocumentType::getSort);
        List<SysDocumentType> rootEntities = sysDocumentTypeMapper.selectList(queryWrapper);
        rootEntities.forEach(entity -> {
            List<SysDocumentType> children = findAllChildren(entity.getId());
            entity.setChildren(children); // 设置子节点
        });
        return rootEntities;
    }

    private List<SysDocumentType> findAllChildren(Long parentId) {
        return getSysDocumentTypes(parentId);
    }

    @Override
    public Result<List<SysDocumentType>> getTypeList() {
        List<SysDocumentType> rootEntities = getSysDocumentTypes(0);
        return Result.success(rootEntities);
    }
}
