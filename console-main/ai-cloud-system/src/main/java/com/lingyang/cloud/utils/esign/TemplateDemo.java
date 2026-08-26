package com.lingyang.cloud.utils.esign;

import com.alibaba.fastjson2.JSON;
import com.google.gson.Gson;
import com.lingyang.cloud.api.controller.pc.PcContractController;
import com.lingyang.cloud.entity.SysCreditContract;
import com.lingyang.cloud.model.dto.OrderContractDTO;
import com.lingyang.common.core.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * description 模板服务
 *
 * @author 语冰
 * datetime 2022年8月10日上午09:43:24
 */
@Slf4j
public class TemplateDemo extends Exception {

    private static String eSignHost = EsignDemoConfig.EsignHost;
    private static String eSignAppId = EsignDemoConfig.EsignAppId;
    private static String eSignAppSecret = EsignDemoConfig.EsignAppSecret;

    public static void main(String[] args) throws EsignDemoException {
        Gson gson = new Gson();

        /* 获取制作合同模板页面*/
//		EsignHttpResponse getTemplateCreateUrl = getTemplateCreateUrl();
//		JsonObject getTemplateCreateUrlObject = gson.fromJson(getTemplateCreateUrl.getBody(), JsonObject.class);
//
//		String docTemplateCreateUrl = getTemplateCreateUrlObject.getAsJsonObject("data").get("docTemplateCreateUrl").getAsString();
//		String docTemplateId = getTemplateCreateUrlObject.getAsJsonObject("data").get("docTemplateId").getAsString();
//		System.err.println("制作合同模板的页面链接:"+docTemplateCreateUrl);
//		System.err.println("合同模板id:"+docTemplateId);
//
//		/* 获取编辑合同模板页面*/
//		EsignHttpResponse getTemplateEditUrl = getTemplateEditUrl();
//		JsonObject getTemplateEditUrlObject = gson.fromJson(getTemplateEditUrl.getBody(), JsonObject.class);
//		String docTemplateEditUrl = getTemplateEditUrlObject.getAsJsonObject("data").get("docTemplateEditUrl").getAsString();
//		System.err.println("编辑合同模板的页面链接:"+docTemplateEditUrl);
//
//		/* 查询合同模板中控件详情*/
//		EsignHttpResponse getComponentsInfo = getComponentsInfo();
//		JsonObject getComponentsInfoObject = gson.fromJson(getComponentsInfo.getBody(), JsonObject.class);
//		System.err.println("查询模板详情返回："+getComponentsInfoObject);


        /* 填写模板生成文件*/
//        EsignHttpResponse createByDocTemplate = createByDocTemplate();
//        JsonObject createByDocTemplateObject = gson.fromJson(createByDocTemplate.getBody(), JsonObject.class);
//        String fileId = createByDocTemplateObject.getAsJsonObject("data").get("fileId").getAsString();
//        String fileDownloadUrl = createByDocTemplateObject.getAsJsonObject("data").get("fileDownloadUrl").getAsString();
//        System.err.println("填充后文件id:" + fileId);
//        System.err.println("文件下载链接:" + fileDownloadUrl);

//        SysContract contract = new SysContract();
//
//        contract.setContractNo("HT" + System.currentTimeMillis());
//        contract.setClientName("周宇");
//        contract.setClientContactPerson("周宇");
//        contract.setClientContactPhone("13677495631");
//        contract.setClientContactAddress("广东省广州市黄埔区");
//        contract.setStatus(2);
//
//        contract.setFileId("48f14fc5c6804364a6639d112aa42595");
//        contract.setFileDownloadUrl("fileDownloadUrl");
//        EsignHttpResponse file = PlatformSignToC.createByFile(contract);
//        String body = file.getBody();
//        System.out.println(body);

        EsignHttpResponse response = SignDemo.signUrl("5d7f58af020746caabc8dce18876ff04", "13677495631");
        System.out.println(response.getBody());

        //删除合同模板
//		EsignHttpResponse deleteDocTemplate = deleteDocTemplate();
//		JsonObject deleteDocTemplateObject = gson.fromJson(deleteDocTemplate.getBody(), JsonObject.class);
//		System.err.println("删除模板文件返回:"+deleteDocTemplateObject);
    }

    /**
     * 获取制作合同模板页面
     *
     * @return
     * @throws EsignDemoException
     */
    private static EsignHttpResponse getTemplateCreateUrl() throws EsignDemoException {
        String apiaddr = "/v3/doc-templates/doc-template-create-url";
        //请求参数body体,json格式。get或者delete请求时jsonString传空json:"{}"或者null
        String jsonParm = "{\"docTemplateName\":\"测试模板\",\"docTemplateType\":0,\"fileId\":\"b3f01axxxxxx05d320da35\",\"redirectUrl\":\"https://www.esign.cn\"}";

        //请求方法
        EsignRequestType requestType = EsignRequestType.POST;
        //生成签名鉴权方式的的header
        Map<String, String> header = EsignHttpHelper.signAndBuildSignAndJsonHeader(eSignAppId, eSignAppSecret, jsonParm, requestType.name(), apiaddr, true);
        //发起接口请求
        return EsignHttpHelper.doCommHttp(eSignHost, apiaddr, requestType, jsonParm, header, true);
    }

