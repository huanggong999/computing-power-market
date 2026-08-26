package com.lingyang.common.security.config;

import com.lingyang.common.core.utils.StringUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2023/8/7 13:58
 */
@Setter
@Component
@ConfigurationProperties(prefix = "security")
@Getter
public class SecurityProperties {
    /**
     * 开启
     * 默认: false
     */
    private boolean enabled = false;

    /**
     * 开启日志输出
     * 默认: false
     */
    private boolean logEnable  = false;

    /**
     * token密钥
     */
    private String tokenSecret;

    /**
     * token 过期时间，单位秒
     * 默认 43200秒
     */
    private Long tokenExpireTime = 43200L;

    /**
     * 统一登陆地址
     * 请求为：POST
     * 默认/login
     */
    private String loginUrl = "/login";

    /**
     * 重复登陆
     * 默认：true
     */
    private Boolean repeatedLogin = true;


    /**
     * 请求来源，请求头key
     * 默认：source
     */
    private String requestSourceHeadKey = "source";

    /**
     * 请求地址白名单配置
     * 默认：为空
     */
    private String[] ignores = new String[0];

    /**
     * 验证码
     */
    private CaptchaProperties captcha = new CaptchaProperties();
    /**
     * xss拦截配置
     */
    private XssProperties xss = new XssProperties();

    /**
     * 跨域配置
     */
    private CorsProperties cors = new CorsProperties();


    public String getTokenSecret() {
        if (StringUtils.isEmpty(tokenSecret)) {
            tokenSecret = "$jwt_#dGiCMZ%1qopUxoyzQ^_secret_@iJujOpybAHfxG0HS";
        }
        return tokenSecret;
    }


    public String[] getIgnores() {
        List<String> ignoresList = new ArrayList<>(ignores.length + 2);
        ignoresList.addAll(Arrays.stream(ignores).toList());
        ignoresList.add(loginUrl);
        ignoresList.add(captcha.getApiUrl());
        return ignoresList.toArray(new String[]{});
    }
}