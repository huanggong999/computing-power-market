package com.lingyang.cloud.api.controller.pc;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.api.model.dto.PcRealNameDTO;
import com.lingyang.cloud.api.model.vo.PcRealNameVO;
import com.lingyang.cloud.common.login.model.LoginCodeInfoVO;
import com.lingyang.cloud.common.login.model.PcLoginUserInfo;
import com.lingyang.cloud.config.WeiXinConfig;
import com.lingyang.cloud.config.dto.UnionIdDTO;
import com.lingyang.cloud.config.vo.WxSessionResultVO;
import com.lingyang.cloud.entity.*;
import com.lingyang.cloud.mapper.*;
import com.lingyang.cloud.model.dto.CompanyDTO;
import com.lingyang.cloud.model.dto.FileUploadDTO;
import com.lingyang.cloud.model.dto.SysActiveCouponDTO;
import com.lingyang.cloud.model.dto.SysActiveCouponDetailsDTO;
import com.lingyang.cloud.model.edit.customer.PcCustomerEdit;
import com.lingyang.cloud.model.edit.customer.PcCustomerInfoEdit;
import com.lingyang.cloud.model.query.coupon.SysCustomerCouponQuery;
import com.lingyang.cloud.model.vo.FileUploadVO;
import com.lingyang.cloud.model.vo.customer.SysCustomerCouponListVO;
import com.lingyang.cloud.service.*;
import com.lingyang.cloud.utils.SendSmsUtils;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.security.SecurityContext;
import com.lingyang.common.core.utils.BeanUtils;
import com.lingyang.common.core.utils.RandomUtils;
import com.lingyang.common.core.utils.StringUtils;
import com.lingyang.common.security.exception.LoginException;
import com.lingyang.common.security.utils.PasswordUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/10/21 16:59
 */
@Slf4j
@RestController
@Tag(name = "pc端-用户相关")
@RequestMapping("/pc/customer")
public class PcCustomerController {

    @Autowired
    private SysExtendMapper sysExtendMapper;

    @Autowired
    private SysActiveRecordMapper sysActiveRecordMapper;

    @Autowired
    private SysActiveCouponMapper sysActiveCouponMapper;

    @Resource
    private SysCustomerService sysCustomerService;

    @Resource
    private SysCustomerMapper sysCustomerMapper;

    @Resource
    private SysCustomerCertLogMapper sysCustomerCertLogMapper;

    @Resource
    private SysCustomerCouponService sysCustomerCouponService;

    @Resource
    private SysCustomerVoucherService sysCustomerVoucherService;

    @Resource
    private SysCustomerBillService sysCustomerBillService;

    @Resource
    private SysCustomerCreditLineService sysCustomerCreditLineService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private WeiXinConfig weiXinConfig;

    @Resource
    private FileService fileService;

    @Autowired
    private SysInvoiceManageService sysInvoiceManageService;


    @GetMapping("/getLoginPcCodeUrl")
    @Operation(summary = "获取PC登录微信二维码链接")
    public Result<String> getLoginPcCodeUrl() {
       return Result.success("https://open.weixin.qq.com/connect/qrconnect?appid=wx577c07baa5a6a681&redirect_uri=https%3A%2F%2Feyunai.net%2F%23%2Flogin&response_type=code&scope=snsapi_login#wechat_redirect");
    }


    @Operation(summary = "获取unionId")
    @GetMapping("/getUnionId")
    public Result<UnionIdDTO> getUnionId(@RequestParam String code) throws IOException {
        log.info("获取unionId请求参数: {}", JSON.toJSONString(code, true));
        WxSessionResultVO sessionResultVO = weiXinConfig.getPcSessionKey(code);
        log.info("获取 Unionid 返回参数: {}", JSON.toJSONString(sessionResultVO, true));
        if (StringUtils.isBlank(sessionResultVO.getUnionid())) {
            throw new LoginException("微信获取 Unionid 异常: " + sessionResultVO.getErrcode());
        }
        List<SysCustomerEntity> ss = sysCustomerMapper.selectList(Wrappers.lambdaQuery(SysCustomerEntity.class)
                .eq(SysCustomerEntity::getUnionId, sessionResultVO.getUnionid()));
        if (CollectionUtils.isNotEmpty(ss)) {
            UnionIdDTO unionId = UnionIdDTO.builder()
                    .unionId(sessionResultVO.getUnionid())
                    .openId(sessionResultVO.getOpenid())
                    .isBind(true)
                    .build();
            return Result.success(unionId);
        } else {
            UnionIdDTO unionId = UnionIdDTO.builder()
                    .unionId(sessionResultVO.getUnionid())
                    .openId(sessionResultVO.getOpenid())
                    .isBind(false)
                    .build();
            return Result.success(unionId);
        }

    }


