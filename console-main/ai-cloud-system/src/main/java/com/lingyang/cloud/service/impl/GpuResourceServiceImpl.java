package com.lingyang.cloud.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.entity.*;
import com.lingyang.cloud.mapper.*;
import com.lingyang.cloud.model.dto.GpuResourceQueryParam;
import com.lingyang.cloud.model.edit.gpu.GpuResourceEdit;
import com.lingyang.cloud.model.query.gpu.GpuResourceQuery;
import com.lingyang.cloud.model.vo.pc.GpuMarketItemVO;
import com.lingyang.cloud.model.vo.system.GpuResourceVO;
import com.lingyang.cloud.service.GpuResourcePriceService;
import com.lingyang.cloud.service.GpuResourceService;
import com.lingyang.cloud.service.GpuResourceStockService;
import com.lingyang.cloud.service.GpuRegionService;
import com.lingyang.cloud.service.GpuResourceSpecService;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * GPU资源服务实现
 * @author Claude
 * @Date: 2025/05/13
 */
@Service
@Slf4j
public class GpuResourceServiceImpl implements GpuResourceService {

    @Resource
    private GpuResourceMapper gpuResourceMapper;

    @Resource
    private GpuResourceSpecMapper gpuResourceSpecMapper;

    @Resource
    private GpuResourcePriceMapper gpuResourcePriceMapper;

    @Resource
    private GpuResourceStockMapper gpuResourceStockMapper;

    @Resource
    private GpuResourceComponentMapper gpuResourceComponentMapper;

    @Resource
    private GpuRegionMapper gpuRegionMapper;

    @Resource
    private GpuZoneMapper gpuZoneMapper;

    @Resource
    private GpuResourcePriceService gpuResourcePriceService;

    @Resource
    private GpuResourceStockService gpuResourceStockService;

    @Resource
    private GpuRegionService gpuRegionService;

