package com.lingyang.cloud.api.controller.system;

import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.lingyang.cloud.entity.SysExtend;
import com.lingyang.cloud.entity.SysOrderEntity;
import com.lingyang.cloud.entity.SysOrderSourceEntity;
import com.lingyang.cloud.entity.SysRemit;
import com.lingyang.cloud.enums.customer.SysTransactionType;
import com.lingyang.cloud.enums.order.OrderOnlinePayEnum;
import com.lingyang.cloud.enums.order.OrderStatusEnum;
import com.lingyang.cloud.enums.order.OrderTypeEnum;
import com.lingyang.cloud.mapper.SysOrderMapper;
import com.lingyang.cloud.mapper.SysOrderSourceMapper;
import com.lingyang.cloud.mapper.SysRemitMapper;
import com.lingyang.cloud.model.dto.SysExtendVerifyDTO;
import com.lingyang.cloud.model.dto.SysRemitQuery;
import com.lingyang.cloud.model.dto.SysRemitVerifyDTO;
import com.lingyang.cloud.service.SysCustomerService;
import com.lingyang.cloud.service.SysRemitService;
import com.lingyang.common.cache.CacheQueueService;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.utils.DateUtils;
import com.lingyang.common.core.utils.RandomUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;

import static com.lingyang.cloud.common.constant.CacheQueueConstant.CUSTOMER_BILL_HANDLER_QUEUE_TYPE;

@RestController
@RequestMapping("/system/remit")
@Tag(name = "后台系统-对公打款")
public class SysRemitController {
    
    @Autowired
    private SysRemitService sysRemitService;

    @Autowired
    private SysRemitMapper sysRemitMapper;

    @Autowired
    private SysOrderMapper sysOrderMapper;

    @Autowired
    private SysOrderSourceMapper sysOrderSourceMapper;

    @Resource
    private SysCustomerService sysCustomerService;
    @Resource
    private CacheQueueService cacheQueueService;

    @Operation(summary = "获取分页列表", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })
    @GetMapping("/page")
    public Result<PageResult<SysRemit>> page(SysRemitQuery query) {
        return sysRemitService.getPage(PageQuery.build(query));
    }


    @Operation(summary = "审核操作")
    @PostMapping("/verify")
    public Result<Void> verify(@RequestBody SysRemitVerifyDTO dto) {
        SysRemit remit = sysRemitMapper.selectById(dto.getId());
        sysRemitMapper.update(null,
                new LambdaUpdateWrapper<SysRemit>()
                        .set(SysRemit::getStatus, dto.getStatus())
                        .set(SysRemit::getRemark, dto.getRemark())
                        .eq(SysRemit::getId, dto.getId())
                );

        // 增加余额充值订单
        if (dto.getStatus().equals(2)) {
            String orderNo = "Order" + DateUtils.getDate(DateUtils.YYYYMMDDHHMMSS) + RandomUtils.getNumberRandom(6);

            SysOrderEntity order = new SysOrderEntity();
            order.setOrderNo(orderNo);
            order.setOrderType(OrderTypeEnum.BALANCE);
            order.setOriginalPrice(remit.getAmount());
            order.setPremiumPrice(BigDecimal.ZERO);
            order.setUserDiscountAmount(BigDecimal.ZERO);
            order.setFinalPayAmount(remit.getAmount());
            order.setOnlinePayAmount(remit.getAmount());
            order.setOnlinePayType(OrderOnlinePayEnum.REMIT_PAY);
            order.setPayTime(new Date());
            order.setOrderStatus(OrderStatusEnum.PAID);
            order.setCreateById(remit.getUserId());
            sysOrderMapper.insert(order);

            SysOrderSourceEntity source = new SysOrderSourceEntity();
            source.setOrderNo(orderNo);
            source.setOrderId(order.getId());
            source.setNumber(1);
            source.setUnitPrice(remit.getAmount());
            source.setFinalUnitPrice(remit.getAmount());
            source.setCreateById(remit.getUserId());
            sysOrderSourceMapper.insert(source);


            sysCustomerService.updateCustomerBalance(order.getId(),
                    order.getOrderNo(),
                    order.getCreateById(),
                    order.getFinalPayAmount(),
                    SysTransactionType.RECHARGE);
            // 发布账单处理
            cacheQueueService.addDelayQueue(CUSTOMER_BILL_HANDLER_QUEUE_TYPE, order.getCreateById().toString(), 5);

        }

        return  Result.success();
    }
    
}
