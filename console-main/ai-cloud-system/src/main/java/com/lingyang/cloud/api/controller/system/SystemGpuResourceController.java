package com.lingyang.cloud.api.controller.system;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.lingyang.cloud.client.GpuSchedulerApiClient;
import com.lingyang.cloud.entity.*;
import com.lingyang.cloud.mapper.GpuResourceComponentMapper;
import com.lingyang.cloud.mapper.GpuResourcePriceMapper;
import com.lingyang.cloud.mapper.GpuResourceStockMapper;
import com.lingyang.cloud.model.edit.gpu.*;
import com.lingyang.cloud.model.query.gpu.*;
import com.lingyang.cloud.model.vo.pc.GpuResourceDetailVO;
import com.lingyang.cloud.model.vo.system.*;
import com.lingyang.cloud.service.*;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.log.annotation.Log;
import com.lingyang.common.log.enums.BusinessType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统管理端-GPU资源管理控制器
 * @author Claude
 * @Date: 2025/05/13
 */
@RestController
@RequestMapping("/system/gpu")
@Tag(name = "系统管理-AI算力中心")
public class SystemGpuResourceController {

    @Resource
    private GpuResourceService gpuResourceService;

    @Resource
    private GpuResourceSpecService gpuResourceSpecService;

    @Resource
    private GpuRegionService gpuRegionService;

    @Resource
    private GpuZoneService gpuZoneService;

    @Resource
    private GpuResourcePriceMapper gpuResourcePriceMapper;

    @Resource
    private GpuResourceStockMapper gpuResourceStockMapper;

    @Resource
    private GpuResourceComponentMapper gpuResourceComponentMapper;

    @Resource
    private GpuSchedulerApiClient gpuSchedulerApiClient;

    @Resource
    private GpuComponentService gpuComponentService;

    // ==================== GPU资源管理 ====================

    @GetMapping("/resource/page")
    @Operation(summary = "GPU资源分页列表")
    @Log(value = "GPU资源列表", businessType = BusinessType.GET)
    public Result<PageResult<GpuResourceVO>> resourcePage(GpuResourceQuery query, PageQuery pageQuery) {
        return Result.success(gpuResourceService.getResourcePage(query, pageQuery));
    }