    @Resource
    private GpuResourceSpecService gpuResourceSpecService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public PageResult<GpuMarketItemVO> getMarketList(GpuResourceQueryParam queryParam, PageQuery pageQuery) {
        pageQuery.startPage();
        List<GpuResourceEntity> list = gpuResourceMapper.selectGpuMarketList(queryParam);

        if (ObjectUtils.isEmpty(list)) {
            return PageResult.of(new ArrayList<>());
        }

        List<GpuMarketItemVO> voList = list.stream().map(entity -> {
            GpuMarketItemVO vo = new GpuMarketItemVO();
            vo.setResourceId(entity.getId());
            vo.setResourceNo(entity.getResourceNo());
            vo.setMachineId(entity.getMachineId());
            vo.setMachineUuid(entity.getMachineUuid());
            vo.setRegionCode(entity.getRegionCode());
            vo.setZoneCode(entity.getZoneCode());
            vo.setRentableUntil(entity.getRentableUntil() != null ? entity.getRentableUntil().format(DATE_FORMATTER) : null);
            vo.setCacheOptimized(entity.getCacheOptimized() != null && entity.getCacheOptimized() == 1);
            vo.setCpuCores(entity.getCpuCores());
            vo.setCpuModel(entity.getCpuModel());
            vo.setMemory(entity.getMemorySize());
            vo.setSystemDisk(entity.getSystemDisk());
            vo.setDataDisk(entity.getDataDisk());
            vo.setExpandable(entity.getExpandable());
            vo.setGpuDriver(entity.getGpuDriver());
            vo.setCudaVersion(entity.getCudaVersion());
            return vo;
        }).toList();
        PageResult<GpuMarketItemVO> pageResult = PageResult.of(voList);

        List<Long> resourceIds = list.stream().map(GpuResourceEntity::getId).toList();
        List<Long> specIds = list.stream().map(GpuResourceEntity::getSpecId).distinct().toList();
        List<String> regionCodes = list.stream().map(GpuResourceEntity::getRegionCode).distinct().toList();
        List<String> zoneCodes = list.stream()
                .map(GpuResourceEntity::getZoneCode)
                .filter(code -> code != null && !code.isEmpty())
                .distinct()
                .toList();

        Map<Long, GpuResourceEntity> resourceMap = list.stream()
                .collect(Collectors.toMap(GpuResourceEntity::getId, r -> r));
        Map<Long, GpuResourceSpecEntity> specMap = gpuResourceSpecMapper.selectBatchIds(specIds)
                .stream().collect(Collectors.toMap(GpuResourceSpecEntity::getId, s -> s));
        Map<Long, GpuResourceStockEntity> stockMap = resourceIds.stream()
                .map(gpuResourceStockMapper::selectByResourceId)
                .filter(s -> s != null)
                .collect(Collectors.toMap(GpuResourceStockEntity::getResourceId, s -> s));
        Map<String, GpuRegionEntity> regionMap = regionCodes.stream()
                .map(code -> {
                    LambdaQueryWrapper<GpuRegionEntity> regionWrapper = Wrappers.lambdaQuery(GpuRegionEntity.class)
                            .eq(GpuRegionEntity::getRegionCode, code);
                    return gpuRegionMapper.selectOne(regionWrapper);
                })
                .filter(r -> r != null)
                .collect(Collectors.toMap(GpuRegionEntity::getRegionCode, r -> r));
        Map<String, GpuZoneEntity> zoneMap = zoneCodes.stream()
                .map(code -> {
                    LambdaQueryWrapper<GpuZoneEntity> zoneWrapper = Wrappers.lambdaQuery(GpuZoneEntity.class)
                            .eq(GpuZoneEntity::getZoneCode, code);
                    return gpuZoneMapper.selectOne(zoneWrapper);
                })
                .filter(z -> z != null)
                .collect(Collectors.toMap(GpuZoneEntity::getZoneCode, z -> z));

        Map<Long, GpuResourcePriceEntity> priceMap = new java.util.HashMap<>();
        for (Long rid : resourceIds) {
            GpuResourcePriceEntity price;
            if (ObjectUtils.isNotEmpty(queryParam.getBillingType())) {
                price = gpuResourcePriceMapper.selectByResourceAndBilling(rid, queryParam.getBillingType());
                if (price == null && "on_demand".equals(queryParam.getBillingType())) {
                    price = gpuResourcePriceMapper.selectByResourceAndBilling(rid, "hourly");
                }
            } else {
                List<GpuResourcePriceEntity> prices = gpuResourcePriceMapper.selectByResourceId(rid);
                price = prices.isEmpty() ? null : prices.get(0);
            }
            if (price != null) {
                priceMap.put(rid, price);
            }
        }

        pageResult.getList().forEach(vo -> {
            GpuResourceEntity entity = resourceMap.get(vo.getResourceId());
            if (entity == null) {
                return;
            }

            GpuResourceSpecEntity spec = specMap.get(entity.getSpecId());
            if (spec != null) {
                vo.setModel(spec.getModel());
                vo.setVram(spec.getVram());
            }

            GpuRegionEntity region = regionMap.get(entity.getRegionCode());
            if (region != null) {
                vo.setRegion(region.getRegionName());
            }

            GpuZoneEntity zone = zoneMap.get(entity.getZoneCode());
            if (zone != null) {
                vo.setZone(zone.getZoneName());
            }

            GpuResourceStockEntity stock = stockMap.get(entity.getId());
            if (stock != null) {
                vo.setAvailableCount(stock.getAvailableCount());
                vo.setTotalCount(stock.getTotalCount());
                vo.setRentableCount(stock.getAvailableCount());
            } else {
                vo.setAvailableCount(0);
                vo.setTotalCount(0);
                vo.setRentableCount(0);
            }

            GpuResourcePriceEntity price = priceMap.get(entity.getId());
            if (price != null) {
                vo.setPrice(price.getUnitPrice() != null ? price.getUnitPrice().toPlainString() : null);
                vo.setDiscountPrice(isValidDiscountPrice(price) ? price.getDiscountPrice().toPlainString() : null);
                vo.setDiscountRate(price.getDiscountRate());
            }
        });

        return pageResult;
    }

    @Override
    public PageResult<GpuResourceVO> getResourcePage(GpuResourceQuery query, PageQuery pageQuery) {
        pageQuery.startPage();
        LambdaQueryWrapper<GpuResourceEntity> wrapper = Wrappers.lambdaQuery(GpuResourceEntity.class)
                .like(ObjectUtils.isNotEmpty(query.getResourceNo()), GpuResourceEntity::getResourceNo, query.getResourceNo())
                .eq(ObjectUtils.isNotEmpty(query.getSpecId()), GpuResourceEntity::getSpecId, query.getSpecId())
                .eq(ObjectUtils.isNotEmpty(query.getRegionCode()), GpuResourceEntity::getRegionCode, query.getRegionCode())
                .eq(ObjectUtils.isNotEmpty(query.getZoneCode()), GpuResourceEntity::getZoneCode, query.getZoneCode())
                .eq(ObjectUtils.isNotEmpty(query.getStatus()), GpuResourceEntity::getStatus, query.getStatus())
                .like(ObjectUtils.isNotEmpty(query.getMachineId()), GpuResourceEntity::getMachineId, query.getMachineId())
                .orderByDesc(GpuResourceEntity::getCreateTime);
        List<GpuResourceEntity> list = gpuResourceMapper.selectList(wrapper);
        return PageResult.of(convertToResourceVO(list));
    }

