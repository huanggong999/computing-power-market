package com.lingyang.cloud.api.controller.pc;

import com.lingyang.cloud.model.dto.BatchRenewDTO;
import com.lingyang.cloud.model.dto.CreateGpuPodDTO;
import com.lingyang.cloud.model.dto.RenewDTO;
import com.lingyang.cloud.model.dto.ResetPasswordDTO;
import com.lingyang.cloud.model.dto.SetNameDTO;
import com.lingyang.cloud.model.dto.SetShutdownScheduleDTO;
import com.lingyang.cloud.model.query.GpuPodInstanceQuery;
import com.lingyang.cloud.model.vo.GpuPodInstanceVO;
import com.lingyang.cloud.model.vo.ShutdownScheduleVO;
import com.lingyang.cloud.model.vo.SshInfoVO;
import com.lingyang.cloud.model.vo.VncInfoVO;
import com.lingyang.cloud.service.GpuPodService;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * GPU Pod 实例管理控制器
 */
@RestController
@RequestMapping("/pc/gpu-pod")
@Tag(name = "PC端-GPU Pod实例管理", description = "GPU Pod实例生命周期管理相关接口")
public class GpuPodController {

    @Resource
    private GpuPodService gpuPodService;

    @Operation(summary = "获取GPU Pod实例列表", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "每页数量，默认为10", in = ParameterIn.QUERY)
    })
    @GetMapping("/instances")
    public Result<PageResult<GpuPodInstanceVO>> listInstances(@Valid GpuPodInstanceQuery query) {
        return Result.success(gpuPodService.listInstances(query));
    }

    @Operation(summary = "创建GPU Pod实例")
    @PostMapping("/instances")
    public Result<GpuPodInstanceVO> create(@Valid @RequestBody CreateGpuPodDTO dto) {
        return Result.success(gpuPodService.createInstance(dto));
    }

    @Operation(summary = "获取GPU Pod实例详情")
    @GetMapping("/instances/{id}")
    public Result<GpuPodInstanceVO> detail(@PathVariable String id) {
        return Result.success(gpuPodService.getInstance(id));
    }

    @Operation(summary = "启动GPU Pod实例")
    @PostMapping("/instances/{id}/start")
    public Result<Void> start(@PathVariable String id) {
        gpuPodService.startInstance(id);
        return Result.success();
    }

    @Operation(summary = "停止GPU Pod实例")
    @PostMapping("/instances/{id}/stop")
    public Result<Void> stop(@PathVariable String id) {
        gpuPodService.stopInstance(id);
        return Result.success();
    }

    @Operation(summary = "重启GPU Pod实例")
    @PostMapping("/instances/{id}/restart")
    public Result<Void> restart(@PathVariable String id) {
        gpuPodService.restartInstance(id);
        return Result.success();
    }

    @Operation(summary = "释放GPU Pod实例")
    @PostMapping("/instances/{id}/release")
    public Result<Void> release(@PathVariable String id) {
        gpuPodService.releaseInstance(id);
        return Result.success();
    }

    @Operation(summary = "重置GPU Pod实例密码")
    @PostMapping("/instances/{id}/reset-password")
    public Result<String> resetPassword(@PathVariable String id, @RequestBody ResetPasswordDTO dto) {
        return Result.success(gpuPodService.resetPassword(id, dto.getNewPassword()));
    }

    @Operation(summary = "续费GPU Pod实例")
    @PostMapping("/instances/{id}/renew")
    public Result<Void> renew(@PathVariable String id, @RequestBody RenewDTO dto) {
        gpuPodService.renewInstance(id, dto);
        return Result.success();
    }

    @Operation(summary = "批量续费GPU Pod实例")
    @PostMapping("/instances/batch-renew")
    public Result<Void> batchRenew(@RequestBody BatchRenewDTO dto) {
        gpuPodService.batchRenewInstances(dto);
        return Result.success();
    }

    @Operation(summary = "获取GPU Pod实例快捷工具")
    @GetMapping("/instances/{id}/tools")
    public Result<Object> getTools(@PathVariable String id) {
        return Result.success(gpuPodService.getInstanceTools(id));
    }

    @Operation(summary = "获取GPU Pod实例监控数据")
    @GetMapping("/instances/{id}/monitor")
    public Result<Object> getMonitor(@PathVariable String id, @RequestParam Map<String, Object> params) {
        return Result.success(gpuPodService.getInstanceMonitor(id, params));
    }

    @Operation(summary = "设置GPU Pod实例名称")
    @PutMapping("/instances/{id}/name")
    public Result<Void> setName(@PathVariable String id, @RequestBody SetNameDTO dto) {
        gpuPodService.setInstanceName(id, dto);
        return Result.success();
    }

    @Operation(summary = "设置GPU Pod实例定时关机")
    @PostMapping("/instances/{id}/shutdown-schedule")
    public Result<Void> setShutdownSchedule(@PathVariable String id, @RequestBody SetShutdownScheduleDTO dto) {
        gpuPodService.setShutdownSchedule(id, dto);
        return Result.success();
    }

    @Operation(summary = "获取GPU Pod实例定时关机状态")
    @GetMapping("/instances/{id}/shutdown-schedule")
    public Result<ShutdownScheduleVO> getShutdownSchedule(@PathVariable String id) {
        return Result.success(gpuPodService.getShutdownSchedule(id));
    }

    @Operation(summary = "获取GPU Pod实例SSH连接信息")
    @GetMapping("/instances/{id}/ssh")
    public Result<SshInfoVO> getSshInfo(@PathVariable String id) {
        return Result.success(gpuPodService.getSshInfo(id));
    }

    @Operation(summary = "获取GPU Pod实例VNC连接信息")
    @GetMapping("/instances/{id}/vnc")
    public Result<VncInfoVO> getVncInfo(@PathVariable String id) {
        return Result.success(gpuPodService.getVncInfo(id));
    }

    @Operation(summary = "获取GPU Pod实例日志")
    @GetMapping("/instances/{id}/logs")
    public Result<String> getLogs(@PathVariable String id, @RequestParam int tailLines) {
        return Result.success(gpuPodService.getInstanceLogs(id, tailLines));
    }
}
