package com.lingyang.cloud.api.controller.system;

import com.lingyang.cloud.model.dto.SysEcsWorkDetailDTO;
import com.lingyang.cloud.model.dto.SysEcsWorkPageDTO;
import com.lingyang.cloud.model.query.customer.SysCustomerEcsWorkQuery;
import com.lingyang.cloud.model.vo.customer.SysCustomerEcsWorkOperationVO;
import com.lingyang.cloud.model.vo.customer.SysCustomerEcsWorkVO;
import com.lingyang.cloud.service.SysCustomerEcsWorkService;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/5/22 14:55
 */
@Slf4j
@Tag(name = "后台系统-服务器工单")
@RestController
@RequestMapping("/system/ecs/work")
public class SysCustomerEcsWorkController {

    @Resource
    private SysCustomerEcsWorkService sysCustomerEcsWorkService;


    @Operation(summary = "工单列表", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })
    @GetMapping("/page")
    public Result<PageResult<SysEcsWorkPageDTO>> getPage(SysCustomerEcsWorkQuery query) {
        PageResult<SysEcsWorkPageDTO> pageResult = sysCustomerEcsWorkService.getPage(PageQuery.build(query));
        return Result.success(pageResult);
    }

    @Operation(summary = "工单详情")
    @GetMapping("/detail/{id}")
    public Result<SysEcsWorkDetailDTO> getDetail(@PathVariable Long id) {
        SysEcsWorkDetailDTO sysEcsWorkPageDTO = sysCustomerEcsWorkService.getDetail(id);
        return Result.success(sysEcsWorkPageDTO);
    }

    @Operation(summary = "开通账号")
    @PostMapping("/openWork")
    public Result<Boolean> openWork(@RequestBody SysCustomerEcsWorkVO vo) {
        return Result.success(sysCustomerEcsWorkService.openWork(vo));
    }

    @Operation(summary = "运维状态操作")
    @PostMapping("/operation")
    public Result<Boolean> operation(@RequestBody SysCustomerEcsWorkOperationVO vo) {
        return Result.success(sysCustomerEcsWorkService.operation(vo));
    }

}
