package com.lingyang.cloud.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.entity.SysDocument;
import com.lingyang.cloud.entity.SysDocumentType;
import com.lingyang.cloud.entity.SysEcsEntity;
import com.lingyang.cloud.entity.SysHomeEcsEntity;
import com.lingyang.cloud.mapper.SysDocumentMapper;
import com.lingyang.cloud.mapper.SysDocumentTypeMapper;
import com.lingyang.cloud.model.query.home.SysDocumentQuery;
import com.lingyang.cloud.model.query.home.SysHomeEcsQuery;
import com.lingyang.cloud.service.SysDocumentService;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.utils.Optional;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysDocumentServiceImpl implements SysDocumentService {

    @Autowired
    private SysDocumentTypeMapper sysDocumentTypeMapper;

    @Autowired
    private SysDocumentMapper sysDocumentMapper;

    @Override
    public Result<PageResult<SysDocument>> getPage(PageQuery<SysDocumentQuery> pageQuery) {
        SysDocumentQuery query = pageQuery.getQuery();
        pageQuery.startPage();
        LambdaQueryWrapper<SysDocument> queryWrapper = Wrappers.lambdaQuery(SysDocument.class)
                .eq(query.getStatus() != null, SysDocument::getStatus, query.getStatus())
                .like(StringUtils.isNotBlank(query.getName()), SysDocument::getName, query.getName())
                .orderByDesc(SysDocument::getCreateTime);
        List<SysDocument> value = sysDocumentMapper.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(value)) {
            for (SysDocument sysDocument : value) {
                SysDocumentType documentType = sysDocumentTypeMapper.selectById(sysDocument.getTypeId());
                if (documentType != null) {
                    sysDocument.setTypeName(documentType.getName());
                }
            }
        }
        return Result.success(Optional.of(value)
                .flatMap(entities -> {

                    PageResult<SysDocument> result = PageResult.of(entities);

                    return result;
                })
                .orElseGet(() -> {
                    return PageResult.of(List.of());
                }));
    }
}
