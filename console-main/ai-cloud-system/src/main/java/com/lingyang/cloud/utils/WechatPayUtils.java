package com.lingyang.cloud.utils;

import com.alibaba.fastjson2.JSONObject;
import com.lingyang.cloud.api.model.callback.WeChatPayCallbackResultBody;
import com.lingyang.cloud.tencent.config.WeChatPayConfig;
import com.lingyang.common.core.exception.http.HttpServiceException;
import com.lingyang.common.core.model.result.Result;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2023/9/7 9:47
 */
@Component
@Slf4j
public class WechatPayUtils {

    @Resource
    private WeChatPayConfig payConfig;

    public JSONObject decrypt(WeChatPayCallbackResultBody resultBody) {
        try {
            WeChatPayCallbackResultBody.WxPayCallbackResource resource = resultBody.getResource();
            String associatedData = resource.getAssociated_data();
            String nonce = resource.getNonce();
            String ciphertext = resource.getCiphertext();
            // 你的Apiv3秘钥转换成Utf8的Byte
            byte[] aesKey = payConfig.getApiV3Key().getBytes(StandardCharsets.UTF_8);
            byte[] associatedDataBytes = associatedData.getBytes(StandardCharsets.UTF_8);
            byte[] nonceBytes = nonce.getBytes(StandardCharsets.UTF_8);
            AesUtil aesUtil = new AesUtil(aesKey);
            log.info("微信支付回调 - 开始解密: {},{},{}", associatedDataBytes, nonceBytes, ciphertext);
            String decryptedString = aesUtil.decryptToString(associatedDataBytes, nonceBytes, ciphertext);
            // 这里所需要的的工具类在文末
            log.info("微信支付回调 - 解密结果: {}", decryptedString);
            // 解密得到的json结果
            return JSONObject.parseObject(decryptedString);
        } catch (Exception e) {
            log.error("微信支付回调 - 解密失败：", e);
            Result.throwsError(HttpServiceException.class,"微信支付回调 - 解密失败");
        }
        return null;
    }

}
