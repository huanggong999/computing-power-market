package com.lingyang.cloud.api.controller.system;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.lingyang.cloud.api.model.dto.PcContainerDetailsDTO;
import com.lingyang.cloud.api.model.dto.PcContainerListDTO;
import com.lingyang.cloud.api.model.dto.PcImageRepositoryDTO;
import com.lingyang.cloud.api.model.edit.PcInstancesStatusHandlerEdit;
import com.lingyang.cloud.api.model.query.PcConsoleEcsQuery;
import com.lingyang.cloud.api.model.vo.*;
import com.lingyang.cloud.api.service.pc.PcConsoleService;
import com.lingyang.cloud.api.service.pc.PcContainerService;
import com.lingyang.cloud.api.service.pc.PcImageRepositoryService;
import com.lingyang.cloud.entity.*;
import com.lingyang.cloud.enums.source.EcsStatusEnum;
import com.lingyang.cloud.enums.source.SourceRegionsEnum;
import com.lingyang.cloud.enums.system.StatusEnum;
import com.lingyang.cloud.mapper.SysCustomerMapper;
import com.lingyang.cloud.mapper.SysCustomerNetworkMapper;
import com.lingyang.cloud.mapper.SysExtendConfigMapper;
import com.lingyang.cloud.mapper.SysExtendMapper;
import com.lingyang.cloud.model.dto.ExtendRelevanceDTO;
import com.lingyang.cloud.model.dto.SysCompanyVerifyDTO;
import com.lingyang.cloud.model.query.coupon.SysCouponQuery;
import com.lingyang.cloud.model.query.customer.SysCompanyQuery;
import com.lingyang.cloud.model.query.customer.SysCustomerQuery;
import com.lingyang.cloud.model.vo.customer.SysCustomerDiscountBatchVO;
import com.lingyang.cloud.model.vo.customer.SysCustomerDiscountVO;
import com.lingyang.cloud.model.vo.customer.SysCustomerListVO;
import com.lingyang.cloud.service.*;
import com.lingyang.common.core.exception.http.HttpServiceException;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.utils.DateUtils;
import com.volcengine.cr.model.DeleteTagsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/10/30 11:00
 */
@RestController
@RequestMapping("/system/customer")
@Tag(name = "后台系统-客户相关")
public class SysCustomerController {

    @Resource
    private SysCustomerService sysCustomerService;
    @Resource
    private SysCustomerCouponService sysCustomerCouponService;
    @Resource
    private SysCouponService sysCouponService;
    @Resource
    private SysCustomerVoucherService sysCustomerVoucherService;
    @Resource
    private SysCustomerCreditLineService sysCustomerCreditLineService;

    @Autowired
    private SysCustomerMapper sysCustomerMapper;

    @Resource
    private PcConsoleService pcConsoleService;

    @Autowired
    private SysCustomerNetworkMapper sysCustomerNetworkMapper;

    @Resource
    private PcContainerService pcContainerService;
    @Autowired
    private SysExtendMapper sysExtendMapper;
    @Autowired
    private SysExtendConfigMapper sysExtendConfigMapper;

    @Resource
    private PcImageRepositoryService pcImageRepositoryService;

