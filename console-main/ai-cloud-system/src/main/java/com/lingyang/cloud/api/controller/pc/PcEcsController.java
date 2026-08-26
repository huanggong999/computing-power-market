package com.lingyang.cloud.api.controller.pc;

import com.alibaba.excel.util.StringUtils;
import com.lingyang.cloud.entity.SysEcsEntity;
import com.lingyang.cloud.enums.source.SourceRegionsEnum;
import com.lingyang.cloud.model.query.esc.PcEcsQuery;
import com.lingyang.cloud.service.SysEcsService;
import com.lingyang.cloud.voengine.client.VoEngineApiClient;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import com.volcengine.ApiException;
import com.volcengine.ecs.EcsApi;
import com.volcengine.ecs.model.DescribeImagesRequest;
import com.volcengine.ecs.model.DescribeImagesResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/1 16:39
 */
@RestController
@RequestMapping("/pc/ecs")
@Tag(name = "pc端-云服务器相关")
public class PcEcsController {

    @Resource
    private SysEcsService sysEcsService;

    @Operation(summary = "获取ecs列表", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })
    @GetMapping("/getEcsList")
    public Result<PageResult<SysEcsEntity>> getEcsList(@Valid PcEcsQuery query) {
        return Result.success(sysEcsService.getEcsPage(PageQuery.build(query)));
    }

    @Operation(summary = "获取镜像列表", responses = {
            @ApiResponse(description = "返回结果参考：https://api.volcengine.com/api-docs/view?action=DescribeImages&serviceCode=ecs&version=2020-04-01#%E5%93%8D%E5%BA%94%E5%8F%82%E6%95%B0")
    })
    @GetMapping("/getEcsImgList")
    public Result<DescribeImagesResponse> getEcsImgList(@Parameter(name = "visibility", description = " public：公共镜像  private：自定义镜像 shared：共享镜像")
                                                        String visibility,
                                                        @Parameter(name = "nextToken", description = "分页查询凭证，用于标记分页的位置，初次调用该接口时无需设置。下次查询时，取值为上一次API调用返回的NextToken参数值。", in = ParameterIn.QUERY)
                                                        String nextToken,
                                                        @Parameter(name = "maxResults", description = """
                                                                分页查询时设置的每页行数。
                                                                取值范围：1 ~ 100
                                                                默认值：15""", in = ParameterIn.QUERY)
                                                        Integer maxResults,
                                                        @Parameter(name = "instanceTypeId", description = "实例的规格ID，传入本参数时，将返回该规格可用的镜像ID列表", in = ParameterIn.QUERY,required = false)
                                                        String instanceTypeId,
                                                        @Parameter(name = "osType", description = """
                                                                操作系统类型。取值：
                                                                Linux
                                                                Windows""", in = ParameterIn.QUERY)
                                                        String osType,
                                                        @Parameter(name = "platform", description = """
                                                                镜像操作系统的发行版本。取值：
                                                                CentOS
                                                                Debian
                                                                veLinux
                                                                Windows Server
                                                                Fedora
                                                                OpenSUSE
                                                                Ubuntu""", in = ParameterIn.QUERY)
                                                            String platform,
                                                        @Parameter(name = "region", description = "地域ID", in = ParameterIn.QUERY)
                                                        @RequestParam("region") SourceRegionsEnum region
    ) {
        EcsApi ecsApi = VoEngineApiClient.getEcsApi(region.getId());
        try {
            DescribeImagesRequest request = new DescribeImagesRequest();
            request.setVisibility(visibility);
            request.setNextToken(nextToken);
            request.setMaxResults(maxResults);
            if (StringUtils.isNotBlank(instanceTypeId)){
                request.setInstanceTypeId(instanceTypeId);
            }
            request.setOsType(osType);
            request.setPlatform(platform);
            DescribeImagesResponse describeImagesResponse = ecsApi.describeImages(request);
            return Result.success(describeImagesResponse);
        } catch (ApiException e) {
            return Result.error("该资源已售罄，请联系客服");
        }
    }


}