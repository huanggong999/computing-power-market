package com.lingyang.cloud.api.controller.pc;

import com.lingyang.cloud.api.model.edit.PcInstancesStatusHandlerEdit;
import com.lingyang.cloud.api.model.query.PcConsoleEcsQuery;
import com.lingyang.cloud.api.model.vo.PcConsoleEcsDetailVO;
import com.lingyang.cloud.api.model.vo.PcConsoleEcsListVO;
import com.lingyang.cloud.api.model.vo.PcConsoleHomeVO;
import com.lingyang.cloud.api.model.vo.PcConsoleSourceInfoVO;
import com.lingyang.cloud.api.service.pc.PcConsoleService;
import com.lingyang.cloud.enums.source.EcsStatusEnum;
import com.lingyang.cloud.enums.source.SourceRegionsEnum;
import com.lingyang.cloud.model.vo.applets.AppletsEcsWorkOperationVO;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.security.SecurityContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/19 18:02
 */
@Tag(name = "pc端-控制台")
@RestController
@RequestMapping("/pc/console")
public class PcConsoleController {

    @Resource
    private PcConsoleService pcConsoleService;

    @GetMapping("/home")
    @Operation(summary = "获取控制台主页数据")
    public Result<PcConsoleHomeVO> getHome() {
        return Result.success(pcConsoleService.getHome(SecurityContext.getUserInfo().getUserId()));
    }

    @Operation(summary = "获取资源总览")
    @GetMapping("/getSourceInfo")
    public Result<PcConsoleSourceInfoVO> getSourceInfo() {
        return Result.success(pcConsoleService.getSourceInfo(SecurityContext.getUserInfo().getUserId()));
    }

    @Operation(summary = "获取实列列表", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })
    @GetMapping("/getInstancePageList")
    public Result<PageResult<PcConsoleEcsListVO>> getInstancePageList(PcConsoleEcsQuery query) {
        query.setCustomerId(SecurityContext.getUserInfo().getUserId());
        return Result.success(pcConsoleService.getInstancePageList(PageQuery.build(query)));
    }

    @Operation(summary = "test", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })
    @GetMapping("/test")
    public Result<PageResult<PcConsoleEcsListVO>> test(PcConsoleEcsQuery query) {
        query.setCustomerId(5L);
        return Result.success(pcConsoleService.getInstancePageList(PageQuery.build(query)));
    }

    @Operation(summary = "获取实列详情")
    @GetMapping("/getInstanceDetail/{id}")
    public Result<PcConsoleEcsDetailVO> getInstanceDetail(@PathVariable("id") String id) {
        return Result.success(pcConsoleService.getInstanceDetail(id));
    }


    @Operation(summary = "批量启动实列")
    @PutMapping("/batchStartInstances/{sourceRegIons}")
    public Result<Void> batchStartInstances(@RequestBody @Parameter(name = "idList", description = "实列id集合", required = true) List<Long> idList,
                                            @PathVariable @Parameter(name = "sourceRegIons", description = "实列所属地域", in = ParameterIn.PATH, required = true)
                                            SourceRegionsEnum sourceRegIons) {
        PcInstancesStatusHandlerEdit edit = new PcInstancesStatusHandlerEdit();
        edit.setIdList(idList);
        edit.setStatusEnum(EcsStatusEnum.STARTING);
        pcConsoleService.handlerInstancesStatus(edit, sourceRegIons);
        return Result.success();
    }

    @Operation(summary = "批量停止实列")
    @PutMapping("/batchStopInstances/{sourceRegIons}")
    public Result<Void> batchStopInstances(@RequestBody PcInstancesStatusHandlerEdit edit,
                                           @PathVariable @Parameter(name = "sourceRegIons", description = "实列所属地域", in = ParameterIn.PATH, required = true)
                                           SourceRegionsEnum sourceRegIons) {
        edit.setStatusEnum(EcsStatusEnum.STOPPING);
        pcConsoleService.handlerInstancesStatus(edit, sourceRegIons);
        return Result.success();
    }

    @Operation(summary = "批量重启实列")
    @PutMapping("/batchRestartInstances/{sourceRegIons}")
    public Result<Void> batchRestartInstances(@RequestBody @Parameter(name = "idList", description = "实列id集合", required = true) List<Long> idList,
                                              @PathVariable @Parameter(name = "sourceRegIons", description = "实列所属地域", in = ParameterIn.PATH, required = true)
                                              SourceRegionsEnum sourceRegIons) {
        PcInstancesStatusHandlerEdit edit = new PcInstancesStatusHandlerEdit();
        edit.setIdList(idList);
        edit.setStatusEnum(EcsStatusEnum.REBOOTING);
        pcConsoleService.handlerInstancesStatus(edit, sourceRegIons);
        return Result.success();
    }

    @Operation(summary = "批量删除实列")
    @PutMapping("/batchDeletedInstances/{sourceRegIons}")
    public Result<Void> batchDeletedInstances(@RequestBody @Parameter(name = "idList", description = "实列id集合", required = true) List<Long> idList,
                                              @PathVariable @Parameter(name = "sourceRegIons", description = "实列所属地域", in = ParameterIn.PATH, required = true)
                                              SourceRegionsEnum sourceRegIons) {
        PcInstancesStatusHandlerEdit edit = new PcInstancesStatusHandlerEdit();
        edit.setIdList(idList);
        edit.setStatusEnum(EcsStatusEnum.DELETING);
        pcConsoleService.handlerInstancesStatus(edit, sourceRegIons);
        return Result.success();
    }

    @Operation(summary = "自建服务器用户申请操作")
    @PostMapping("/server/apply")
    public Result<Boolean> serverApply(@RequestBody AppletsEcsWorkOperationVO vo) {
        return Result.success(pcConsoleService.serverApply(vo));
    }
}