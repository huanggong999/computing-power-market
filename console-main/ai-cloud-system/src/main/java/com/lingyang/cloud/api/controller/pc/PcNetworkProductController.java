package com.lingyang.cloud.api.controller.pc;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.entity.*;
import com.lingyang.cloud.enums.source.SourceTypeEnum;
import com.lingyang.cloud.mapper.*;
import com.lingyang.cloud.model.dto.PcNetworkFormAddDTO;
import com.lingyang.cloud.model.query.home.SysHomeEcsQuery;
import com.lingyang.cloud.model.query.home.SysNetworkValueQuery;
import com.lingyang.cloud.model.vo.product.SysProductIpEnoughVO;
import com.lingyang.cloud.service.SysNetworkProductService;
import com.lingyang.cloud.service.SysNetworkValueService;
import com.lingyang.cloud.take.MyTask;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.security.SecurityContext;
import com.lingyang.common.core.security.model.LoginUserInfoDetail;
import com.lingyang.common.core.utils.DateUtils;
import com.lingyang.common.core.utils.Optional;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "pc端-网络产品")
@RestController
@RequestMapping("/pc/network-product")
public class PcNetworkProductController {

    @Autowired
    private SysNetworkFormMapper sysNetworkFormMapper;

    @Autowired
    private SysNetworkProductMapper sysNetworkProductMapper;

    @Autowired
    private SysNetworkValueMapper sysNetworkValueMapper;

    @Autowired
    private SysNetworkProductService sysNetworkProductService;
    @Autowired
    private SysNetworkValueService sysNetworkValueService;

    @Autowired
    private SysCustomerNetworkMapper sysCustomerNetworkMapper;


    @Resource
    private SysCustomerDiscountMapper sysCustomerDiscountMapper;

    @Autowired
    private SysMessageMapper sysMessageMapper;

    @Autowired
    private MyTask myTask;


