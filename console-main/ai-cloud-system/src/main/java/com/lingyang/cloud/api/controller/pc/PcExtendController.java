package com.lingyang.cloud.api.controller.pc;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.lingyang.cloud.config.WatermarkUtil;
import com.lingyang.cloud.config.WeiXinConfig;
import com.lingyang.cloud.entity.*;
import com.lingyang.cloud.enums.order.OrderStatusEnum;
import com.lingyang.cloud.mapper.*;
import com.lingyang.cloud.model.dto.FileUploadDTO;
import com.lingyang.cloud.model.edit.order.SysOrderCreateDTO;
import com.lingyang.cloud.model.query.home.SysExtendOrderQuery;
import com.lingyang.cloud.model.query.home.SysExtendWithQuery;
import com.lingyang.cloud.model.query.home.SysFirstExtendQuery;
import com.lingyang.cloud.model.vo.FileUploadVO;
import com.lingyang.cloud.model.vo.order.ExtendOrderVO;
import com.lingyang.cloud.model.vo.order.OrderCreateVO;
import com.lingyang.cloud.service.FileService;
import com.lingyang.cloud.service.SysExtendService;
import com.lingyang.cloud.service.SysExtendWithdrawalRecordService;
import com.lingyang.cloud.service.SysOrderService;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.security.SecurityContext;
import com.lingyang.common.core.security.model.LoginUserInfoDetail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

@Slf4j
@Tag(name = "pc端-推广客户")
@RestController
@RequestMapping("/pc/extend")
public class PcExtendController {

    @Autowired
    private SysExtendService sysExtendService;

    @Autowired
    private SysExtendMapper sysExtendMapper;

    @Autowired
    private SysMessageMapper sysMessageMapper;
    @Autowired
    private SysExtendConfigMapper sysExtendConfigMapper;

    @Autowired
    private SysExtendWithdrawalRecordMapper sysExtendWithdrawalRecordMapper;
    @Autowired
    private SysExtendWithdrawalRecordService sysExtendWithdrawalRecordService;

    @Autowired
    private SysOrderMapper sysOrderMapper;

    @Autowired
    private SysOrderService sysOrderService;

    @Autowired
    private WeiXinConfig weiXinConfig;

    @Resource
    private FileService fileService;


    @Operation(summary = "查询推广配置")
    @GetMapping("/config-detail")
    public Result<SysExtendConfig> configDetail() {
        SysExtendConfig config = sysExtendConfigMapper.selectById(1L);
        return Result.success(config);
    }



    @Operation(summary = "查询推广大使详情")
    @GetMapping("/detail")
    public Result<SysExtend> detail() throws IOException {
        LoginUserInfoDetail userInfo = SecurityContext.getUserInfo();
        SysExtend selectOne = sysExtendMapper.selectOne(
                new LambdaQueryWrapper<SysExtend>()
                        .eq(SysExtend::getUserId, userInfo.getUserId())
                        .eq(SysExtend::getType, 1)
        );
        if (selectOne == null) {
            selectOne = new SysExtend();
            selectOne.setStatus(null);
        } else {
            Long count = sysExtendMapper.selectCount(
                    new LambdaQueryWrapper<SysExtend>()
                            .eq(SysExtend::getParentUserId, userInfo.getUserId())
            );
            selectOne.setExtendCustomerCount(count);
            SysOrderEntity order = sysOrderMapper.selectOne(
                    new QueryWrapper<SysOrderEntity>()
                            .select(" count(*) as first_user_id ")
                            .eq("first_user_id", selectOne.getUserId())
                            .eq("order_status", OrderStatusEnum.PAID.getCode())
                            .groupBy("first_user_id")
            );
            if (order != null) {
                selectOne.setCjCustomerCount(order.getFirstUserId());
            }

            // 获取小程序二维码
            if (StringUtils.isBlank(selectOne.getQrCodeLink())) {
                InputStream qrCodeInputStream = weiXinConfig.getQrCodeInputStream("pages/index/index", "t=" + selectOne.getShareKey());
                FileUploadDTO d = new FileUploadDTO();
                d.setInputStream(qrCodeInputStream);
                FileUploadVO fileUploadVO = fileService.uploadInputStream(d);

                BufferedImage image = WatermarkUtil.getLinkCode(fileUploadVO.getUrl());
                ByteArrayOutputStream o1 = new ByteArrayOutputStream();
                ByteArrayInputStream i1 = null;
                ImageIO.write(image, "png", o1);
                i1 = new ByteArrayInputStream(o1.toByteArray());
                FileUploadDTO p = new FileUploadDTO();
                p.setInputStream(i1);
                FileUploadVO pp = fileService.uploadInputStream(p);
                selectOne.setQrCodeLink(pp.getUrl());
                sysExtendMapper.update(
                        null,
                        new LambdaUpdateWrapper<SysExtend>()
                                .eq(SysExtend::getUserId, userInfo.getUserId())
                                .set(SysExtend::getQrCodeLink, pp.getUrl())
                );
            }


        }

        return Result.success(selectOne);
    }

