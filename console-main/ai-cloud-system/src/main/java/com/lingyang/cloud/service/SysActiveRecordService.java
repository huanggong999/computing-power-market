package com.lingyang.cloud.service;

import com.lingyang.cloud.entity.SysActiveRecordEntity;
import com.lingyang.cloud.model.query.active.SysActiveRecordQuery;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/1/16 15:58
 */
public interface SysActiveRecordService {
    /**
     * 获取活动记录参与情况
     *
     * @param query
     * @return
     */
    Result<PageResult<SysActiveRecordEntity>> getPcActiveRecord(PageQuery<SysActiveRecordQuery> query);
}
