package com.lingyang.common.security.utils;

import com.lingyang.common.cache.CacheService;
import com.lingyang.common.core.enums.HttpStatus;
import com.lingyang.common.core.exception.http.HttpParamsException;
import com.lingyang.common.core.utils.SpringUtils;
import com.lingyang.common.core.utils.StringUtils;
import com.lingyang.common.security.exception.LoginException;

import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2023/8/18 15:09
 */
public class ValidateCodeUtils {

    /**
     * 验证码缓存key前缀
     */
    public static final String CAPTCHA_CODE_KEY = "captcha_codes:";

    private static final CacheService cacheService;


    static {
        cacheService = SpringUtils.getBean(CacheService.class);
    }

    public static void checkValidateCode(String uid, String code) throws LoginException {
        if (StringUtils.isEmpty(uid) || StringUtils.isEmpty(code) || !getValidateCode(uid).equalsIgnoreCase(code)) {
            throw new HttpParamsException(HttpStatus.CAPTCHA_ERROR.getMsg());
        }
    }

    public static String getValidateCode(String uid) throws LoginException  {
        String captchaKey = getCaptchaCodeKey(uid);
        if (!hasValidateCode(captchaKey)) {
            throw new HttpParamsException(HttpStatus.CAPTCHA_ERROR.getMsg());
        }
        return cacheService.getCacheObject(captchaKey);
    }

    public static boolean hasValidateCode(String captchaKey) {
        return cacheService.hasKey(captchaKey);
    }

    public static void setValidateCode(String uid, String code, long expiration) {
       setValidateCode(uid, code, expiration, TimeUnit.SECONDS);
    }

    public static void setValidateCode(String uid, String code, long expiration, TimeUnit timeUnit) {
        cacheService.setCacheObject(getCaptchaCodeKey(uid), code, expiration, timeUnit);
    }

    public static void removeValidateCode(String uid) {
        cacheService.deleteObject(getCaptchaCodeKey(uid));
    }

    public static String getCaptchaCodeKey(String uid) {
        return CAPTCHA_CODE_KEY + uid;
    }
}