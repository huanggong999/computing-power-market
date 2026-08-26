package com.lingyang.cloud.service;

import com.lingyang.cloud.entity.SysApplyEntity;
import com.lingyang.cloud.model.query.apply.SysApplyQuery;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;

public interface SysApplyService {
    Result<PageResult<SysApplyEntity>> getPage(PageQuery<SysApplyQuery> build);

    Boolean save(SysApplyEntity entity);

    Boolean update(SysApplyEntity entity);

    SysApplyEntity getById(Long id);
}
