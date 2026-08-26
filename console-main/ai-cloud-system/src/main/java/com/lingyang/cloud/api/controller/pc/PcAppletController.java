package com.lingyang.cloud.api.controller.pc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
//import com.lingyang.cloud.config.WeiXinConfig;
import com.lingyang.cloud.config.dto.LoginDTO;
import com.lingyang.cloud.config.vo.LoginVO;
import com.lingyang.cloud.config.vo.WxSessionResultVO;
import com.lingyang.common.core.model.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Tag(name = "pc端-小程序操作相关")
@RestController
@RequestMapping("/pc/applet")
public class PcAppletController {

//    @Autowired
//    private WeiXinConfig weiXinConfig;

    // 传openid判断是否有绑定的手机账号
    // 有 账号和密码反给前端，掉登录



    //


//    @Operation(summary = "微信登录")
//    @PostMapping("/login")
//    public Result<LoginVO> login(@RequestBody LoginDTO dto) throws IOException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
//        log.info("微信小程序登录请求参数: {}", JSON.toJSONString(dto, true));
//        WxSessionResultVO sessionResultVO = weiXinConfig.getSessionKey(dto.getCode());
//        log.info("微信小程序获取 Openid 返回参数: {}", JSON.toJSONString(sessionResultVO, true));
//        if (StringUtils.isBlank(sessionResultVO.getOpenid())) {
//            return ReturnResult.fail("微信小程序：获取 Openid 异常: " + sessionResultVO.getErrcode());
//        }
//        dto.setOpenId(sessionResultVO.getOpenid());
//
//        String phone = null;
//        if (StringUtils.isNotEmpty(dto.getPhone())) {
//            phone = dto.getPhone();
//        } else {
//            String userPhone = weiXinConfig.getUserPhone(dto.getPhoneCode());
//            phone = userPhone;
//        }
//        // 根据手机号获取用户信息
//        LambdaUpdateWrapper<DfzUser> wrapper = new LambdaUpdateWrapper<>();
//        DfzUser user = dfzUserService.getOne(wrapper
//                .eq(DfzUser::getPhone, phone)
//        );
//
//        // 登录
//        if (user != null) {
//            if (!DfzUserStatus.ENABLE.getCode().equals(user.getStatus())) {
//                return ReturnResult.fail("您被禁止登录小程序");
//            }
//            user.setWxOpenId(dto.getOpenId());
//            user.setLastLoginTime(LocalDateTime.now());
//
//            // 判断是不是经销商
//            List<DfzDealer> list = dfzDealerService.list(new LambdaQueryWrapper<DfzDealer>()
//                    .eq(DfzDealer::getContactPhone, phone)
//            );
//
//            if (CollectionUtils.isNotEmpty(list)) {
//                user.setOldType(user.getType());
//                user.setType(3);
//                DfzDealer dealer = list.get(0);
//                if (dealer == null) {
//                    return ReturnResult.fail("经销商已删除");
//                }
//                if (dealer.getStatus().equals(2)) {
//                    return ReturnResult.fail("经销商被禁用");
//                }
//                user.setDealerId(dealer.getId());
//            } else {
//                if (user.getType() != null && user.getType().equals(3)) {
//                    user.setType(user.getOldType());
//                    user.setDealerId(null);
//                }
//            }
//
//            dfzUserService.updateById(user);
//
//            // 注册
//        } else {
//            Long userId = IdUtils.nextId();
//            user = new DfzUser();
//            user.setId(userId);
//            user.setUsername("微信用户");
//            user.setPortrait(dto.getPortrait());
//            user.setGender(dto.getGender());
//            user.setCity(dto.getCity());
//            user.setProvince(dto.getProvince());
//            user.setCountry(dto.getCountry());
//            user.setPhone(phone);
//            user.setLastLoginTime(LocalDateTime.now());
//            user.setWxOpenId(dto.getOpenId());
//
//            // 判断是不是经销商
//            List<DfzDealer> list = dfzDealerService.list(new LambdaQueryWrapper<DfzDealer>()
//                    .eq(DfzDealer::getContactPhone, phone)
//            );
//
//            if (CollectionUtils.isNotEmpty(list)) {
//                user.setType(3);
//                DfzDealer dealer = list.get(0);
//                if (dealer == null) {
//                    return ReturnResult.fail("经销商已删除");
//                }
//                if (dealer.getStatus().equals(2)) {
//                    return ReturnResult.fail("经销商被禁用");
//                }
//                user.setDealerId(dealer.getId());
//            } else {
//                if (SystemType.DFZ.equals(type)) {
//                    JSONObject shifu = DfzOpenUtils.getSHIFU(1, 1, phone);
//                    if (shifu != null && shifu.containsKey("total") && Integer.valueOf(shifu.get("total").toString()) > 0) {
//                        JSONArray rows = shifu.getJSONArray("rows");
//                        JSONObject jsonObject = rows.getJSONObject(0);
//                        if (jsonObject.containsKey("name")) {
//                            if (jsonObject.get("name") != null) {
//                                user.setUsername(jsonObject.get("name").toString());
//                            } else {
//                                user.setUsername("微信用户");
//                            }
//                        }
//                        if (jsonObject.containsKey("headPortrait")) {
//                            if (jsonObject.get("headPortrait") != null) {
//                                user.setPortrait(jsonObject.get("headPortrait").toString());
//                            }
//                        }
//                        user.setType(2);
//                        user.setSource("会员系统推送");
//                    }
//                }
//
//            }
//
//            dfzUserService.save(user);
//
//        }
//
//        LoginVO loginVO = new LoginVO();
//        // String token = UserJWTUtil.createToken(user);
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("id", user.getId().toString());
//        jsonObject.put("username", user.getUsername());
//        jsonObject.put("phone", user.getPhone());
//        loginVO.setToken(user.getId().toString());
//        loginVO.setSelectType(user.getType() == null);
//        loginVO.init(user.getId(), user.getUsername(), user.getPortrait(), user.getPhone(), user.getWxOpenId(), user.getType());
//        redisUtil.setAppletUser(loginVO.getUserInfo().getUserId().toString(), jsonObject.toString());
//        return ReturnResult.success(loginVO);
//    }

}