    @GetMapping("/resource/{id}")
    @Operation(summary = "GPU资源详情")
    @Log(value = "GPU资源详情", businessType = BusinessType.GET)
    public Result<GpuResourceDetailVO> resourceDetail(@PathVariable Long id) {
        GpuResourceEntity resource = gpuResourceService.getById(id);
        if (resource == null) {
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
        vo.setClusterId(resource.getClusterId());
        vo.setClusterName(resource.getClusterName());
        vo.setClusterNodeName(resource.getClusterNodeName());
        vo.setSpecId(resource.getSpecId());
        vo.setGpuCount(resource.getGpuCount());
        vo.setGpuDriver(resource.getGpuDriver());
        vo.setCudaVersion(resource.getCudaVersion());
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
        if (resource.getZoneCode() != null) {
            GpuZoneEntity zone = gpuZoneService.getByCode(resource.getZoneCode());
            if (zone != null) {
                vo.setZone(zone.getZoneName());
            }
        }
        GpuResourceStockEntity stock = gpuResourceStockMapper.selectByResourceId(id);
        if (stock != null) {
            vo.setAvailableCount(stock.getAvailableCount());
            vo.setTotalCount(stock.getTotalCount());
        }
        vo.setPrices(gpuResourcePriceMapper.selectByResourceId(id).stream().map(price -> {
            GpuResourceDetailVO.GpuPriceItemVO item = new GpuResourceDetailVO.GpuPriceItemVO();
            item.setBillingType(price.getBillingType());
            item.setBillingTypeName(getBillingTypeName(price.getBillingType()));
            item.setUnitPrice(price.getUnitPrice() != null ? price.getUnitPrice().toPlainString() : null);
            item.setDiscountPrice(price.getDiscountPrice() != null ? price.getDiscountPrice().toPlainString() : null);
            item.setDiscountRate(price.getDiscountRate());
            return item;
        }).toList());
        vo.setComponents(gpuResourceComponentMapper.selectByResourceId(id).stream().map(component -> {
            GpuResourceDetailVO.GpuComponentItemVO item = new GpuResourceDetailVO.GpuComponentItemVO();
            item.setComponentId(component.getComponentId());
            item.setComponentName(component.getComponentName());
            item.setBaseImage(component.getBaseImage());
            item.setImageAddress(component.getImageAddress());
            item.setSortOrder(component.getSortOrder());
            return item;
        }).toList());

        return Result.success(vo);
    }

    @PostMapping("/resource/save")
    @Operation(summary = "新增/编辑GPU资源")
    @Log(value = "保存GPU资源", businessType = BusinessType.INSERT)
    public Result<Void> resourceSave(@RequestBody @Valid GpuResourceEdit edit) {
        gpuResourceService.saveOrUpdate(edit);
        return Result.success();
    }

    @PostMapping("/resource/status")
    @Operation(summary = "上架/下架")
    @Log(value = "更新GPU资源状态", businessType = BusinessType.UPDATE)
    public Result<Void> resourceStatus(@RequestParam Long id, @RequestParam Integer status) {
        gpuResourceService.updateStatus(id, status);
        return Result.success();
    }

    @DeleteMapping("/resource/{id}")
    @Operation(summary = "删除GPU资源")
    @Log(value = "删除GPU资源", businessType = BusinessType.DELETE)
    public Result<Void> resourceDelete(@PathVariable Long id) {
        gpuResourceService.deleteById(id);
        return Result.success();
    }

    // ==================== GPU规格管理 ====================

    @GetMapping("/spec/page")
    @Operation(summary = "GPU规格分页列表")
    @Log(value = "GPU规格列表", businessType = BusinessType.GET)
    public Result<PageResult<GpuSpecVO>> specPage(PageQuery pageQuery,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) Integer status) {
        return Result.success(gpuResourceSpecService.getSpecPage(pageQuery, model, status));
    }

    @GetMapping("/spec/list")
    @Operation(summary = "GPU规格列表")
    @Log(value = "GPU规格列表", businessType = BusinessType.GET)
    public Result<List<GpuSpecVO>> specList() {
        return Result.success(gpuResourceSpecService.listAllEnabled().stream()
                .map(entity -> {
                    GpuSpecVO vo = new GpuSpecVO();
                    BeanUtils.copyProperties(entity, vo);
                    if (ObjectUtils.isNotEmpty(entity.getTags())) {
                        vo.setTags(JSON.parseArray(entity.getTags(), String.class));
                    }
                    return vo;
                }).toList());
    }

    @PostMapping("/spec/save")
    @Operation(summary = "新增GPU规格")
    @Log(value = "保存GPU规格", businessType = BusinessType.INSERT)
    public Result<Void> specSave(@RequestBody @Valid GpuSpecEdit edit) {
        gpuResourceSpecService.saveOrUpdate(edit);
        return Result.success();
    }

    @PostMapping("/spec/update")
    @Operation(summary = "编辑GPU规格")
    @Log(value = "编辑GPU规格", businessType = BusinessType.UPDATE)
    public Result<Void> specUpdate(@RequestBody @Valid GpuSpecEdit edit) {
        gpuResourceSpecService.saveOrUpdate(edit);
        return Result.success();
    }

    @GetMapping("/spec/delete")
    @Operation(summary = "删除GPU规格")
    @Log(value = "删除GPU规格", businessType = BusinessType.DELETE)
    public Result<Void> specDelete(@RequestParam Long id) {
        gpuResourceSpecService.deleteById(id);
        return Result.success();
    }

