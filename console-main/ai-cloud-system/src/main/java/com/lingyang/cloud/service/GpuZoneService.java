package com.lingyang.cloud.service;

import com.lingyang.cloud.entity.GpuZoneEntity;
import com.lingyang.cloud.model.edit.gpu.GpuZoneEdit;
import com.lingyang.cloud.model.vo.system.GpuZoneVO;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;

import java.util.List;

/**
 * GPU专区服务接口
 * @author Claude
 * @Date: 2025/05/13
 */
public interface GpuZoneService {

    /**
     * 分页查询专区
     * @param pageQuery 分页参数
     * @param status 状态 1启用 0禁用
     * @param zoneCode 专区编码
     * @param zoneName 专区名称
     * @return 专区分页结果
     */
    PageResult<GpuZoneVO> getZonePage(PageQuery pageQuery, Integer status, String zoneCode, String zoneName);

    /**
     * 查询所有启用的专区
     * @return 专区列表
     */
    List<GpuZoneVO> listAllEnabled();

    /**
     * 根据编码查询专区
     * @param zoneCode 专区编码
     * @return 专区实体
     */
    GpuZoneEntity getByCode(String zoneCode);

    /**
     * 根据ID查询专区
     * @param id 专区ID
     * @return 专区实体
     */
    GpuZoneEntity getById(Long id);

    /**
     * 保存或更新专区
     * @param edit 编辑参数
     */
    void saveOrUpdate(GpuZoneEdit edit);

    /**
     * 删除专区
     * @param id 专区ID
     */
    void deleteById(Long id);

    /**
     * 更新专区状态
     * @param id 专区ID
     * @param status 状态 1启用 0禁用
     */
    void updateStatus(Long id, Integer status);
}
