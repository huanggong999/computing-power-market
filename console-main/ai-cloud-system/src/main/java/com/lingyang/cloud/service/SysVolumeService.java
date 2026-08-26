package com.lingyang.cloud.service;

import com.lingyang.cloud.entity.SysVolumeEntity;
import com.lingyang.cloud.model.query.volume.SysVolumeQuery;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/5 17:32
 */
public interface SysVolumeService {
    PageResult<SysVolumeEntity> getPageList(PageQuery<SysVolumeQuery> pageQuery);
}
