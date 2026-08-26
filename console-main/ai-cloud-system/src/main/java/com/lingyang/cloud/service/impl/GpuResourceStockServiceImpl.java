package com.lingyang.cloud.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.entity.GpuResourceStockEntity;
import com.lingyang.cloud.mapper.GpuResourceStockMapper;
import com.lingyang.cloud.service.GpuResourceStockService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * GPU资源库存服务实现
 * @author Claude
 * @Date: 2025/05/13
 */
@Service
@Slf4j
public class GpuResourceStockServiceImpl implements GpuResourceStockService {

    @Resource
    private GpuResourceStockMapper gpuResourceStockMapper;

    @Override
    public GpuResourceStockEntity getByResourceId(Long resourceId) {
        return gpuResourceStockMapper.selectByResourceId(resourceId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdate(Long resourceId, Integer availableCount, Integer totalCount) {
        GpuResourceStockEntity existing = getByResourceId(resourceId);
        if (existing == null) {
            GpuResourceStockEntity entity = new GpuResourceStockEntity();
            entity.setResourceId(resourceId);
            entity.setAvailableCount(availableCount);
            entity.setTotalCount(totalCount);
            entity.setLastSyncTime(LocalDateTime.now());
            gpuResourceStockMapper.insert(entity);
        } else {
            GpuResourceStockEntity entity = new GpuResourceStockEntity();
            entity.setId(existing.getId());
            entity.setAvailableCount(availableCount);
            entity.setTotalCount(totalCount);
            entity.setLastSyncTime(LocalDateTime.now());
            gpuResourceStockMapper.updateById(entity);
        }
    }

    @Override
    public boolean updateAvailableCount(Long resourceId, int delta) {
        GpuResourceStockEntity stock = getByResourceId(resourceId);
        if (stock == null) {
            return false;
        }
        int newCount = stock.getAvailableCount() + delta;
        if (newCount < 0 || newCount > stock.getTotalCount()) {
            return false;
        }
        LambdaUpdateWrapper<GpuResourceStockEntity> wrapper = Wrappers.lambdaUpdate(GpuResourceStockEntity.class)
                .eq(GpuResourceStockEntity::getResourceId, resourceId)
                .set(GpuResourceStockEntity::getAvailableCount, newCount)
                .set(GpuResourceStockEntity::getLastSyncTime, LocalDateTime.now());
        return gpuResourceStockMapper.update(null, wrapper) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByResourceId(Long resourceId) {
        LambdaQueryWrapper<GpuResourceStockEntity> wrapper = Wrappers.lambdaQuery(GpuResourceStockEntity.class)
                .eq(GpuResourceStockEntity::getResourceId, resourceId);
        gpuResourceStockMapper.delete(wrapper);
    }

    @Override
    public List<GpuResourceStockEntity> listAll() {
        return gpuResourceStockMapper.selectList(null);
    }
}
