package com.lingyang.cloud.api.controller.applets;

import com.lingyang.cloud.api.service.applets.AppletsConsoleService;
import com.lingyang.cloud.model.dto.AppletsConsoleListDTO;
import com.lingyang.cloud.model.dto.AppletsIncomeDTO;
import com.lingyang.cloud.model.vo.applets.AppletsIncomeListVO;
import com.lingyang.common.core.model.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/3/7 10:37
 */
@Tag(name = "小程序端-控制台")
@RestController
@RequestMapping("/applets/console")
public class AppletsConsoleController {

    @Resource
    private AppletsConsoleService appletsConsoleService;

    @Operation(summary = "获取控制台资源列表")
    @GetMapping("/list")
    public Result<AppletsConsoleListDTO> list() {
        return Result.success(appletsConsoleService.list());
    }

    @Operation(summary = "收支明细列表")
    @PostMapping("/income/list")
    public Result<AppletsIncomeDTO> incomeList(@RequestBody AppletsIncomeListVO vo) {
        return Result.success(appletsConsoleService.incomeList(vo));
    }
}
