package com.lingyang.cloud.service;

import com.lingyang.cloud.entity.SysNetworkFormEntity;
import com.lingyang.cloud.model.query.home.SysHomeEcsQuery;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;

public interface SysNetworkFormService {
    Result<PageResult<SysNetworkFormEntity>> getPage(PageQuery<SysHomeEcsQuery> build);

    SysNetworkFormEntity getById(Long id);

    void save(SysNetworkFormEntity entity);

    void update(SysNetworkFormEntity entity);
}
