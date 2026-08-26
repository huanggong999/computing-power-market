package com.lingyang.cloud.utils;

import com.alibaba.fastjson.JSON;
import com.lingyang.common.core.utils.RandomUtils;
import com.volcengine.model.request.SmsSendRequest;
import com.volcengine.model.response.SmsSendResponse;
import com.volcengine.service.sms.SmsService;
import com.volcengine.service.sms.SmsServiceInfoConfig;
import com.volcengine.service.sms.impl.SmsServiceImpl;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class SendSmsUtils {
    private static final String ACCESS_KEY = System.getenv().getOrDefault("VOLC_SMS_ACCESS_KEY", "");
    private static final String ACCESS_SECRET = System.getenv().getOrDefault("VOLC_SMS_ACCESS_SECRET", "");
    private static SmsService smsService = SmsServiceImpl.getInstance(new SmsServiceInfoConfig(ACCESS_KEY, ACCESS_SECRET));

    public static void sendSmsRegister(String phone, String code)  {
        SmsSendRequest req = new SmsSendRequest();
        req.setPhoneNumbers(phone);
        req.setSmsAccount("80c6457a");
        req.setTemplateId("ST_819954f8");
        req.setSign("逸云数智");

        Map<String,String> param = new HashMap<>();
        param.put("code", code);
        req.setTemplateParamByMap(param);

        try {
            SmsSendResponse response = smsService.sendV2(req);
            System.out.println(JSON.toJSONString(response));
        } catch (Exception e) {
            log.info("发送短信失败：{}" ,e.getMessage(), e);
        }
    }

    public static void sendSmsLogin(String phone, String code)  {
        SmsSendRequest req = new SmsSendRequest();
        req.setPhoneNumbers(phone);
        req.setSmsAccount("80c6457a");
        req.setTemplateId("SPT_09a29a26");
        req.setSign("逸云数智");

        Map<String,String> param = new HashMap<>();
        param.put("code", code);
        req.setTemplateParamByMap(param);

        try {
            SmsSendResponse response = smsService.sendV2(req);
            System.out.println(JSON.toJSONString(response));
        } catch (Exception e) {
            log.info("发送短信失败：{}" ,e.getMessage(), e);
        }
    }


    public static void sendSmsPwd(String phone, String code)  {
        SmsSendRequest req = new SmsSendRequest();
        req.setPhoneNumbers(phone);
        req.setSmsAccount("80c6457a");
        req.setTemplateId("ST_8231fdb2");
        req.setSign("逸云数智");

        Map<String,String> param = new HashMap<>();
        param.put("code", code);
        req.setTemplateParamByMap(param);

        try {
            SmsSendResponse response = smsService.sendV2(req);
            System.out.println(JSON.toJSONString(response));
        } catch (Exception e) {
            log.info("发送短信失败：{}" ,e.getMessage(), e);
        }
    }

    public static void sendB(String phone)  {
        SmsSendRequest req = new SmsSendRequest();
        req.setPhoneNumbers(phone);
        req.setSmsAccount("80c6457a");
        req.setTemplateId("ST_82ec15a6");
        req.setSign("逸云数智");
        try {
            SmsSendResponse response = smsService.sendV2(req);
            System.out.println(JSON.toJSONString(response));
        } catch (Exception e) {
            log.info("发送短信失败：{}" ,e.getMessage(), e);
        }
    }

    /**
     * AGIC产品即将到期提醒
     * @param phone
     */
    public static void sendAGICDueDateReminder(String phone)  {
        SmsSendRequest req = new SmsSendRequest();
        req.setPhoneNumbers(phone);
        req.setSmsAccount("80c6457a");
        req.setTemplateId("ST_84e18146");
        req.setSign("逸云数智");
        try {
            SmsSendResponse response = smsService.sendV2(req);
            System.out.println(JSON.toJSONString(response));
        } catch (Exception e) {
            log.info("发送短信失败：{}" ,e.getMessage(), e);
        }
    }


    public static void main(String[] args) throws IOException {
        String numberRandom = RandomUtils.getNumberRandom(6);
        sendSmsLogin("14736398065",numberRandom);
    }

    public static void sendSmsUpdatePhone(String phone, String code) {
        SmsSendRequest req = new SmsSendRequest();
        req.setPhoneNumbers(phone);
        req.setSmsAccount("80c6457a");
        req.setTemplateId("ST_82994119");
        req.setSign("逸云数智");

        Map<String,String> param = new HashMap<>();
        param.put("code", code);
        req.setTemplateParamByMap(param);

        try {
            SmsSendResponse response = smsService.sendV2(req);
            System.out.println(JSON.toJSONString(response));
        } catch (Exception e) {
            log.info("发送短信失败：{}" ,e.getMessage(), e);
        }
    }

    public static void sendSmsBindPhone(String phone, String code) {
        SmsSendRequest req = new SmsSendRequest();
        req.setPhoneNumbers(phone);
        req.setSmsAccount("80c6457a");
        req.setTemplateId("ST_82a7c388");
        req.setSign("逸云数智");

        Map<String,String> param = new HashMap<>();
        param.put("code", code);
        req.setTemplateParamByMap(param);

        try {
            SmsSendResponse response = smsService.sendV2(req);
            System.out.println(JSON.toJSONString(response));
        } catch (Exception e) {
            log.info("发送短信失败：{}" ,e.getMessage(), e);
        }
    }
}
