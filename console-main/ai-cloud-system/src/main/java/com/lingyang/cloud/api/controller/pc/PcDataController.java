package com.lingyang.cloud.api.controller.pc;

import com.lingyang.cloud.entity.SysModuleEntity;
import com.lingyang.cloud.enums.module.ModuleTypeEnum;
import com.lingyang.cloud.enums.system.StatusEnum;
import com.lingyang.cloud.model.query.module.SysModuleQuery;
import com.lingyang.cloud.service.SysModuleService;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "pc端-数据")
@RestController
@RequestMapping("/pc/data")
public class PcDataController {

    @Autowired
    private SysModuleService sysModuleService;


    @Operation(summary = "获取分页列表", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })
    @GetMapping("/page")
    public Result<PageResult<SysModuleEntity>> page(SysModuleQuery query) {
        query.setType(ModuleTypeEnum.DATA.getCode());
        query.setStatus(StatusEnum.OK.getCode());
        return sysModuleService.getPage(PageQuery.build(query));
    }

    @Operation(summary = "获取详情")
    @GetMapping("/detail/{id}")
    public Result<SysModuleEntity> getDetail(@PathVariable("id") Long id) {
        return Result.success(sysModuleService.getById(id));
    }
}
