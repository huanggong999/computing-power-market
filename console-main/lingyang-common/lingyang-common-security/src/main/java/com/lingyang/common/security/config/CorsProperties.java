package com.lingyang.common.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2023/8/7 15:30
 */
@ConfigurationProperties("security.cors")
@Configuration
@Data
public class CorsProperties {

    /**
     * 允许访问的域
     */
    private List<String> allowedOrigins;

    /**
     * 允许访问的请求方式
     */
    private List<String> allowedMethods;


    public CorsConfigurationSource getCorsSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        //允许访问的域
//        configuration.setAllowedOrigins(allowedOrigins == null ? List.of("*") : allowedOrigins);
        configuration.setAllowedOriginPatterns(allowedOrigins == null ? List.of("*") : allowedOrigins);
        //允许使用GET和POST方法
        configuration.setAllowedMethods(allowedMethods == null ? List.of("*") : allowedMethods);
        //允许带凭证
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        //对所有URL生效
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}