    @GetMapping("/spec/status")
    @Operation(summary = "修改GPU规格状态")
    @Log(value = "修改GPU规格状态", businessType = BusinessType.UPDATE)
    public Result<Void> specStatus(@RequestParam Long id, @RequestParam Integer status) {
        gpuResourceSpecService.updateStatus(id, status);
        return Result.success();
    }

    // ==================== 地区管理 ====================

    @GetMapping("/region/page")
    @Operation(summary = "地区分页列表")
    @Log(value = "地区分页列表", businessType = BusinessType.GET)
    public Result<PageResult<GpuRegionVO>> regionPage(PageQuery pageQuery,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String regionCode,
            @RequestParam(required = false) String regionName) {
        return Result.success(gpuRegionService.getRegionPage(pageQuery, status, regionCode, regionName));
    }

    @GetMapping("/region/list")
    @Operation(summary = "地区列表")
    @Log(value = "地区列表", businessType = BusinessType.GET)
    public Result<List<GpuRegionVO>> regionList() {
        return Result.success(gpuRegionService.listAllEnabled());
    }

    @PostMapping("/region/save")
    @Operation(summary = "新增/编辑地区")
    @Log(value = "保存地区", businessType = BusinessType.INSERT)
    public Result<Void> regionSave(@RequestBody @Valid GpuRegionEdit edit) {
        gpuRegionService.saveOrUpdate(edit);
        return Result.success();
    }

    @PostMapping("/region/update")
    @Operation(summary = "编辑地区")
    @Log(value = "编辑地区", businessType = BusinessType.UPDATE)
    public Result<Void> regionUpdate(@RequestBody @Valid GpuRegionEdit edit) {
        gpuRegionService.saveOrUpdate(edit);
        return Result.success();
    }

    @GetMapping("/region/delete")
    @Operation(summary = "删除地区")
    @Log(value = "删除地区", businessType = BusinessType.DELETE)
    public Result<Void> regionDelete(@RequestParam Long id) {
        gpuRegionService.deleteById(id);
        return Result.success();
    }

    @GetMapping("/region/status")
    @Operation(summary = "修改地区状态")
    @Log(value = "修改地区状态", businessType = BusinessType.UPDATE)
    public Result<Void> regionStatus(@RequestParam Long id, @RequestParam Integer status) {
        gpuRegionService.updateStatus(id, status);
        return Result.success();
    }

    // ==================== 专区管理 ====================

    @GetMapping("/zone/page")
    @Operation(summary = "专区分页列表")
    @Log(value = "专区分页列表", businessType = BusinessType.GET)
    public Result<PageResult<GpuZoneVO>> zonePage(PageQuery pageQuery,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String zoneCode,
            @RequestParam(required = false) String zoneName) {
        return Result.success(gpuZoneService.getZonePage(pageQuery, status, zoneCode, zoneName));
    }

    @GetMapping("/zone/list")
    @Operation(summary = "专区列表")
    @Log(value = "专区列表", businessType = BusinessType.GET)
    public Result<List<GpuZoneVO>> zoneList() {
        return Result.success(gpuZoneService.listAllEnabled());
    }

    @PostMapping("/zone/save")
    @Operation(summary = "新增/编辑专区")
    @Log(value = "保存专区", businessType = BusinessType.INSERT)
    public Result<Void> zoneSave(@RequestBody @Valid GpuZoneEdit edit) {
        gpuZoneService.saveOrUpdate(edit);
        return Result.success();
    }

    @GetMapping("/zone/status")
    @Operation(summary = "修改专区状态")
    @Log(value = "修改专区状态", businessType = BusinessType.UPDATE)
    public Result<Void> zoneStatus(@RequestParam Long id, @RequestParam Integer status) {
        gpuZoneService.updateStatus(id, status);
        return Result.success();
    }

    @DeleteMapping("/zone/{id}")
    @Operation(summary = "删除专区")
    @Log(value = "删除专区", businessType = BusinessType.DELETE)
    public Result<Void> zoneDelete(@PathVariable Long id) {
        gpuZoneService.deleteById(id);
        return Result.success();
    }

