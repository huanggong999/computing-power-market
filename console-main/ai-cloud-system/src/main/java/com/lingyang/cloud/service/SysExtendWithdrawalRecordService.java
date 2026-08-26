package com.lingyang.cloud.service;

import com.lingyang.cloud.entity.SysExtendWithdrawalRecord;
import com.lingyang.cloud.model.query.home.SysExtendWithQuery;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;

public interface SysExtendWithdrawalRecordService {

    Result<PageResult<SysExtendWithdrawalRecord>> getPage(PageQuery<SysExtendWithQuery> build);

}
