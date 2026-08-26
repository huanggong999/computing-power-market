package com.lingyang.cloud.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.lingyang.cloud.config.vo.WxSessionResultVO;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;
import java.util.UUID;

/**
 * 微信小程序配置
 */
@Slf4j
@Getter
@Component
public class WeiXinConfig {

    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private RestTemplate restTemplate;
    @Value("${applet.weixin.tempImage}")
    private String urlImage;
    @Value("${applet.weixin.appId}")
    private String appId;
    @Value("${applet.weixin.appKey}")
    private String appKey;

    @Value("${applet.weixin.envVersion}")
    private String envVersion;

    public String getToken() throws IOException {
        String wxToken = redisTemplate.opsForValue().get("wxToken");
        if (StringUtils.isEmpty(wxToken)) {
            wxToken = postToken(appId, appKey);
            redisTemplate.opsForValue().set("wxToken", wxToken);
        }
        return wxToken;
    }

    private String refreshToken() {
        String wxToken;
        try {
            wxToken = postToken(appId, appKey);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        redisTemplate.opsForValue().set("wxToken", wxToken);
        return wxToken;
    }

    /**
     * 获取手机号文档
     * https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/user-info/phone-number/getPhoneNumber.html
     */
    public String getUserPhone(String code) throws IOException {
        return getUserPhone(code, false);
    }

    public String getUserPhone(String code, boolean f) throws IOException {
        String token = getToken();
        String url = "https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token=" + token;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", code);
        String re = restTemplate.postForObject(url, jsonObject.toJSONString(), String.class);
        log.info("微信小程序获取手机号返回: {}", re);
        JSONObject object = JSON.parseObject(re);
        if (object == null) {
            throw new RuntimeException("获取失败");
        }
        if (Integer.valueOf(40001).equals(object.get("errcode"))) {
            if (f) {
                throw new RuntimeException("获取失败" + object.get("errcode"));
            }
            refreshToken();
            return getUserPhone(code, true);
        }
        JSONObject phoneInfoJson = (JSONObject) object.get("phone_info");
        if (phoneInfoJson == null) {
            throw new RuntimeException("获取失败" + object.get("errcode"));
        }
        return (String) phoneInfoJson.get("phoneNumber");
    }

    /**
     * 接口调用凭证 access_token
     */
    public static String postToken(String appId, String appKey) throws IOException {

        String requestUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appId + "&secret=" + appKey;
        URL url = new URL(requestUrl);
        // 打开和URL之间的连接
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        // 设置通用的请求属性
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setUseCaches(false);
        connection.setDoOutput(true);
        connection.setDoInput(true);

        // 得到请求的输出流对象
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes("");
        out.flush();
        out.close();

        // 建立实际的连接
        connection.connect();
        // 定义 BufferedReader输入流来读取URL的响应
        BufferedReader in;
        if (requestUrl.contains("nlp")) {
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "GBK"));
        } else {
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        }
        StringBuilder result = new StringBuilder();
        String getLine;
        while ((getLine = in.readLine()) != null) {
            result.append(getLine);
        }
        in.close();
        JSONObject jsonObject = JSONObject.parseObject(result.toString());
        return jsonObject.getString("access_token");
    }

    public InputStream getQrCodeInputStream(String page, String scent) {
        String base64 = getBase64(page, scent);
        ByteArrayInputStream stream = null;
        String url = base64.substring(22, base64.length());
        byte[] bytes = Base64.getDecoder().decode(base64);
        stream = new ByteArrayInputStream(bytes);
        return stream;
    }

    public String getBase64(String page, String scent) {
        return getBase64(page, scent, true);
    }

    private String getBase64(String page, String scent, boolean retry) {
        File file = new File(urlImage);
        if (!file.exists()) {
            file.mkdirs();
        }
        String filePath = urlImage + UUID.randomUUID() + ".png";
        byte[] data;
        try {
            generateQrCode(filePath, page, scent, getToken(), envVersion);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        File file1 = new File(filePath);
        try (InputStream in = Files.newInputStream(file1.toPath())) {
            data = new byte[in.available()];
            final int read = in.read(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        final boolean delete = file1.delete();
        if (delete) {
            // 删除成功
        }

        if (retry) {
            String s = new String(data);
            if (s.contains("errcode")) {
                log.info("生成微信小程序二维码返回错误:{}", s);
                JSONObject jsonObject = JSON.parseObject(s);
                if (Integer.valueOf(40001).equals(jsonObject.get("errcode"))) {
                    refreshToken();
                    return getBase64(page, scent, false);
                }
                if (Integer.valueOf(42001).equals(jsonObject.get("errcode"))) {
                    refreshToken();
                    return getBase64(page, scent, false);
                }
                throw new RuntimeException("生成失败" + jsonObject.get("errcode"));
            }
        }
        return Base64.getEncoder().encodeToString(data);
    }


    /**
     * 生成微信小程序二维码
     *
     * @param filePath    本地生成二维码路径
     * @param page        当前小程序相对页面 必须是已经发布的小程序存在的页面（否则报错），例如 pages/index/index, 根路径前不要填加 /,不能携带参数（参数请放在scene字段里），如果不填写这个字段，默认跳主页面
     * @param scene       最大32个可见字符，只支持数字，大小写英文以及部分特殊字符：!#$&\'()*+,/:;=?@-._~，其它字符请自行编码为合法字符（因不支持%，中文无法使用 urlencode 处理，请使用其他编码方式）
     * @param accessToken 接口调用凭证
     */
    public static void generateQrCode(String filePath, String page, String scene, String accessToken, String envVersion) {

        try {

            //调用微信接口生成二维码
            URL url = new URL("https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=" + accessToken);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");// 提交模式
            // conn.setConnectTimeout(10000);//连接超时 单位毫秒
            // conn.setReadTimeout(2000);//读取超时 单位毫秒
            // 发送POST请求必须设置如下两行
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            PrintWriter printWriter = new PrintWriter(httpURLConnection.getOutputStream());
            // 发送请求参数
            JSONObject paramJson = new JSONObject();
            //这就是你二维码里携带的参数 String型  名称不可变
            paramJson.put("scene", scene);
            //注意该接口传入的是page而不是path
            paramJson.put("page", page);
            //这是设置扫描二维码后跳转的页面
            paramJson.put("width", 480);
            paramJson.put("env_version", envVersion);
            paramJson.put("is_hyaline", true);
            paramJson.put("auto_color", true);
            paramJson.put("check_path", false);
            printWriter.write(paramJson.toString());
            // flush输出流的缓冲
            printWriter.flush();

            //开始获取数据
            BufferedInputStream bis = new BufferedInputStream(httpURLConnection.getInputStream());
            OutputStream os = null;
            try {
                os = Files.newOutputStream(new File(filePath).toPath());
                int len;
                byte[] arr = new byte[1024];
                while ((len = bis.read(arr)) != -1) {
                    os.write(arr, 0, len);
                    os.flush();
                }
            } catch (Exception e) {

            } finally {
                if (os != null) {
                    os.close();
                }
            }
        } catch (Exception e) {
            log.info("异常 {}", e.getMessage());
            // e.printStackTrace();
        }

    }


    /**
     * 获取 session key 和 openid
     */
    public WxSessionResultVO getSessionKey(String code) throws IOException {
        String requestUrl = "https://api.weixin.qq.com/sns/jscode2session?grant_type=authorization_code&appid=" + appId + "&secret=" + appKey + "&js_code=" + code;
        URL url = new URL(requestUrl);
        // 打开和URL之间的连接
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        // 设置通用的请求属性
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setUseCaches(false);
        connection.setDoOutput(true);
        connection.setDoInput(true);

        // 得到请求的输出流对象
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes("");
        out.flush();
        out.close();

        // 建立实际的连接
        connection.connect();
        // 定义 BufferedReader输入流来读取URL的响应
        BufferedReader in;
        if (requestUrl.contains("nlp")) {
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "GBK"));
        } else {
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        }
        StringBuilder result = new StringBuilder();
        String getLine;
        while ((getLine = in.readLine()) != null) {
            result.append(getLine);
        }
        in.close();
        WxSessionResultVO resultVO = JSONObject.parseObject(result.toString(), WxSessionResultVO.class);
        return resultVO;
    }


    public String getMediaId(File file) throws IOException {
        String token = getToken();
        String url = "https://api.weixin.qq.com/cv/img/superresolution?access_token=" + token;
        StringBuilder resp = new StringBuilder();
        String result = null;
        try {
            URL urlObj = new URL(url);
            HttpsURLConnection conn = (HttpsURLConnection) urlObj.openConnection();
            conn.setRequestMethod("POST");//以POST方式提交表单
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", "UTF-8");
            //数据边界
            String boundary = "----------" + System.currentTimeMillis();
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            //获取输出流
            OutputStream out = conn.getOutputStream();
            //创建文件输入流
//            InputStream fis = file.getInputStream();
            System.out.println(file.getName());
            InputStream fis = new FileInputStream(file);
            StringBuilder sb = new StringBuilder();
            sb.append("--");
            sb.append(boundary);
            sb.append("\r\n");
            sb.append("Content-Disposition: form-data;name=\"media\"; filename=\"" + file.getName() + "\"\r\n");
            sb.append("Content-Type: applicatin/octet-stream\r\n\r\n");
            out.write(sb.toString().getBytes());

            byte[] bytes = new byte[1024];
            int len;
            while ((len = fis.read(bytes)) != -1) {
                out.write(bytes, 0, len);
            }
            String foot = "\r\n--" + boundary + "--\r\n";
            out.write(foot.getBytes());
            out.flush();
            out.close();
            //读取数据
            if (HttpsURLConnection.HTTP_OK == conn.getResponseCode()) {

                StringBuffer strbuffer = null;
                BufferedReader reader = null;
                try {
                    strbuffer = new StringBuffer();
                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String lineString = null;
                    while ((lineString = reader.readLine()) != null) {
                        strbuffer.append(lineString);
                    }
                    result = strbuffer.toString();
                    System.out.println("返回是个速：" + result);
                } catch (IOException e) {
                    System.out.println("发送POST请求出现异常！" + e);
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        reader.close();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JSONObject.parseObject(result).get("media_id").toString();
    }

    public void downloadMediaId(String mediaId, String filePath) throws IOException {
        String token = getToken();
        try {
            URL url = new URL("https://api.weixin.qq.com/cgi-bin/media/get?access_token=" + token + "&media_id=" + mediaId);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            BufferedInputStream bis = new BufferedInputStream(httpURLConnection.getInputStream());
            OutputStream os = null;
            try {
                os = Files.newOutputStream(new File(filePath).toPath());
                int len;
                byte[] arr = new byte[1024];
                while ((len = bis.read(arr)) != -1) {
                    os.write(arr, 0, len);
                    os.flush();
                }
            } catch (Exception e) {

            } finally {
                if (os != null) {
                    os.close();
                }
            }
        } catch (Exception e) {
            log.info("高清异常 {}", e.getMessage());
            // e.printStackTrace();
        }

    }




    /**
     * 获取 网站的key
     */
    public WxSessionResultVO getPcSessionKey(String code) throws IOException {
        String requestUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=wx577c07baa5a6a681&secret=b2ad2586b2a8d5dbaf9f0c3d8e141819&code="+code+"&grant_type=authorization_code";
        URL url = new URL(requestUrl);
        // 打开和URL之间的连接
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        // 设置通用的请求属性
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setUseCaches(false);
        connection.setDoOutput(true);
        connection.setDoInput(true);

        // 得到请求的输出流对象
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes("");
        out.flush();
        out.close();

        // 建立实际的连接
        connection.connect();
        // 定义 BufferedReader输入流来读取URL的响应
        BufferedReader in;
        if (requestUrl.contains("nlp")) {
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "GBK"));
        } else {
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        }
        StringBuilder result = new StringBuilder();
        String getLine;
        while ((getLine = in.readLine()) != null) {
            result.append(getLine);
        }
        in.close();
        WxSessionResultVO resultVO = JSONObject.parseObject(result.toString(), WxSessionResultVO.class);
        return resultVO;
    }


}
