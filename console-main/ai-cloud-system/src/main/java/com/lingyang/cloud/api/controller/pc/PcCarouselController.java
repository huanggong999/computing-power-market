package com.lingyang.cloud.api.controller.pc;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lingyang.cloud.entity.SysCarouselEntity;
import com.lingyang.cloud.enums.system.StatusEnum;
import com.lingyang.cloud.mapper.SysCarouselMapper;
import com.lingyang.cloud.service.SysCarouselService;
import com.lingyang.common.core.model.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "pc端-轮播图")
@RestController
@RequestMapping("/pc/carousel")
public class PcCarouselController {

    @Autowired
    private SysCarouselService sysCarouselService;

    @Autowired
    private SysCarouselMapper sysCarouselMapper;


    @Operation(summary = "查询轮播图列表")
    @GetMapping("/list")
    public Result<List<SysCarouselEntity>> list(Integer type) {
        return Result.success(sysCarouselService.list(type));
    }

    @Operation(summary = "查询小程序轮播图列表")
    @GetMapping("/appletList")
    public Result<List<SysCarouselEntity>> appletList(@RequestParam(required = false) Integer type) {
        return Result.success(sysCarouselMapper.selectList(
                new LambdaQueryWrapper<SysCarouselEntity>()
                        .eq(SysCarouselEntity::getEquipmentType, 2)
                        .eq(type != null, SysCarouselEntity::getType, type)
                        .eq(SysCarouselEntity::getStatus, StatusEnum.OK.getCode())
                        .orderByAsc(SysCarouselEntity::getSort)
        ));
    }


}
