package com.lingyang.cloud.api.controller.system;

import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.lingyang.cloud.entity.*;
import com.lingyang.cloud.mapper.*;
import com.lingyang.cloud.model.dto.ExtendRelevanceDTO;
import com.lingyang.cloud.model.dto.SysExtendVerifyDTO;
import com.lingyang.cloud.model.dto.SysExtendWithVerifyDTO;
import com.lingyang.cloud.model.query.home.SysExtendOrderQuery;
import com.lingyang.cloud.model.query.home.SysExtendQuery;
import com.lingyang.cloud.model.query.home.SysExtendWithQuery;
import com.lingyang.cloud.model.query.home.SysFirstExtendQuery;
import com.lingyang.cloud.model.vo.order.ExtendOrderVO;
import com.lingyang.cloud.service.SysExtendService;
import com.lingyang.cloud.service.SysExtendWithdrawalRecordService;
import com.lingyang.cloud.service.SysOrderService;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.security.SecurityContext;
import com.lingyang.common.core.security.model.LoginUserInfoDetail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "后台系统-推广大使")
@RestController
@RequestMapping("/system/extend")
public class SysExtendController {

    @Autowired
    private SysExtendService sysExtendService;

    @Autowired
    private SysExtendMapper sysExtendMapper;

    @Autowired
    private SysOrderService sysOrderService;

    @Autowired
    private SysExtendActivityMapper sysExtendActivityMapper;

    @Autowired
    private SysExtendActivityCouponMapper sysExtendCouponMapper;

    @Autowired
    private SysCouponMapper sysCouponMapper;



    @Value(
            "${extend.shareUrl}"
    )
    private String extendShare;

    @Autowired
    private SysExtendWithdrawalRecordService sysExtendWithdrawalRecordService;

    @Autowired
    private SysExtendWithdrawalRecordMapper sysExtendWithdrawalRecordMapper;


