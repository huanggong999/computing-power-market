package com.lingyang.cloud.api.controller.pc;

import com.alibaba.fastjson2.JSONObject;
import com.lingyang.cloud.client.InstanceResourceAdapterClient;
import com.lingyang.cloud.service.CpuCreationService;
import com.lingyang.cloud.service.VolcanoCpuSalePriceService;
import com.lingyang.common.core.model.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pc/cpu-instance")
@Tag(name = "PC端-独立CPU实例")
public class CpuInstanceAdapterController {
    @Resource
    private InstanceResourceAdapterClient client;
    @Resource
    private CpuCreationService creationService;
    @Resource
    private VolcanoCpuSalePriceService priceService;

    @GetMapping("/regions")
    @Operation(summary = "查询CPU地域和可用区")
    public Result<JSONObject> regions() { return Result.success(client.listCpuRegions()); }

    @GetMapping("/zones")
    @Operation(summary = "查询可用区名称等明细")
    public Result<JSONObject> zones(@RequestParam(required = false) String region) {
        return Result.success(client.listZones(region));
    }

    @GetMapping("/instance-types")
    @Operation(summary = "查询纯CPU规格")
    public Result<JSONObject> instanceTypes(@RequestParam String region) { return Result.success(client.listCpuInstanceTypes(region)); }

    @GetMapping("/prices")
    @Operation(summary = "查询独立CPU价格目录")
    public Result<JSONObject> prices(@RequestParam String region, @RequestParam(required = false) String instanceTypeId,
                                     @RequestParam(required = false) String billingMode) {
        return Result.success(priceService.getDisplayPrices(region, instanceTypeId, billingMode));
    }

    @GetMapping("/instances")
    @Operation(summary = "查询独立CPU实例列表")
    public Result<JSONObject> instances(@RequestParam(defaultValue = "false") boolean includeReleased) { return Result.success(creationService.mergeList(client.listCpuInstances(includeReleased))); }

    @GetMapping("/operations/{id}")
    @Operation(summary = "查询CPU创建任务")
    public Result<JSONObject> operation(@PathVariable String id) { return Result.success(creationService.get(id)); }

    @DeleteMapping("/operations/{id}")
    @Operation(summary = "清除失败CPU创建任务")
    public Result<JSONObject> clearOperation(@PathVariable String id) { return Result.success(creationService.clearFailedOperation(id)); }

    @GetMapping("/instances/{id}")
    @Operation(summary = "查询独立CPU实例详情")
    public Result<JSONObject> detail(@PathVariable String id) { return Result.success(client.getCpuInstance(id)); }

    @PostMapping("/instances")
    @Operation(summary = "购买独立CPU实例")
    public Result<JSONObject> purchase(@RequestBody JSONObject body) {
        return Result.success(creationService.submit(body));
    }

    @PostMapping("/instances/{id}/start")
    @Operation(summary = "启动独立CPU实例")
    public Result<JSONObject> start(@PathVariable String id) { return Result.success(client.startCpuInstance(id)); }

    @PostMapping("/instances/{id}/stop")
    @Operation(summary = "停止独立CPU实例")
    public Result<JSONObject> stop(@PathVariable String id, @RequestParam(defaultValue = "false") boolean force) { return Result.success(client.stopCpuInstance(id, force)); }

    @PostMapping("/instances/{id}/restart")
    @Operation(summary = "重启独立CPU实例")
    public Result<JSONObject> restart(@PathVariable String id, @RequestParam(defaultValue = "false") boolean force) {
        return Result.success(client.restartCpuInstance(id, force));
    }

    @GetMapping("/instances/{id}/ssh")
    @Operation(summary = "查询独立CPU实例SSH连接信息")
    public Result<JSONObject> ssh(@PathVariable String id) { return Result.success(client.getCpuSshInfo(id)); }

    @PostMapping("/instances/{id}/reset-password")
    @Operation(summary = "重置独立CPU实例root密码")
    public Result<JSONObject> resetPassword(@PathVariable String id, @RequestBody JSONObject body) {
        return Result.success(client.resetCpuPassword(id, body));
    }

    @PutMapping("/instances/{id}/name")
    @Operation(summary = "修改独立CPU实例名称")
    public Result<JSONObject> rename(@PathVariable String id, @RequestBody(required = false) JSONObject body,
                                     @RequestParam(required = false) String name) {
        JSONObject payload = body == null ? new JSONObject() : body;
        if (payload.getString("name") == null && name != null) payload.put("name", name);
        return Result.success(client.renameCpuInstance(id, payload));
    }

    @DeleteMapping("/instances/{id}")
    @Operation(summary = "释放独立CPU实例")
    public Result<JSONObject> release(@PathVariable String id) { return Result.success(client.releaseCpuInstance(id)); }
}
