package com.lingyang.cloud.utils.esign;

import java.util.Map;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/3/21 15:54
 */
public class Demo {
    private static String eSignHost= EsignDemoConfig.EsignHost;
    private static String eSignAppId=EsignDemoConfig.EsignAppId;
    private static String eSignAppSecret=EsignDemoConfig.EsignAppSecret;

    public static void main(String[] args) throws EsignDemoException {
        String path = "/v3/doc-templates?pageNum=" + 1 + "&pageSize=" + 10;

        //请求参数body体,json格式。get或者delete请求时jsonString传空json:"{}"或者null
        String jsonParm=null;
        //请求方法
        EsignRequestType requestType= EsignRequestType.GET;
        //生成签名鉴权方式的的header
        Map<String, String> header = EsignHttpHelper.signAndBuildSignAndJsonHeader(eSignAppId,eSignAppSecret,jsonParm,requestType.name(),path,true);
        //发起接口请求
        EsignHttpResponse esignHttpResponse = EsignHttpHelper.doCommHttp(eSignHost, path, requestType, jsonParm, header, true);
        System.out.println(esignHttpResponse);
    }
}
