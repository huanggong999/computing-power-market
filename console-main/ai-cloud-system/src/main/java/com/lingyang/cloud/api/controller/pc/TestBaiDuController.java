package com.lingyang.cloud.api.controller.pc;

import cn.hutool.json.JSONObject;
import okhttp3.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/5/15 11:20
 */
@RestController
@RequestMapping("/pc/baidu")
public class TestBaiDuController {
    public static final String API_KEY = "63v6nItjuPAGvNYrXzgvLSwG";
    public static final String SECRET_KEY = "odGW44CWe4CryWjGX9n4pvQ9Pa8WjW4s";

    public static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().readTimeout(300, TimeUnit.SECONDS).build();

    /**
     * 企业四要素验证
     * @param args
     * @throws IOException
     */
//    public static void main(String []args) throws IOException {
//        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
//        String param ="idcard=" + "420203199602053310" + "&name=" + "胡恒" + "&company=" + "广州羚羊网络科技有限责任公司" + "&regnum=" + "91440101MA5AQHWFXB";
//        RequestBody body = RequestBody.create(mediaType, param);
//        Request request = new Request.Builder()
//                .url("https://aip.baidubce.com/rest/2.0/ocr/v1/four_factors_verification?access_token=" + getAccessToken())
//                .method("POST", body)
//                .addHeader("Content-Type", "application/x-www-form-urlencoded")
//                .addHeader("Accept", "application/json")
//                .build();
//        Response response = HTTP_CLIENT.newCall(request).execute();
//        assert response.body() != null;
//        System.out.println(response.body().string());
//    }
    public static void main(String []args) throws IOException {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        String param ="name=" + "胡恒" + "&company=" + "广州羚羊网络科技有限责任公司" + "&regnum=" + "91440101MA5AQHWFXB";
        RequestBody body = RequestBody.create(mediaType, param);
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rest/2.0/ocr/v1/three_factors_verification?access_token=" + getAccessToken())
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Accept", "application/json")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        ResponseBody body2 = response.body();
        assert body2 != null;
        String content = body2.string();
        JSONObject json = new JSONObject(content);
        JSONObject wordsResult = json.getJSONObject("words_result");
        String verifyResult = wordsResult.getStr("verifyresult");
        if ("1".equals(verifyResult)){

        }else {
            String nameMatch = wordsResult.getStr("namematch");
            String companyMatch = wordsResult.getStr("companymatch");
            String regNumMatch	 = wordsResult.getStr("regnummatch");
            if (!"1".equals(nameMatch)){

            }
            if (!"1".equals(companyMatch)){

            }
            if (!"1".equals(regNumMatch)){

            }
        }
        System.out.println(response.body().string());
    }



    /**
     * 身份证和名字比较
     * @param args
     * @throws IOException
     */
//    public static void main(String[] args) throws IOException {
//        MediaType mediaType = MediaType.parse("application/json");
//        String userInfo = "id_card_number=" + "452225199911100016" + "&name=" + "吴思镇";
//        RequestBody body = RequestBody.create(mediaType, userInfo);
//        Request request = new Request.Builder()
//                .url("https://aip.baidubce.com/rest/2.0/face/v3/person/idmatch?access_token=" + getAccessToken())
//                .method("POST", body)
//                .addHeader("Content-Type", "application/json")
//                .build();
//        Response response = HTTP_CLIENT.newCall(request).execute();
//        //根据error_code判断，为0时表示匹配为同一个人。否则按错误码表的定义，如222351表示身份证号码与名字不匹配。
//        assert response.body() != null;
//        System.out.println(response.body().string());
//    }

    private static String getAccessToken() throws IOException {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "grant_type=client_credentials&client_id=" + API_KEY
                + "&client_secret=" + SECRET_KEY);
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/oauth/2.0/token")
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        assert response.body() != null;
        return (String) new JSONObject(response.body().string()).get("access_token");
    }
}
