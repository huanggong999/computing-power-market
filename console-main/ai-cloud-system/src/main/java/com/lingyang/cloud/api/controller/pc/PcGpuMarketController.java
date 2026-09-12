package com.lingyang.cloud.api.controller.pc;

import com.lingyang.cloud.entity.GpuRegionEntity;
import com.lingyang.cloud.entity.GpuResourceEntity;
import com.lingyang.cloud.entity.GpuResourcePriceEntity;
import com.lingyang.cloud.entity.GpuResourceSpecEntity;
import com.lingyang.cloud.entity.GpuResourceStockEntity;
import com.lingyang.cloud.entity.GpuZoneEntity;
import com.lingyang.cloud.client.GpuSchedulerApiClient;
import com.lingyang.cloud.config.GpuMarketProperties;
import com.lingyang.cloud.mapper.GpuResourcePriceMapper;
import com.lingyang.cloud.mapper.GpuResourceStockMapper;
import com.lingyang.cloud.model.dto.GpuResourceQueryParam;
import com.lingyang.cloud.model.vo.pc.*;
import com.lingyang.cloud.service.GpuRegionService;
import com.lingyang.cloud.service.GpuResourceService;
import com.lingyang.cloud.service.GpuResourceSpecService;
import com.lingyang.cloud.service.GpuZoneService;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.prepost.PreAuthorize;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * PC端算力市场控制器
 * @author Claude
 * @Date: 2025/05/13
 */
@RestController
@RequestMapping("/pc/gpu/market")
@Tag(name = "PC端-算力市场")
public class PcGpuMarketController {

    @Resource
    private GpuResourceService gpuResourceService;

    @Resource
    private GpuRegionService gpuRegionService;

    @Resource
    private GpuZoneService gpuZoneService;

    @Resource
    private GpuResourceSpecService gpuResourceSpecService;

    @Resource
    private GpuResourceStockMapper gpuResourceStockMapper;

    @Resource
    private GpuResourcePriceMapper gpuResourcePriceMapper;

    @Resource
    private GpuSchedulerApiClient gpuSchedulerApiClient;

    @Resource
    private GpuMarketProperties gpuMarketProperties;

    /**
     * GPU资源列表
     */
    @GetMapping("/list")
    @Operation(summary = "GPU资源列表")
    @PreAuthorize("permitAll()")
    public Result<PageResult<GpuMarketItemVO>> list(
            @RequestParam(required = false) @Parameter(description = "计费方式 on_demand/hourly/daily/weekly/monthly") String billingType,
            @RequestParam(required = false) @Parameter(description = "地区编码") String regionCode,
            @RequestParam(required = false) @Parameter(description = "专区编码") String zoneCode,
            @RequestParam(required = false) @Parameter(description = "GPU型号列表") List<String> gpuModels,
            @RequestParam(required = false) @Parameter(description = "GPU数量") Integer gpuCount,
            @RequestParam(required = false) @Parameter(description = "排序字段 price/vram/model") String sortBy,
            @RequestParam(required = false) @Parameter(description = "排序方向 asc/desc") String sortOrder,
            PageQuery pageQuery
    ) {
        GpuResourceQueryParam queryParam = new GpuResourceQueryParam();
        queryParam.setBillingType(billingType);
        queryParam.setRegionCode(regionCode);
        queryParam.setZoneCode(zoneCode);
        queryParam.setGpuModels(gpuModels);
        queryParam.setGpuCount(gpuCount);
        queryParam.setSortBy(sortBy);
        queryParam.setSortOrder(sortOrder);
        return getMarketList(queryParam, pageQuery);
    }

    @PostMapping("/list")
    @Operation(summary = "GPU资源列表")
    @PreAuthorize("permitAll()")
    public Result<PageResult<GpuMarketItemVO>> listPost(@RequestBody GpuResourceQueryParam queryParam) {
        PageQuery pageQuery = PageQuery.build(queryParam.getPageNo(), queryParam.getPageSize());
        return getMarketList(queryParam, pageQuery);
    }

