package com.lingyang.cloud.service;

import com.lingyang.cloud.entity.SysNetworkProductEntity;
import com.lingyang.cloud.model.query.home.SysHomeEcsQuery;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;

public interface SysNetworkProductService {
    Result<PageResult<SysNetworkProductEntity>> getPage(PageQuery<SysHomeEcsQuery> build);

    SysNetworkProductEntity getById(Long id);

    void save(SysNetworkProductEntity entity);

    void update(SysNetworkProductEntity entity);
}
