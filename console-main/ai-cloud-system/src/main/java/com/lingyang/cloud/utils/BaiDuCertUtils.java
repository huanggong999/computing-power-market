package com.lingyang.cloud.utils;

import cn.hutool.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/5/15 17:49
 */
@Slf4j
@Component
public class BaiDuCertUtils {

    public static final String API_KEY = "63v6nItjuPAGvNYrXzgvLSwG";
    public static final String SECRET_KEY = "odGW44CWe4CryWjGX9n4pvQ9Pa8WjW4s";

    public static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().readTimeout(300, TimeUnit.SECONDS).build();

    /**
     * 企业三要素验证（0.3块一次）
     * @param name
     * @param company
     * @param regnum
     * @return
     * @throws IOException
     */
    public Response threeFactorsVerification(String name,String company, String regnum) throws IOException {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        String param ="name=" + name + "&company=" + company + "&regnum=" + regnum;
        RequestBody body = RequestBody.create(mediaType, param);
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rest/2.0/ocr/v1/three_factors_verification?access_token=" + getAccessToken())
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Accept", "application/json")
                .build();
        Response execute = HTTP_CLIENT.newCall(request).execute();
        log.info("企业三要素验证返回:{}", execute);
        return execute;
    }

    /**
     * 企业四要素验证(太贵了，不用，1块钱1次)
     * @param idCard
     * @param name
     * @param company
     * @param regnum
     * @return
     * @throws IOException
     */
    public Response fourFactorsVerification(String idCard, String name,String company, String regnum) throws IOException {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        String param ="idcard=" + idCard + "&name=" + name + "&company=" + company + "&regnum=" + regnum;
        RequestBody body = RequestBody.create(mediaType, param);
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rest/2.0/ocr/v1/four_factors_verification?access_token=" + getAccessToken())
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Accept", "application/json")
                .build();
        Response execute = HTTP_CLIENT.newCall(request).execute();
        log.info("企业四要素验证返回:{}", execute);
        return execute;
    }

    /**
     * 身份证验证
     *
     * @param idCardImage
     * @return
     * @throws IOException
     */
    public Response idCard(String idCardImage) throws IOException {
        // 本地文件路径
        String filePath = "[本地文件路径]";
        byte[] imgData = FileUtil.readFileByBytes(filePath);
        String imgStr = Base64Util.encode(imgData);
        String imgParam = URLEncoder.encode(imgStr, "UTF-8");
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        String idCardInfo = "id_card_side=" + "front" + "&image=" + imgParam;
        RequestBody body = RequestBody.create(mediaType, idCardInfo);
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rest/2.0/ocr/v1/idcard?access_token=" + getAccessToken())
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Accept", "application/json")
                .build();
        Response execute = HTTP_CLIENT.newCall(request).execute();
        assert execute.body() != null;
        System.out.println(execute.body().string());
        log.info("身份证和名字比较验证返回:{}", execute);
        return execute;
    }

    /**
     * 身份证和名字比较（0.3块一次）
     * @param idCard
     * @param name
     * @throws IOException
     */
    public Response idMatch(String idCard, String name) throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        String userInfo = "id_card_number=" + idCard + "&name=" + name;
        RequestBody body = RequestBody.create(mediaType, userInfo);
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rest/2.0/face/v3/person/idmatch?access_token=" + getAccessToken())
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        //根据error_code判断，为0时表示匹配为同一个人。否则按错误码表的定义，如222351表示身份证号码与名字不匹配。
        Response execute = HTTP_CLIENT.newCall(request).execute();
        log.info("身份证和名字比较验证返回:{}", execute);
        return execute;
    }

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
