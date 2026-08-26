package com.lingyang.cloud.service;

import com.lingyang.cloud.entity.GpuResourceEntity;
import com.lingyang.cloud.model.dto.GpuResourceQueryParam;
import com.lingyang.cloud.model.edit.gpu.GpuResourceEdit;
import com.lingyang.cloud.model.query.gpu.GpuResourceQuery;
import com.lingyang.cloud.model.vo.pc.GpuMarketItemVO;
import com.lingyang.cloud.model.vo.system.GpuResourceVO;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;

import java.util.List;

/**
 * GPU资源服务接口
 * @author Claude
 * @Date: 2025/05/13
 */
public interface GpuResourceService {

    /**
     * 获取GPU市场列表（用户端）
     * @param queryParam 查询参数
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    PageResult<GpuMarketItemVO> getMarketList(GpuResourceQueryParam queryParam, PageQuery pageQuery);

    /**
     * 获取GPU资源管理列表（管理端）
     * @param query 查询参数
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    PageResult<GpuResourceVO> getResourcePage(GpuResourceQuery query, PageQuery pageQuery);

    /**
     * 根据ID查询资源详情
     * @param resourceId 资源ID
     * @return 资源实体
     */
    GpuResourceEntity getById(Long resourceId);

    /**
     * 保存或更新GPU资源
     * @param edit 编辑参数
     */
    void saveOrUpdate(GpuResourceEdit edit);

    /**
     * 更新资源状态
     * @param id 资源ID
     * @param status 状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 删除GPU资源
     * @param id 资源ID
     */
    void deleteById(Long id);

    /**
     * 根据规格ID查询资源列表
     * @param specId 规格ID
     * @return 资源列表
     */
    List<GpuResourceEntity> listBySpecId(Long specId);

    /**
     * 根据地区编码查询资源列表
     * @param regionCode 地区编码
     * @return 资源列表
     */
    List<GpuResourceEntity> listByRegionCode(String regionCode);

    List<GpuResourceEntity> listListedMarketResources();
}
