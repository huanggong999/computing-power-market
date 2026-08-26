package com.lingyang.cloud.service;

import com.lingyang.cloud.entity.GpuResourcePriceEntity;
import com.lingyang.cloud.model.edit.gpu.GpuPriceEdit;

import java.util.List;

/**
 * GPU资源价格服务接口
 * @author Claude
 * @Date: 2025/05/13
 */
public interface GpuResourcePriceService {

    /**
     * 根据资源ID查询价格列表
     * @param resourceId 资源ID
     * @return 价格列表
     */
    List<GpuResourcePriceEntity> listByResourceId(Long resourceId);

    /**
     * 根据资源ID和计费方式查询价格
     * @param resourceId 资源ID
     * @param billingType 计费方式
     * @return 价格实体
     */
    GpuResourcePriceEntity getByResourceAndBilling(Long resourceId, String billingType);

    /**
     * 批量保存价格
     * @param resourceId 资源ID
     * @param prices 价格列表
     */
    void batchSave(Long resourceId, List<GpuPriceEdit> prices);

    /**
     * 删除资源的所有价格
     * @param resourceId 资源ID
     */
    void deleteByResourceId(Long resourceId);
}
