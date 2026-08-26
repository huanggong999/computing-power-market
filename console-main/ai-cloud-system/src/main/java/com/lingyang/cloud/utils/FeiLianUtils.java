package com.lingyang.cloud.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.google.gson.Gson;
import com.lingyang.cloud.model.dto.feilian.AddDepartmentDTO;
import com.lingyang.cloud.model.dto.feilian.DepartmentInfoDTO;
import com.lingyang.cloud.model.dto.feilian.VpnFixedInfoDTO;
import com.lingyang.cloud.model.dto.feilian.VpnInfoDTO;
import com.lingyang.cloud.model.vo.feilian.*;
import com.lingyang.common.core.exception.http.HttpServiceException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author: 吴思镇
 * 飞连工具类
 * @Date: 2025/6/9 15:05
 */
@Slf4j
@Component
public class FeiLianUtils {


    /**
     * 测试
     */
//    public static final String URL = "https://lab.agi-c.eyunai.net:8443";

    /**
     * 正式
     */
//    public static final String URL = "https://agi-c.eyunai.net:8443";

    @Value("${feiLian.url}")
    private String URL;

    @Value("${feiLian.appId}")
    private String ACCESS_KEY_ID;

    @Value("${feiLian.secret}")
    private String ACCESS_KEY_SECRET;

    public static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().readTimeout(300, TimeUnit.SECONDS).build();

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 获取飞连token
     * @return
     */
    public String getAccessToken() {
        try {
            String accessToken =  redisTemplate.opsForValue().get("fei_lian_token");
            if (accessToken != null){
                return accessToken;
            }
//            String accessToken;
            MediaType mediaType = MediaType.parse("application/json;charset=utf-8");
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("access_key_id", ACCESS_KEY_ID);
            paramMap.put("access_key_secret", ACCESS_KEY_SECRET);
            Gson gson = new Gson();
            String json = gson.toJson(paramMap);
            RequestBody body = RequestBody.create(mediaType, json);
            Request request = new Request.Builder()
                    .url(URL + "/api/open/v1/token")
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json;charset=utf-8")
                    .build();
            Response response = HTTP_CLIENT.newCall(request).execute();
            assert response.body() != null;
            String responseBody = response.body().string();
            JSONObject jsonObject = JSON.parseObject(responseBody);
            if (jsonObject.getInteger("code") != 0) {
                log.error("飞连获取token失败:{}", jsonObject.get("message"));
                throw new HttpServiceException("飞连获取token失败");
            }
            JSONObject data = JSON.parseObject(jsonObject.get("data").toString());
            accessToken = data.get("access_token").toString();
            redisTemplate.opsForValue().set("fei_lian_token", accessToken, data.getInteger("expires_in"), TimeUnit.SECONDS);
            return accessToken;
        }catch (IOException e){
            log.error("飞连获取token失败:{}",e.getMessage(),e);
            throw new HttpServiceException("飞连获取token失败");
        }
    }

    public static void main(String[] args) {
        FeiLianUtils feiLianUtils = new FeiLianUtils();
//        System.out.println(feiLianUtils.getDepartmentList("od_abw53X7zRger"));
        String email = "angelica25021302@agi.com";
        System.out.println(feiLianUtils.getUserInfo(email));
        String userId = feiLianUtils.getUserInfo(email).get("id").toString();
//        System.out.println(feiLianUtils.getVpnList());
//        System.out.println(feiLianUtils.getVpnIps(new VpnIpVO()));
//        System.out.println(feiLianUtils.getAccessToken());
        ResetPasswordVO vo = new ResetPasswordVO();
        vo.setId(userId);
        vo.setCustomPassword("123456789");
        System.out.println(feiLianUtils.resetPassword( vo));

    }