    private Result<PageResult<GpuMarketItemVO>> getMarketList(GpuResourceQueryParam queryParam, PageQuery pageQuery) {
        List<String> gpuModels = queryParam.getGpuModels();
        if (gpuModels != null) {
            gpuModels = gpuModels.stream()
                    .flatMap(model -> Arrays.stream(model.split(",")))
                    .map(String::trim)
                    .filter(model -> !model.isEmpty())
                    .collect(Collectors.toList());
        }

        queryParam.setGpuModels(gpuModels);
        queryParam.setStatus(1);
        queryParam.setExcludedRegionCodes(gpuMarketProperties.getHiddenRegions());

        return Result.success(gpuResourceService.getMarketList(queryParam, pageQuery));
    }

    /**
     * 筛选条件元数据
     */
    @GetMapping("/meta")
    @Operation(summary = "筛选条件元数据")
    @PreAuthorize("permitAll()")
    public Result<GpuMarketMetaVO> meta() {
        GpuMarketMetaVO metaVO = new GpuMarketMetaVO();

        List<com.lingyang.cloud.entity.GpuResourceEntity> listedResources = gpuResourceService.listListedMarketResources();
        listedResources = listedResources.stream()
                .filter(resource -> !gpuMarketProperties.isHiddenRegion(resource.getRegionCode()))
                .toList();
        Set<String> regionCodes = listedResources.stream()
                .map(com.lingyang.cloud.entity.GpuResourceEntity::getRegionCode)
                .filter(code -> code != null && !code.isEmpty())
                .collect(Collectors.toCollection(LinkedHashSet::new));
        Set<String> zoneCodes = listedResources.stream()
                .map(com.lingyang.cloud.entity.GpuResourceEntity::getZoneCode)
                .filter(code -> code != null && !code.isEmpty())
                .collect(Collectors.toCollection(LinkedHashSet::new));

        Map<String, GpuRegionEntity> regionMap = regionCodes.stream()
                .map(gpuRegionService::getByCode)
                .filter(region -> region != null)
                .collect(Collectors.toMap(GpuRegionEntity::getRegionCode, Function.identity(), (left, right) -> left));
        List<GpuRegionItemVO> regions = regionCodes.stream()
                .map(regionMap::get)
                .filter(region -> region != null)
                .map(region -> {
                    GpuRegionItemVO vo = new GpuRegionItemVO();
                    vo.setRegionCode(region.getRegionCode());
                    vo.setRegionName(region.getRegionName());
                    return vo;
                }).toList();
        metaVO.setRegions(regions);

        Map<String, GpuZoneEntity> zoneMap = zoneCodes.stream()
                .map(gpuZoneService::getByCode)
                .filter(zone -> zone != null)
                .collect(Collectors.toMap(GpuZoneEntity::getZoneCode, Function.identity(), (left, right) -> left));
        List<GpuZoneItemVO> zones = zoneCodes.stream()
                .map(zoneMap::get)
                .filter(zone -> zone != null)
                .map(zone -> {
                    GpuZoneItemVO vo = new GpuZoneItemVO();
                    vo.setZoneCode(zone.getZoneCode());
                    vo.setZoneName(zone.getZoneName());
                    return vo;
                }).toList();
        metaVO.setZones(zones);

        // GPU型号统计
        metaVO.setGpuModels(gpuResourceSpecService.getModelStats());

        // GPU数量选项
        metaVO.setGpuCounts(listedResources.stream()
                .map(GpuResourceEntity::getGpuCount)
                .filter(count -> count != null && count > 0)
                .distinct()
                .sorted()
                .toList());

        // 计费方式
        Set<String> billingTypes = listedResources.stream()
                .flatMap(resource -> gpuResourcePriceMapper.selectByResourceId(resource.getId()).stream())
                .map(price -> price.getBillingType())
                .filter(type -> type != null && !type.isEmpty())
                .collect(Collectors.toCollection(LinkedHashSet::new));
        metaVO.setBillingTypes(billingTypes.stream()
                .map(type -> createBillingType(type, getBillingTypeName(type)))
                .toList());

        return Result.success(metaVO);
    }

    /**
     * 官网只读展示火山云 GPU 目录，不进入当前平台下单资源列表。
     */
    @GetMapping("/volcano/catalog")
    @Operation(summary = "火山云 GPU 目录（官网展示）")
    @PreAuthorize("permitAll()")
    public Result<com.alibaba.fastjson2.JSONObject> volcanoCatalog(
            @RequestParam(required = false) @Parameter(description = "计费方式 monthly/on_demand/hourly") String billingType) {
        return Result.success(gpuSchedulerApiClient.getCachedGpuCatalog(billingType));
    }

