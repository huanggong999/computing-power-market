package com.lingyang.cloud.api.controller.pc;


import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.client.GpuPodApiClient;
import com.lingyang.cloud.client.GpuPodTenantProvider;
import com.lingyang.cloud.entity.SysCustomerCreateImageLogEntity;
import com.lingyang.cloud.mapper.SysCustomerCreateImageLogMapper;
import com.lingyang.cloud.model.edit.image.ImportImageDTO;
import com.lingyang.cloud.voengine.client.VoEngineApiClient;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.security.SecurityContext;
import com.volcengine.ApiException;
import com.volcengine.ecs.EcsApi;
import com.volcengine.ecs.model.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Tag(name = "pc端-镜像管理")
@RestController
@RequestMapping("/pc/image")
@Slf4j
public class PcImageController {

    private static final int MAX_CUSTOM_IMAGES = 10;

    @Resource
    private SysCustomerCreateImageLogMapper sysCustomerCreateImageLogMapper;

    @Resource
    private GpuPodApiClient gpuPodApiClient;

    @Resource
    private GpuPodTenantProvider gpuPodTenantProvider;

    @Operation(summary = "我的镜像列表")
    @GetMapping("/mine")
    public Result<JSONObject> mine() {
        GpuPodTenantProvider.Tenant tenant = gpuPodTenantProvider.getCurrentTenant();
        return Result.success(gpuPodApiClient.listMyImages(tenant.tenantId()));
    }

    @Operation(summary = "获取我的镜像上传命令")
    @PostMapping("/mine/push-command")
    public Result<JSONObject> pushCommand(@RequestBody Map<String, Object> request) {
        GpuPodTenantProvider.Tenant tenant = gpuPodTenantProvider.getCurrentTenant();
        JSONObject currentImages = gpuPodApiClient.listMyImages(tenant.tenantId());
        if (currentImages == null
                || !Boolean.TRUE.equals(currentImages.getBoolean("success"))
                || Boolean.TRUE.equals(currentImages.getBoolean("degraded"))) {
            return Result.error("暂时无法校验自定义镜像数量，请稍后重试");
        }
        if (isCustomImageLimitReached(currentImages)) {
            return Result.error("自定义镜像最多只能上传10个，请先删除已有镜像后再试");
        }
        return Result.success(gpuPodApiClient.getMyImagePushCommand(tenant.tenantId(), request));
    }

    /**
     * 校验当前租户的自定义镜像仓库数量。上传实际通过 Docker push 完成，
     * 因此必须在生成上传命令的后端入口再次校验，避免绕过前端限制。
     */
    private boolean isCustomImageLimitReached(JSONObject imageResult) {
        if (imageResult == null) {
            return false;
        }
        Object images = imageResult.get("images");
        if (!(images instanceof List<?> imageList)) {
            return false;
        }
        Set<String> repositories = new HashSet<>();
        for (Object image : imageList) {
            if (image instanceof JSONObject imageObject) {
                String repo = imageObject.getString("repo");
                if (repo == null || repo.isBlank()) {
                    repo = imageObject.getString("image");
                    if (repo != null) {
                        int tagSeparator = repo.lastIndexOf(':');
                        if (tagSeparator > repo.lastIndexOf('/')) {
                            repo = repo.substring(0, tagSeparator);
                        }
                    }
                }
                if (repo != null && !repo.isBlank()) {
                    repositories.add(repo);
                }
            } else if (image != null) {
                repositories.add(String.valueOf(image));
            }
        }
        return repositories.size() >= MAX_CUSTOM_IMAGES;
    }

    @Operation(summary = "获取我的镜像下载命令")
    @GetMapping("/mine/pull-command")
    public Result<JSONObject> pullCommand(@RequestParam String repo,
                                          @RequestParam(defaultValue = "latest") String tag) {
        GpuPodTenantProvider.Tenant tenant = gpuPodTenantProvider.getCurrentTenant();
        return Result.success(gpuPodApiClient.getMyImagePullCommand(tenant.tenantId(), repo, tag));
    }

    @Operation(summary = "删除我的镜像")
    @DeleteMapping("/mine")
    public Result<JSONObject> deleteMine(@RequestParam String repo,
                                         @RequestParam(defaultValue = "latest") String tag) {
        GpuPodTenantProvider.Tenant tenant = gpuPodTenantProvider.getCurrentTenant();
        return Result.success(gpuPodApiClient.deleteMyImage(tenant.tenantId(), repo, tag));
    }

