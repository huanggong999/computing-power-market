package com.lingyang.cloud.service;

import com.lingyang.cloud.entity.SysRemit;
import com.lingyang.cloud.model.dto.SysRemitQuery;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;

public interface SysRemitService {
    Result<PageResult<SysRemit>> getPage(PageQuery<SysRemitQuery> build);
}
