package com.lingyang.cloud.service;

import com.lingyang.cloud.entity.SysExtend;
import com.lingyang.cloud.model.query.home.SysExtendQuery;
import com.lingyang.cloud.model.query.home.SysFirstExtendQuery;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;

public interface SysExtendService {
    Result<PageResult<SysExtend>> getPage(PageQuery<SysExtendQuery> build);

    Result<PageResult<SysExtend>> getFirstPage(PageQuery<SysFirstExtendQuery> build);
}