    @Override
    public GpuResourceEntity getById(Long resourceId) {
        return gpuResourceMapper.selectById(resourceId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdate(GpuResourceEdit edit) {
        GpuResourceEntity entity = new GpuResourceEntity();
        entity.setId(edit.getId());
        entity.setMachineId(edit.getMachineId());
        entity.setMachineUuid(edit.getMachineUuid());
        entity.setRegionCode(edit.getRegionCode());
        entity.setZoneCode(edit.getZoneCode());
        entity.setClusterId(edit.getClusterId());
        entity.setClusterName(edit.getClusterName());
        entity.setClusterNodeName(edit.getClusterNodeName());
        entity.setSpecId(edit.getSpecId());
        entity.setGpuCount(edit.getGpuCount());
        entity.setGpuDriver(edit.getGpuDriver());
        entity.setCudaVersion(edit.getCudaVersion());
        entity.setCacheOptimized(edit.getCacheOptimized() != null && edit.getCacheOptimized() ? 1 : 0);
        entity.setCpuCores(edit.getCpuCores());
        entity.setCpuModel(edit.getCpuModel());
        entity.setMemorySize(edit.getMemorySize());
        entity.setSystemDisk(edit.getSystemDisk());
        entity.setDataDisk(edit.getDataDisk());
        entity.setExpandable(edit.getExpandable());
        entity.setRentableUntil(edit.getRentableUntil());
        entity.setStatus(edit.getStatus());

        if (edit.getId() == null) {
            // 新增：生成资源编号
            entity.setResourceNo(generateResourceNo());
            gpuResourceMapper.insert(entity);
        } else {
            gpuResourceMapper.updateById(entity);
        }

        // 保存价格
        gpuResourcePriceService.batchSave(entity.getId(), edit.getPrices());

        // 保存库存
        gpuResourceStockService.saveOrUpdate(entity.getId(), edit.getAvailableCount(), edit.getTotalCount());

        // 保存组件
        saveResourceComponents(entity.getId(), edit.getComponents());
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        GpuResourceEntity entity = new GpuResourceEntity();
        entity.setId(id);
        entity.setStatus(status);
        gpuResourceMapper.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        gpuResourceMapper.deleteById(id);
        gpuResourcePriceService.deleteByResourceId(id);
        gpuResourceStockService.deleteByResourceId(id);
        gpuResourceComponentMapper.deleteByResourceId(id);
    }

    @Override
    public List<GpuResourceEntity> listBySpecId(Long specId) {
        LambdaQueryWrapper<GpuResourceEntity> wrapper = Wrappers.lambdaQuery(GpuResourceEntity.class)
                .eq(GpuResourceEntity::getSpecId, specId);
        return gpuResourceMapper.selectList(wrapper);
    }

    @Override
    public List<GpuResourceEntity> listByRegionCode(String regionCode) {
        LambdaQueryWrapper<GpuResourceEntity> wrapper = Wrappers.lambdaQuery(GpuResourceEntity.class)
                .eq(GpuResourceEntity::getRegionCode, regionCode);
        return gpuResourceMapper.selectList(wrapper);
    }

    @Override
    public List<GpuResourceEntity> listListedMarketResources() {
        LambdaQueryWrapper<GpuResourceEntity> wrapper = Wrappers.lambdaQuery(GpuResourceEntity.class)
                .eq(GpuResourceEntity::getStatus, 1)
                .eq(GpuResourceEntity::getDelFlag, 0);
        return gpuResourceMapper.selectList(wrapper).stream()
                .filter(resource -> gpuResourceStockMapper.selectByResourceId(resource.getId()) != null)
                .toList();
    }

    private String generateResourceNo() {
        return "RES-" + java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" +
                String.format("%03d", (int) (Math.random() * 1000));
    }

    private void saveResourceComponents(Long resourceId, List<GpuResourceEdit.GpuComponentItemEdit> components) {
        gpuResourceComponentMapper.deleteByResourceId(resourceId);
        if (ObjectUtils.isEmpty(components)) {
            return;
        }
        int index = 0;
        for (GpuResourceEdit.GpuComponentItemEdit item : components) {
            if (item == null || item.getComponentId() == null || ObjectUtils.isEmpty(item.getImageAddress())) {
                continue;
            }
            GpuResourceComponentEntity entity = new GpuResourceComponentEntity();
            entity.setResourceId(resourceId);
            entity.setComponentId(item.getComponentId());
            entity.setComponentName(item.getComponentName());
            entity.setBaseImage(item.getBaseImage());
            entity.setImageAddress(item.getImageAddress());
            entity.setSortOrder(item.getSortOrder() == null ? index : item.getSortOrder());
            gpuResourceComponentMapper.insert(entity);
            index++;
        }
    }

    private List<GpuResourceVO> convertToResourceVO(List<GpuResourceEntity> list) {
        if (ObjectUtils.isEmpty(list)) {
            return new ArrayList<>();
        }

        List<Long> resourceIds = list.stream().map(GpuResourceEntity::getId).toList();
        List<Long> specIds = list.stream().map(GpuResourceEntity::getSpecId).distinct().toList();
        List<String> regionCodes = list.stream().map(GpuResourceEntity::getRegionCode).distinct().toList();
        List<String> zoneCodes = list.stream().map(GpuResourceEntity::getZoneCode).filter(ObjectUtils::isNotEmpty).distinct().toList();

        Map<Long, GpuResourceSpecEntity> specMap = gpuResourceSpecMapper.selectBatchIds(specIds)
                .stream().collect(Collectors.toMap(GpuResourceSpecEntity::getId, s -> s));
        Map<Long, GpuResourceStockEntity> stockMap = resourceIds.stream()
                .map(gpuResourceStockMapper::selectByResourceId)
                .filter(s -> s != null)
                .collect(Collectors.toMap(GpuResourceStockEntity::getResourceId, s -> s));
        Map<String, GpuRegionEntity> regionMap = regionCodes.stream()
                .map(code -> {
                    LambdaQueryWrapper<GpuRegionEntity> regionWrapper = Wrappers.lambdaQuery(GpuRegionEntity.class)
                            .eq(GpuRegionEntity::getRegionCode, code);
                    return gpuRegionMapper.selectOne(regionWrapper);
                })
                .filter(r -> r != null)
                .collect(Collectors.toMap(GpuRegionEntity::getRegionCode, r -> r));
        Map<String, GpuZoneEntity> zoneMap = zoneCodes.stream()
                .map(code -> {
                    LambdaQueryWrapper<GpuZoneEntity> wrapper = Wrappers.lambdaQuery(GpuZoneEntity.class)
                            .eq(GpuZoneEntity::getZoneCode, code);
                    return gpuZoneMapper.selectOne(wrapper);
                })
                .filter(z -> z != null)
                .collect(Collectors.toMap(GpuZoneEntity::getZoneCode, z -> z));

        Map<Long, GpuResourcePriceEntity> priceMap = new java.util.HashMap<>();
        for (Long rid : resourceIds) {
            GpuResourcePriceEntity price = gpuResourcePriceMapper.selectByResourceAndBilling(rid, "hourly");
            if (price != null) {
                priceMap.put(rid, price);
            }
        }

        return list.stream().map(entity -> {
            GpuResourceVO vo = new GpuResourceVO();
            vo.setId(entity.getId());
            vo.setResourceNo(entity.getResourceNo());
            vo.setMachineId(entity.getMachineId());
            vo.setMachineUuid(entity.getMachineUuid());
            vo.setCpuCores(entity.getCpuCores());
            vo.setCpuModel(entity.getCpuModel());
            vo.setMemorySize(entity.getMemorySize());
            vo.setRentableUntil(entity.getRentableUntil() != null ? entity.getRentableUntil().format(DATE_FORMATTER) : null);
            vo.setStatus(entity.getStatus());

            GpuResourceSpecEntity spec = specMap.get(entity.getSpecId());
            if (spec != null) {
                vo.setModel(spec.getModel());
                vo.setVram(spec.getVram());
            }

            GpuRegionEntity region = regionMap.get(entity.getRegionCode());
            if (region != null) {
                vo.setRegionName(region.getRegionName());
            }

            if (ObjectUtils.isNotEmpty(entity.getZoneCode())) {
                GpuZoneEntity zone = zoneMap.get(entity.getZoneCode());
                if (zone != null) {
                    vo.setZoneName(zone.getZoneName());
                }
            }

            GpuResourceStockEntity stock = stockMap.get(entity.getId());
            if (stock != null) {
                vo.setAvailableCount(stock.getAvailableCount());
                vo.setTotalCount(stock.getTotalCount());
            }

            GpuResourcePriceEntity price = priceMap.get(entity.getId());
            if (price != null) {
                vo.setPrice(price.getUnitPrice() != null ? price.getUnitPrice().toPlainString() : null);
                vo.setDiscountPrice(isValidDiscountPrice(price) ? price.getDiscountPrice().toPlainString() : null);
            }

            return vo;
        }).toList();
    }

    private boolean isValidDiscountPrice(GpuResourcePriceEntity price) {
        return price.getUnitPrice() != null
                && price.getDiscountPrice() != null
                && price.getDiscountPrice().compareTo(BigDecimal.ZERO) > 0
                && price.getDiscountPrice().compareTo(price.getUnitPrice()) < 0;
    }
}
