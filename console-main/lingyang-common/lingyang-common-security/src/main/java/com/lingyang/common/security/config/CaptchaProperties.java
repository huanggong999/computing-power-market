package com.lingyang.common.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2023/8/14 10:28
 */
@Data
@ConfigurationProperties("security.captcha")
@Configuration
public class CaptchaProperties {
    /**
     * 是否开启
     * 默认true
     */
    private Boolean enabled = true;
    /**
     * api地址
     * 默认 /code
     */
    private String apiUrl = "/code";

    /**
     * 有效时间，单位秒
     * 默认 60秒
     */
    private Long expireTime = 60L;

    /**
     * 校验路径
     */
    private String[] checkUrls = new String[0];

    /**
     * 排除请求头
     */
    private Map<String, String[]> ignoresHeads ;
}
