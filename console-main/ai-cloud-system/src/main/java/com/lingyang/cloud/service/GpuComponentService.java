package com.lingyang.cloud.service;

import com.lingyang.cloud.model.edit.gpu.GpuComponentEdit;
import com.lingyang.cloud.model.query.gpu.GpuComponentQuery;
import com.lingyang.cloud.model.vo.system.GpuComponentVO;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;

/**
 * GPU基础组件镜像服务接口
 */
public interface GpuComponentService {

    /**
     * 分页查询GPU基础组件镜像
     */
    PageResult<GpuComponentVO> getComponentPage(GpuComponentQuery query, PageQuery pageQuery);

    /**
     * 新增或编辑GPU基础组件镜像
     */
    void saveOrUpdate(GpuComponentEdit edit);

    /**
     * 修改GPU基础组件镜像状态
     */
    void updateStatus(Long id, Integer status);
}
