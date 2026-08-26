package com.lingyang.cloud.service;

import com.lingyang.cloud.entity.GpuResourceStockEntity;

import java.util.List;

/**
 * GPU资源库存服务接口
 * @author Claude
 * @Date: 2025/05/13
 */
public interface GpuResourceStockService {

    /**
     * 根据资源ID查询库存
     * @param resourceId 资源ID
     * @return 库存实体
     */
    GpuResourceStockEntity getByResourceId(Long resourceId);

    /**
     * 保存或更新库存
     * @param resourceId 资源ID
     * @param availableCount 可用数量
     * @param totalCount 总数量
     */
    void saveOrUpdate(Long resourceId, Integer availableCount, Integer totalCount);

    /**
     * 更新可用数量
     * @param resourceId 资源ID
     * @param delta 变化量（正数增加，负数减少）
     * @return 是否成功
     */
    boolean updateAvailableCount(Long resourceId, int delta);

    /**
     * 删除库存
     * @param resourceId 资源ID
     */
    void deleteByResourceId(Long resourceId);

    /**
     * 获取所有库存统计
     * @return 库存列表
     */
    List<GpuResourceStockEntity> listAll();
}