    @GetMapping("/getLoginCode")
    @Operation(summary = "获取登录小程序二维码（暂时废弃）")
    public Result<String> getLoginCode() {
        String numberRandom = RandomUtils.getNumberRandom(8);
        InputStream qrCodeInputStream = weiXinConfig.getQrCodeInputStream("pages/index/index", "logincode=" + numberRandom);
        FileUploadDTO d = new FileUploadDTO();
        d.setInputStream(qrCodeInputStream);
        d.setPathPrefix("/logincode/");
        FileUploadVO fileUploadVO = fileService.uploadInputStream(d);
        // 五分钟
        redisTemplate.opsForValue().set("login_code_" + numberRandom, numberRandom, 5, java.util.concurrent.TimeUnit.MINUTES);
        return Result.success(fileUploadVO.getUrl());
    }

    @GetMapping("/getLoginCodeStatus")
    @Operation(summary = "是否已登录小程序二维码（暂时废弃）")
    public Result<LoginCodeInfoVO> getLoginCodeStatus(String numberRandom) {
        Object o = redisTemplate.opsForValue().get("login_code_" + numberRandom);
        if (o == null) {
            return Result.error("二维码已过期，请刷新二维码");
        }
        LoginCodeInfoVO v = new LoginCodeInfoVO();
        String id = (String) redisTemplate.opsForValue().get("login_code_id_" + numberRandom);
        if (StringUtils.isBlank(id)) {
            v.setLogin(false);
            return Result.success(v);
        } else {
            SysCustomerEntity customer = sysCustomerMapper.selectById(id);
            v.setLogin(true);
            v.setId(Long.parseLong(id));
            v.setUsername(customer.getPhone());
        }
        return Result.success(v);
    }

    @GetMapping("/sendSMS")
    @Operation(summary = "发送验证码")
    public Result<String> sendSMS(@RequestParam("type") Integer type,
                                           @RequestParam("phone") String phone
                                           ) {
        String numberRandom = RandomUtils.getNumberRandom(6);
        // 注册
        if (type == 1) {
            new Thread(() -> {
                SendSmsUtils.sendSmsRegister(phone, numberRandom);
            }).start();
            redisTemplate.opsForValue().set("sms_register_" + phone, numberRandom, 5, java.util.concurrent.TimeUnit.MINUTES);
        }
        // 登录
        if (type == 2) {
            new Thread(() -> {
                SendSmsUtils.sendSmsLogin(phone, numberRandom);
            }).start();
            redisTemplate.opsForValue().set("sms_login_" + phone, numberRandom, 5, java.util.concurrent.TimeUnit.MINUTES);
        }
        //修改手机号
        if (type == 3) {
            new Thread(() -> {
                SendSmsUtils.sendSmsUpdatePhone(phone, numberRandom);
            }).start();
            redisTemplate.opsForValue().set("sms_updateCustomer_" + phone, numberRandom, 5, java.util.concurrent.TimeUnit.MINUTES);
        }
        return Result.success();
    }

