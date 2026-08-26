package com.lingyang.cloud.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 微信支付配置
 */
@Data
@Component
public class WeiXinPayConfig {

    /**
     * AppId
     */
    @Value("${applet.weixin.appId}")
    private String appId;
    /**
     * App密钥
     */
    @Value("${applet.weixin.appKey}")
    private String appSecret;

    /**
     * 微信支付商户号
     */
    @Value("${tencent.pay.mch_id}")
    private String mchId;

    /**
     * 微信支付商户密钥
     */
    @Value("${tencent.pay.apiV2Key}")
    private String mchKey;

    /**
     * 服务商模式下的子商户公众账号ID，普通模式请不要配置，请在配置文件中将对应项删除
     */
    private String subAppId;

    /**
     * 服务商模式下的子商户号，普通模式请不要配置，最好是请在配置文件中将对应项删除
     */
    private String subMchId;

    /**
     * 用户预约单微信支付回调地址
     */
    @Value("${applet.weixin.reserveOrderNotifyUrl}")
    private String reserveOrderNotifyUrl;

    /**
     * apiclient_cert.p12文件的绝对路径，或者如果放在项目中，请以classpath:开头指定
     */
    @Value("${tencent.pay.privateCertPath}")
    private String keyPath;




}
