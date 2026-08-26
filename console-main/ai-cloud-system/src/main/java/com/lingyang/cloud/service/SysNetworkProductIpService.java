package com.lingyang.cloud.service;

import com.lingyang.cloud.entity.SysNetworkProductIpEntity;
import com.lingyang.cloud.model.query.home.SysProductIpQuery;
import com.lingyang.cloud.model.vo.product.SysProductIpExportVO;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;

/**
 * @author Administrator
 */
public interface SysNetworkProductIpService {
    Result<PageResult<SysNetworkProductIpEntity>> getPage(PageQuery<SysProductIpQuery> build);

    void save(SysNetworkProductIpEntity entity);

    void update(SysNetworkProductIpEntity entity);

    void handlerCustomerAgicIp(String messageContent);

    void export(SysProductIpExportVO vo);
}
