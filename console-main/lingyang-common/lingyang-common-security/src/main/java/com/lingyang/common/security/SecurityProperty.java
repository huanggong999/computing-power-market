package com.lingyang.common.security;

import com.lingyang.common.core.enums.HttpStatus;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.utils.ServletUtils;
import com.lingyang.common.security.access.permission.PermissionService;
import com.lingyang.common.security.config.SecurityProperties;
import com.lingyang.common.security.filter.AbstractFilter;
import jakarta.annotation.Resource;
import jakarta.servlet.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2023/8/7 11:49
 */
@Configuration
@ConditionalOnProperty(value = "security.enabled", havingValue = "true")
@EnableMethodSecurity
public class SecurityProperty {
    @Resource
    private SecurityProperties securityProperties;

    @Autowired
    private List<AbstractFilter> filterList;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 禁用basic明文验证
                .httpBasic(AbstractHttpConfigurer::disable)
                // 前后端分离架构不需要csrf保护
                .csrf(AbstractHttpConfigurer::disable)
                // 禁用默认登录页
                .formLogin(AbstractHttpConfigurer::disable)
                // 禁用退出登陆
                .logout(AbstractHttpConfigurer::disable)
                // 前后端分离是无状态的，不需要session了，直接禁用。
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 跨域配置
                .cors(configurer -> configurer.configurationSource(securityProperties.getCors().getCorsSource()))
                // 不需要校验路径
                .authorizeHttpRequests(registry -> registry
                        // 允许直接访问授权登录接口
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        // 白名单
                        .requestMatchers(securityProperties.getIgnores()).permitAll()
                        // 允许任意请求被已登录用户访问，不检查Authority
                        .anyRequest().authenticated()
                )
                // 配置过滤器中的异常处理
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        // 身份认证失败
                        .authenticationEntryPoint((request, response, exception) -> {
                            Logger logger = LoggerFactory.getLogger(this.getClass());
                            logger.info("AuthenticationException : {}", exception.getMessage());
                            ServletUtils.response(response, Result.result(HttpStatus.NOT_LOGIN));
                        })
                        // 权限认证失败
                        .accessDeniedHandler((request, response, exception) -> {
                            Logger logger = LoggerFactory.getLogger(this.getClass());
                            logger.info("AccessDeniedException: {}", exception.getMessage());
                            ServletUtils.response(response, Result.result(HttpStatus.NOT_PERMISSION));
                        })
                )
        ;
        // 添加过滤器
        if (filterList != null) {
            final List<Class<? extends Filter>> parenFilterClass = new ArrayList<>(1);
            parenFilterClass.add(0, ExceptionTranslationFilter.class);
            filterList.stream()
                    .filter(AbstractFilter::enable)
                    .sorted(Comparator.comparingInt(Ordered::getOrder))
                    .toList()
                    .forEach(filter -> {
                        http.addFilterAfter(filter, parenFilterClass.get(0));
                        parenFilterClass.add(0, filter.getClass());
                    });
        }
        return http.build();
    }


    /**
     * 密码加密器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean("ss")
    public PermissionService permissionService() {
        return new PermissionService();
    }
}