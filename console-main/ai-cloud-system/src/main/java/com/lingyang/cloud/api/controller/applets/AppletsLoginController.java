package com.lingyang.cloud.api.controller.applets;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.api.service.applets.AppletsConsoleService;
import com.lingyang.cloud.config.WeiXinConfig;
import com.lingyang.cloud.config.dto.UnionIdDTO;
import com.lingyang.cloud.config.vo.WxSessionResultVO;
import com.lingyang.cloud.entity.SysCustomerEntity;
import com.lingyang.cloud.mapper.SysCustomerMapper;
import com.lingyang.cloud.model.vo.applets.AppletsLoginBindVO;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.security.SecurityContext;
import com.lingyang.common.core.utils.StringUtils;
import com.lingyang.common.security.exception.LoginException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/3/7 10:37
 */
@Tag(name = "小程序端-登录相关")
@RestController
@RequestMapping("/applets/login")
@Slf4j
public class AppletsLoginController {

    @Resource
    private AppletsConsoleService appletsConsoleService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private WeiXinConfig weiXinConfig;

    @Resource
    private SysCustomerMapper customerMapper;


    @Operation(summary = "openId绑定手机号")
    @PostMapping("/bindPhone")
    public Result<Boolean> bindPhone(@RequestBody AppletsLoginBindVO vo) {
        boolean flag = appletsConsoleService.bindPhone(vo);
        return Result.success(flag);
    }

    @Operation(summary = "扫码登录授权")
    @GetMapping("/authLoginCode")
    public Result<String> authLoginCode(Long code) {
        Object o = redisTemplate.opsForValue().get("login_code_" + code);
        if (o == null) {
            return Result.error("二维码已过期，请刷新二维码后，重新扫码");
        }
        Long userId = SecurityContext.getUserInfo().getUserId();
        redisTemplate.opsForValue().set("login_code_id_" + code, userId.toString(), 15, java.util.concurrent.TimeUnit.MINUTES);
        return Result.success();
    }

    @Operation(summary = "获取unionId")
    @GetMapping("/getUnionId")
    public Result<UnionIdDTO> getUnionId(@RequestParam String code) throws IOException {
        log.info("获取unionId请求参数: {}", JSON.toJSONString(code, true));
        WxSessionResultVO sessionResultVO = weiXinConfig.getSessionKey(code);
        log.info("微信小程序获取 Unionid 返回参数: {}", JSON.toJSONString(sessionResultVO, true));
        if (StringUtils.isBlank(sessionResultVO.getUnionid())) {
            throw new LoginException("微信小程序：获取 Unionid 异常: " + sessionResultVO.getErrcode());
        }
        SysCustomerEntity sysCustomerEntity = customerMapper.selectOne(Wrappers.lambdaQuery(SysCustomerEntity.class)
                .eq(SysCustomerEntity::getUnionId, sessionResultVO.getUnionid()));
        boolean flag = sysCustomerEntity != null;
        UnionIdDTO unionId = UnionIdDTO.builder()
                .unionId(sessionResultVO.getUnionid())
                .openId(sessionResultVO.getOpenid())
                .isBind(flag)
                .build();
        return Result.success(unionId);
    }

    @Operation(summary = "测试，绑定openId")
    @GetMapping("/test/bindOpenId")
    public Result<String> testBindOpenId(@RequestParam String code) throws IOException {
        log.info("获取openID请求参数: {}", JSON.toJSONString(code, true));
        WxSessionResultVO sessionResultVO = weiXinConfig.getSessionKey(code);
        log.info("微信小程序获取 openID 返回参数: {}", JSON.toJSONString(sessionResultVO, true));
       return Result.success(sessionResultVO.getOpenid());
    }
}