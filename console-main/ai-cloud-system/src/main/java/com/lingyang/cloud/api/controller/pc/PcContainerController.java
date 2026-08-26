package com.lingyang.cloud.api.controller.pc;

import com.lingyang.cloud.api.model.dto.PcContainerDetailsDTO;
import com.lingyang.cloud.api.model.dto.PcContainerListDTO;
import com.lingyang.cloud.api.model.dto.PcContainerOverviewDTO;
import com.lingyang.cloud.api.model.vo.PcContainerImageVO;
import com.lingyang.cloud.api.model.vo.PcContainerListVO;
import com.lingyang.cloud.api.service.pc.PcContainerService;
import com.lingyang.cloud.entity.SysImageEntity;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.security.SecurityContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/2/12 10:27
 */
@RestController
@RequestMapping("/pc/container")
@Tag(name = "pc端-容器相关")
public class PcContainerController {

    @Resource
    private PcContainerService pcContainerService;

    @Operation(summary = "容器概览")
    @GetMapping("/get/container/overview")
    public Result<PcContainerOverviewDTO> getContainerOverview() {
        return Result.success(pcContainerService.getContainerOverview());
    }

    @Operation(summary = "删除容器")
    @DeleteMapping("/delete/container/{id}")
    public Result<Boolean> deleteContainer(@PathVariable Long id) {
        Boolean result = pcContainerService.deleteContainer(id);
        if (!result){
            return Result.error("删除失败");
        }
        return Result.success();
    }

    @Operation(summary = "容器列表")
    @PostMapping("/list")
    public Result<List<PcContainerListDTO>> list(@RequestBody PcContainerListVO vo) {
        Long userId = SecurityContext.getUserInfo().getUserId();
        vo.setCustomerId(userId);
        return Result.success(pcContainerService.list(vo));
    }

    @Operation(summary = "容器详情")
    @GetMapping("/details/{id}")
    public Result<PcContainerDetailsDTO> details(@PathVariable Long id) {
        return Result.success(pcContainerService.details(id));
    }

    @Operation(summary = "查询可用的镜像版本")
    @PostMapping("/get/image/version")
    public Result<List<SysImageEntity>> getImageVersion(@RequestBody PcContainerImageVO vo) {
        return Result.success(pcContainerService.getImageVersion(vo));
    }

}