    /**
     * 获取编辑合同模板页面
     *
     * @return
     * @throws EsignDemoException
     */
    private static EsignHttpResponse getTemplateEditUrl() throws EsignDemoException {
        String docTemplateId = "a6f7e3b4e7xxxxxxxedc6fc89";
        String apiaddr = "/v3/doc-templates/" + docTemplateId + "/doc-template-edit-url";
        //请求参数body体,json格式。get或者delete请求时jsonString传空json:"{}"或者null
        String jsonParm = "{\"redirectUrl\":\"https://www.esign.cn\"}";

        //请求方法
        EsignRequestType requestType = EsignRequestType.POST;
        //生成签名鉴权方式的的header
        Map<String, String> header = EsignHttpHelper.signAndBuildSignAndJsonHeader(eSignAppId, eSignAppSecret, jsonParm, requestType.name(), apiaddr, true);
        //发起接口请求
        return EsignHttpHelper.doCommHttp(eSignHost, apiaddr, requestType, jsonParm, header, true);
    }

    /**
     * 查询合同模板中控件详情
     */
    private static EsignHttpResponse getComponentsInfo() throws EsignDemoException {
        String docTemplateId = "a6f7e3b4xxxxxxxxfdedc6fc89";
        String apiaddr = "/v3/doc-templates/" + docTemplateId;

        //请求参数body体,json格式。get或者delete请求时jsonString传空json:"{}"或者null
        String jsonParm = null;
        //请求方法
        EsignRequestType requestType = EsignRequestType.GET;
        //生成签名鉴权方式的的header
        Map<String, String> header = EsignHttpHelper.signAndBuildSignAndJsonHeader(eSignAppId, eSignAppSecret, jsonParm, requestType.name(), apiaddr, true);
        //发起接口请求
        return EsignHttpHelper.doCommHttp(eSignHost, apiaddr, requestType, jsonParm, header, true);
    }

    public static EsignHttpResponse createByDocTemplate(OrderContractDTO d) throws EsignDemoException {
        String apiaddr = "/v3/files/create-by-doc-template";
        //请求参数body体,json格式。get或者delete请求时jsonString传空json:"{}"或者null
        String jsonParm = "{\n" +
                "    \"docTemplateId\": \"" + PcContractController.docTemplateId + "\",\n" +
                "    \"fileName\": \"产品及服务协议" + DateUtils.getDate("yyyyMMdd") + ".doc\",\n" +
                "    \"requiredCheck\": false,\n" +
                "    \"components\":[\n" +
                "        {\n" +
                "            \"componentId\":\"\",\n" +
                "            \"componentKey\":\"p1\",\n" +
                "            \"componentValue\":\"" + d.getClientName() + "\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"componentId\":\"\",\n" +
                "            \"componentKey\":\"p2\",\n" +
                "            \"componentValue\":\"" + d.getClientName() + "\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"componentId\":\"\",\n" +
                "            \"componentKey\":\"p3\",\n" +
                "            \"componentValue\":\"" + DateUtils.getDate("yyyy-MM-dd") + "\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"componentId\":\"\",\n" +
                "            \"componentKey\":\"p4\",\n" +
                "            \"componentValue\":\"" + DateUtils.getDate("yyyy-MM-dd") + "\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"componentId\":\"\",\n" +
                "            \"componentKey\":\"p5\",\n" +
                "            \"componentValue\":\"" + DateUtils.getDate("yyyy-MM-dd") + "\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        //请求方法
        EsignRequestType requestType = EsignRequestType.POST;
        //生成签名鉴权方式的的header
        Map<String, String> header = EsignHttpHelper.signAndBuildSignAndJsonHeader(eSignAppId, eSignAppSecret, jsonParm, requestType.name(), apiaddr, true);
        //发起接口请求
        return EsignHttpHelper.doCommHttp(eSignHost, apiaddr, requestType, jsonParm, header, true);
    }