    @GetMapping("/info")
    @Operation(summary = "获取用户信息")
    public Result<PcLoginUserInfo> getCustomerInfo() {
        PcLoginUserInfo userInfo = SecurityContext.getUserInfo();
        SysCustomerEntity entity = new SysCustomerEntity();
        entity.setId(userInfo.getUserId());
        SysCustomerEntity customer = sysCustomerService.getByEntity(entity);
        userInfo.setBalance(customer.getBalance());
        userInfo.setVoucherBalance(sysCustomerVoucherService.getUserVoucherBalance(userInfo.getUserId()));
        userInfo.setArrearsAmount(sysCustomerBillService.getArrearsAmount(userInfo.getUserId()));
        userInfo.setCreditAmount(sysCustomerCreditLineService.getCreditAmount(userInfo.getUserId()));
        userInfo.setTotalBalance(userInfo.getBalance().subtract(userInfo.getArrearsAmount()));
        SysCustomerCouponQuery query = new SysCustomerCouponQuery();
        query.setCustomerId(userInfo.getUserId());
        userInfo.setCouponNum(sysCustomerCouponService.getList(query).size());
        userInfo.setInvoiceTotalAmount(sysInvoiceManageService.getTotalAmount(userInfo.getUserId()).getData().getAmount());

        userInfo.setRegisterTime(customer.getCreateTime());

        userInfo.setType(customer.getType());
        userInfo.setCustomerName(customer.getCustomerName());
        userInfo.setPhone(customer.getPhone());
        userInfo.setAvatar(customer.getAvatar());
        userInfo.setCompanyName(customer.getCompanyName());
        userInfo.setCompanyCode(customer.getCompanyCode());
        userInfo.setCompanyAddress(customer.getCompanyAddress());
        userInfo.setCompanyImg(customer.getCompanyImg());
        userInfo.setCompanyContactName(customer.getCompanyContactName());
        userInfo.setCompanyContactPhone(customer.getCompanyContactPhone());
        userInfo.setCertStatus(customer.getCertStatus());
        userInfo.setCertType(customer.getCertType());
        userInfo.setCertTime(customer.getCertTime());

        SysCustomerCertLogEntity sysCustomerCertLogEntity = sysCustomerCertLogMapper.selectOne(Wrappers.lambdaQuery(SysCustomerCertLogEntity.class)
                .eq(SysCustomerCertLogEntity::getCustomerId, userInfo.getUserId())
                .eq(SysCustomerCertLogEntity::getCertStatus, 3)
                .orderByDesc(SysCustomerCertLogEntity::getCreateTime)
                .last("limit 1"));
        if (sysCustomerCertLogEntity != null){
            userInfo.setRealName(sysCustomerCertLogEntity.getName());
            userInfo.setIdCard(sysCustomerCertLogEntity.getIdCard());
        }
        return Result.success(userInfo);
    }

