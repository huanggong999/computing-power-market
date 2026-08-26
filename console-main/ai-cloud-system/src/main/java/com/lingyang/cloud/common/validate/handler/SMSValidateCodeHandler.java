package com.lingyang.cloud.common.validate.handler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lingyang.cloud.common.validate.param.SMSValidateCodeParam;
import com.lingyang.cloud.entity.SysCustomerEntity;
import com.lingyang.cloud.mapper.SysCustomerMapper;
import com.lingyang.cloud.utils.SendSmsUtils;
import com.lingyang.common.core.exception.http.HttpParamsException;
import com.lingyang.common.core.security.ValidateCodeResult;
import com.lingyang.common.core.utils.Optional;
import com.lingyang.common.core.utils.RandomUtils;
import com.lingyang.common.security.model.ValidateCodeType;
import com.lingyang.common.security.utils.ValidateCodeUtils;
import com.lingyang.common.security.validate.ValidateCodeHandlerInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.lingyang.cloud.common.constant.CommonHandlerConstant.SMS_VALIDATE_HANDLER_VALUE;

/**
 * @Description: 获取短信验证码处理
 * @Author: 王小龙
 * @Date: 2024/10/17 17:43
 */
@Service
public class SMSValidateCodeHandler implements ValidateCodeHandlerInterface<SMSValidateCodeParam> {

    @Autowired
    private SysCustomerMapper sysCustomerMapper;
    @Override
    public ValidateCodeResult handlerCode(SMSValidateCodeParam param) {
        String phone = Optional.of(param.getPhone())
                .orElseThrow(() -> new HttpParamsException("手机号为空"));
        if (ValidateCodeUtils.hasValidateCode(ValidateCodeUtils.getCaptchaCodeKey(phone))) {
            throw new HttpParamsException("验证码未失效");
        }
        ValidateCodeResult validateCodeResult = new ValidateCodeResult();
        String numberRandom = RandomUtils.getNumberRandom(6);
        validateCodeResult.setCode(numberRandom);
        validateCodeResult.setUid(phone);
        // todo 发送验证码
        if (param.getTmsg() == 1) {
            SysCustomerEntity selectOne = sysCustomerMapper.selectOne(
                    new LambdaQueryWrapper<SysCustomerEntity>()
                            .eq(SysCustomerEntity::getPhone, phone)
            );
            if (selectOne == null) {
                throw new HttpParamsException("您未注册，发送失败");
            }
            SendSmsUtils.sendSmsLogin(phone, numberRandom);
        } else if (param.getTmsg() == 2)  {
            SendSmsUtils.sendSmsRegister(phone, numberRandom);
        }else if (param.getTmsg() == 3){
            SysCustomerEntity selectOne = sysCustomerMapper.selectOne(
                    new LambdaQueryWrapper<SysCustomerEntity>()
                            .eq(SysCustomerEntity::getPhone, phone)
            );
            if (selectOne == null) {
                throw new HttpParamsException("您未注册，发送失败");
            }
            SendSmsUtils.sendSmsPwd(phone, numberRandom);
        }else if (param.getTmsg() == 4){
            SysCustomerEntity selectOne = sysCustomerMapper.selectOne(
                    new LambdaQueryWrapper<SysCustomerEntity>()
                            .eq(SysCustomerEntity::getPhone, phone)
            );
            if (selectOne != null) {
                throw new HttpParamsException("当前手机号已经被其他账号使用");
            }
            SendSmsUtils.sendSmsUpdatePhone(phone, numberRandom);
        }else if (param.getTmsg() == 5){
            SysCustomerEntity selectOne = sysCustomerMapper.selectOne(
                    new LambdaQueryWrapper<SysCustomerEntity>()
                            .eq(SysCustomerEntity::getPhone, phone)
            );
            if (selectOne == null) {
                throw new HttpParamsException("当前手机号未注册，请您先未注册");
            }
            SendSmsUtils.sendSmsBindPhone(phone, numberRandom);
        }
        return validateCodeResult;
    }

    @Override
    public ValidateCodeType.Type type() {
        return ValidateCodeType.Type.create(SMS_VALIDATE_HANDLER_VALUE);
    }

    @Override
    public Class<? extends SMSValidateCodeParam> jsonClass() {
        return SMSValidateCodeParam.class;
    }
}
