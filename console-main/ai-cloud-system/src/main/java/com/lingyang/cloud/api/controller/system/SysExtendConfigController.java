package com.lingyang.cloud.api.controller.system;

import com.lingyang.cloud.entity.SysExtendConfig;
import com.lingyang.cloud.entity.SysModuleEntity;
import com.lingyang.cloud.mapper.SysExtendConfigMapper;
import com.lingyang.common.core.model.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "后台系统-推广配置")
@RestController
@RequestMapping("/system/extend-config")
public class SysExtendConfigController {

    @Autowired
    private SysExtendConfigMapper sysExtendConfigMapper;

    @Operation(summary = "修改推广配置")
    @PostMapping("/update")
    public Result<Void> update(@RequestBody SysExtendConfig config) {
        config.setId(1L);
        sysExtendConfigMapper.updateById(config);
        return Result.success();
    }

    @Operation(summary = "查询推广配置")
    @GetMapping("/detail")
    public Result<SysExtendConfig> detail() {
        SysExtendConfig config = sysExtendConfigMapper.selectById(1L);
        return Result.success(config);
    }


}
