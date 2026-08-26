package com.lingyang.cloud.api.controller.pc;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.lingyang.cloud.entity.SysOrderEntity;
import com.lingyang.cloud.entity.SysOrderSourceEntity;
import com.lingyang.cloud.entity.SysRemit;
import com.lingyang.cloud.enums.order.OrderOnlinePayEnum;
import com.lingyang.cloud.enums.order.OrderStatusEnum;
import com.lingyang.cloud.enums.order.OrderTypeEnum;
import com.lingyang.cloud.mapper.SysOrderMapper;
import com.lingyang.cloud.mapper.SysOrderSourceMapper;
import com.lingyang.cloud.mapper.SysRemitMapper;
import com.lingyang.cloud.model.dto.SysRemitQuery;
import com.lingyang.cloud.model.dto.SysRemitVerifyDTO;
import com.lingyang.cloud.model.vo.RemitVO;
import com.lingyang.cloud.service.SysRemitService;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.security.SecurityContext;
import com.lingyang.common.core.utils.DateUtils;
import com.lingyang.common.core.utils.RandomUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;

@RestController
@RequestMapping("/pc/remit")
@Tag(name = "pc端-对公打款")
public class PcRemitController {
    
    @Autowired
    private SysRemitService sysRemitService;

    @Autowired
    private SysRemitMapper sysRemitMapper;

    @Autowired
    private SysOrderMapper sysOrderMapper;

    @Autowired
    private SysOrderSourceMapper sysOrderSourceMapper;

    @Operation(summary = "获取分页列表", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })
    @GetMapping("/page")
    public Result<PageResult<SysRemit>> page(SysRemitQuery query) {
        query.setUserId(SecurityContext.getUserInfo().getUserId());
        return sysRemitService.getPage(PageQuery.build(query));
    }


    @Operation(summary = "新增对公打款")
    @PostMapping("/add")
    public Result<Void> add(@RequestBody SysRemit dto) {
        dto.setUserId(SecurityContext.getUserInfo().getUserId());
        sysRemitMapper.insert(dto);
        return  Result.success();
    }

    @Operation(summary = "账户信息")
    @GetMapping("/account")
    public Result<RemitVO> account() {
        RemitVO vo = new RemitVO();
        vo.setChina(new RemitVO.China());
        vo.setNoChina(new RemitVO.NoChina());
        return  Result.success(vo);
    }


    
}
