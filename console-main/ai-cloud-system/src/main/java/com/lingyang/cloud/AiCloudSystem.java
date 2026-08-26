package com.lingyang.cloud;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/9/29 15:06
 */
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
@EnableAsync
@EnableScheduling
@EnableAspectJAutoProxy(exposeProxy = true)
@MapperScan("com.lingyang.cloud.mapper")
public class AiCloudSystem {

    public static void main(String[] args) {
        SpringApplication.run(AiCloudSystem.class, args);
    }
}
