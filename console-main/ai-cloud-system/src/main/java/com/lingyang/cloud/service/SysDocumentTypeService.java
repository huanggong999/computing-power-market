package com.lingyang.cloud.service;

import com.lingyang.cloud.entity.SysDocumentType;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;

import java.util.List;

public interface SysDocumentTypeService {

    /**
     * 文档分类分页查询
     * @param build
     * @return
     */
    Result<PageResult<SysDocumentType>> getPage(PageQuery<Object> build);

    /**
     * 文档分类列表查询
     * @return
     */
    Result<List<SysDocumentType>> getTypeList();
}
