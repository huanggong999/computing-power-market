package com.lingyang.cloud.api.controller.pc;

import com.lingyang.cloud.api.model.dto.PcBucketDetailDTO;
import com.lingyang.cloud.api.model.query.PcBucketListQuery;
import com.lingyang.cloud.api.model.vo.PcBucketCreateVO;
import com.lingyang.cloud.api.service.pc.PcBucketService;
import com.lingyang.cloud.entity.SysCustomerBucketEntity;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/2/12 10:27
 */
@RestController
@RequestMapping("/pc/bucket")
@Tag(name = "pc端-对象存储相关")
public class PcBucketController {

    @Resource
    private PcBucketService pcBucketService;

    @Operation(summary = "桶列表",parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })
    @GetMapping("/page")
    public Result<PageResult<SysCustomerBucketEntity>> list(PcBucketListQuery vo) {
        return Result.success(pcBucketService.list(PageQuery.build(vo)));
    }

    @Operation(summary = "创建桶")
    @PostMapping("/create")
    public Result<Boolean> create(@RequestBody PcBucketCreateVO vo) {
        return Result.success(pcBucketService.create(vo));
    }

    @Operation(summary = "删除桶")
    @DeleteMapping("/delete/{name}")
    public Result<Boolean> delete(@PathVariable String name) {
        return Result.success(pcBucketService.delete(name));
    }

    @Operation(summary = "存储桶详情")
    @GetMapping("/detail/{name}")
    public Result<PcBucketDetailDTO> detail(@PathVariable String name) {
        return Result.success(pcBucketService.detail(name));
    }

    @Operation(summary = "查询桶名是否已经存在(true 已存在     false 不存在)")
    @GetMapping("/check/{name}")
    public Result<Boolean> check(@PathVariable String name) {
        return Result.success(pcBucketService.check(name));
    }
}
