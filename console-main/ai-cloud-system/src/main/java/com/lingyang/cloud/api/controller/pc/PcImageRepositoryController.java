package com.lingyang.cloud.api.controller.pc;

import com.lingyang.cloud.api.model.dto.PcImageOciOverviewDTO;
import com.lingyang.cloud.api.model.dto.PcImageOciOverviewPageDTO;
import com.lingyang.cloud.api.model.dto.PcImageRepositoryDTO;
import com.lingyang.cloud.api.model.query.PcImageOciOverviewQuery;
import com.lingyang.cloud.api.model.vo.PcImageRepositoryDeleteVO;
import com.lingyang.cloud.api.model.vo.PcImageRepositoryVO;
import com.lingyang.cloud.api.service.pc.PcImageRepositoryService;
import com.lingyang.cloud.entity.SysCustomerImageRepositoryEntity;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.security.SecurityContext;
import com.volcengine.cr.model.DeleteTagsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/2/17 10:58
 */
@RestController
@RequestMapping("/pc/image/repository")
@Tag(name = "pc端-镜像仓库")
public class PcImageRepositoryController {

    @Resource
    private PcImageRepositoryService pcImageRepositoryService;

    @Operation(summary = "镜像列表")
    @PostMapping("list")
    public Result<List<SysCustomerImageRepositoryEntity>> list(@RequestBody PcImageRepositoryVO vo) {
        Long userId = SecurityContext.getUserInfo().getUserId();
        vo.setCustomerId(userId);
        return Result.success(pcImageRepositoryService.list(vo));
    }

    @Operation(summary = "镜像详情")
    @GetMapping("detail/{instanceName}")
    public Result<PcImageRepositoryDTO> detail(@PathVariable String instanceName) {
        return Result.success(pcImageRepositoryService.detail(instanceName));
    }

    @Operation(summary = "设置仓库实例密码")
    @PostMapping("set/password")
    public Result<Void> setPassword(@RequestBody PcImageRepositoryVO vo) {
        Boolean flag = pcImageRepositoryService.setPassword(vo);
        return Result.result(flag);
    }

    @Operation(summary = "OCI概览信息")
    @GetMapping("oci/overview/{instanceName}")
    public Result<PcImageOciOverviewDTO> ociOverview(@PathVariable String instanceName) {
        return Result.success(pcImageRepositoryService.ociOverview(instanceName));
    }

    @Operation(summary = "OCI概览信息分页", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })
    @PostMapping("oci/overview/page")
    public Result<List<PcImageOciOverviewPageDTO>> page(@RequestBody PcImageOciOverviewQuery query) {
        return pcImageRepositoryService.page(query);
    }

    @Operation(summary = "获取镜像的username")
    @GetMapping("get/image/username")
    public Result<String> getUsername() {
        return Result.success(pcImageRepositoryService.getUsername());
    }

    @Operation(summary = "删除镜像")
    @PostMapping("delete/image")
    public Result<DeleteTagsResponse> deleteImage(@RequestBody PcImageRepositoryDeleteVO vo) {
        return Result.success(pcImageRepositoryService.deleteImage(vo));
    }

    @Operation(summary = "判断镜像名称是否已存在,true 已存在     false 不存在")
    @GetMapping("image/name/exist/{imageName}")
    public Result<Boolean> imageNameExist(@PathVariable String imageName) {
        return Result.success(pcImageRepositoryService.imageNameExist(imageName));
    }
}
