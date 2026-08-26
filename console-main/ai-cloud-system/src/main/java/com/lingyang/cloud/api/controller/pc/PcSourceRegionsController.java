package com.lingyang.cloud.api.controller.pc;

import com.lingyang.cloud.enums.source.SourceRegionsEnum;
import com.lingyang.cloud.model.vo.regions.SourceRegionVO;
import com.lingyang.cloud.model.vo.regions.SourceRegionZoneVO;
import com.lingyang.cloud.voengine.client.VoEngineApiClient;
import com.lingyang.common.core.model.result.Result;
import com.volcengine.ApiException;
import com.volcengine.ecs.EcsApi;
import com.volcengine.ecs.model.DescribeZonesRequest;
import com.volcengine.ecs.model.ZoneForDescribeZonesOutput;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/1 16:40
 */
@RestController
@RequestMapping("/pc/region")
@Tag(name = "pc端-区域相关")
public class PcSourceRegionsController {

    @GetMapping("/getRegions")
    @Operation(summary = "获取区域列表")
    public Result<List<SourceRegionVO>> getRegions() {
        return Result.success(SourceRegionsEnum.getSourceRegions());
    }

    @Operation(summary = "获取区域可用区列表", parameters = {
            @Parameter(name = "regionId", description = "区域id", in = ParameterIn.PATH)
    })
    @GetMapping("/getZones/{regionId}")
    public Result<List<SourceRegionZoneVO>> getRegionsZones(@PathVariable("regionId") String regionId) {
        EcsApi ecsApi = VoEngineApiClient.getEcsApi(regionId);
        try {
            List<ZoneForDescribeZonesOutput> zones = ecsApi.describeZones(new DescribeZonesRequest()).getZones();
            if (ObjectUtils.isEmpty(zones)) {
                return Result.success(List.of());
            }
            List<SourceRegionZoneVO> resultList = new ArrayList<>();
            for (ZoneForDescribeZonesOutput zone : zones) {
                resultList.add(new SourceRegionZoneVO(zone.getZoneId()));
            }
            return Result.success(resultList);
        } catch (ApiException e) {
            return Result.error(e.getResponseBody());
        }
    }
}