    @Operation(summary = "修改推广大使收款人信息")
    @PostMapping("/updateBank")
    public Result<Void> updateBank(@RequestBody SysExtend data) {
        data.setUserId(SecurityContext.getUserInfo().getUserId());
        sysExtendMapper.updateById(data);
        return Result.success();
    }

    @Operation(summary = "推广大使提现")
    @GetMapping("/with")
    public Result<Void> with(BigDecimal amount) {
        LoginUserInfoDetail userInfo = SecurityContext.getUserInfo();
        SysExtend selectOne = sysExtendMapper.selectOne(
                new LambdaQueryWrapper<SysExtend>()
                        .eq(SysExtend::getUserId, userInfo.getUserId())
                        .eq(SysExtend::getType, 1)
        );
        if (amount.compareTo(selectOne.getCanWithdrawalAmount()) > 0) {
            return Result.error("提现金额大于可提现金额");
        }
        // 新增提现记录
        SysExtendConfig extendConfig = sysExtendConfigMapper.selectById(1L);
        SysExtendWithdrawalRecord record = new SysExtendWithdrawalRecord();
        BigDecimal multiply = amount.multiply(extendConfig.getWithServiceScale());
        record.setUserId(userInfo.getUserId());
        record.setWithdrawalAmount(amount.subtract(multiply));
        record.setTotalAmount(amount);
        record.setServiceAmount(multiply);
        record.setServiceRate(extendConfig.getWithServiceScale());
        record.setOrderNo("TX" + System.currentTimeMillis());
        record.setStatus(1);
        // 银行卡这些也填充一下
        record.setAddress(selectOne.getAddress());
        record.setBank(selectOne.getBank());
        record.setBankNo(selectOne.getBankNo());
        record.setIdCard(selectOne.getIdCard());
        record.setBankUserName(selectOne.getBankUserName());
        sysExtendWithdrawalRecordMapper.insert(record);


        // 扣除提现金额
        LambdaUpdateWrapper<SysExtend> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SysExtend::getUserId, userInfo.getUserId())
                .set(SysExtend::getCanWithdrawalAmount, selectOne.getCanWithdrawalAmount().subtract(amount));
        sysExtendMapper.update(null, updateWrapper);
        return Result.success();
    }


    @Operation(summary = "申请推广大使")
    @PostMapping("/apply")
    public Result<Void> apply(@RequestBody SysExtend e) {
        LoginUserInfoDetail userInfo = SecurityContext.getUserInfo();
        SysExtend selectOne = sysExtendMapper.selectOne(
                new LambdaQueryWrapper<SysExtend>()
                        .eq(SysExtend::getUserId, userInfo.getUserId())
        );
        if (selectOne != null) {
            if (selectOne.getType().equals(1) && selectOne.getStatus().equals(1)) {
                return Result.error("你已经提交过资料，正在审核中");
            }
            e.setId(selectOne.getId());
        }
        SysExtendConfig extendConfig = sysExtendConfigMapper.selectById(1L);
        if (e.getId() == null) {
            e.setType(1);
            e.setLevel(2);
            e.setStatus(1);
            e.setFirstScale(extendConfig.getFirstScale());
            e.setTwoScale(extendConfig.getTwoScale());
            e.setUserId(userInfo.getUserId());
            sysExtendMapper.insert(e);
        }else {
            e.setType(1);
            e.setUserId(userInfo.getUserId());
            e.setFirstScale(extendConfig.getFirstScale());
            e.setTwoScale(extendConfig.getTwoScale());
            e.setStatus(1);
            sysExtendMapper.updateById(e);
        }


        SysMessage m = new SysMessage();
        m.setMsgType(3);
        m.setText("申请推广大使");
        m.setStatus(1);
        m.setUserId(userInfo.getUserId());

        sysMessageMapper.insert(m);


        return Result.success();
    }

    @Operation(summary = "根据邀请码查询推广大使")
    @GetMapping("/getByCode")
    public Result<SysExtend> getByCode(@RequestParam(required = false) String code) {
        if (StringUtils.isBlank(code)) {
            return Result.error("缺少code");
        }
        SysExtend extend =  sysExtendMapper.selectOne(
                new LambdaQueryWrapper<SysExtend>()
                        .eq(SysExtend::getShareKey, code)
        );

        if (extend == null) {
            return Result.error("code无效");
        }

        return Result.success(extend);
    }

    public static void main(String[] args) {
        System.out.println("t%253D66EAA99D29".replace("t%253D", ""));
    }


    @Operation(summary = "关联推广大使（已有账号关联）")
    @GetMapping("/relevance")
    public Result<Void> relevance(String code) {
        log.info("sdfsdfsdf {}", code);
        if (code.contains("t=")) {
            code = code.replace("t=", "");
        }
        log.info("22222222222sdfsdfsdf {}", code);
        SysExtend extend =  sysExtendMapper.selectOne(
                new LambdaQueryWrapper<SysExtend>()
                        .eq(SysExtend::getShareKey, code)
        );

        if (extend == null) {
            return Result.success();
        }

        LoginUserInfoDetail userInfo = SecurityContext.getUserInfo();
        SysExtend selectOne = sysExtendMapper.selectOne(
                new LambdaQueryWrapper<SysExtend>()
                        .eq(SysExtend::getUserId, userInfo.getUserId())
        );
        if (selectOne != null) {
            if (selectOne.getParentUserId() != null && selectOne.getParentUserId() != 0) {
                return Result.success();
            }

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
                            .eq(SysExtend::getUserId, userInfo.getUserId())
            );
        } else {
            selectOne = new SysExtend();
            selectOne.setType(2);
            selectOne.setLevel(3);
            selectOne.setParentUserId(extend.getUserId());
            selectOne.setUserId(userInfo.getUserId());
            sysExtendMapper.insert(selectOne);
        }
        return Result.success();
    }


    @Operation(summary = "获取推广客户列表", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })
    @GetMapping("/first-page")
    public Result<PageResult<SysExtend>> firstPage(SysFirstExtendQuery query) {
        LoginUserInfoDetail userInfo = SecurityContext.getUserInfo();

        if (query.getType() == null) {
            query.setType(1);
        }
        query.setUserId(userInfo.getUserId());
        return sysExtendService.getFirstPage(PageQuery.build(query));
    }

    @Operation(summary = "获取提现分页列表", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })
    @GetMapping("/withPage")
    public Result<PageResult<SysExtendWithdrawalRecord>> withPage(SysExtendWithQuery query) {
        LoginUserInfoDetail userInfo = SecurityContext.getUserInfo();
        query.setUserId(userInfo.getUserId());
        return sysExtendWithdrawalRecordService.getPage(PageQuery.build(query));
    }


    @Operation(summary = "获取分佣订单分页列表", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })
    @GetMapping("/orderPage")
    public Result<PageResult<ExtendOrderVO>> orderPage(SysExtendOrderQuery query) {
        query.setUserId(
                SecurityContext.getUserInfo().getUserId()
        );
        return sysOrderService.getExtendPage(PageQuery.build(query));
    }

}
