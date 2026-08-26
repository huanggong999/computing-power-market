package com.lingyang.cloud.api.controller.system;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.lingyang.cloud.entity.SysOrderEntity;
import com.lingyang.cloud.entity.SysOrderSourceEntity;
import com.lingyang.cloud.handler.OnlinePayHandler;
import com.lingyang.cloud.handler.model.OnlinePayRefundsParam;
import com.lingyang.cloud.mapper.SysOrderMapper;
import com.lingyang.cloud.model.dto.SysEcsWorkPageDTO;
import com.lingyang.cloud.model.query.order.SysOrderQuery;
import com.lingyang.cloud.service.SysOrderService;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.utils.DateUtils;
import com.lingyang.common.core.utils.Optional;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/29 18:42
 */
@Tag(name = "后台系统-订单相关")
@RestController
@RequestMapping("/system/order")
@Slf4j
public class SysOrderController {


    @Resource
    private SysOrderService sysOrderService;

    @Autowired
    private SysOrderMapper sysOrderMapper;

    @Resource
    private OnlinePayHandler onlinePayHandler;

    @GetMapping("/page")
    @Operation(summary = "获取订单列表", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })
    public Result<PageResult<SysOrderEntity>> getOrderPage(SysOrderQuery query) {
        return Result.success(sysOrderService.getPage(PageQuery.build(query)));
    }

    @GetMapping("/orderSourceList/{orderId}")
    @Operation(summary = "获取订单资源列表", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })
    public Result<PageResult<SysOrderSourceEntity>> getOrderSourceList(@PathVariable("orderId") Long orderId) {
        return Result.success(sysOrderService.getOrderSourceList(PageQuery.build(orderId)));
    }

    @Operation(summary = "获取资源订单详情")
    @GetMapping("/detail")
    public Result<SysEcsWorkPageDTO> getOrderDetail(@RequestParam String orderNo) {
        return Result.success(sysOrderService.getOrderDetail(orderNo));
    }


    @GetMapping("/product/open")
    @Operation(summary = "开通产品")
    public Result<Void> openProduct(@RequestParam Long orderId) {
        SysOrderEntity order = sysOrderMapper.selectById(orderId);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (order.getActualStatus() == null) {
            return Result.error("开通状态错误");
        }
        if (order.getActualStatus().equals(2)) {
            return Result.error("产品已开通");
        }
        if (order.getActualStatus().equals(3)) {
            return Result.error("产品已过期");
        }
        Date date = new Date();
        sysOrderMapper.update(null,
                    new LambdaUpdateWrapper<SysOrderEntity>()
                            .set(SysOrderEntity::getActualStatus, 2)
                            .set(SysOrderEntity::getActualAgiOpenTime, date)
                            .set(SysOrderEntity::getActualAgiExpireTime, DateUtils.addDays(date, order.getNetworkDay()))
                            .eq(SysOrderEntity::getId, orderId)

                );
        return Result.success();
    }

    @Operation(description = "退款")
    @GetMapping("/refund")
    public Result<Void> refund(@RequestParam Long orderId) {
        SysOrderEntity sysOrderEntity = sysOrderMapper.selectById(orderId);
        log.info("手动退款开始：{}", sysOrderEntity);
        Optional.of(sysOrderEntity.getOnlinePaySerialNumber())
                .ifPresent(onlinePaySerialNumber -> {
                    OnlinePayRefundsParam refundsParam  = new OnlinePayRefundsParam();
                    refundsParam.setOrderNo(sysOrderEntity.getOrderNo());
                    refundsParam.setAmount(sysOrderEntity.getOnlinePayAmount());
                    refundsParam.setPayNumber(onlinePaySerialNumber);
                    refundsParam.setPayTotalAmount(sysOrderEntity.getOnlinePayAmount());
                    refundsParam.setReason("订单取消");
                    onlinePayHandler.refunds(sysOrderEntity.getOnlinePayType(), refundsParam);
                });
        return Result.success();
    }
}
