package com.lingyang.cloud.service;

import com.lingyang.cloud.entity.GpuResourceSpecEntity;
import com.lingyang.cloud.model.edit.gpu.GpuSpecEdit;
import com.lingyang.cloud.model.vo.pc.GpuModelStatVO;
import com.lingyang.cloud.model.vo.system.GpuSpecVO;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;

import java.util.List;

/**
 * GPU规格服务接口
 * @author Claude
 * @Date: 2025/05/13
 */
public interface GpuResourceSpecService {

    /**
     * 分页查询规格列表
     * @param pageQuery 分页参数
     * @param model 型号筛选
     * @param status 状态筛选
     * @return 分页结果
     */
    PageResult<GpuSpecVO> getSpecPage(PageQuery pageQuery, String model, Integer status);

    /**
     * 查询所有启用的规格
     * @return 规格列表
     */
    List<GpuResourceSpecEntity> listAllEnabled();

    /**
     * 根据ID查询规格
     * @param id 规格ID
     * @return 规格实体
     */
    GpuResourceSpecEntity getById(Long id);

    /**
     * 根据型号查询规格
     * @param model 型号
     * @return 规格实体
     */
    GpuResourceSpecEntity getByModel(String model);

    /**
     * 保存或更新规格
     * @param edit 编辑参数
     */
    void saveOrUpdate(GpuSpecEdit edit);

    /**
     * 删除规格
     * @param id 规格ID
     */
    void deleteById(Long id);

    /**
     * 更新规格状态
     * @param id 规格ID
     * @param status 状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 获取GPU型号统计（用于筛选面板）
     * @return 型号统计列表
     */
    List<GpuModelStatVO> getModelStats();
}
