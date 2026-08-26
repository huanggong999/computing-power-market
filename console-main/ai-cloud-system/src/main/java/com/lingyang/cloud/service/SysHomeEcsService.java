package com.lingyang.cloud.service;

import com.lingyang.cloud.entity.SysHomeEcsEntity;
import com.lingyang.cloud.model.query.home.SysHomeEcsQuery;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;

public interface SysHomeEcsService {


    SysHomeEcsEntity getById(Long id);

    void save(SysHomeEcsEntity entity);

    void update(SysHomeEcsEntity entity);

    Result<PageResult<SysHomeEcsEntity>> getPage(PageQuery<SysHomeEcsQuery> build);

    Result<PageResult<SysHomeEcsEntity>> getHomePage(PageQuery<SysHomeEcsQuery> build);
}
