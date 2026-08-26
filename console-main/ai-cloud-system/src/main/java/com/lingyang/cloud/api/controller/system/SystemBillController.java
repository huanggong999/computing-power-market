package com.lingyang.cloud.api.controller.system;

import com.lingyang.cloud.entity.SysCustomerBillEntity;
import com.lingyang.cloud.model.query.customer.SysCustomerBillQuery;
import com.lingyang.cloud.model.vo.customer.SysCustomerBillOverviewVO;
import com.lingyang.cloud.service.SysCustomerBillService;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/12/2 11:50
 */
@Tag(name = "后台系统-账单相关")
@RestController
@RequestMapping("/system/bill")
public class SystemBillController {
    @Resource
    private SysCustomerBillService sysCustomerBillService;

    @Operation(summary = "获取账单总览列表", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })
    @GetMapping("/overviewPage")
    public Result<PageResult<SysCustomerBillOverviewVO>> getBillOverviewPage(SysCustomerBillQuery query) {
        return Result.success( sysCustomerBillService.getBillOverviewPage(PageQuery.build(query)));
    }

    @Operation(summary = "获取账单列表", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })
    @GetMapping("/page")
    public Result<PageResult<SysCustomerBillEntity>> getBillPage(SysCustomerBillQuery query) {
        return Result.success(sysCustomerBillService.getBillPage(PageQuery.build(query)));
    }
}