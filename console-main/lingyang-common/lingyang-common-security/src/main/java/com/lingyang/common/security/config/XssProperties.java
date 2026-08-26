package com.lingyang.common.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2023/8/7 15:04
 */
@ConfigurationProperties("security.xss")
@Configuration
@Data
public class XssProperties {

    /**
     * xss 排除地址
     */
    private String[] ignores;

}