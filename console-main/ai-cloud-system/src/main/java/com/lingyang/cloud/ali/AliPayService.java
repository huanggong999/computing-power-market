package com.lingyang.cloud.ali;

import com.alibaba.fastjson2.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.lingyang.cloud.enums.order.OrderOnlinePayEnum;
import com.lingyang.cloud.handler.model.OnlinePayParam;
import com.lingyang.cloud.handler.model.OnlinePayRefundsParam;
import com.lingyang.cloud.service.SysPayService;
import com.lingyang.common.core.exception.http.HttpServiceException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/27 17:09
 */
@Service
@Slf4j
public class AliPayService implements SysPayService {

    @Resource(name = "aliPayHttpClient")
    private DefaultAlipayClient alipayClient;
    @Resource
    private AlipayConfig alipayConfig;

    @Override
    public Object pay(OnlinePayParam payParam) {
//        log.info("订单号：{}，进行支付宝支付：{}", payParam.getOrderNo(), JSONObject.toJSONString(payParam));
//        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
//        request.setReturnUrl(alipayConfig.getReturnUrl());
//        request.setNotifyUrl(alipayConfig.getNotifyUrl());
//        AlipayTradePagePayModel model = new AlipayTradePagePayModel();
//        model.setOutTradeNo(payParam.getOrderNo());
//        model.setTotalAmount(payParam.getPayAmount().toString());
//        model.setSubject("在线支付");
//        model.setProductCode("FAST_INSTANT_TRADE_PAY");
//        model.setTimeExpire(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, payParam.getTimeExpire()));
//        request.setBizModel(model);
//        try {
//            String body = alipayClient.pageExecute(request).getBody();
//            log.info("订单号：{}，进行支付宝支付：{}", payParam.getOrderNo(), body);
//            return body;
//        } catch (AlipayApiException e) {
//            log.error("支付宝支付接口调用异常：", e);
//            throw new HttpServiceException(e.getMessage());
//        }
        return null;
    }

    @Override
    public void refunds(OnlinePayRefundsParam oa) {
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        AlipayTradeRefundModel model = new AlipayTradeRefundModel();
        model.setOutTradeNo(oa.getOrderNo());
        model.setTradeNo(oa.getPayNumber());
        model.setRefundReason(oa.getReason());
        model.setRefundAmount(oa.getAmount().toString());
        request.setBizModel(model);
        try {
            AlipayTradeRefundResponse response = alipayClient.execute(request);
            log.info("订单号：{}，进行支付宝退款：{}", oa.getOrderNo(), JSONObject.toJSONString(response));
        } catch (AlipayApiException e) {
            log.error("支付宝退款接口调用异常：", e);
            throw new HttpServiceException(e.getMessage());
        }
    }

    @Override
    public OrderOnlinePayEnum getPayType() {
        return OrderOnlinePayEnum.ALI_PAY;
    }

}
