package com.lingyang.cloud.api.controller.pc;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.api.model.dto.PcVoucherListDTO;
import com.lingyang.cloud.entity.SysActiveRecordEntity;
import com.lingyang.cloud.entity.SysCustomerEntity;
import com.lingyang.cloud.mapper.SysCustomerMapper;
import com.lingyang.cloud.model.dto.SysActiveDetailsDTO;
import com.lingyang.cloud.model.query.active.SysActiveRecordQuery;
import com.lingyang.cloud.service.SysActiveCenterService;
import com.lingyang.cloud.service.SysActiveRecordService;
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

import java.util.List;

/**
 * @author 吴思镇
 */
@Tag(name = "pc端-活动中心")
@RestController
@RequestMapping("/pc/active")
public class PcActiveRecordController {

    @Autowired
    private SysActiveCenterService sysActiveCenterService;

    @Autowired
    private SysActiveRecordService sysActiveRecordService;

    @Autowired
    private SysCustomerMapper sysCustomerMapper;

    @Operation(summary = "获取活动详情")
    @GetMapping("/details/{id}")
    private Result<SysActiveDetailsDTO> getActiveDetails(@PathVariable Long id) {
        return sysActiveCenterService.getPcActiveDetails(id);
    }

    @Operation(summary = "获取活动记录参与情况", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })

    @GetMapping("/record/page")
    private Result<PageResult<SysActiveRecordEntity>> getActiveRecord(SysActiveRecordQuery query) {
        return sysActiveRecordService.getPcActiveRecord(PageQuery.build(query));
    }

    @Operation(summary = "获取活动列表")
    @GetMapping("/details/list")
    private Result<List<SysActiveDetailsDTO>> getActiveList() {
        return sysActiveCenterService.getActiveList();
    }

    @Operation(summary = "通过用户id查询用户信息")
    @GetMapping("/getUserById/{userId}")
    private Result<String> getUserById(@PathVariable Long userId) {
        SysCustomerEntity sysCustomerEntity = sysCustomerMapper.selectOne(Wrappers.lambdaQuery(SysCustomerEntity.class).eq(SysCustomerEntity::getId, userId));
        return Result.success(sysCustomerEntity.getCustomerName());
    }

    @Operation(summary = "通过查询充值活动赠送的代金卷列表")
    @GetMapping("/getCouponList")
    private Result<List<PcVoucherListDTO>> getCouponList() {
        return sysActiveCenterService.getCouponList();
    }
}
