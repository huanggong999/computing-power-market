package com.lingyang.cloud.api.controller.system;

import com.lingyang.cloud.entity.SysActiveCenterEntity;
import com.lingyang.cloud.entity.SysActiveRecordEntity;
import com.lingyang.cloud.model.dto.SysActiveCouponDTO;
import com.lingyang.cloud.model.dto.SysActiveDetailsDTO;
import com.lingyang.cloud.model.query.active.SysActiveCouponQuery;
import com.lingyang.cloud.model.query.active.SysActiveQuery;
import com.lingyang.cloud.model.query.active.SysActiveRecordQuery;
import com.lingyang.cloud.model.vo.active.SysActiveVO;
import com.lingyang.cloud.service.SysActiveCenterService;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 吴思镇
 */
@Tag(name = "后台系统-活动中心")
@RestController
@RequestMapping("/system/active/center")
public class SysActiveCenterController {

    @Autowired
    private SysActiveCenterService sysActiveCenterService;

    @Operation(summary = "获取活动分页列表", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })
    @GetMapping("/page")
    public Result<PageResult<SysActiveCenterEntity>> page(SysActiveQuery query) {
        return sysActiveCenterService.getPage(PageQuery.build(query));
    }

    @Operation(summary = "查询优惠劵分页列表", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })
    @GetMapping("/getCoupons")
    public Result<PageResult<SysActiveCouponDTO>> getCoupons(SysActiveCouponQuery query) {
        return sysActiveCenterService.getCoupons(PageQuery.build(query));

    }


    @Operation(summary = "新增活动")
    @PostMapping("/save")
    public Result<Void> save(@RequestBody @Valid SysActiveVO vo) {
        return sysActiveCenterService.save(vo);
    }

    @Operation(summary = "活动详情")
    @GetMapping("/getActiveDetails/{id}")
    public Result<SysActiveDetailsDTO> getActiveDetails(@PathVariable Long id) {
        return sysActiveCenterService.getActiveDetails(id);
    }

    @Operation(summary = "修改活动")
    @PutMapping("/update")
    public Result<Void> update(@RequestBody @Valid SysActiveVO vo) {
        return sysActiveCenterService.update(vo);
    }


    @Operation(summary = "删除活动")
    @GetMapping("/delete")
    public Result<Void> delete(@RequestParam Long id) {
        return sysActiveCenterService.delete(id);
    }

    @Operation(summary = "活动记录", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })
    @GetMapping("/getActiveRecord")
    public Result<PageResult<SysActiveRecordEntity>> getActiveRecord(SysActiveRecordQuery query) {
        return sysActiveCenterService.getActiveRecord(PageQuery.build(query));
    }


}
