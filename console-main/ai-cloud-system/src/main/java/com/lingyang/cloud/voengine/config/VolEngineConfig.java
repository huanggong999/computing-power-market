package com.lingyang.cloud.voengine.config;

import com.lingyang.common.core.utils.IdUtils;
import com.lingyang.common.core.utils.StringUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/10/31 11:20
 */
@Data
@ConfigurationProperties(prefix = "vol-engine")
@Configuration
public class VolEngineConfig {

    /**
     * 项目名称
     */
    private String projectName;
    /**
     * api key
     */
    private String accessKey;
    /**
     * api secret
     */
    private String accessSecret;

    /**
     * 默认区域，当参数中不传说区域，使用当前区域
     */
    private String regions;

    /**
     * 对象存储
     */
    private Tos tos;


    @Data
    public static class Tos {

        /**
         * 默认区域，当参数中不传说区域，使用当前区域
         */
        private String region;

        /**
         * endpoint
         */
        private String endpoint;

        /**
         * 桶名称
         */
        private String bucketName;

        /**
         * 获取访问域名
         * @return 访问域名
         */
        public String getHostUrl(String fileName) {
            if (StringUtils.isEmpty(region) || StringUtils.isEmpty(bucketName)) {
                return null;
            }
            if (StringUtils.isEmpty(fileName)) {
                fileName = IdUtils.simpleUUID();
            }
            if (!fileName.startsWith("/")) {
                fileName = "/" + fileName;
            }
            //https://ai-cloud-system.tos-cn-beijing.volces.com/2024/11/29/ea57a1d3d79042b6ac107fc6079d5b33.png
            //https://ai-cloud-system.tos-cn-beijing.volces.com/2024/11/29/374cd0fc51054e9e9bd29cf59b1265c9.png
            return "https://" + bucketName + "." + endpoint + fileName;
        }
    }
}