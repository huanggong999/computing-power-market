package com.lingyang.cloud.api.controller.pc;

import com.lingyang.cloud.entity.SysApplyEntity;
import com.lingyang.cloud.model.query.apply.SysApplyQuery;
import com.lingyang.cloud.service.SysApplyService;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "pc端-应用")
@RestController
@RequestMapping("/pc/apply")
public class PcApplyController {

    @Autowired
    private SysApplyService sysApplyService;

    @Operation(summary = "获取分页列表", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })
    @GetMapping("/page")
    public Result<PageResult<SysApplyEntity>> page(SysApplyQuery query) {
        return sysApplyService.getPage(PageQuery.build(query));
    }

    @Operation(summary = "查询应用")
    @GetMapping("/{id}")
    public Result<SysApplyEntity> detail(@PathVariable("id") Long id) {
        return Result.success(sysApplyService.getById(id));
    }

}
