package com.lingyang.cloud.api.controller.pc;

import com.lingyang.cloud.api.model.vo.PcIsEmailExistVO;
import com.lingyang.cloud.api.model.vo.PcUpdateAutoRenewVO;
import com.lingyang.cloud.api.model.vo.PcUpdateCustomerNameVO;
import com.lingyang.cloud.entity.SysOrderEntity;
import com.lingyang.cloud.entity.SysOrderSourceEntity;
import com.lingyang.cloud.mapper.SysOrderMapper;
import com.lingyang.cloud.model.dto.SysNetworkProductValueDTO;
import com.lingyang.cloud.model.edit.order.SysOrderCreateDTO;
import com.lingyang.cloud.model.query.order.SysOrderQuery;
import com.lingyang.cloud.model.vo.order.OrderCreateVO;
import com.lingyang.cloud.service.SysNetworkValueService;
import com.lingyang.cloud.service.SysOrderService;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.security.SecurityContext;
import com.lingyang.common.core.utils.IpUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/12 17:29
 */
@Slf4j
@Tag(name = "pc端-订单相关")
@RestController
@RequestMapping("/pc/order")
public class PcOrderController {

    @Resource
    private SysOrderService sysOrderService;

    @Resource
    private SysNetworkValueService sysNetworkValueService;

    @Resource
    private SysOrderMapper sysOrderMapper;

    @Operation(summary = "构建订单信息")
    @PostMapping("/buildOrderInfo")
    private Result<SysOrderEntity> buildOrderInfo(@RequestBody SysOrderCreateDTO createDTO) {
        return Result.success(sysOrderService.buildOrderInfo(createDTO));
    }

    @Operation(summary = "创建订单")
    @PostMapping("/createOrder")
    public Result<OrderCreateVO> createOrder(@RequestBody SysOrderCreateDTO createDTO) {
        if (createDTO.getIsApplet() != null && createDTO.getIsApplet()) {
            createDTO.setIp(IpUtils.getIpAddr());
        }
        log.info("电风扇地方圣诞快乐反馈了受打击拉法基拉萨登记理发 : {}", createDTO.getLoginCode());
        return Result.success(sysOrderService.createOrder(createDTO));
    }

    @Operation(summary = "构建AGIC订单信息")
    @PostMapping("/buildAGOrderInfo")
    public Result<SysOrderEntity> buildAGOrderInfo(@RequestBody SysOrderCreateDTO createDTO) {
        return Result.success(sysOrderService.buildAGOrderInfo(createDTO));
    }

    @Operation(summary = "创建AGIC订单")
    @PostMapping("/createAGOrder")
    public Result<OrderCreateVO> createAGOrder(@RequestBody SysOrderCreateDTO createDTO) {
        if (createDTO.getIsApplet() != null && createDTO.getIsApplet()) {
            createDTO.setIp(IpUtils.getIpAddr());
        }
        log.info("电风扇地方圣诞快乐反馈了受打击拉法基拉萨登记理发 : {}", createDTO.getLoginCode());
        return Result.success(sysOrderService.createAGOrder(createDTO));
    }

    @Operation(summary = "获取支付宝支付表单")
    @GetMapping("/getAlipayForm")
    public String getAlipayForm(@RequestParam String orderNo) {
        return sysOrderService.getAlipayForm(orderNo);
    }

    @Operation(summary = "获取微信支付二维码")
    @GetMapping("/getWechatPayQrCode")
    public Result<OrderCreateVO> getWechatPayQrCode(@RequestParam String orderNo) {
        return sysOrderService.getWechatPayQrCode(orderNo);
    }


    @Operation(summary = "取消订单")
    @DeleteMapping("/cancelOrder")
    public Result<Void> cancelOrder(Long orderId) {
        sysOrderService.cancelOrder(orderId);
        return Result.success();
    }

    public String getIp(HttpServletRequest request) {
        if (request.getHeader("x-forwarded-for") == null) {
            return request.getRemoteAddr();
        } else {
            String ipListStr = request.getHeader("x-forwarded-for");
            if (ipListStr.contains(",")) {
                String[] list = ipListStr.split(",");
                return list[0];
            } else {
                return ipListStr;
            }
        }
    }

    @GetMapping("/page")
    @Operation(summary = "获取订单列表", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })
    public Result<PageResult<SysOrderEntity>> getOrderPage(SysOrderQuery query) {
        query.setCustomerId(SecurityContext.getUserInfo().getUserId());
        return Result.success(sysOrderService.getPage(PageQuery.build(query)));
    }

    @GetMapping("/agic/page")
    @Operation(summary = "获取AGIC订单列表", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })
    public Result<PageResult<SysOrderEntity>> getAGICOrderPage(SysOrderQuery query) {
        query.setCustomerId(SecurityContext.getUserInfo().getUserId());
        return Result.success(sysOrderService.getAGICOrderPage(PageQuery.build(query)));
    }

    @GetMapping("/getProductDetail")
    @Operation(summary = "获取AGI-C产品详情")
    public Result<SysNetworkProductValueDTO> getProductDetail(@RequestParam Long networkValueId) {
        return Result.success(sysNetworkValueService.getProductDetail(networkValueId));
    }

    @Operation(summary = "修改网络产品客户名称")
    @PostMapping("/updateAgiCustomerName")
    public Result<Boolean> updateAgiCustomerName(@RequestBody PcUpdateCustomerNameVO vo) {
        return Result.success(sysOrderService.updateAgiCustomerName(vo));
    }

    @Operation(summary = "修改网络产品是否自动续费")
    @PostMapping("/updateAgiIsAutoRenew")
    public Result<Boolean> updateAgiIsAutoRenew(@RequestBody PcUpdateAutoRenewVO vo) {
       return Result.success(sysOrderService.updateAgiIsAutoRenew(vo));
    }
    @Operation(summary = "判断邮箱是否已经在飞连存在")
    @PostMapping("/isEmailExist")
    public Result<List<String>> isEmailExist(@RequestBody PcIsEmailExistVO  vo) {
        return Result.success(sysOrderService.isEmailExist(vo));
    }

    @GetMapping("/orderSourceList/{orderId}")
    @Operation(summary = "获取订单资源列表", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })
    public Result<PageResult<SysOrderSourceEntity>> getOrderSourceList(@PathVariable("orderId") Long orderId) {
        SysOrderEntity order = sysOrderMapper.selectById(orderId);
        if (order == null || !Objects.equals(order.getCreateById(), SecurityContext.getUserInfo().getUserId())) {
            return Result.error("订单不存在");
        }
        return Result.success(sysOrderService.getOrderSourceList(PageQuery.build(orderId)));
    }

    @GetMapping("/detail")
    @Operation(summary = "获取订单详情")
    public Result<SysOrderEntity> getDetail(@RequestParam("orderNo") String orderNo) {
        SysOrderEntity sysOrderEntity = sysOrderService.getOrderDetailByOrderNo(orderNo);
        if (sysOrderEntity == null || !Objects.equals(sysOrderEntity.getCreateById(), SecurityContext.getUserInfo().getUserId())) {
            return Result.error("订单不存在");
        }
        return Result.success(sysOrderEntity);
    }


}
