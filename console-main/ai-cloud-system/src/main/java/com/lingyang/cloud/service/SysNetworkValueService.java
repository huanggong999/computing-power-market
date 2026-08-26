package com.lingyang.cloud.service;

import com.lingyang.cloud.entity.SysNetworkValueEntity;
import com.lingyang.cloud.model.dto.SysNetworkProductValueDTO;
import com.lingyang.cloud.model.query.home.SysNetworkValueQuery;
import com.lingyang.cloud.model.vo.product.SysProductIpEnoughVO;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;

public interface SysNetworkValueService {
    Result<PageResult<SysNetworkValueEntity>> getPage(PageQuery<SysNetworkValueQuery> build);

    Result<PageResult<SysNetworkValueEntity>> getPcPage(PageQuery<SysNetworkValueQuery> build);

    SysNetworkProductValueDTO getProductDetail(Long id);

    Result<Boolean> stop(Long valueId);
    Result<Boolean> start(Long valueId);

    Result<Boolean> cancel(Long valueId);

    Result<Boolean> resetPassword(String email);

    Result<Boolean> ipEnough(SysProductIpEnoughVO vo);
}
