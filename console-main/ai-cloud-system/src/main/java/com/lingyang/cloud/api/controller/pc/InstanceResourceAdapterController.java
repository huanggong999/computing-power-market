package com.lingyang.cloud.api.controller.pc;

import com.alibaba.fastjson2.JSONObject;
import com.lingyang.cloud.client.InstanceResourceAdapterClient;
import com.lingyang.common.core.model.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pc/instance-resource")
@Tag(name = "PC端-实例资源变更")
public class InstanceResourceAdapterController {
    @Resource
    private InstanceResourceAdapterClient client;

    @GetMapping("/instances/{id}/resize/options")
    @Operation(summary = "查询实例资源变更选项")
    public Result<JSONObject> options(@PathVariable String id) { return Result.success(client.getResizeOptions(id)); }

    @PostMapping("/instances/{id}/resize")
    @Operation(summary = "提交实例资源变更")
    public Result<JSONObject> resize(@PathVariable String id, @RequestBody JSONObject body) { return Result.success(client.submitResize(id, body)); }

    @GetMapping("/instances/{id}/resize/tasks")
    @Operation(summary = "查询实例资源变更任务历史")
    public Result<JSONObject> tasks(@PathVariable String id) { return Result.success(client.listResizeTasks(id)); }

    @GetMapping("/instances/{id}/resize/tasks/{taskId}")
    @Operation(summary = "查询实例资源变更任务进度")
    public Result<JSONObject> task(@PathVariable String id, @PathVariable String taskId) { return Result.success(client.getResizeTask(id, taskId)); }

    @PostMapping("/instances/{id}/bandwidth")
    @Operation(summary = "调整实例公网带宽")
    public Result<JSONObject> bandwidth(@PathVariable String id, @RequestBody JSONObject body) { return Result.success(client.changeBandwidth(id, body)); }
}
