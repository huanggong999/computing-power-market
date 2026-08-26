package com.lingyang.cloud.service;

import com.lingyang.cloud.entity.GpuRegionEntity;
import com.lingyang.cloud.model.edit.gpu.GpuRegionEdit;
import com.lingyang.cloud.model.vo.system.GpuRegionVO;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;

import java.util.List;

/**
 * GPU地区服务接口
 * @author Claude
 * @Date: 2025/05/13
 */
public interface GpuRegionService {

    /**
     * 分页查询地区
     * @param pageQuery 分页参数
     * @param status 状态 1启用 0禁用
     * @param regionCode 地区编码
     * @param regionName 地区名称
     * @return 地区分页结果
     */
    PageResult<GpuRegionVO> getRegionPage(PageQuery pageQuery, Integer status, String regionCode, String regionName);

    /**
     * 查询所有启用的地区
     * @return 地区列表
     */
    List<GpuRegionVO> listAllEnabled();

    /**
     * 根据编码查询地区
     * @param regionCode 地区编码
     * @return 地区实体
     */
    GpuRegionEntity getByCode(String regionCode);

    /**
     * 根据ID查询地区
     * @param id 地区ID
     * @return 地区实体
     */
    GpuRegionEntity getById(Long id);

    /**
     * 保存或更新地区
     * @param edit 编辑参数
     */
    void saveOrUpdate(GpuRegionEdit edit);

    /**
     * 删除地区
     * @param id 地区ID
     */
    void deleteById(Long id);

    /**
     * 更新地区状态
     * @param id 地区ID
     * @param status 状态 1启用 0禁用
     */
    void updateStatus(Long id, Integer status);
}