    @Operation(summary = "获取列表", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })
    @GetMapping("/page")
    public Result<PageResult<SysCustomerListVO>> getPage(SysCustomerQuery query) {
        PageResult<SysCustomerListVO> pageResult = sysCustomerService.getPage(PageQuery.build(query));
        return Result.success(pageResult);
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/delete/{customerId}")
    public Result<Boolean> delete(@PathVariable Long customerId) {
        return sysCustomerService.remove(customerId);
    }


    @Operation(summary = "赠送优惠卷", parameters = {
            @Parameter(name = "customerId", description = "客户id", in = ParameterIn.PATH)
    })
    @PutMapping("/giveCoupon/{customerId}")
    public Result<Void> giveCoupon(@PathVariable("customerId") Long customerId,
                                   @RequestBody @Parameter(description = "优惠卷id列表") List<Long> couponIdList) {
        if (ObjectUtils.isEmpty(couponIdList)) {
            return Result.fail();
        }
        SysCouponQuery query = new SysCouponQuery();
        query.setCouponIdList(couponIdList);
        return Result.result(sysCustomerCouponService.receiveCoupon(customerId, sysCouponService.getList(query)));
    }

    @Operation(summary = "添加代金卷")
    @PostMapping("/addCustomerVoucher")
    public Result<Void> addCustomerVoucher(@RequestBody @Valid SysCustomerVoucherEntity entity) {
        if (entity.getUseTimeEnd() != null && entity.getUseTimeEnd().before(new Date())) {
            // 时间小于当前时间的处理逻辑，例如返回错误信息
           throw new HttpServiceException("到期时间已过期，请重新选择");
        }
        entity.setUseAmount(BigDecimal.ZERO);
        entity.setStatus(StatusEnum.OK);
        entity.setUseTimeStart(entity.getUseTimeStart() == null ? DateUtils.getNowDate() : entity.getUseTimeStart());
        return Result.result(sysCustomerVoucherService.addCustomerVoucher(entity));
    }

    @Operation(summary = "修改客户资源折扣比列")
    @PutMapping("/updateCustomerDiscount")
    public Result<Void> updateCustomerDiscount(@RequestBody SysCustomerDiscountVO discountRation) {
        sysCustomerService.updateCustomerDiscount(discountRation);
        return Result.success();
    }

    @Operation(summary = "修改客户资源折扣比列（批量）")
    @PutMapping("/updateBatchCustomerDiscount")
    public Result<Void> updateBatchCustomerDiscount(@RequestBody SysCustomerDiscountBatchVO discountRation) {
        for (SysCustomerDiscountBatchVO.VV vv : discountRation.getList()) {
            SysCustomerDiscountVO s = new SysCustomerDiscountVO();
            s.setCustomerId(discountRation.getCustomerId());
            s.setSourceTypeList(Arrays.asList(vv.getSourceType()));
            s.setDiscountRation(vv.getDiscountRation());
            sysCustomerService.updateCustomerDiscount(s);
        }

        List<SysCustomerNetwork> customerNetworkList = discountRation.getCustomerNetworkList();
        if (CollectionUtil.isNotEmpty(customerNetworkList)) {
            sysCustomerNetworkMapper.delete(
                    new LambdaQueryWrapper<SysCustomerNetwork>()
                            .eq(SysCustomerNetwork::getUserId, discountRation.getCustomerId())
            );
            for (SysCustomerNetwork sysCustomerNetwork : customerNetworkList) {
                sysCustomerNetwork.setUserId(discountRation.getCustomerId());
            }
            sysCustomerNetworkMapper.batchInsert(customerNetworkList);
        } else {
            sysCustomerNetworkMapper.delete(
                    new LambdaQueryWrapper<SysCustomerNetwork>()
                            .eq(SysCustomerNetwork::getUserId, discountRation.getCustomerId())
            );
        }

        return Result.success();
    }

    @Operation(summary = "添加授信额")
    @PostMapping("/addCustomerCreditLine")
    public Result<Void> addCustomerCreditLine(@RequestBody @Valid SysCustomerCreditLineEntity entity) {
        entity.setUseAmount(BigDecimal.ZERO);
        entity.setStatus(StatusEnum.OK);
        entity.setUseTimeStart(entity.getUseTimeStart() == null ? DateUtils.getNowDate() : entity.getUseTimeStart());
        return Result.result(sysCustomerCreditLineService.addCustomerCreditLine(entity));
    }



    @Operation(summary = "企业认证列表", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })
    @GetMapping("/company/page")
    public Result<PageResult<SysCustomerEntity>> getCompanyPage(SysCompanyQuery query) {
        PageResult<SysCustomerEntity> pageResult = sysCustomerService.getCompanyPage(PageQuery.build(query));
        return Result.success(pageResult);
    }

    @Operation(summary = "审核企业认证")
    @PostMapping("/verifyCompany")
    public Result<Void> verifyCompany(@RequestBody SysCompanyVerifyDTO query) {

        sysCustomerMapper.update(null,
                new LambdaUpdateWrapper<SysCustomerEntity>()
                        .eq(SysCustomerEntity::getId, query.getId())
                        .set(SysCustomerEntity::getCompanyStatus, query.getCompanyStatus())
                        .set(query.getCompanyStatus() == 3, SysCustomerEntity::getType, 2)
                        .set(SysCustomerEntity::getCompanyVerifyRemark, query.getCompanyVerifyRemark())
                        .set(SysCustomerEntity::getCompanyVerifyTime, DateUtils.getNowDate())
                );
        return Result.success();
    }

    @Operation(summary = "获取实列列表", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })
    @GetMapping("/getInstancePageList")
    public Result<PageResult<PcConsoleEcsListVO>> getInstancePageList(PcConsoleEcsQuery query) {
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

    @Operation(summary = "删除容器")
    @DeleteMapping("/delete/container/{id}")
    public Result<Boolean> deleteContainer(@PathVariable Long id) {
        Boolean result = pcContainerService.deleteContainer(id);
        if (!result){
            return Result.error("删除失败");
        }
        return Result.success();
    }

    @Operation(summary = "容器列表")
    @PostMapping("/container/list")
    public Result<List<PcContainerListDTO>> containerList(@RequestBody PcContainerListVO vo) {
        return Result.success(pcContainerService.list(vo));
    }

    @Operation(summary = "容器详情")
    @GetMapping("/container/details/{id}")
    public Result<PcContainerDetailsDTO> containerDetails(@PathVariable Long id) {
        return Result.success(pcContainerService.details(id));
    }

    @Operation(summary = "镜像列表")
    @PostMapping("/image/repository/list")
    public Result<List<SysCustomerImageRepositoryEntity>> imageRepositoryList(@RequestBody PcImageRepositoryVO vo) {
        return Result.success(pcImageRepositoryService.list(vo));
    }

    @Operation(summary = "镜像详情")
    @GetMapping("detail/{instanceName}")
    public Result<PcImageRepositoryDTO> imageDetail(@PathVariable String instanceName) {
        return Result.success(pcImageRepositoryService.detail(instanceName));
    }

    @Operation(summary = "删除镜像")
    @PostMapping("/image/repository/delete/image")
    public Result<DeleteTagsResponse> deleteImage(@RequestBody PcImageRepositoryDeleteVO vo) {
        return Result.success(pcImageRepositoryService.deleteImage(vo));
    }

    @Value(
            "${extend.shareUrl}"
    )
    private String extendShare;
    @Operation(summary = "开通推广权限")
    @GetMapping("/openExtend")
    public Result<Void> openExtend(Long customerId) {
        SysExtend e = sysExtendMapper.selectOne(
                new LambdaQueryWrapper<SysExtend>()
                        .eq(SysExtend::getUserId, customerId)
        );
        if (e != null) {
            e.setId(e.getId());
        } else {
            e = new SysExtend();
        }
        SysExtendConfig extendConfig = sysExtendConfigMapper.selectById(1L);
        String uuid = null;
        String key = null;
        uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
        key = extendShare + "invite?code=" + uuid;

        e.setShareKey(uuid);
        e.setLink(key);
        if (e.getId() == null) {
            e.setType(1);
            e.setLevel(2);
            e.setStatus(2);
            e.setFirstScale(extendConfig.getFirstScale());
            e.setTwoScale(extendConfig.getTwoScale());
            e.setUserId(customerId);
            sysExtendMapper.insert(e);
        }else {
            e.setType(1);
            e.setUserId(customerId);
            e.setFirstScale(extendConfig.getFirstScale());
            e.setTwoScale(extendConfig.getTwoScale());
            e.setStatus(2);
            sysExtendMapper.updateById(e);
        }
        return Result.success();
    }

    @Operation(description = "修改上级")
    @PostMapping("/updateParent")
    public Result<Boolean> updateParent(@RequestBody ExtendRelevanceDTO dto){
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
            SysExtend sysExtend = new SysExtend();
            sysExtend.setType(2);
            sysExtend.setUserId(dto.getNextId());
            sysExtend.setLevel(3);
            sysExtend.setParentUserId(extend.getUserId());
            sysExtendMapper.insert(sysExtend);

            sysExtendMapper.update(null,
                    new LambdaUpdateWrapper<SysExtend>()
                            .set(SysExtend::getFirstCount, extend.getFirstCount() + 1)
                            .eq(SysExtend::getUserId, extend.getUserId())
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
        }
        return Result.success();
    }
}
