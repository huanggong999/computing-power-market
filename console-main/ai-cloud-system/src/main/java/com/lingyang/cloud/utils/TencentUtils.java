package com.lingyang.cloud.utils;

import com.tencentcloudapi.common.AbstractModel;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.faceid.v20180301.FaceidClient;
import com.tencentcloudapi.faceid.v20180301.models.GetEidResultRequest;
import com.tencentcloudapi.faceid.v20180301.models.GetEidResultResponse;
import com.tencentcloudapi.faceid.v20180301.models.GetEidTokenRequest;
import com.tencentcloudapi.faceid.v20180301.models.GetEidTokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/5/16 10:38
 */
@Slf4j
@Component
public class TencentUtils {

    public static final String SECRET_ID = System.getenv().getOrDefault("TENCENT_SECRET_ID", "");
    public static final String SECRET_KEY = System.getenv().getOrDefault("TENCENT_SECRET_KEY", "");

    public static final String MERCHANT_ID = "00EI2505161114099784";

    public String getEidToken(String idCard, String name) {
        try{
            // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey，此处还需注意密钥对的保密
            // 代码泄露可能会导致 SecretId 和 SecretKey 泄露，并威胁账号下所有资源的安全性
            // 以下代码示例仅供参考，建议采用更安全的方式来使用密钥
            // 请参见：https://cloud.tencent.com/document/product/1278/85305
            // 密钥可前往官网控制台 https://console.cloud.tencent.com/cam/capi 进行获取
            Credential cred = new Credential(SECRET_ID, SECRET_KEY);
            // 使用临时密钥示例
            // Credential cred = new Credential("SecretId", "SecretKey", "Token");
            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("faceid.tencentcloudapi.com");
            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            // 实例化要请求产品的client对象,clientProfile是可选的
            FaceidClient client = new FaceidClient(cred, "", clientProfile);
            // 实例化一个请求对象,每个接口都会对应一个request对象
            GetEidTokenRequest req = new GetEidTokenRequest();
            req.setMerchantId(MERCHANT_ID);
            req.setName(name);
            req.setIdCard(idCard);
            // 返回的resp是一个GetEidTokenResponse的实例，与请求对象对应
            GetEidTokenResponse resp = client.GetEidToken(req);
            // 输出json格式的字符串回包
            return AbstractModel.toJsonString(resp);
        } catch (TencentCloudSDKException e) {
            log.error("获取EidToken获取失败：{}",e.getMessage(),e);
        }
        return null;
    }

    public String getEidResult(String eidToken){
        try{
            Credential cred = new Credential(SECRET_ID, SECRET_KEY);
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("faceid.tencentcloudapi.com");
            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            // 实例化要请求产品的client对象,clientProfile是可选的
            FaceidClient client = new FaceidClient(cred, "", clientProfile);
            // 实例化一个请求对象,每个接口都会对应一个request对象
            GetEidResultRequest req = new GetEidResultRequest();
            req.setEidToken(eidToken);
            req.setInfoType(String.valueOf(1));
            // 返回的resp是一个GetEidTokenResponse的实例，与请求对象对应
            GetEidResultResponse resp = client.GetEidResult(req);
            // 输出json格式的字符串回包
            return AbstractModel.toJsonString(resp);
        } catch (TencentCloudSDKException e) {
            log.error("获取E证通结果信息失败：{}",e.getMessage(),e);
        }
        return null;
    }

    public static void main(String [] args) {
        try{
            // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey，此处还需注意密钥对的保密
            // 代码泄露可能会导致 SecretId 和 SecretKey 泄露，并威胁账号下所有资源的安全性
            // 以下代码示例仅供参考，建议采用更安全的方式来使用密钥
            // 请参见：https://cloud.tencent.com/document/product/1278/85305
            // 密钥可前往官网控制台 https://console.cloud.tencent.com/cam/capi 进行获取
            Credential cred = new Credential(SECRET_ID, SECRET_KEY);
            // 使用临时密钥示例
            // Credential cred = new Credential("SecretId", "SecretKey", "Token");
            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("faceid.tencentcloudapi.com");
            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            // 实例化要请求产品的client对象,clientProfile是可选的
            String merchantId = "00EI2505161114099784";
            FaceidClient client = new FaceidClient(cred, "", clientProfile);
            // 实例化一个请求对象,每个接口都会对应一个request对象
            GetEidTokenRequest req = new GetEidTokenRequest();
            req.setName("吴思镇");
            req.setIdCard("452225199911100016");
            req.setMerchantId(merchantId);
            // 返回的resp是一个GetEidTokenResponse的实例，与请求对象对应
            GetEidTokenResponse resp = client.GetEidToken(req);
            // 输出json格式的字符串回包
            System.out.println(AbstractModel.toJsonString(resp));
        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
        }
    }
}
