package com.lingyang.cloud.api.service.pc;

import com.lingyang.cloud.api.model.dto.PcBucketDetailDTO;
import com.lingyang.cloud.api.model.query.PcBucketListQuery;
import com.lingyang.cloud.api.model.vo.PcBucketCreateVO;
import com.lingyang.cloud.entity.SysCustomerBucketEntity;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/02/19 18:06
 */
public interface PcBucketService {

    /**
     * 获取客户桶列表
     * @param query
     * @return
     */
    PageResult<SysCustomerBucketEntity> list(PageQuery<PcBucketListQuery> query);

    Boolean create(PcBucketCreateVO vo);

    Boolean delete(String name);

    PcBucketDetailDTO detail(String name);

    Boolean check(String name);
}