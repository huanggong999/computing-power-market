package com.lingyang.common.security.validate;

import com.google.code.kaptcha.Producer;
import com.lingyang.common.core.security.ValidateCodeResult;
import com.lingyang.common.security.model.ValidateCodeType;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2023/8/15 14:20
 */
@Service
@ConditionalOnProperty(value = "security.captcha.enabled", havingValue = "true")
public class ChatValidateCodeHandler implements ValidateCodeHandlerInterface<String> {
    @Resource(name = "captchaProducer")
    private Producer captchaProducer;

    @Override
    public ValidateCodeResult handlerCode(String param) {
        // 生成验证码
        String code = captchaProducer.createText();
        BufferedImage image = captchaProducer.createImage(code);
        return new ValidateCodeResult(code, image);
    }


    @Override
    public ValidateCodeType.Type type() {
        return ValidateCodeType.CHAR;
    }

    @Override
    public Class<? extends String> jsonClass() {
        return String.class;
    }
}