    /**
     * 根据名称获取部门 ID
     * @param name
     * @return
     */
    public String getDepartmentIdByName(String name) {
        String accessToken = "BrReWAFlToDDUjxxnLztoeqHRHeojxwQDLfAFftv";
        String url = URL + "/api/open/v1/department/get_id";
        MediaType mediaType = MediaType.parse("application/json;charset=utf-8");

        JSONObject bodyJson = new JSONObject();
        bodyJson.put("name", name);
        RequestBody body = RequestBody.create(mediaType, bodyJson.toJSONString());
        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .addHeader("Authorization", accessToken)
                .build();

        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            assert response.body() != null;
            String responseBody = response.body().string();
            JSONObject jsonObject = JSON.parseObject(responseBody);
            if (jsonObject.getInteger("code") != 0) {
                log.error("名称获取部门 ID失败:{}", jsonObject.get("message"));
                throw new HttpServiceException("名称获取部门 ID失败");
            }
            return jsonObject.getJSONObject("data").getString("id");
        } catch (IOException e) {
            log.error("名称获取部门 ID失败", e);
            throw new HttpServiceException("名称获取部门 ID失败");
        }
    }

    /**
     * 获取飞连部门列表
     * @param departmentId
     * @return
     */
    public List<DepartmentInfoDTO> getDepartmentList(String departmentId) {
        try {
            String accessToken = getAccessToken();
//            String accessToken = "vmlwGyZlZxlzUnVDNkBldLmJTPpoRoIvGmpuAFPz";
            HttpUrl.Builder urlBuilder = HttpUrl.parse(URL + "/api/open/v1/department/list").newBuilder();
            if (departmentId != null && !departmentId.isEmpty()) {
                urlBuilder.addQueryParameter("id", departmentId);
            }
            String url = urlBuilder.build().toString();

            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("Content-Type", "application/json;charset=utf-8")
                    .addHeader("Authorization", accessToken)
                    .build();

            Response response = HTTP_CLIENT.newCall(request).execute();
            assert response.body() != null;
            String responseBody = response.body().string();
            JSONObject jsonObject = JSON.parseObject(responseBody);

            if (jsonObject.getInteger("code") != 0) {
                log.error("飞连获取部门列表失败:{}", jsonObject.get("message"));
                throw new HttpServiceException("飞连获取部门列表失败");
            }
            // 返回部门数据
            // 反序列化为 List<DepartmentDTO>
            List<DepartmentInfoDTO> departmentList = JSON.parseArray(
                    jsonObject.getString("data"), DepartmentInfoDTO.class
            );
            return departmentList;
        } catch (IOException e) {
            log.error("飞连获取部门列表失败", e);
            throw new HttpServiceException("飞连获取部门列表失败");
        }
    }

    /**
     * 创建飞连部门
     * @param vo
     * @return
     */
    public AddDepartmentDTO createDepartment(CreateDepartmentVO vo) {
        String accessToken = getAccessToken();
        String url = URL + "/api/open/v1/department/create";
        MediaType mediaType = MediaType.parse("application/json;charset=utf-8");

        JSONObject bodyJson = new JSONObject();
        bodyJson.put("name", vo.getName());
        bodyJson.put("parent_id", vo.getParentId());
        if ( vo.getType() != null) bodyJson.put("type", vo.getType());

        RequestBody body = RequestBody.create(mediaType, bodyJson.toJSONString());
        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .addHeader("Authorization", accessToken)
                .build();

        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            assert response.body() != null;
            String responseBody = response.body().string();
            JSONObject jsonObject = JSON.parseObject(responseBody);
            if (jsonObject.getInteger("code") != 0) {
                log.error("创建飞连部门失败:{}", jsonObject.get("message"));
                throw new HttpServiceException("创建飞连部门失败");
            }
            return JSON.parseObject(String.valueOf(jsonObject.getJSONObject("data")), AddDepartmentDTO.class);
        } catch (IOException e) {
            log.error("创建飞连部门失败", e);
            throw new HttpServiceException("创建飞连部门失败");
        }
    }

    /**
     * 更新部门
     */
    public Boolean updateDepartment(UpdateDepartmentVO vo) {
        String accessToken = getAccessToken();
        String url = URL + "/api/open/v1/department/update";
        MediaType mediaType = MediaType.parse("application/json;charset=utf-8");

        JSONObject bodyJson = new JSONObject();
        bodyJson.put("id", vo.getId());
        bodyJson.put("name", vo.getName());
        bodyJson.put("parent_id", vo.getParentId());

        RequestBody body = RequestBody.create(mediaType, bodyJson.toJSONString());
        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .addHeader("Authorization", accessToken)
                .build();

        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            assert response.body() != null;
            String responseBody = response.body().string();
            JSONObject jsonObject = JSON.parseObject(responseBody);
            if (jsonObject.getInteger("code") != 0) {
                log.error("飞连更新部门失败:{}", jsonObject.get("message"));
                throw new HttpServiceException("更新部门失败");
            }
            String result = jsonObject.getJSONObject("data").getString("result");
            return "success".equals(result);
        } catch (IOException e) {
            log.error("飞连更新部门失败", e);
            throw new HttpServiceException("更新部门失败");
        }
    }

    /**
     * 创建飞连用户
     * @param vo
     * @return
     */
    public Boolean createUser(CreateUserVO vo) {
        String accessToken = getAccessToken();
        String url = URL + "/api/open/v1/user/create";
        MediaType mediaType = MediaType.parse("application/json;charset=utf-8");

        JSONObject bodyJson = new JSONObject();
        bodyJson.put("full_name", vo.getFullName());
        bodyJson.put("department_id", vo.getDepartmentId());
//        if ( vo.getMobile() != null) bodyJson.put("mobile", vo.getMobile());
        if (vo.getEmail() != null) bodyJson.put("email", vo.getEmail());
        if (vo.getPassword() != null) bodyJson.put("password", vo.getPassword());
        if (vo.getInviteType() != null) bodyJson.put("invite_type", vo.getInviteType());

        RequestBody body = RequestBody.create(mediaType, bodyJson.toJSONString());
        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .addHeader("Authorization", accessToken)
                .build();

        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            assert response.body() != null;
            String responseBody = response.body().string();
            JSONObject jsonObject = JSON.parseObject(responseBody);
            if (jsonObject.getInteger("code") != 0) {
                log.error("飞连添加用户失败:{}", jsonObject.get("message"));
                throw new HttpServiceException("飞连添加用户失败");
            }
            String result = jsonObject.getJSONObject("data").getString("result");
            return "success".equals(result);
        } catch (IOException e) {
            log.error("飞连添加用户失败", e);
            throw new HttpServiceException("飞连添加用户失败");
        }
    }

    /**
     * 更新用户状态
     */
    public Boolean updateUserStatus(UpdateUserStatusVO vo) {
        String accessToken = getAccessToken();
        String url = URL + "/api/open/v1/user/status/update";
        MediaType mediaType = MediaType.parse("application/json;charset=utf-8");

        JSONObject bodyJson = new JSONObject();
        bodyJson.put("id", vo.getId());
        bodyJson.put("status", vo.getStatus());


        RequestBody body = RequestBody.create(mediaType, bodyJson.toJSONString());
        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .addHeader("Authorization", accessToken)
                .build();

        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            assert response.body() != null;
            String responseBody = response.body().string();
            JSONObject jsonObject = JSON.parseObject(responseBody);
            if (jsonObject.getInteger("code") != 0) {
                log.error("飞连更新用户状态失败:{}", jsonObject.get("message"));
                throw new HttpServiceException("更新用户状态失败");
            }
            String result = jsonObject.getJSONObject("data").getString("result");
            return "success".equals(result);
        } catch (IOException e) {
            log.error("飞连更新用户状态败", e);
            throw new HttpServiceException("更新用户状态失败");
        }
    }

    /**
     * 删除飞连用户
     * @param userId
     * @return
     */
    public Boolean deleteUser(String userId) {
        String accessToken = getAccessToken();
        String url = URL + "/api/open/v1/user/delete";
        MediaType mediaType = MediaType.parse("application/json;charset=utf-8");

        JSONObject bodyJson = new JSONObject();
        bodyJson.put("id", userId);

        RequestBody body = RequestBody.create(mediaType, bodyJson.toJSONString());
        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .addHeader("Authorization", accessToken)
                .build();

        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            assert response.body() != null;
            String responseBody = response.body().string();
            JSONObject jsonObject = JSON.parseObject(responseBody);
            if (jsonObject.getInteger("code") != 0) {
                log.error("飞连删除用户失败:{}", jsonObject.get("message"));
                throw new HttpServiceException("飞连删除用户失败");
            }
            String result = jsonObject.getJSONObject("data").getString("result");
            return "success".equals(result);
        } catch (IOException e) {
            log.error("飞连删除用户失败", e);
            throw new HttpServiceException("飞连删除用户失败");
        }
    }

    /**
     * 修改飞连用户
     * @param vo
     * @return
     */
    public Boolean updateUser(UpdateUserVO vo) {
        String accessToken = getAccessToken();
        String url = URL + "/api/open/v1/user/update";
        MediaType mediaType = MediaType.parse("application/json;charset=utf-8");

        JSONObject bodyJson = new JSONObject();
        bodyJson.put("id", vo.getId());
        if (vo.getExpireDate() != null) bodyJson.put("expire_date", vo.getExpireDate());
        // 其他可选参数可按需添加

        RequestBody body = RequestBody.create(mediaType, bodyJson.toJSONString());
        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .addHeader("Authorization", accessToken)
                .build();

        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            assert response.body() != null;
            String responseBody = response.body().string();
            JSONObject jsonObject = JSON.parseObject(responseBody);
            if (jsonObject.getInteger("code") != 0) {
                log.error("飞连更新用户失败:{}", jsonObject.get("message"));
                throw new HttpServiceException("飞连更新用户失败");
            }
            String result = jsonObject.getJSONObject("data").getString("result");
            return "success".equals(result);
        } catch (IOException e) {
            log.error("飞连更新用户失败", e);
            throw new HttpServiceException("飞连更新用户失败");
        }
    }

    /**
     * 飞连邮箱换用户信息
     * @param email
     * @return
     */
    public JSONObject getUserInfo(String email) {
        String accessToken = getAccessToken();
//        String accessToken = "HMLoMUNoTXsGmaFGsVAsfUdGAtHiYosZohMXzNzK";
        String url = URL + "/api/open/v1/user/get_id";
        MediaType mediaType = MediaType.parse("application/json;charset=utf-8");

        JSONObject bodyJson = new JSONObject();
        bodyJson.put("email", email);

        RequestBody body = RequestBody.create(mediaType, bodyJson.toJSONString());
        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .addHeader("Authorization", accessToken)
                .build();

        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            assert response.body() != null;
            String responseBody = response.body().string();
            JSONObject jsonObject = JSON.parseObject(responseBody);
            if (jsonObject.getInteger("code") != 0) {
                log.error("飞连获取用户信息失败:{}", jsonObject.get("message"));
                throw new HttpServiceException("飞连获取用户信息失败");
            }
            return jsonObject.getJSONObject("data");
        } catch (IOException e) {
            log.error("飞连获取用户信息失败", e);
            throw new HttpServiceException("飞连获取用户信息失败");
        }
    }

    /**
     * 飞连邮箱换用户信息
     * @param email
     * @return
     */
    public JSONObject getUserInfo2(String email) {
        String accessToken = getAccessToken();
//        String accessToken = "PYGWSnBjtnjNRtwGBrQEkVJthMBIYnQjEPmOEeQA";
        String url = URL + "/api/open/v1/user/get_id";
        MediaType mediaType = MediaType.parse("application/json;charset=utf-8");

        JSONObject bodyJson = new JSONObject();
        bodyJson.put("email", email);

        RequestBody body = RequestBody.create(mediaType, bodyJson.toJSONString());
        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .addHeader("Authorization", accessToken)
                .build();

        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            assert response.body() != null;
            String responseBody = response.body().string();
            return JSON.parseObject(responseBody);
        } catch (IOException e) {
            log.error("飞连获取用户信息失败", e);
            throw new HttpServiceException("飞连获取用户信息失败");
        }
    }

    /**
     * 重置飞连用户初始密码
     * @param vo
     * @return
     */
    public String resetPassword(ResetPasswordVO vo) {
        String accessToken = getAccessToken();
//        String accessToken = "HMLoMUNoTXsGmaFGsVAsfUdGAtHiYosZohMXzNzK";
        String url = URL + "/api/open/v1/user/start_passwd/reset";
        MediaType mediaType = MediaType.parse("application/json;charset=utf-8");

        JSONObject bodyJson = new JSONObject();
        bodyJson.put("id", vo.getId());
        bodyJson.put("custom_passwd", vo.getCustomPassword());

        RequestBody body = RequestBody.create(mediaType, bodyJson.toJSONString());
        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .addHeader("Authorization", accessToken)
                .build();

        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            assert response.body() != null;
            String responseBody = response.body().string();
            JSONObject jsonObject = JSON.parseObject(responseBody);
            if (jsonObject.getInteger("code") != 0) {
                log.error("飞连重置正常用户初始密码失败:{}", jsonObject.get("message"));
                throw new HttpServiceException("飞连重置正常用户初始密码失败");
            }
            return jsonObject.getJSONObject("data").getString("start_passwd");
        } catch (IOException e) {
            log.error("飞连重置正常用户初始密码失败", e);
            throw new HttpServiceException("飞连重置正常用户初始密码失败");
        }
    }

    /**
     * 获取飞连VP列表
     * @return
     */
    public List<VpnInfoDTO> getVpnList() {
        try {
            String accessToken = getAccessToken();
//            String accessToken = "vmlwGyZlZxlzUnVDNkBldLmJTPpoRoIvGmpuAFPz";
            HttpUrl.Builder urlBuilder = HttpUrl.parse(URL + "/api/open/v1/vpn/server/list").newBuilder();
            String url = urlBuilder.build().toString();

            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("Content-Type", "application/json;charset=utf-8")
                    .addHeader("Authorization", accessToken)
                    .build();

            Response response = HTTP_CLIENT.newCall(request).execute();
            assert response.body() != null;
            String responseBody = response.body().string();
            JSONObject jsonObject = JSON.parseObject(responseBody);

            if (jsonObject.getInteger("code") != 0) {
                log.error("获取飞连VP列表失败:{}", jsonObject.get("message"));
                throw new HttpServiceException("获取飞连VP列表失败");
            }
            // 返回部门数据
            // 反序列化为 List<VpnInfoDTO>
            String data = jsonObject.getString("data");
            JSONObject jsonObject1 = JSON.parseObject(data);
            return JSON.parseArray(
                    jsonObject1.getString("servers"), VpnInfoDTO.class
            );
        } catch (IOException e) {
            log.error("获取飞连VP列表失败", e);
            throw new HttpServiceException("获取飞连VP列表失败");
        }
    }

    /**
     * 添加飞连VPN权限
     * @param vo
     * @return
     */
    public Boolean addVpn(AddVpnVO vo) {
        String accessToken = getAccessToken();
        String url = URL + "/api/open/v1/vpn/permission/add";
        MediaType mediaType = MediaType.parse("application/json;charset=utf-8");

        JSONObject bodyJson = new JSONObject();
        bodyJson.put("identity_ids", vo.getIdentityIds());
        bodyJson.put("identity_type", vo.getIdentityType());
        bodyJson.put("days", vo.getDays());

        RequestBody body = RequestBody.create(mediaType, bodyJson.toJSONString());
        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .addHeader("Authorization", accessToken)
                .build();

        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            assert response.body() != null;
            String responseBody = response.body().string();
            JSONObject jsonObject = JSON.parseObject(responseBody);
            if (jsonObject.getInteger("code") != 0) {
                log.error("飞连添加VPN使用授权失败:{}", jsonObject.get("message"));
                throw new HttpServiceException("飞连添加VPN使用授权失败");
            }
            String result = jsonObject.getJSONObject("data").getString("result");
            return "success".equals(result);
        } catch (IOException e) {
            log.error("飞连添加VPN使用授权失败", e);
            throw new HttpServiceException("飞连添加VPN使用授权失败");
        }
    }

    /**
     * 修改飞连VPN权限
     * @param vo
     * @return
     */
    public Boolean updatedVpn(UpdateVpnVO vo) {
        String accessToken = getAccessToken();
        String url = URL + "/api/open/v1/vpn/permission/expire/update";
        MediaType mediaType = MediaType.parse("application/json;charset=utf-8");

        JSONObject bodyJson = new JSONObject();
        bodyJson.put("ids", vo.getIds());
        bodyJson.put("days", vo.getDays());

        RequestBody body = RequestBody.create(mediaType, bodyJson.toJSONString());
        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .addHeader("Authorization", accessToken)
                .build();

        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            assert response.body() != null;
            String responseBody = response.body().string();
            JSONObject jsonObject = JSON.parseObject(responseBody);
            if (jsonObject.getInteger("code") != 0) {
                log.error("飞连更新VPN使用授权过期时间失败:{}", jsonObject.get("message"));
                throw new HttpServiceException("飞连更新VPN使用授权过期时间失败");
            }
            String result = jsonObject.getJSONObject("data").getString("result");
            return "success".equals(result);
        } catch (IOException e) {
            log.error("飞连更新VPN使用授权过期时间失败", e);
            throw new HttpServiceException("飞连更新VPN使用授权过期时间失败");
        }
    }

    /**
     * 删除飞连VPN权限
     * @param ids
     * @return
     */
    public Boolean deletedVpn(Integer[] ids) {
        String accessToken = getAccessToken();
        String url = URL + "/api/open/v2/vpn/permission/delete";
        MediaType mediaType = MediaType.parse("application/json;charset=utf-8");

        JSONObject bodyJson = new JSONObject();
        bodyJson.put("ids", ids);
        RequestBody body = RequestBody.create(mediaType, bodyJson.toJSONString());
        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .addHeader("Authorization", accessToken)
                .build();

        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            assert response.body() != null;
            String responseBody = response.body().string();
            JSONObject jsonObject = JSON.parseObject(responseBody);
            if (jsonObject.getInteger("code") != 0) {
                log.error("飞连删除VPN使用授权失败:{}", jsonObject.get("message"));
                throw new HttpServiceException("飞连删除VPN使用授权失败");
            }
            String result = jsonObject.getJSONObject("data").getString("result");
            return "success".equals(result);
        } catch (IOException e) {
            log.error("飞连删除VPN使用授权失败", e);
            throw new HttpServiceException("飞连删除VPN使用授权失败");
        }
    }

    /**
     * 飞连获取VPN节点预留IP列表
     */
    public List<VpnFixedInfoDTO> getVpnIps(VpnIpVO vo) {
        try {
            String accessToken = getAccessToken();
            HttpUrl.Builder urlBuilder = HttpUrl.parse(URL + "/api/open/v1/vpn/fixed/list").newBuilder();
            urlBuilder.addQueryParameter("vpn_id", vo.getVpnId().toString());
            urlBuilder.addQueryParameter("user_id", vo.getUserId());
            urlBuilder.addQueryParameter("limit", String.valueOf(200));
            String url = urlBuilder.build().toString();

            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("Content-Type", "application/json;charset=utf-8")
                    .addHeader("Authorization", accessToken)
                    .build();

            Response response = HTTP_CLIENT.newCall(request).execute();
            assert response.body() != null;
            String responseBody = response.body().string();
            JSONObject jsonObject = JSON.parseObject(responseBody);
            log.info("飞连获取VPN节点预留IP列表:{}", jsonObject);
            if (jsonObject.getInteger("code") != 0) {
                log.error("飞连获取VPN节点预留IP列表失败:{}", jsonObject.get("message"));
                throw new HttpServiceException("飞连获取VPN节点预留IP列表失败");
            }
            String data = jsonObject.getString("data");
            JSONObject jsonObject1 = JSON.parseObject(data);
            JSONArray items = jsonObject1.getJSONArray("items");
            return JSON.parseArray(items.toJSONString(), VpnFixedInfoDTO.class);
        } catch (IOException e) {
            log.error("飞连获取VPN节点预留IP列表失败", e);
            throw new HttpServiceException("飞连获取VPN节点预留IP列表失败");
        }
    }

    /**
     * 飞连添加VPN节点预留IP
     */
    public Boolean addVpnIp(AddVpnIpVO vo) {
        String accessToken = getAccessToken();
        String url = URL + "/api/open/v1/vpn/fixed/add";
        MediaType mediaType = MediaType.parse("application/json;charset=utf-8");

        JSONObject bodyJson = new JSONObject();
        bodyJson.put("vpn_id", vo.getVpnId());
        bodyJson.put("fixed_ips", vo.getFixedIps());
        String[] userIds = vo.getUserIds();
        if (userIds != null)bodyJson.put("user_ids", userIds);
        if (vo.getDepartmentIds() != null)bodyJson.put("department_ids", vo.getDepartmentIds());
        if (vo.getRoleIds() != null)bodyJson.put("role_ids", vo.getRoleIds());
        if (vo.getDeviceIds() != null)bodyJson.put("device_ids", vo.getDeviceIds());
        if (vo.getDeviceGroupIds() != null)bodyJson.put("device_group_ids", vo.getDeviceGroupIds());

        RequestBody body = RequestBody.create(mediaType, bodyJson.toJSONString());
        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .addHeader("Authorization", accessToken)
                .build();

        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            assert response.body() != null;
            String responseBody = response.body().string();
            JSONObject jsonObject = JSON.parseObject(responseBody);
            if (jsonObject.getInteger("code") != 0) {
                log.error("飞连添加VPN节点预留IP失败:{}", jsonObject.get("message"));
                throw new HttpServiceException("添加VPN节点预留IP失败");
            }
            String result = jsonObject.getJSONObject("data").getString("result");
            return "success".equals(result);
        } catch (IOException e) {
            log.error("飞连添加VPN节点预留IP失败", e);
            throw new HttpServiceException("添加VPN节点预留IP失败");
        }
    }

    /**
     * 飞连删除VPN节点预留IP
     */
    public Boolean deleteVpnIp(Integer[] ids) {
        String accessToken = getAccessToken();
        String url = URL + "/api/open/v1/vpn/fixed/delete";
        MediaType mediaType = MediaType.parse("application/json;charset=utf-8");

        JSONObject bodyJson = new JSONObject();
        bodyJson.put("ids", ids);

        RequestBody body = RequestBody.create(mediaType, bodyJson.toJSONString());
        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .addHeader("Authorization", accessToken)
                .build();

        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            assert response.body() != null;
            String responseBody = response.body().string();
            JSONObject jsonObject = JSON.parseObject(responseBody);
            if (jsonObject.getInteger("code") != 0) {
                log.error("飞连删除VPN节点预留IP失败:{}", jsonObject.get("message"));
                throw new HttpServiceException("删除VPN节点预留IP失败");
            }
            String result = jsonObject.getJSONObject("data").getString("result");
            return "success".equals(result);
        } catch (IOException e) {
            log.error("飞连删除VPN节点预留IP失败", e);
            throw new HttpServiceException("删除VPN节点预留IP失败");
        }
    }
}