    @Operation(summary = "注册用户")
    @PutMapping("/register")
    public Result<Boolean> register(@RequestBody @Valid PcCustomerEdit pcCustomerEdit) {
        String phone = pcCustomerEdit.getPhone();
        SysCustomerEntity entity = new SysCustomerEntity();
        entity.setPhone(phone);
        entity = sysCustomerService.getByEntity(entity);
        if (entity != null) {
            return Result.error("当前手机号已经注册");
        }
        String openId = pcCustomerEdit.getOpenId();
        if (StringUtils.isNotBlank(openId)){
            SysCustomerEntity sysCustomerEntity = sysCustomerMapper.selectOne(Wrappers.lambdaQuery(SysCustomerEntity.class)
                    .eq(SysCustomerEntity::getOpenId,openId));
            if (ObjectUtils.isNotEmpty(sysCustomerEntity)){
                return Result.error("当前微信已绑定其他手机号");
            }
        }
//        // 验证码校验
//        Object o = redisTemplate.opsForValue().get("sms_register_" + phone);
//        if (o == null) {
//            return Result.error("验证码已失效");
//        } else {
//            if (!pcCustomerEdit.getSmsCode().equals(o.toString())) {
//                return Result.error("验证码不正确");
//            }
//        }

        entity = BeanUtils.copyBean(pcCustomerEdit, SysCustomerEntity.class);
        entity.setPassword(PasswordUtils.encryptPassword(pcCustomerEdit.getPassword()));
        
        // 生成accountId: 年月+4位序号，例如：2025020001
        String yearMonth = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMM"));
        // 获取当前月份最大序号
        Long maxSeq = sysCustomerMapper.selectMaxSeqByYearMonth(yearMonth);
        String sequence = String.format("%04d", (maxSeq != null ? maxSeq + 1 : 1));
        Long accountId = Long.parseLong(yearMonth + sequence);
        entity.setAccountId(accountId);
        
        boolean insert = sysCustomerService.insert(entity);


        String vcode = pcCustomerEdit.getVcode();
        if (StringUtils.isNotBlank(vcode)) {
            try {
                // 如果包含URL编码，进行解码
                if (vcode.contains("%")) {
                    vcode = java.net.URLDecoder.decode(vcode, "UTF-8");
                }
            } catch (Exception e) {
                log.warn("URL decode failed: {}", e.getMessage());
            }
            // 在relevance方法中增加URL解码处理
            if (vcode.contains("t=")) {
                vcode = vcode.replace("t=", "");

            }
            log.info("注册时获取的vcode: {}", vcode);
            SysExtend extend =  sysExtendMapper.selectOne(
                    new LambdaQueryWrapper<SysExtend>()
                            .eq(SysExtend::getShareKey, vcode)
            );

            if (extend != null) {
                SysExtend selectOne = new SysExtend();
                selectOne.setType(2);
                selectOne.setLevel(1);
                selectOne.setParentUserId(extend.getUserId());
                selectOne.setUserId(entity.getId());
                sysExtendMapper.insert(selectOne);
            }


        }
        //分享活动注册
        if (pcCustomerEdit.getActivityId() != null && pcCustomerEdit.getInviterId() != null ){
            SysCustomerEntity sysCustomerEntity = sysCustomerMapper.selectOne(Wrappers.lambdaQuery(SysCustomerEntity.class)
                    .eq(SysCustomerEntity::getId, pcCustomerEdit.getInviterId()));
            SysActiveRecordEntity sysActiveRecordEntity = new SysActiveRecordEntity();
            sysActiveRecordEntity.setActiveId(pcCustomerEdit.getActivityId());
            sysActiveRecordEntity.setShareUserId(pcCustomerEdit.getInviterId());
            sysActiveRecordEntity.setShareUsername(sysCustomerEntity.getCustomerName());
            sysActiveRecordEntity.setShareUserPhone(sysCustomerEntity.getPhone());
            sysActiveRecordEntity.setRegisterUserId(entity.getId());
            sysActiveRecordEntity.setRegisterUsername(entity.getCustomerName());
            sysActiveRecordEntity.setRegisterUserPhone(entity.getPhone());
            sysActiveRecordEntity.setInvitationTime(new Date());
            sysActiveRecordMapper.insert(sysActiveRecordEntity);
            //赠送优惠卷
            List<SysActiveCouponDetailsDTO> coupons = sysActiveCouponMapper.getCoupons(pcCustomerEdit.getActivityId());
            List<SysActiveCouponDTO> list = filterAndConvertCoupons(coupons, 1);
            List<SysCouponEntity> couponEntityList = BeanUtil.copyToList(list, SysCouponEntity.class);
            sysCustomerCouponService.receiveCoupon(pcCustomerEdit.getInviterId(), couponEntityList);
        }
        return Result.success();
    }

    private List<SysActiveCouponDTO> filterAndConvertCoupons(List<SysActiveCouponDetailsDTO> coupons, int couponType) {
        return coupons.stream()
                .filter(coupon -> coupon.getCouponType() == couponType)
                .map(coupon -> BeanUtil.copyProperties(coupon, SysActiveCouponDTO.class))
                .collect(Collectors.toList());
    }

    @Operation(summary = "重置密码")
    @PutMapping("/resetPasswordByPhone")
    public Result<Boolean> resetPassword(@RequestBody PcCustomerEdit edit) {
        String password = edit.getPassword();
        if (StringUtils.isEmpty(password)) {
            return Result.error("新密码为空");
        }
        String phone = edit.getPhone();
        if (StringUtils.isEmpty(phone)) {
            return Result.error("手机号为空");
        }
        SysCustomerEntity selectOne = sysCustomerMapper.selectOne(
                new LambdaQueryWrapper<SysCustomerEntity>()
                        .eq(SysCustomerEntity::getPhone, phone)
        );
        if (selectOne == null) {
            return Result.error("您还未注册");
        }
        SysCustomerEntity entity = new SysCustomerEntity();
        entity.setPhone(phone);
        entity.setPassword(PasswordUtils.encryptPassword(password));
        return Result.result(sysCustomerService.updateByPhone(entity));
    }