    @Operation(summary = "获取分页列表", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })
    @GetMapping("/page")
    public Result<PageResult<SysExtend>> page(SysExtendQuery query) {
        return sysExtendService.getPage(PageQuery.build(query));
    }

    @Operation(summary = "审核操作")
    @PostMapping("/verify")
    public Result<Void> verify(@RequestBody SysExtendVerifyDTO dto) {
        String uuid = null;
        String key = null;
        if (dto.getStatus().equals(2)) {
            uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
            key = extendShare + "invite?code=" + uuid;
        }
        sysExtendMapper.update(null,
                new LambdaUpdateWrapper<SysExtend>()
                        .set(uuid != null, SysExtend::getShareKey, uuid)
                        .set(key != null, SysExtend::getLink, key)
                        .set(SysExtend::getStatus, dto.getStatus())
                        .set(SysExtend::getVerifyRemark, dto.getVerifyRemark())
                        .eq(SysExtend::getId, dto.getId())
                );
        return  Result.success();
    }


    @Operation(summary = "修改佣金比例操作")
    @GetMapping("/updateScale")
    public Result<Void> updateScale(Long id, BigDecimal firstScale, BigDecimal twoScale) {
        sysExtendMapper.update(null,
                new LambdaUpdateWrapper<SysExtend>()
                        .set(firstScale != null, SysExtend::getFirstScale, firstScale)
                        .set(twoScale != null, SysExtend::getTwoScale, twoScale)
                        .eq(SysExtend::getId, id)
        );
        return  Result.success();
    }

    @Operation(summary = "获取一级用户列表", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })
    @GetMapping("/first-page")
    public Result<PageResult<SysExtend>> firstPage(SysFirstExtendQuery query) {
        return sysExtendService.getFirstPage(PageQuery.build(query));
    }

    @Operation(summary = "获取提现分页列表", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })
    @GetMapping("/firstPage")
    public Result<PageResult<SysExtendWithdrawalRecord>> firstPage(SysExtendWithQuery query) {
        return sysExtendWithdrawalRecordService.getPage(PageQuery.build(query));
    }

    @Operation(summary = "提现审核操作")
    @PostMapping("/with-verify")
    public Result<Void> withverify(@RequestBody SysExtendWithVerifyDTO dto) {
        // 就在这里写逻辑
        sysExtendWithdrawalRecordMapper.update(null,
                new LambdaUpdateWrapper<SysExtendWithdrawalRecord>()
                        .set(SysExtendWithdrawalRecord::getStatus, dto.getStatus())
                        .set(SysExtendWithdrawalRecord::getVerifyRemark, dto.getVerifyRemark())
                        .set(SysExtendWithdrawalRecord::getWithImage, dto.getWithImage())
                        .eq(SysExtendWithdrawalRecord::getId, dto.getId())
        );
        SysExtendWithdrawalRecord sysExtendWithdrawalRecord = sysExtendWithdrawalRecordMapper.selectById(dto.getId());
        SysExtend extend = sysExtendMapper.selectOne(
                new LambdaQueryWrapper<SysExtend>()
                        .eq(SysExtend::getUserId, sysExtendWithdrawalRecord.getUserId()
                        ));
        // 根据状态回填提现金额
        if (dto.getStatus().equals(3)) {
            sysExtendMapper.update(null,
                    new LambdaUpdateWrapper<SysExtend>()
                            .set(SysExtend::getCanWithdrawalAmount, extend.getCanWithdrawalAmount().add(sysExtendWithdrawalRecord.getTotalAmount()))
                            .eq(SysExtend::getUserId, sysExtendWithdrawalRecord.getUserId())
            );
        }
        return  Result.success();
    }


    @Operation(summary = "获取分佣订单分页列表", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })
    @GetMapping("/orderPage")
    public Result<PageResult<ExtendOrderVO>> orderPage(SysExtendOrderQuery query) {
        return sysOrderService.getExtendPage(PageQuery.build(query));
    }

    @Operation(summary = "修改推广活动")
    @PostMapping("/update")
    public Result<Void> update(@RequestBody SysExtendActivity config) {
        config.setId(1L);
        sysExtendActivityMapper.updateById(config);

        sysExtendCouponMapper.delete(new LambdaQueryWrapper<SysExtendActivityCoupon>()
                .eq(SysExtendActivityCoupon::getActivityId, config.getId())
        );
        List<SysCouponEntity> couponList = config.getCouponList();
        if (CollectionUtils.isNotEmpty(couponList)) {
            List<SysExtendActivityCoupon> activityCoupons = new ArrayList<>();
            for (SysCouponEntity sysCouponEntity : couponList) {
                SysExtendActivityCoupon coupon = new SysExtendActivityCoupon();
                coupon.setCouponId(sysCouponEntity.getId());
                coupon.setActivityId(config.getId());
                activityCoupons.add(coupon);
            }
            sysExtendCouponMapper.batchInsert(activityCoupons);
        }
        return Result.success();
    }

    @Operation(summary = "查询推广活动")
    @GetMapping("/detail")
    public Result<SysExtendActivity> detail() {
        SysExtendActivity config = sysExtendActivityMapper.selectById(1L);
        List<SysExtendActivityCoupon> coupons = sysExtendCouponMapper.selectList(
                new LambdaQueryWrapper<SysExtendActivityCoupon>()
                        .eq(SysExtendActivityCoupon::getActivityId, config.getId())
        );
        if (CollectionUtils.isNotEmpty(coupons)) {
            List<SysCouponEntity> s = new ArrayList<>();
            for (SysExtendActivityCoupon coupon : coupons) {
                SysCouponEntity sysCouponEntity = sysCouponMapper.selectById(coupon.getCouponId());
                if (sysCouponEntity != null) {
                    s.add(sysCouponEntity);
                }
            }

            config.setCouponList(s);
        }
        return Result.success(config);
    }

    @Operation(summary = "设置下级推广大使")
    @PostMapping("/relevance")
    public Result<Void> relevance(@RequestBody ExtendRelevanceDTO dto) {
        SysExtend extend =  sysExtendMapper.selectOne(
                new LambdaQueryWrapper<SysExtend>()
                        .eq(SysExtend::getId, dto.getParenId())
        );

        if (extend == null) {
            return Result.error("上级用户无效");
        }

        if (!extend.getType().equals(1)) {
            return Result.error("上级用户不是推广大使，请申请推广大使审核");
        }

        SysExtend selectOne = sysExtendMapper.selectOne(
                new LambdaQueryWrapper<SysExtend>()
                        .eq(SysExtend::getUserId, dto.getNextId())
        );
        if (selectOne != null) {
            if (selectOne.getParentUserId() != null && selectOne.getParentUserId() != 0) {
                SysExtend oldP = sysExtendMapper.selectOne(
                        new LambdaQueryWrapper<SysExtend>()
                                .eq(SysExtend::getUserId, selectOne.getParentUserId())
                );
                if (oldP.getParentUserId() != null && oldP.getParentUserId() != 0) {
                    SysExtend oldtwo = sysExtendMapper.selectOne(
                            new LambdaQueryWrapper<SysExtend>()
                                    .eq(SysExtend::getUserId, oldP.getParentUserId())
                    );
                    sysExtendMapper.update(null,
                            new LambdaUpdateWrapper<SysExtend>()
                                    .set(SysExtend::getTwoCount, oldtwo.getTwoCount() - 1)
                                    .eq(SysExtend::getUserId, oldtwo.getUserId())
                    );
                }

                sysExtendMapper.update(null,
                        new LambdaUpdateWrapper<SysExtend>()
                                .set(SysExtend::getFirstCount, oldP.getFirstCount() - 1)
                                .eq(SysExtend::getUserId, oldP.getUserId())
                );





                if (extend.getParentUserId() != null && extend.getParentUserId() != 0) {
                    SysExtend two = sysExtendMapper.selectOne(
                            new LambdaQueryWrapper<SysExtend>()
                                    .eq(SysExtend::getUserId, extend.getParentUserId())
                    );
                    sysExtendMapper.update(null,
                            new LambdaUpdateWrapper<SysExtend>()
                                    .set(SysExtend::getTwoCount, two.getTwoCount() + 1)
                                    .eq(SysExtend::getUserId, two.getUserId())
                    );
                }

                sysExtendMapper.update(null,
                        new LambdaUpdateWrapper<SysExtend>()
                                .set(SysExtend::getFirstCount, extend.getFirstCount() + 1)
                                .eq(SysExtend::getUserId, extend.getUserId())
                );

                sysExtendMapper.update(null,
                        new LambdaUpdateWrapper<SysExtend>()
                                .set(SysExtend::getParentUserId, extend.getUserId())
                                .eq(SysExtend::getUserId, dto.getNextId())
                );

            } else {
                if (extend.getParentUserId() != null && extend.getParentUserId() != 0) {
                    SysExtend two = sysExtendMapper.selectOne(
                            new LambdaQueryWrapper<SysExtend>()
                                    .eq(SysExtend::getUserId, extend.getParentUserId())
                    );
                    sysExtendMapper.update(null,
                            new LambdaUpdateWrapper<SysExtend>()
                                    .set(SysExtend::getTwoCount, two.getTwoCount() + 1)
                                    .eq(SysExtend::getUserId, two.getUserId())
                    );
                }

                sysExtendMapper.update(null,
                        new LambdaUpdateWrapper<SysExtend>()
                                .set(SysExtend::getFirstCount, extend.getFirstCount() + 1)
                                .eq(SysExtend::getUserId, extend.getUserId())
                );

                sysExtendMapper.update(null,
                        new LambdaUpdateWrapper<SysExtend>()
                                .set(SysExtend::getParentUserId, extend.getUserId())
                                .eq(SysExtend::getUserId, dto.getNextId())
                );
            }


        }  else {
            return Result.error("用户不存在");
        }
        return Result.success();
    }


    public static void main(String[] args) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        System.out.println( uuid.substring(0, 10).toUpperCase());

    }


}
