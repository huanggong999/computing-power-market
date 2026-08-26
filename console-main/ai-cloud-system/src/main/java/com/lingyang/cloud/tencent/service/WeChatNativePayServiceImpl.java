package com.lingyang.cloud.tencent.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.lingyang.cloud.config.WeiXinPayConfig;
import com.lingyang.cloud.enums.order.OrderOnlinePayEnum;
import com.lingyang.cloud.handler.model.OnlinePayParam;
import com.lingyang.cloud.handler.model.OnlinePayRefundsParam;
import com.lingyang.cloud.service.SysPayService;
import com.lingyang.cloud.tencent.client.WeChatPayClient;
import com.lingyang.cloud.tencent.config.WeChatPayConfig;
import com.lingyang.common.core.utils.AmountUtils;
import com.lingyang.common.core.utils.DateUtils;
import com.lingyang.common.core.utils.RandomUtils;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import com.wechat.pay.java.service.payments.nativepay.model.Amount;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayRequest;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayResponse;
import com.wechat.pay.java.service.refund.RefundService;
import com.wechat.pay.java.service.refund.model.AmountReq;
import com.wechat.pay.java.service.refund.model.CreateRequest;
import com.wechat.pay.java.service.refund.model.Refund;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.UUID;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/27 15:53
 */
@Service
public class WeChatNativePayServiceImpl implements SysPayService {
    private static final Logger log = LoggerFactory.getLogger(WeChatNativePayServiceImpl.class);
    @Resource
    private WeChatPayClient weChatPayClient;
    @Resource
    private WeChatPayConfig weChatPayConfig;

    @javax.annotation.Resource
    private com.github.binarywang.wxpay.service.WxPayService wxPayService;

    @Autowired
    private WeiXinPayConfig weiXinPayConfig;

    @Override
    public Object pay(OnlinePayParam onlinePayParam) {
        if (onlinePayParam.getIsApplet() == null || !onlinePayParam.getIsApplet()) {
            NativePayService nativePayService = weChatPayClient.getNativePayService();
            PrepayRequest request = new PrepayRequest();
            request.setAppid(weChatPayConfig.getAppId());
            request.setMchid(weChatPayConfig.getMchId());
            request.setNotifyUrl(weChatPayConfig.getPayNotifyUrl());
            request.setOutTradeNo(onlinePayParam.getOrderNo());
            request.setDescription("在线支付");
            Amount amount = new Amount();
            amount.setTotal(AmountUtils.yuanToDivide(onlinePayParam.getPayAmount()).intValue());
            request.setAmount(amount);
            PrepayResponse prepay = nativePayService.prepay(request);
            return prepay.getCodeUrl();
        } else {
            String ip = onlinePayParam.getIp();
            String body = "用户充值";
            WxPayUnifiedOrderRequest unifiedOrder = new WxPayUnifiedOrderRequest();
            unifiedOrder.setSpbillCreateIp(ip);
            unifiedOrder.setBody(body);
            unifiedOrder.setOutTradeNo(onlinePayParam.getOrderNo());
            unifiedOrder.setTotalFee((onlinePayParam.getPayAmount().multiply(new BigDecimal(100))).intValue());
            unifiedOrder.setNonceStr(getUuidStr());
            unifiedOrder.setNotifyUrl(weiXinPayConfig.getReserveOrderNotifyUrl());
            unifiedOrder.setSign("MD5");
            Long validate = System.currentTimeMillis();
            validate += 30 * 60 * 1000;
            unifiedOrder.setTimeExpire(new SimpleDateFormat("yyyyMMddHHmmss").format(validate));
            log.info("【微信支付】微信统一下单openid {}", onlinePayParam.getOpenid());
            unifiedOrder.setOpenid(onlinePayParam.getOpenid());
            unifiedOrder.setTradeType("JSAPI");

            unifiedOrder.setProductId(onlinePayParam.getOrderNo());
            unifiedOrder.setAttach("用户充值");

            Object result = null;
            try {
                log.info("【微信支付】微信统一下单参数 {}", JSON.toJSONString(unifiedOrder, true));
                result = wxPayService.createOrder(unifiedOrder);
            } catch (WxPayException e) {
                throw new RuntimeException(e);
            }
            com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(com.alibaba.fastjson.JSONObject.toJSONString(result));
            jsonObject.put("orderNo", onlinePayParam.getOrderNo());
            log.info("【微信支付】充值-预下单返回结果: {}", com.alibaba.fastjson.JSONObject.toJSONString(jsonObject));
            return jsonObject;
        }
    }

    public static String getUuidStr() {
        return UUID.randomUUID().toString().replace("-", "");
    }



    @Override
    public void refunds(OnlinePayRefundsParam oa) {
        RefundService refundService = weChatPayClient.getRefundService();
        CreateRequest request = new CreateRequest();
        AmountReq amount = new AmountReq();
        amount.setRefund(AmountUtils.yuanToDivide(oa.getAmount()).longValue());
        amount.setCurrency("CNY");
        amount.setTotal(AmountUtils.yuanToDivide(oa.getPayTotalAmount()).longValue());
        request.setAmount(amount);
        request.setOutTradeNo(oa.getOrderNo());
        String orderNo = "Order" + DateUtils.getDate(DateUtils.YYYYMMDDHHMMSS) + RandomUtils.getNumberRandom(6);
        request.setOutRefundNo(orderNo);
        request.setTransactionId(oa.getPayNumber());
        request.setReason(oa.getReason());
        Refund refund = refundService.create(request);
        log.info("订单号：{}，进行微信退款：{}", oa.getOrderNo(), JSONObject.toJSONString(refund));
    }

    @Override
    public OrderOnlinePayEnum getPayType() {
        return OrderOnlinePayEnum.WECHAT_PAY;
    }

}