    public static EsignHttpResponse createCreditByDocTemplate(SysCreditContract d) throws EsignDemoException {
        String apiaddr = "/v3/files/create-by-doc-template";
        //请求参数body体,json格式。get或者delete请求时jsonString传空json:"{}"或者null
        String jsonParm = "{\n" +
                "    \"docTemplateId\": \""+ PcContractController.docTemplateIdCredit +"\",\n" +
                "    \"fileName\": \"授信额合同"+DateUtils.getDate("yyyyMMdd")+".doc\",\n" +
                "    \"requiredCheck\": false,\n" +
                "    \"components\":[\n" +
                "        {\n" +
                "            \"componentId\":\"\",\n" +
                "            \"componentKey\":\"jf\",\n" +
//                "            \"componentValue\":\""+d.getClientContactPerson() + "    " +  d.getClientContactPhone() +"\"\n" +
                "            \"componentValue\":\""+d.getClientContactPerson() +"\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"componentId\":\"\",\n" +
                "            \"componentKey\":\"amount\",\n" +
                "            \"componentValue\":\""+ d.getAmount() +"\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"componentId\":\"\",\n" +
                "            \"componentKey\":\"year1\",\n" +
                "            \"componentValue\":\""+DateUtils.getDate("yyyy")+"\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"componentId\":\"\",\n" +
                "            \"componentKey\":\"zq\",\n" +
                "            \"componentValue\":\""+d.getZq()+"\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"componentId\":\"\",\n" +
                "            \"componentKey\":\"year2\",\n" +
                "            \"componentValue\":\""+DateUtils.getDate("yyyy")+"\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"componentId\":\"\",\n" +
                "            \"componentKey\":\"month1\",\n" +
                "            \"componentValue\":\""+DateUtils.getDate("MM")+"\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"componentId\":\"\",\n" +
                "            \"componentKey\":\"month2\",\n" +
                "            \"componentValue\":\""+DateUtils.getDate("MM")+"\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"componentId\":\"\",\n" +
                "            \"componentKey\":\"day1\",\n" +
                "            \"componentValue\":\""+DateUtils.getDate("dd")+"\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"componentId\":\"\",\n" +
                "            \"componentKey\":\"day2\",\n" +
                "            \"componentValue\":\""+DateUtils.getDate("dd")+"\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        //请求方法
        EsignRequestType requestType = EsignRequestType.POST;
        //生成签名鉴权方式的的header
        Map<String, String> header = EsignHttpHelper.signAndBuildSignAndJsonHeader(eSignAppId, eSignAppSecret, jsonParm, requestType.name(), apiaddr, true);
        //发起接口请求
        EsignHttpResponse esignHttpResponse = EsignHttpHelper.doCommHttp(eSignHost, apiaddr, requestType, jsonParm, header, true);
        log.info("合同模版返回: {}", JSON.toJSONString(esignHttpResponse));
        return esignHttpResponse;
    }


    /**
     * 填写模板生成文件
     *
     * @return
     * @throws EsignDemoException
     */
    public static EsignHttpResponse createByDocTemplate() throws EsignDemoException {
        String apiaddr = "/v3/files/create-by-doc-template";
        //请求参数body体,json格式。get或者delete请求时jsonString传空json:"{}"或者null
        String jsonParm = "{\n" +
                "    \"docTemplateId\": \""+ PcContractController.docTemplateId +"\",\n" +
                "    \"fileName\": \"测试模板111.doc\",\n" +
                "    \"requiredCheck\": false,\n" +
                "    \"components\":[\n" +
                "        {\n" +
                "            \"componentId\":\"\",\n" +
                "            \"componentKey\":\"p1\",\n" +
                "            \"componentValue\":\"这里是填充的文本\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"componentId\":\"\",\n" +
                "            \"componentKey\":\"p2\",\n" +
                "            \"componentValue\":\"1234567890\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"componentId\":\"\",\n" +
                "            \"componentKey\":\"p3\",\n" +
                "            \"componentValue\":\"这里是填充的文本\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"componentId\":\"\",\n" +
                "            \"componentKey\":\"p4\",\n" +
                "            \"componentValue\":\"1234567890\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"componentId\":\"\",\n" +
                "            \"componentKey\":\"p5\",\n" +
                "            \"componentValue\":\"20250101\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"componentId\":\"\",\n" +
                "            \"componentKey\":\"p6\",\n" +
                "            \"componentValue\":\"20250101\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        //请求方法
        EsignRequestType requestType = EsignRequestType.POST;
        //生成签名鉴权方式的的header
        Map<String, String> header = EsignHttpHelper.signAndBuildSignAndJsonHeader(eSignAppId, eSignAppSecret, jsonParm, requestType.name(), apiaddr, true);
        //发起接口请求
        return EsignHttpHelper.doCommHttp(eSignHost, apiaddr, requestType, jsonParm, header, true);
    }

    /**
     * 删除模板
     */
    private static EsignHttpResponse deleteDocTemplate() throws EsignDemoException {
        String docTemplateId = "a6f7e3b4xxxxxxxxfc89";
        String apiaddr = "/v3/doc-templates/" + docTemplateId;

        //请求参数body体,json格式。get或者delete请求时jsonString传空json:"{}"或者null
        String jsonParm = "{}";
        //请求方法
        EsignRequestType requestType = EsignRequestType.DELETE;
        //生成签名鉴权方式的的header
        Map<String, String> header = EsignHttpHelper.signAndBuildSignAndJsonHeader(eSignAppId, eSignAppSecret, jsonParm, requestType.name(), apiaddr, true);
        //发起接口请求
        return EsignHttpHelper.doCommHttp(eSignHost, apiaddr, requestType, jsonParm, header, true);
    }

}