package com.lingyang.cloud.tencent.config;

import com.lingyang.cloud.tencent.pay.CustomRSAAutoCertificateConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2023/8/24 18:19
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "tencent.pay")
public class WeChatPayConfig {
    /**
     * appId
     */
    private String appId;
    /**
     * 微信支付商户号
     */
    private String mchId;

    /**
     * 商户序列号
     */
    private String merchantSerialNumber;

    /**
     * APIv2密钥（调用APIv2的下载平台证书接口、处理回调通知中报文时，要通过该密钥来解密信息）
     */
    private String apiV2Key;

    /**
     * APIv3密钥（调用APIv3的下载平台证书接口、处理回调通知中报文时，要通过该密钥来解密信息）
     */
    private String apiV3Key;

    /**
     * apiclient_cert.pem证书文件的绝对路径，或者以classpath:开头的类路径。
     */
    private String privateCertPath;

    /**
     * 微信支付异步回调通知地址。通知url必须以https开头（SSL协议），外网可访问，不能携带参数。
     */
    private String payNotifyUrl;


    /**
     * 微信退款异步回调通知地址。通知url必须以https开头（SSL协议），外网可访问，不能携带参数。
     */
    private String refundsNotifyUrl;

    @Bean("customCertificateConfig")
    public CustomRSAAutoCertificateConfig customCertificateConfig() {
        return new CustomRSAAutoCertificateConfig.Builder()
                .merchantId(getMchId())
                .privateKeyFromPath(getPrivateCertPath())
                .merchantSerialNumber(getMerchantSerialNumber())
                .apiV3Key(getApiV3Key())
                .build();
    }
}
