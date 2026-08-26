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
 * @Date: 2023/8/15 13:59
 */
@Service
@ConditionalOnProperty(value = "security.captcha.enabled", havingValue = "true")
public class MathValidateCodeHandler implements ValidateCodeHandlerInterface<String> {
    @Resource(name = "captchaProducerMath")
    private Producer captchaProducerMath;

    @Override
    public ValidateCodeResult handlerCode(String param) {
        String capText = captchaProducerMath.createText();
        String capStr = capText.substring(0, capText.lastIndexOf("@"));
        String code = capText.substring(capText.lastIndexOf("@") + 1);
        BufferedImage image = captchaProducerMath.createImage(capStr);
        return new ValidateCodeResult(code, image);
    }


    @Override
    public ValidateCodeType.Type type() {
        return ValidateCodeType.MATH;
    }

    @Override
    public Class<String> jsonClass() {
        return String.class;
    }
}
