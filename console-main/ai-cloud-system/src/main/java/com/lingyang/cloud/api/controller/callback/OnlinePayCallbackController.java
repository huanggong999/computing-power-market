package com.lingyang.cloud.api.controller.callback;

import com.alibaba.fastjson2.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.lingyang.cloud.ali.AlipayConfig;
import com.lingyang.cloud.api.model.callback.WeChatPayCallbackResultBody;
import com.lingyang.cloud.entity.SysOrderEntity;
import com.lingyang.cloud.service.SysOrderService;
import com.lingyang.cloud.utils.WechatPayUtils;
import com.lingyang.common.cache.CacheQueueService;
import com.lingyang.common.cache.queue.QueueMessageBody;
import com.lingyang.common.core.exception.http.HttpServiceException;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.lingyang.cloud.common.constant.CacheQueueConstant.ORDER_EXPIRE_QUEUE_TYPE;

/**
 * @Description: 在线支付回调
 * @Author: 王小龙
 * @Date: 2024/11/27 16:12
 */
@Slf4j
@RestController
@RequestMapping("/apiCallback/onlinePayCallback")
public class OnlinePayCallbackController {

    @Resource
    private WechatPayUtils wechatPayUtils;
    @Resource
    private SysOrderService sysOrderService;

    @javax.annotation.Resource
    private com.github.binarywang.wxpay.service.WxPayService wxPayService;
    @Resource
    private AlipayConfig alipayConfig;

    @Resource
    private CacheQueueService cacheQueueService;

    @PostMapping("/wechatPayNotify")
    public Map<String, Object> payNotify(@RequestBody WeChatPayCallbackResultBody weChatPayCallbackResultBody) {
        log.info("===========微信回调开始============");
        String code = "SUCCESS";
        String message = "";
        JSONObject data;
        try {
            log.info("微信回调数据：{}", weChatPayCallbackResultBody);
            data = wechatPayUtils.decrypt(weChatPayCallbackResultBody);
            log.info("微信回调解密完成：{}", data.toString());
            String state = data.getString("trade_state");
            if ("SUCCESS".equals(state)) {
                String orderNo = data.getString("out_trade_no");
                SysOrderEntity sysOrderEntity = sysOrderService.getOrderDetailByOrderNo(orderNo);
                sysOrderService.paySuccess(sysOrderEntity, data.getString("transaction_id"));
                for (Object queue : cacheQueueService.getAllQueue()) {
                    if (queue instanceof QueueMessageBody messageBody) {
                        if (messageBody.getMessageType().equals(ORDER_EXPIRE_QUEUE_TYPE) && messageBody.getBody().equals(sysOrderEntity.getId().toString())) {
                            cacheQueueService.remove(messageBody);
                            log.info("订单{}已支付成功", sysOrderEntity.getId());
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("微信回调解析失败：", e);
            // 处理退款
            code = "FAIL";
            message = "失败";
        }
        Map<String, Object> resultMap = new HashMap<>(2);
        resultMap.put("code", code);
        resultMap.put("message", message);
        return resultMap;
    }

    @PostMapping(path = "/pay-wx-notify",
            consumes = {"application/xml", "text/xml"},
            produces = "application/xml;charset=utf-8")
    public String payWxNotify(@RequestBody String xmlData) {
        log.info("===========微信小程序回调开始============");
        try {
            log.info("【微信支付】支付回调返回数据:{}", xmlData);
            final WxPayOrderNotifyResult notifyResult = wxPayService.parseOrderNotifyResult(xmlData);
            log.info("【微信支付】notifyResult的值:{}", notifyResult);
            if ("SUCCESS".equals(notifyResult.getReturnCode()) && "SUCCESS".equals(notifyResult.getResultCode())) {
                // 平台订单号
                String orderNo = notifyResult.getOutTradeNo();
                String transactionId = notifyResult.getTransactionId();
                SysOrderEntity sysOrderEntity = sysOrderService.getOrderDetailByOrderNo(orderNo);
                sysOrderService.paySuccess(sysOrderEntity, transactionId);
                for (Object queue : cacheQueueService.getAllQueue()) {
                    if (queue instanceof QueueMessageBody messageBody) {
                        if (messageBody.getMessageType().equals(ORDER_EXPIRE_QUEUE_TYPE) && messageBody.getBody().equals(sysOrderEntity.getId().toString())) {
                            cacheQueueService.remove(messageBody);
                            log.info("订单{}已支付成功", sysOrderEntity.getId());
                        }
                    }
                }
                return WxPayNotifyResponse.success("成功");
            }  else {
                return WxPayNotifyResponse.fail(notifyResult.getReturnMsg());
            }
        } catch (Exception e) {
            log.info("【微信支付】支付回调通知异常", e);
            return WxPayNotifyResponse.fail("支付回调通知异常");
        }
    }

    /**
     * 支付宝支付成功后.回调该接口
     */
    @PostMapping("/ali_pay_notify")
    public String aliPayNotify(@RequestParam Map<String, String> params) {
        try {
            log.info("获取到支付宝支付成功回调： {}", JSONObject.toJSONString(params));
            boolean flag = AlipaySignature.rsaCheckV1(params, alipayConfig.getAlipayPublicKey(), alipayConfig.getCharset(), alipayConfig.getSignType());
            if (flag) {
                log.info("支付回调验签成功");
                if ("TRADE_SUCCESS".equals(params.get("trade_status"))) {
                    //处理充值成功
                    String orderNo = params.get("out_trade_no");
                    SysOrderEntity sysOrderEntity = sysOrderService.getOrderDetailByOrderNo(orderNo);
                    sysOrderService.paySuccess(sysOrderEntity, params.get("trade_no"));
                    for (Object queue : cacheQueueService.getAllQueue()) {
                        if (queue instanceof QueueMessageBody messageBody) {
                            if (messageBody.getMessageType().equals(ORDER_EXPIRE_QUEUE_TYPE) && messageBody.getBody().equals(sysOrderEntity.getId().toString())) {
                                cacheQueueService.remove(messageBody);
                                log.info("订单{}已支付成功", sysOrderEntity.getId());
                            }
                        }
                    }
                }
            }
            return flag ? "success" : "error";
        } catch (AlipayApiException e) {
            log.info("支付宝错误回调，验签失败: {}", e.getErrMsg());
            throw new HttpServiceException(e.getErrMsg());
        }
    }



    /**
     * 支付宝支付成功后.通知页面
     */
    @GetMapping("/ali_pay_return")
    public void aliPayReturnUrl(@RequestParam Map<String, String> params, HttpServletResponse response) throws IOException {
        try {
            boolean flag = AlipaySignature.rsaCheckV1(params, alipayConfig.getAlipayPublicKey(), alipayConfig.getCharset(), alipayConfig.getSignType());
            if (!flag) {
                throw new HttpServiceException("支付宝同步回调验签失败");
            }
        } catch (Exception e) {
            throw new HttpServiceException(e.getLocalizedMessage());
        }
        response.sendRedirect(alipayConfig.getSuccessWebUrl());
    }
}
