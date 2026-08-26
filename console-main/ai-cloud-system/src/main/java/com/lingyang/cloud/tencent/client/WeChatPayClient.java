package com.lingyang.cloud.tencent.client;

import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import com.wechat.pay.java.service.refund.RefundService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2023/8/29 11:09
 */
@Component
public class WeChatPayClient {

    @Resource
    private Config customCertificateConfig;

    public NativePayService nativePayService;

    public RefundService refundService;

    public NativePayService getNativePayService() {
        if (nativePayService == null ) {
            nativePayService = new NativePayService.Builder().config(customCertificateConfig).build();
        }
        return nativePayService;
    }

    public RefundService getRefundService() {
        if (refundService == null ) {
            refundService = new RefundService.Builder().config(customCertificateConfig).build();
        }
        return refundService;
    }
}