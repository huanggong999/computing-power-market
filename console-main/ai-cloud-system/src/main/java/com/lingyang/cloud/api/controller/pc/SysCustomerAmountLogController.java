package com.lingyang.cloud.api.controller.pc;

import com.lingyang.cloud.entity.SysCustomerAmountLogEntity;
import com.lingyang.cloud.enums.customer.SysCustomerAmountType;
import com.lingyang.cloud.service.SysCustomerAmountLogService;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.security.SecurityContext;
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
 * @Date: 2024/12/5 16:41
 */
@Tag(name = "pc端-客户金额记录")
@RestController
@RequestMapping("/pc/customer/amount")
public class SysCustomerAmountLogController {
    @Resource
    private SysCustomerAmountLogService sysCustomerAmountLogService;


    @Operation(summary = "获取金额记录日志列表", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })
    @GetMapping("/page")
    public Result<PageResult<SysCustomerAmountLogEntity>> getPage(SysCustomerAmountType amountType) {
        SysCustomerAmountLogEntity entity = new SysCustomerAmountLogEntity();
        entity.setCustomerId(SecurityContext.getUserInfo().getUserId());
        entity.setAmountType(amountType);
        PageResult<SysCustomerAmountLogEntity> pageResult = sysCustomerAmountLogService.getPage(PageQuery.build(entity));
        return Result.success(pageResult);
    }
}
