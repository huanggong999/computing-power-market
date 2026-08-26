package com.lingyang.cloud.service;

import com.lingyang.cloud.entity.SysContract;
import com.lingyang.cloud.model.dto.OrderContractDTO;
import com.lingyang.cloud.model.query.home.SysContractQuery;
import com.lingyang.cloud.utils.esign.EsignDemoException;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;

public interface SysContractService {
    Result<Void> orderContract(OrderContractDTO d) throws EsignDemoException;

    Result<PageResult<SysContract>> getPage(PageQuery<SysContractQuery> build);

    Result<PageResult<SysContract>> getAdminPage(PageQuery<SysContractQuery> build);
}
