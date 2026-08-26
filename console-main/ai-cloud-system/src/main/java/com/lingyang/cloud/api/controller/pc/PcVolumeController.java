package com.lingyang.cloud.api.controller.pc;

import com.lingyang.cloud.entity.SysVolumeEntity;
import com.lingyang.cloud.enums.system.SystemConfigEnum;
import com.lingyang.cloud.model.config.EcsSystemVolumeConfigModel;
import com.lingyang.cloud.model.config.SystemPriceRationConfigModel;
import com.lingyang.cloud.model.query.volume.SysVolumeQuery;
import com.lingyang.cloud.service.SysConfigService;
import com.lingyang.cloud.service.SysVolumeService;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/5 14:40
 */
@RestController
@Tag(name = "pc端-云盘相关")
@RequestMapping("/pc/volume")
public class PcVolumeController {

    @Resource
    private SysVolumeService sysVolumeService;
    @Resource
    private SysConfigService sysConfigService;

    @Operation(summary = "获取系统盘")
    @GetMapping("/getSystemVolume")
    public Result<EcsSystemVolumeConfigModel> getSystemVolume() {
        EcsSystemVolumeConfigModel systemVolume =  sysConfigService.getConfig(SystemConfigEnum.ECS_SYSTEM_VOLUME).getValue(EcsSystemVolumeConfigModel.class);
        SystemPriceRationConfigModel priceRation = sysConfigService.getConfig(SystemConfigEnum.SELL_PRICE_RATIO).getValue(SystemPriceRationConfigModel.class);
        priceRation.calculatePremium(systemVolume);
        return Result.success(systemVolume);
    }

    @Operation(summary = "获取数据盘云盘列表", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })
    @GetMapping("/getList")
    public Result<PageResult<SysVolumeEntity>> getVolumeList(SysVolumeQuery query) {
        return Result.success(sysVolumeService.getPageList(PageQuery.build(query)));
    }
}
