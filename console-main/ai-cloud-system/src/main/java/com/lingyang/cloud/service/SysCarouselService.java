package com.lingyang.cloud.service;

import com.lingyang.cloud.entity.SysCarouselEntity;
import com.lingyang.cloud.model.query.carousel.SysCarouselQuery;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;

import java.util.List;

public interface SysCarouselService {
    Result<PageResult<SysCarouselEntity>> getPage(PageQuery<SysCarouselQuery> build);

    SysCarouselEntity getById(Long id);

    Boolean save(SysCarouselEntity entity);

    Boolean update(SysCarouselEntity entity);

    List<SysCarouselEntity> list(Integer type);
}
