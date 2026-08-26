package com.lingyang.cloud.service;

import com.lingyang.cloud.entity.SysApplyTypeEntity;
import com.lingyang.cloud.model.vo.apply.SysApplyTypeListVO;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;

import java.util.List;

public interface SysApplyTypeService {
    Result<PageResult<SysApplyTypeEntity>> getPage(PageQuery<Object> build);

    Boolean save(SysApplyTypeEntity entity);

    Boolean update(SysApplyTypeEntity entity);

    List<SysApplyTypeEntity> firstList();

    List<SysApplyTypeListVO> getAll();
}
