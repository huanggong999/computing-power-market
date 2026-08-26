package com.lingyang.cloud.api.controller.system;

import com.lingyang.cloud.entity.SysCouponEntity;
import com.lingyang.cloud.model.query.coupon.SysCouponQuery;
import com.lingyang.cloud.service.SysCouponService;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.web.controller.BaseController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/10/23 15:01
 */
@RestController
@Tag(name = "后台系统-优惠券相关")
@RequestMapping("/system/coupon")
public class SysCouponController extends BaseController<SysCouponService, SysCouponQuery, SysCouponEntity, SysCouponEntity, SysCouponEntity> {

    @Override
    public Result<PageResult<SysCouponEntity>> page(SysCouponQuery query) {
        return super.page(query);
    }

    @Override
    public Result<SysCouponEntity> detail(Long id) {
        return super.detail(id);
    }

    @Override
    public Result<Void> save(@RequestBody @Valid SysCouponEntity t) {
        return super.save(t);
    }

    @Override
    public Result<Void> update(@RequestBody SysCouponEntity t) {
        return super.update(t);
    }

    @Override
    public Result<Void> remove(Long id) {
        return super.remove(id);
    }

    @Operation(summary = "删除活动")
    @GetMapping("/delete")
    public Result<Void> delete(@RequestParam Long id) {
        service.remove(id);
        return Result.success();
    }
}