    // ==================== GPU集群管理 ====================

    @GetMapping("/cluster/page")
    @Operation(summary = "GPU集群分页列表")
    @Log(value = "GPU集群分页列表", businessType = BusinessType.GET)
    public Result<PageResult<GpuClusterVO>> clusterPage(GpuClusterQuery query, PageQuery pageQuery) {
        return Result.success(gpuSchedulerApiClient.getClusterPage(query, pageQuery));
    }

    @GetMapping("/cluster/summary")
    @Operation(summary = "GPU集群资源详情")
    @Log(value = "GPU集群资源详情", businessType = BusinessType.GET)
    public Result<JSONObject> clusterSummary(@RequestParam(required = false) String clusterId) {
        return Result.success(gpuSchedulerApiClient.getClusterSummary(clusterId));
    }

    @GetMapping("/cluster/monitor/overview")
    @Operation(summary = "GPU集群监控总览")
    @Log(value = "GPU集群监控总览", businessType = BusinessType.GET)
    public Result<JSONObject> clusterMonitorOverview() {
        return Result.success(gpuSchedulerApiClient.getMonitorOverview());
    }

    @GetMapping("/volcano/catalog")
    @Operation(summary = "火山云 GPU 目录")
    @Log(value = "火山云 GPU 目录", businessType = BusinessType.GET)
    public Result<JSONObject> volcanoGpuCatalog() {
        return Result.success(gpuSchedulerApiClient.getGpuCatalog());
    }

    @GetMapping("/cluster/node/page")
    @Operation(summary = "GPU集群节点分页列表")
    @Log(value = "GPU集群节点分页列表", businessType = BusinessType.GET)
    public Result<PageResult<JSONObject>> clusterNodePage(GpuClusterNodeQuery query, PageQuery pageQuery) {
        return Result.success(gpuSchedulerApiClient.getNodePage(query, pageQuery));
    }

    @GetMapping("/cluster/node-pool/page")
    @Operation(summary = "GPU节点池分页列表")
    @Log(value = "GPU节点池分页列表", businessType = BusinessType.GET)
    public Result<PageResult<GpuNodePoolVO>> nodePoolPage(GpuNodePoolQuery query, PageQuery pageQuery) {
        return Result.success(gpuSchedulerApiClient.getNodePoolPage(query, pageQuery));
    }

    @GetMapping("/cluster/component/page")
    @Operation(summary = "GPU组件分页列表")
    @Log(value = "GPU组件分页列表", businessType = BusinessType.GET)
    public Result<PageResult<GpuComponentVO>> componentPage(GpuComponentQuery query, PageQuery pageQuery) {
        return Result.success(gpuComponentService.getComponentPage(query, pageQuery));
    }

    @PostMapping("/cluster/component/save")
    @Operation(summary = "新增GPU组件")
    @Log(value = "保存GPU组件", businessType = BusinessType.INSERT)
    public Result<Void> componentSave(@RequestBody @Valid GpuComponentEdit edit) {
        gpuComponentService.saveOrUpdate(edit);
        return Result.success();
    }

    @PostMapping("/cluster/component/update")
    @Operation(summary = "编辑GPU组件")
    @Log(value = "编辑GPU组件", businessType = BusinessType.UPDATE)
    public Result<Void> componentUpdate(@RequestBody @Valid GpuComponentEdit edit) {
        gpuComponentService.saveOrUpdate(edit);
        return Result.success();
    }

    @GetMapping("/cluster/component/status")
    @Operation(summary = "修改GPU组件状态")
    @Log(value = "修改GPU组件状态", businessType = BusinessType.UPDATE)
    public Result<Void> componentStatus(@RequestParam Long id, @RequestParam Integer status) {
        gpuComponentService.updateStatus(id, status);
        return Result.success();
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
