package com.lingyang.cloud.api.controller.system;

import com.alibaba.fastjson2.JSONObject;
import com.lingyang.cloud.entity.SysConfigEntity;
import com.lingyang.cloud.enums.system.SystemConfigEnum;
import com.lingyang.cloud.service.SysConfigService;
import com.lingyang.common.core.model.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/10/23 15:31
 */

@Tag(name = "后台系统-配置相关")
@RestController
@RequestMapping("/system/config")
public class SysConfigController {
    @Resource
    private SysConfigService sysConfigService;


    @Operation(summary = "获取系统配置",
            parameters = {@Parameter(name = "config",description = "配置key", in = ParameterIn.PATH)})
    @GetMapping("/getConfig/{config}")
    public Result<SysConfigEntity> getConfig(@PathVariable SystemConfigEnum config) {
        return Result.success(sysConfigService.getConfig(config));
    }

    @Operation(summary = "修改系统配置",
            parameters = {@Parameter(name = "config",description = "配置key", in = ParameterIn.PATH)})
    @PutMapping("/update/{config}")
    public Result<Boolean> updateConfig(@PathVariable SystemConfigEnum config, @RequestBody JSONObject configValue) {
        SysConfigEntity entity = new SysConfigEntity();
        entity.setId(config.getCode());
        entity.setConfigKey(config.name());
        entity.setConfigValue(configValue);
        return Result.result(sysConfigService.updateConfig(entity));
    }
}