    @Operation(summary = "获取分页列表", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })
    @GetMapping("/page")
    public Result<PageResult<SysNetworkProductEntity>> page(SysHomeEcsQuery query) {
        query.setStatus(1);
        return sysNetworkProductService.getPage(PageQuery.build(query));
    }


    @Operation(summary = "查询产品详情")
    @GetMapping("/{id}")
    public Result<SysNetworkProductEntity> detail(@PathVariable("id") Long id) {
        return Result.success(sysNetworkProductService.getById(id));
    }

    @Operation(summary = "查询产品咨询表单")
    @GetMapping("/form/{id}")
    public Result<SysNetworkFormEntity> formDetail(@PathVariable("id") Long id) {
        SysNetworkProductEntity service = sysNetworkProductService.getById(id);
        return Result.success(sysNetworkFormMapper.selectById(service.getFormId()));
    }

    @Operation(summary = "查询产品支付表单")
    @GetMapping("/pay-form/{id}")
    public Result<SysNetworkFormEntity> formPayDetail(@PathVariable("id") Long id) {
        SysNetworkProductEntity service = sysNetworkProductService.getById(id);
        return Result.success(sysNetworkFormMapper.selectById(service.getPayFormId()));
    }

    @Operation(summary = "新增产品咨询记录")
    @PostMapping("/form/add")
    public Result<SysNetworkFormEntity> formAdd(@RequestBody PcNetworkFormAddDTO dto) {
        SysNetworkProductEntity entity1 = sysNetworkProductMapper.selectById(dto.getProductId());
        SysNetworkFormEntity formEntity = sysNetworkFormMapper.selectById(entity1.getFormId());
        LoginUserInfoDetail userInfo = SecurityContext.getUserInfo();
        SysNetworkValueEntity entity = new SysNetworkValueEntity();
        entity.setFormType(1);
        entity.setProductId(dto.getProductId());
        entity.setFormId(formEntity.getId());
        entity.setUserId(userInfo.getUserId());
        entity.setProductName(entity1.getName());
        entity.setFormName(formEntity.getName());
        entity.setJson(dto.getJson());
        sysNetworkValueMapper.insert(entity);

        SysMessage m = new SysMessage();
        m.setMsgType(1);
        m.setText("新增AGIC产品咨询");
        m.setStatus(1);
        m.setUserId(userInfo.getUserId());

        sysMessageMapper.insert(m);

        return Result.success();
    }

    @Operation(summary = "新增购买咨询记录")
    @PostMapping("/form/addPay")
    public Result<Long> formAddPay(@RequestBody PcNetworkFormAddDTO dto) {
        SysNetworkProductEntity entity1 = sysNetworkProductMapper.selectById(dto.getProductId());
        SysNetworkFormEntity formEntity = sysNetworkFormMapper.selectById(entity1.getFormId());
        LoginUserInfoDetail userInfo = SecurityContext.getUserInfo();


        // 首次买
        if (dto.getNoOrNoXufei()) {
            SysNetworkValueEntity entity = new SysNetworkValueEntity();
            entity.setFormType(2);
            entity.setProductId(dto.getProductId());
            entity.setFormId(formEntity.getId());
            entity.setUserId(userInfo.getUserId());
            entity.setProductName(entity1.getName());
            entity.setFormName(formEntity.getName());
            entity.setBandwidth(dto.getBandwidth());
            entity.setNetworkCount(dto.getNetworkCount());
            entity.setNetworkDay(dto.getNetworkDay());
            entity.setIpCount(dto.getIpCount());
            entity.setJson(dto.getJson());
            entity.setActualStatus(1);
            entity.setPayStatus(0);
            entity.setOriginalAmount(entity1.getPayPrice().multiply(
                            BigDecimal.valueOf(dto.getNetworkDay())
                    ).multiply(BigDecimal.valueOf(dto.getBandwidth()))
                    .multiply(BigDecimal.valueOf(dto.getNetworkCount())));

            // 处理折扣
            List<SysCustomerNetwork> networks = sysCustomerNetworkMapper.selectList(
                    new LambdaQueryWrapper<SysCustomerNetwork>()
                            .eq(SysCustomerNetwork::getUserId, userInfo.getUserId())
                            .eq(SysCustomerNetwork::getNetworkId, entity1.getId())
            );
            BigDecimal orDefault = null;
            if (CollectionUtils.isNotEmpty(networks)) {
                SysCustomerNetwork e = networks.get(0);
                orDefault = e.getDiscountRation();
            } else {
                // 处理平台溢价
                Map<SourceTypeEnum, BigDecimal> sourceDiscount = Optional.of(sysCustomerDiscountMapper.selectList(Wrappers.lambdaQuery(SysCustomerDiscountEntity.class)
                                .eq(SysCustomerDiscountEntity::getCustomerId, SecurityContext.getUserInfo().getUserId())))
                        .flatMap(list -> list.stream().collect(Collectors.toMap(SysCustomerDiscountEntity::getSourceType, SysCustomerDiscountEntity::getDiscountRation)))
                        .orElse(new HashMap<>(0));
                orDefault = sourceDiscount.get(SourceTypeEnum.AGIC);
            }

            if (orDefault == null || orDefault.compareTo(BigDecimal.ONE) == 0) {
                // sysNetworkProductEntity.setPayPrice(e.getDiscountRation().setScale(2, RoundingMode.UP));
                entity.setUnitPrice(entity1.getPayPrice());
            } else {
                entity.setUnitPrice(entity1.getPayPrice().multiply(orDefault).setScale(2, RoundingMode.UP));
            }

            sysNetworkValueMapper.insert(entity);


            Date date = new Date();
            sysNetworkValueMapper.update(null,
                    new LambdaUpdateWrapper<SysNetworkValueEntity>()
                            .set(SysNetworkValueEntity::getActualStatus, 2)
                            .set(SysNetworkValueEntity::getActualAgiOpenTime, date)
//                            .set(SysNetworkValueEntity::getUserpwd, userpwd)
                            .set(SysNetworkValueEntity::getActualAgiExpireTime, DateUtils.addDays(date, entity.getNetworkDay()))
                            .set(SysNetworkValueEntity::getOriginalAmount, entity1.getPayPrice().multiply(
                                            BigDecimal.valueOf(entity.getNetworkDay())
                                    ).multiply(BigDecimal.valueOf(entity.getBandwidth()))
                                    .multiply(BigDecimal.valueOf(entity.getNetworkCount())))
                            .eq(SysNetworkValueEntity::getId, entity.getId())

            );

            SysMessage m = new SysMessage();
            m.setMsgType(2);
            m.setText("新增AGIC购买咨询");
            m.setStatus(1);
            m.setUserId(userInfo.getUserId());

            sysMessageMapper.insert(m);
            return Result.success(entity.getId());
        } else {
            SysNetworkValueEntity entity = new SysNetworkValueEntity();
            entity.setFormType(2);
            entity.setProductId(dto.getProductId());
            entity.setFormId(formEntity.getId());
            entity.setUserId(userInfo.getUserId());
            entity.setProductName(entity1.getName());
            entity.setFormName(formEntity.getName());
            entity.setBandwidth(dto.getBandwidth());
            entity.setNetworkCount(dto.getNetworkCount());
            entity.setNetworkDay(dto.getNetworkDay());
            entity.setJson(dto.getJson());
            entity.setActualStatus(1);
            entity.setPayStatus(0);
            entity.setOriginalAmount(entity1.getPayPrice().multiply(
                            BigDecimal.valueOf(dto.getNetworkDay())
                    ).multiply(BigDecimal.valueOf(dto.getBandwidth()))
                    .multiply(BigDecimal.valueOf(dto.getNetworkCount())));

            // 处理折扣
            List<SysCustomerNetwork> networks = sysCustomerNetworkMapper.selectList(
                    new LambdaQueryWrapper<SysCustomerNetwork>()
                            .eq(SysCustomerNetwork::getUserId, userInfo.getUserId())
                            .eq(SysCustomerNetwork::getNetworkId, entity1.getId())
            );
            BigDecimal orDefault = null;
            if (CollectionUtils.isNotEmpty(networks)) {
                SysCustomerNetwork e = networks.get(0);
                orDefault = e.getDiscountRation();
            } else {
                // 处理平台溢价
                Map<SourceTypeEnum, BigDecimal> sourceDiscount = Optional.of(sysCustomerDiscountMapper.selectList(Wrappers.lambdaQuery(SysCustomerDiscountEntity.class)
                                .eq(SysCustomerDiscountEntity::getCustomerId, SecurityContext.getUserInfo().getUserId())))
                        .flatMap(list -> list.stream().collect(Collectors.toMap(SysCustomerDiscountEntity::getSourceType, SysCustomerDiscountEntity::getDiscountRation)))
                        .orElse(new HashMap<>(0));
                orDefault = sourceDiscount.get(SourceTypeEnum.AGIC);
            }

            if (orDefault == null || orDefault.compareTo(BigDecimal.ONE) == 0) {
                // sysNetworkProductEntity.setPayPrice(e.getDiscountRation().setScale(2, RoundingMode.UP));
                entity.setUnitPrice(entity1.getPayPrice());
            } else {
                entity.setUnitPrice(entity1.getPayPrice().multiply(orDefault).setScale(2, RoundingMode.UP));
            }

            sysNetworkValueMapper.insert(entity);

            SysMessage m = new SysMessage();
            m.setMsgType(2);
            m.setText("新增AGIC购买咨询");
            m.setStatus(1);
            m.setUserId(userInfo.getUserId());

            sysMessageMapper.insert(m);
            return Result.success();
        }




    }

    @Operation(summary = "获取分页列表", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })
    @GetMapping("/payValue/page")
    public Result<PageResult<SysNetworkValueEntity>> valuePage(SysNetworkValueQuery query) {
        return sysNetworkValueService.getPcPage(PageQuery.build(query));
    }

    @Operation(summary = "AGI-C 停线")
    @GetMapping("/stop")
    public Result<Boolean> stop(@RequestParam Long valueId) {
        return sysNetworkValueService.stop(valueId);
    }

    @Operation(summary = "AGI-C 上线")
    @GetMapping("/start")
    public Result<Boolean> start(@RequestParam Long valueId) {
        return sysNetworkValueService.start(valueId);
    }

    @Operation(summary = "AGI-C 撤线")
    @GetMapping("/cancel")
    public Result<Boolean> cancel(@RequestParam Long valueId) {
        return sysNetworkValueService.cancel(valueId);
    }

    @Operation(summary = "AGI-C 账户重置密码")
    @GetMapping("/reset/password")
    public Result<Boolean> resetPassword(@RequestParam String email) {
        return sysNetworkValueService.resetPassword(email);
    }

    @Operation(summary = "判断产品IP库IP数是否足够")
    @PostMapping("/ip/enough")
    public Result<Boolean> ipEnough(@RequestBody SysProductIpEnoughVO vo) {
        return sysNetworkValueService.ipEnough(vo);
    }

    @GetMapping("/test")
    public void test() {
        myTask.aGICDueDateReminder();
    }
}
