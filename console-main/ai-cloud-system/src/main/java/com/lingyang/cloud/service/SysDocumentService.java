package com.lingyang.cloud.service;

import com.lingyang.cloud.entity.SysDocument;
import com.lingyang.cloud.model.query.home.SysDocumentQuery;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;

public interface SysDocumentService {
    Result<PageResult<SysDocument>> getPage(PageQuery<SysDocumentQuery> build);
}