    @Operation(summary = "修改密码")
    @PutMapping("/updatePassword")
    public Result<Boolean> updatePassword(@RequestBody PcCustomerEdit edit) {
        String password = edit.getPassword();
        if (StringUtils.isEmpty(password)) {
            return Result.error("新密码为空");
        }
        SysCustomerEntity entity = new SysCustomerEntity();
        entity.setId(SecurityContext.getUserInfo().getUserId());
        entity.setPassword(PasswordUtils.encryptPassword(password));
        return Result.result(sysCustomerService.updateById(entity));
    }

    @Operation(summary = "修改用户信息")
    @PutMapping("/updateCustomerInfo")
    public Result<Boolean> updateCustomerInfo(@RequestBody PcCustomerInfoEdit edit) {
        if (Optional.ofNullable(edit.getCustomerName()).isEmpty() &&
                Optional.ofNullable(edit.getAvatar()).isEmpty()) {
            return Result.success();
        }

        SysCustomerEntity entity = new SysCustomerEntity();
        entity.setId(SecurityContext.getUserInfo().getUserId());
        Optional.ofNullable(edit.getCustomerName()).ifPresent(entity::setCustomerName);
        Optional.ofNullable(edit.getAvatar()).ifPresent(entity::setAvatar);
        return Result.result(sysCustomerService.updateById(entity));
    }

    @Operation(summary = "修改用户手机号")
    @PutMapping("/updateCustomerPhone")
    public Result<Boolean> updateCustomerPhone(@RequestBody PcCustomerInfoEdit edit) {
        String phone = edit.getPhone();
        if (StringUtils.isEmpty(phone)) {
            return Result.error("手机号为空");
        }

        SysCustomerEntity entity = new SysCustomerEntity();
        entity.setId(SecurityContext.getUserInfo().getUserId());
        entity.setPhone(phone);
        return Result.result(sysCustomerService.updateById(entity));
    }