    @Operation(summary = "获取镜像列表", responses = {
            @ApiResponse(description = "返回结果参考：https://api.volcengine.com/api-docs/view?action=DescribeImages&serviceCode=ecs&version=2020-04-01#%E5%93%8D%E5%BA%94%E5%8F%82%E6%95%B0")
    })
    @GetMapping("/page")
    public Result<DescribeImagesResponse> getEcsImgList(@Parameter(name = "visibility", description = " public：公共镜像  private：自定义镜像 shared：共享镜像")
                                                        String visibility,
                                                        @Parameter(name = "nextToken", description = "分页查询凭证，用于标记分页的位置，初次调用该接口时无需设置。下次查询时，取值为上一次API调用返回的NextToken参数值。", in = ParameterIn.QUERY)
                                                        String nextToken,
                                                        @Parameter(name = "maxResults", description = """
                                                                分页查询时设置的每页行数。
                                                                取值范围：1 ~ 100
                                                                默认值：15""", in = ParameterIn.QUERY)
                                                        Integer maxResults,
                                                        @Parameter(name = "instanceTypeId", description = "实例的规格ID，传入本参数时，将返回该规格可用的镜像ID列表", in = ParameterIn.QUERY)
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
                                                        String platform
    ) {
        EcsApi ecsApi = VoEngineApiClient.getEcsApi();
        try {
            DescribeImagesRequest request = new DescribeImagesRequest();
            boolean tag = "shared".equals(visibility);
            if (tag) {
                visibility = "private";
            }
            request.setVisibility(visibility);
            request.setNextToken(nextToken);
            request.setMaxResults(maxResults);
            request.setInstanceTypeId(instanceTypeId);
            request.setOsType(osType);
            request.setPlatform(platform);
            DescribeImagesResponse describeImagesResponse = ecsApi.describeImages(request);
            log.info("初始获取的镜像数据：{}", describeImagesResponse);
            List<ImageForDescribeImagesOutput> images = describeImagesResponse.getImages();

            if ("private".equals(visibility) && !tag){
                List<SysCustomerCreateImageLogEntity> sysCustomerCreateImageLogEntities = sysCustomerCreateImageLogMapper.selectList(Wrappers.lambdaQuery(SysCustomerCreateImageLogEntity.class)
                        .eq(SysCustomerCreateImageLogEntity::getCustomerId, SecurityContext.getUserInfo().getUserId()));
                List<String> imageIds = sysCustomerCreateImageLogEntities.stream().map(SysCustomerCreateImageLogEntity::getImageId).toList();
                if (ObjectUtils.isNotEmpty(sysCustomerCreateImageLogEntities)){
                    List<ImageForDescribeImagesOutput> filteredImages = images.stream()
                            .filter(image -> imageIds.contains(image.getImageId()))
                            .toList();
                    describeImagesResponse.setImages(filteredImages);
                    log.info("visibility=private的镜像数据：{}", describeImagesResponse);
                }else {
                    describeImagesResponse.setImages(List.of());
                }

            }
            if (tag){
                List<SysCustomerCreateImageLogEntity> sysCustomerCreateImageLogEntities = sysCustomerCreateImageLogMapper.selectList(Wrappers.lambdaQuery(SysCustomerCreateImageLogEntity.class));
                List<String> imageIds = sysCustomerCreateImageLogEntities.stream().map(SysCustomerCreateImageLogEntity::getImageId).toList();
                if (ObjectUtils.isNotEmpty(sysCustomerCreateImageLogEntities)){
                    List<ImageForDescribeImagesOutput> filteredImages = images.stream()
                            .filter(image -> !imageIds.contains(image.getImageId()))
                            .toList();
                    describeImagesResponse.setImages(filteredImages);
                    log.info("visibility=shared的镜像数据：{}", describeImagesResponse);
                }
            }
            log.info("返回的镜像数据：{}", describeImagesResponse);
            return Result.success(describeImagesResponse);
        } catch (ApiException e) {
            return Result.error(e.getResponseBody());
        }
    }



    @Operation(summary = "导入镜像", responses = {
            @ApiResponse(description = "请求参数和返回结果参考：https://api.volcengine.com/api-docs/view?serviceCode=ecs&version=2020-04-01&action=ImportImage")
    })
    @PostMapping("/import")
    public Result<ImportImageResponse> imports(@RequestBody ImportImageDTO dto) {
        SysCustomerCreateImageLogEntity sysCustomerCreateImageLogEntity = sysCustomerCreateImageLogMapper.selectOne(Wrappers.lambdaQuery(SysCustomerCreateImageLogEntity.class)
                .eq(SysCustomerCreateImageLogEntity::getImageName, dto.getImageName()));
        if (sysCustomerCreateImageLogEntity != null) {
            return Result.error("该镜像名称已存在");
        }
        EcsApi ecsApi = VoEngineApiClient.getEcsApi();
        try {
            ImportImageRequest request = new ImportImageRequest();
            request.setArchitecture(dto.getArchitecture());
            request.setBootMode(dto.getBootMode());
            request.setDescription(dto.getDescription());
            request.setImageName(dto.getImageName());
            request.setOsType(dto.getOsType());
            request.setPlatform(dto.getPlatform());
            request.setPlatformVersion(dto.getPlatformVersion());
            request.setUrl(dto.getUrl());
            ImportImageResponse imageResponse = ecsApi.importImage(request);
            if (imageResponse != null){
                String imageId = imageResponse.getImageId();
                SysCustomerCreateImageLogEntity entity = new SysCustomerCreateImageLogEntity();
                entity.setImageId(imageId);
                entity.setImageName(dto.getImageName());
                entity.setCustomerId(SecurityContext.getUserInfo().getUserId());
                sysCustomerCreateImageLogMapper.insert(entity);
            }
            return Result.success(imageResponse);
        } catch (ApiException e) {
            return Result.error(e.getResponseBody());
        }
    }


    @Operation(summary = "删除镜像", responses = {
            @ApiResponse(description = "请求参数和返回结果参考：https://api.volcengine.com/api-docs/view?serviceCode=ecs&version=2020-04-01&action=DeleteImages")
    })
    @DeleteMapping("/del")
    public Result<DeleteImagesResponse> remove(List<String> imageIds) {
        EcsApi ecsApi = VoEngineApiClient.getEcsApi();
        try {
            DeleteImagesRequest request = new DeleteImagesRequest();
            request.setImageIds(imageIds);
            DeleteImagesResponse deleteImagesResponse = ecsApi.deleteImages(request);
            return Result.success(deleteImagesResponse);
        } catch (ApiException e) {
            return Result.error(e.getResponseBody());
        }
    }



}
