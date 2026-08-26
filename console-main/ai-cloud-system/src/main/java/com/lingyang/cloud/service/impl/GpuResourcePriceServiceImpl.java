package com.lingyang.cloud.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.entity.GpuResourcePriceEntity;
import com.lingyang.cloud.mapper.GpuResourcePriceMapper;
import com.lingyang.cloud.model.edit.gpu.GpuPriceEdit;
import com.lingyang.cloud.service.GpuResourcePriceService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * GPU资源价格服务实现
 * @author Claude
 * @Date: 2025/05/13
 */
@Service
@Slf4j
public class GpuResourcePriceServiceImpl implements GpuResourcePriceService {

    @Resource
    private GpuResourcePriceMapper gpuResourcePriceMapper;

    @Override
    public List<GpuResourcePriceEntity> listByResourceId(Long resourceId) {
        return gpuResourcePriceMapper.selectByResourceId(resourceId);
    }

    @Override
    public GpuResourcePriceEntity getByResourceAndBilling(Long resourceId, String billingType) {
        return gpuResourcePriceMapper.selectByResourceAndBilling(resourceId, billingType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSave(Long resourceId, List<GpuPriceEdit> prices) {
        if (prices == null || prices.isEmpty()) {
            return;
        }
        List<GpuResourcePriceEntity> insertList = new ArrayList<>();
        for (GpuPriceEdit edit : prices) {
            GpuResourcePriceEntity entity = new GpuResourcePriceEntity();
            entity.setResourceId(resourceId);
            entity.setBillingType(edit.getBillingType());
            entity.setUnitPrice(edit.getUnitPrice());
            entity.setDiscountPrice(edit.getDiscountPrice());
            entity.setDiscountRate(edit.getDiscountRate());
            entity.setCurrency("CNY");

            GpuResourcePriceEntity existing = getByResourceAndBilling(resourceId, edit.getBillingType());
            if (existing == null) {
                insertList.add(entity);
            } else {
                entity.setId(existing.getId());
                gpuResourcePriceMapper.updateById(entity);
            }
        }
        if (!insertList.isEmpty()) {
            gpuResourcePriceMapper.batchInsert(insertList);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByResourceId(Long resourceId) {
        LambdaQueryWrapper<GpuResourcePriceEntity> wrapper = Wrappers.lambdaQuery(GpuResourcePriceEntity.class)
                .eq(GpuResourcePriceEntity::getResourceId, resourceId);
        gpuResourcePriceMapper.delete(wrapper);
    }
}
