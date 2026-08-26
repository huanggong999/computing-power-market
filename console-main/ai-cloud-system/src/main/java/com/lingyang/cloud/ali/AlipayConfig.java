package com.lingyang.cloud.ali;

import com.alipay.api.AlipayApiException;
import com.alipay.api.DefaultAlipayClient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/27 16:23
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Configuration
@ConfigurationProperties(prefix = "alipay")
public class AlipayConfig extends com.alipay.api.AlipayConfig {
    /**
     * 异步回调地址
     */
    private String notifyUrl;
    /**
     * 同步回调地址
     */
    private String returnUrl;
    /**
     * 支付完成重定向地址
     */
    private String successWebUrl;

    @Bean("aliPayHttpClient")
    public DefaultAlipayClient aliPayHttpClient() throws AlipayApiException {
        return new DefaultAlipayClient(this);
    }
}
