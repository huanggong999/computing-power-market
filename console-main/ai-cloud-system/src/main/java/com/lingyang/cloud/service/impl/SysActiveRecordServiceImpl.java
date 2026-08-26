package com.lingyang.cloud.service.impl;

import com.lingyang.cloud.entity.SysActiveRecordEntity;
import com.lingyang.cloud.mapper.SysActiveRecordMapper;
import com.lingyang.cloud.model.query.active.SysActiveRecordQuery;
import com.lingyang.cloud.service.SysActiveRecordService;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/1/16 15:23
 */
@Service
public class SysActiveRecordServiceImpl implements SysActiveRecordService {

    @Autowired
    private SysActiveRecordMapper sysActiveRecordMapper;

    @Override
    public Result<PageResult<SysActiveRecordEntity>> getPcActiveRecord(PageQuery<SysActiveRecordQuery> pageQuery) {
        SysActiveRecordQuery query = pageQuery.getQuery();
        pageQuery.startPage();
        List<SysActiveRecordEntity> value = sysActiveRecordMapper.getPcActiveRecord(query);
        PageResult<SysActiveRecordEntity> result = PageResult.of(value);
        return Result.success(result);
    }

}