    /**
     * GPU资源详情
     */
    @GetMapping("/detail/{resourceId}")
    @Operation(summary = "GPU资源详情")
    @PreAuthorize("permitAll()")
    public Result<GpuResourceDetailVO> detail(@PathVariable Long resourceId) {
        GpuResourceEntity resource = gpuResourceService.getById(resourceId);
        if (resource == null || resource.getStatus() == null || resource.getStatus() != 1
                || gpuMarketProperties.isHiddenRegion(resource.getRegionCode())) {
            return Result.success(null);
        }

        GpuResourceDetailVO vo = new GpuResourceDetailVO();
        vo.setId(resource.getId());
        vo.setResourceId(resource.getId());
        vo.setResourceNo(resource.getResourceNo());
        vo.setMachineId(resource.getMachineId());
        vo.setMachineUuid(resource.getMachineUuid());
        vo.setRegionCode(resource.getRegionCode());
        vo.setZoneCode(resource.getZoneCode());
        vo.setSpecId(resource.getSpecId());
        vo.setGpuCount(resource.getGpuCount());
        vo.setGpuDriver(resource.getGpuDriver() != null ? resource.getGpuDriver() : "-");
        vo.setCudaVersion(resource.getCudaVersion() != null ? resource.getCudaVersion() : "-");
        vo.setCacheOptimized(resource.getCacheOptimized() != null && resource.getCacheOptimized() == 1);
        vo.setCpuCores(resource.getCpuCores());
        vo.setCpuModel(resource.getCpuModel());
        vo.setMemory(resource.getMemorySize());
        vo.setMemorySize(resource.getMemorySize());
        vo.setSystemDisk(resource.getSystemDisk());
        vo.setDataDisk(resource.getDataDisk());
        vo.setExpandable(resource.getExpandable());
        vo.setRentableUntil(resource.getRentableUntil() != null ? resource.getRentableUntil().toString() : null);
        vo.setStatus(resource.getStatus());

        GpuResourceSpecEntity spec = gpuResourceSpecService.getById(resource.getSpecId());
        if (spec != null) {
            vo.setModel(spec.getModel());
            vo.setVram(spec.getVram());
        }

        GpuRegionEntity region = gpuRegionService.getByCode(resource.getRegionCode());
        if (region != null) {
            vo.setRegion(region.getRegionName());
        }

        if (resource.getZoneCode() != null && !resource.getZoneCode().isEmpty()) {
            GpuZoneEntity zone = gpuZoneService.getByCode(resource.getZoneCode());
            if (zone != null) {
                vo.setZone(zone.getZoneName());
            }
        }

        GpuResourceStockEntity stock = gpuResourceStockMapper.selectByResourceId(resourceId);
        if (stock != null) {
            vo.setAvailableCount(stock.getAvailableCount());
            vo.setTotalCount(stock.getTotalCount());
        }

        vo.setPrices(gpuResourcePriceMapper.selectByResourceId(resourceId).stream().map(price -> {
            GpuResourceDetailVO.GpuPriceItemVO item = new GpuResourceDetailVO.GpuPriceItemVO();
            item.setBillingType(price.getBillingType());
            item.setBillingTypeName(getBillingTypeName(price.getBillingType()));
            item.setUnitPrice(price.getUnitPrice() != null ? price.getUnitPrice().toPlainString() : null);
            item.setDiscountPrice(isValidDiscountPrice(price) ? price.getDiscountPrice().toPlainString() : null);
            item.setDiscountRate(price.getDiscountRate());
            return item;
        }).toList());

        return Result.success(vo);
    }

    private boolean isValidDiscountPrice(GpuResourcePriceEntity price) {
        return price.getUnitPrice() != null
                && price.getDiscountPrice() != null
                && price.getDiscountPrice().compareTo(BigDecimal.ZERO) > 0
                && price.getDiscountPrice().compareTo(price.getUnitPrice()) < 0;
    }

    private GpuBillingTypeVO createBillingType(String code, String name) {
        GpuBillingTypeVO vo = new GpuBillingTypeVO();
        vo.setCode(code);
        vo.setName(name);
        return vo;
    }

    private String getBillingTypeName(String billingType) {
        return switch (billingType) {
            case "on_demand" -> "按量计费";
            case "hourly" -> "按小时";
            case "daily" -> "按天";
            case "weekly" -> "按周";
            case "monthly" -> "按月";
            default -> billingType;
        };
    }
}
