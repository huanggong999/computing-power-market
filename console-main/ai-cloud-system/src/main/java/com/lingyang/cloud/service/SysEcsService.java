package com.lingyang.cloud.service;

import com.lingyang.cloud.entity.SysEcsEntity;
import com.lingyang.cloud.model.query.esc.PcEcsQuery;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/1 16:06
 */
public interface SysEcsService {
    /**
     * 获取服务器资源列表
     * @param pageQuery 分页参数
     * @return 分页数据
     */
    PageResult<SysEcsEntity> getEcsPage(PageQuery<PcEcsQuery> pageQuery);

    void clearEcs();
}