    @PostMapping("/getCouponList")
    @Operation(summary = "获取优惠卷列表", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })
    public Result<PageResult<SysCustomerCouponListVO>> getCouponList(@RequestBody SysCustomerCouponQuery query) {
        PageQuery<SysCustomerCouponQuery> pageQuery = PageQuery.build(query);
        pageQuery.startPage();
        query.setCustomerId(SecurityContext.getUserInfo().getUserId());
        return Result.success(PageResult.of(sysCustomerCouponService.getList(query)));
    }

    @Operation(summary = "获取代金卷列表", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })
    @GetMapping("/getVoucherList")
    public Result<PageResult<SysCustomerVoucherEntity>> getVoucherList() {
        SysCustomerVoucherEntity entity = new SysCustomerVoucherEntity();
        entity.setCustomerId(SecurityContext.getUserInfo().getUserId());
        return Result.success(sysCustomerVoucherService.getPage(PageQuery.build(entity)));
    }

    @Operation(summary = "获取授信额列表", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })
    @GetMapping("/getCreditLineList")
    public Result<PageResult<SysCustomerCreditLineEntity>> getCreditLineList() {
        SysCustomerCreditLineEntity entity = new SysCustomerCreditLineEntity();
        entity.setCustomerId(SecurityContext.getUserInfo().getUserId());
        return Result.success(sysCustomerCreditLineService.getPage(PageQuery.build(entity)));
    }


    @Operation(summary = "提交企业认证")
    @PostMapping("/companyVerify")
    public Result<Void> companyVerify(@RequestBody CompanyDTO dto) {
        SysCustomerEntity sysCustomer = sysCustomerMapper.selectById(SecurityContext.getUserInfo().getUserId());
        if (sysCustomer.getCompanyStatus().equals(2)) {
            return Result.error("审核中");
        }
        if (sysCustomer.getCompanyStatus().equals(3)) {
            return Result.error("已通过");
        }

        sysCustomerMapper.update(null,
                new LambdaUpdateWrapper<SysCustomerEntity>()
                        .eq(SysCustomerEntity::getId, sysCustomer.getId())
                        .set(SysCustomerEntity::getCompanyName, dto.getCompanyName())
                        .set(SysCustomerEntity::getCompanyCode, dto.getCompanyCode())
                        .set(SysCustomerEntity::getCompanyAddress, dto.getCompanyAddress())
                        .set(SysCustomerEntity::getCompanyImg, dto.getCompanyImg())
                        .set(SysCustomerEntity::getCompanyContactName, dto.getCompanyContactName())
                        .set(SysCustomerEntity::getCompanyContactPhone, dto.getCompanyContactPhone())
                        .set(SysCustomerEntity::getCompanyStatus, 2)
                );

        return Result.success();
    }

    @Operation(summary = "实名认证")
    @PostMapping("/realNameVerify")
    public Result<PcRealNameDTO> realNameVerify(@RequestBody PcRealNameVO vo) {
        return Result.success(sysCustomerService.realNameVerify(vo));
    }

    @Operation(summary = "实名认证记录")
    @GetMapping("/realNameVerify/log")
    public Result<List<SysCustomerCertLogEntity>> realNameVerifyLog() {
        Long userId = SecurityContext.getUserInfo().getUserId();
        List<SysCustomerCertLogEntity> list = sysCustomerCertLogMapper.selectList(Wrappers.lambdaQuery(SysCustomerCertLogEntity.class)
                .eq(SysCustomerCertLogEntity::getCustomerId, userId));
        return Result.success(list);
    }

    @Operation(summary = "获取实名认证记录详情")
    @GetMapping("/realNameVerify/log/{id}")
    public Result<SysCustomerCertLogEntity> realNameVerifyLogDetail(@PathVariable Long id) {
        return Result.success(sysCustomerCertLogMapper.selectById(id));
    }

    @Operation(summary = "获取用户EidToken")
    @PostMapping("/getEidToken")
    public Result<String> getEidToken(@RequestBody PcRealNameVO vo) {
        return Result.success( sysCustomerService.getEidToken(vo));
    }

    @Operation(summary = "获取E证通结果信息")
    @GetMapping("/getEidResult/{logId}")
    public Result<Boolean> getEidResult(@PathVariable Long logId) {
        return sysCustomerService.getEidResult(logId);
    }

    @Operation(summary = "初始化用户accountId")
    @PostMapping("/initAccountId")
    public Result<String> initAccountId() {
        // 获取所有未设置accountId的用户，按创建时间排序
        List<SysCustomerEntity> customers = sysCustomerMapper.selectList(
                Wrappers.lambdaQuery(SysCustomerEntity.class)
                        .isNull(SysCustomerEntity::getAccountId)
                        .orderByAsc(SysCustomerEntity::getCreateTime)
        );
        
        if (customers.isEmpty()) {
            return Result.success("没有需要初始化的用户");
        }

        int count = 0;
        for (SysCustomerEntity customer : customers) {
            // 使用用户创建时间生成年月
            String yearMonth = new java.text.SimpleDateFormat("yyyyMM")
                    .format(customer.getCreateTime());
            
            // 获取当前月份最大序号
            Long maxSeq = sysCustomerMapper.selectMaxSeqByYearMonth(yearMonth);
            String sequence = String.format("%04d", (maxSeq != null ? maxSeq + 1 : 1));
            Long accountId = Long.parseLong(yearMonth + sequence);
            
            // 更新用户accountId
            sysCustomerMapper.update(null,
                    new LambdaUpdateWrapper<SysCustomerEntity>()
                            .eq(SysCustomerEntity::getId, customer.getId())
                            .set(SysCustomerEntity::getAccountId, accountId)
            );
            count++;
        }
        
        return Result.success("成功初始化 " + count + " 个用户的accountId");
    }

}