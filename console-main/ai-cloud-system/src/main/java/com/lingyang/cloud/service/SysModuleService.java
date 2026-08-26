package com.lingyang.cloud.service;

import com.lingyang.cloud.entity.SysApplyEntity;
import com.lingyang.cloud.entity.SysModuleEntity;
import com.lingyang.cloud.model.query.module.SysModuleQuery;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;

public interface SysModuleService {
    Result<PageResult<SysModuleEntity>> getPage(PageQuery<SysModuleQuery> build);

    SysModuleEntity getById(Long id);

    Boolean save(SysModuleEntity entity);

    Boolean update(SysModuleEntity entity);
}
