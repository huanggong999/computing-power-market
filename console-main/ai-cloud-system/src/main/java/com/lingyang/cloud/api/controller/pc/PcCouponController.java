package com.lingyang.cloud.api.controller.pc;

import com.lingyang.cloud.entity.SysCouponEntity;
import com.lingyang.cloud.enums.system.StatusEnum;
import com.lingyang.cloud.model.query.coupon.SysCouponQuery;
import com.lingyang.cloud.model.query.coupon.SysCustomerCouponQuery;
import com.lingyang.cloud.model.vo.customer.SysCustomerCouponListVO;
import com.lingyang.cloud.service.SysCouponService;
import com.lingyang.cloud.service.SysCustomerCouponService;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.security.SecurityContext;
import com.lingyang.common.core.utils.DateUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/10/24 10:34
 */
@Tag(name = "pc端-优惠券相关")
@RestController
@RequestMapping("/pc/coupon")
public class PcCouponController {

    @Resource
    private SysCouponService sysCouponService;
    @Resource
    private SysCustomerCouponService sysCustomerCouponService;

    @GetMapping("/page")
    @Operation(summary = "获取优惠卷列表", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })
    public Result<PageResult<SysCouponEntity>> getPage() {
        SysCouponQuery query = new SysCouponQuery();
        Date nowDate = DateUtils.getNowDate();
        query.setReceiveTimeStart(DateUtils.setStartTime(nowDate));
        query.setReceiveTimeEnd(DateUtils.setEndTime(nowDate));
        query.setStatus(StatusEnum.OK);
        PageResult<SysCouponEntity> page = sysCouponService.getPage(PageQuery.build(query));

        if (ObjectUtils.isNotEmpty(page.getList())) {
            Long userId = null;
            try {
                userId = SecurityContext.getUserInfo().getUserId();
            } catch (AuthenticationServiceException ignored) {
            }
            Map<Long, SysCustomerCouponListVO> couponEntityMap = new HashMap<>(0);
            // 校验客户是否领取
            SysCustomerCouponQuery customerCouponQuery = new SysCustomerCouponQuery();
            Set<Long> couponIdList = page.getList().stream().map(SysCouponEntity::getId).collect(Collectors.toSet());
            if (userId != null) {
                customerCouponQuery.setCustomerId(userId);
                customerCouponQuery.setCouponIds(couponIdList);
                List<SysCustomerCouponListVO> customerCouponEntityList = sysCustomerCouponService.getList(customerCouponQuery);
                if (ObjectUtils.isNotEmpty(customerCouponEntityList)) {
                    couponEntityMap = customerCouponEntityList.stream()
                            .collect(Collectors.toMap(SysCustomerCouponListVO::getCouponId, e -> e));
                }
            }
            customerCouponQuery = new SysCustomerCouponQuery();
            customerCouponQuery.setCouponIds(couponIdList);
            List<SysCustomerCouponListVO> customerCouponEntityList = sysCustomerCouponService.getList(customerCouponQuery);
            Map<Long, List<SysCustomerCouponListVO>> couponReceiveMap = new HashMap<>(0);
            if (ObjectUtils.isNotEmpty(customerCouponEntityList)) {
                couponReceiveMap = customerCouponEntityList.stream().collect(Collectors.groupingBy(SysCustomerCouponListVO::getCouponId));
            }
            Map<Long, SysCustomerCouponListVO> finalCouponEntityMap = couponEntityMap;
            Map<Long, List<SysCustomerCouponListVO>> finalCouponReceiveMap = couponReceiveMap;
            page.getList().forEach(e -> {
                List<SysCustomerCouponListVO> list = finalCouponReceiveMap.get(e.getId());
                int size = 0;
                if (ObjectUtils.isNotEmpty(list)) {
                    size = list.size();
                }
                e.setReceiveNum(e.getReceiveNum() - size);
                e.setCustomerReceive(finalCouponEntityMap.get(e.getId()) != null);
            });
        }
        return Result.success(page);
    }


    @Operation(summary = "领取优惠卷", parameters = {
            @Parameter(name = "couponId", description = "优惠卷id", in = ParameterIn.PATH),
    })
    @PostMapping("/receive/{couponId}")
    public Result<Boolean> receive(@PathVariable("couponId") Long couponId) {
        // 获取优惠卷
        SysCouponEntity couponEntity = sysCouponService.getDetail(couponId);
        if (ObjectUtils.isEmpty(couponEntity)) {
            return Result.error("优惠卷不存在");
        }
        Long userId = SecurityContext.getUserInfo().getUserId();
        sysCustomerCouponService.checkCouponReceive(userId, couponEntity);
        return Result.success(sysCustomerCouponService.receiveCoupon(userId, List.of(couponEntity)));
    }
}