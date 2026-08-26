package com.lingyang.cloud.api.controller.pc;

import com.lingyang.cloud.entity.SysConfigEntity;
import com.lingyang.cloud.enums.system.SystemConfigEnum;
import com.lingyang.cloud.service.SysConfigService;
import com.lingyang.common.core.model.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/6 18:00
 */
@Tag(name = "pc端-配置相关")
@RestController
@RequestMapping("/pc/config")
public class PcConfigController {

    @Resource
    private SysConfigService sysConfigService;

    @Operation(summary = "获取系统配置",
            parameters = {@Parameter(name = "config",description = "配置key", in = ParameterIn.PATH)})
    @GetMapping("/getConfig/{config}")
    public Result<SysConfigEntity> getConfig(@PathVariable SystemConfigEnum config) {
        return Result.success(sysConfigService.getConfig(config));
    }

}