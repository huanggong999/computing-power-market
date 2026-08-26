package com.lingyang.cloud.api.model.callback;

import com.lingyang.common.core.utils.DateUtils;
import com.lingyang.common.core.utils.Optional;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2023/9/7 9:45
 */
@Data
public class WeChatPayCallbackResultBody {
    /**
     * 通知的唯一ID
     */
    @Schema(description = "通知唯一id")
    private String id;

    @Schema(description = "通知创建的时间，格式为rfc3339格式，如2018-06-08T10:34:56+08:00 ")
    private String create_time;

    @Schema(description = "通知的类型，支付成功通知的类型为TRANSACTION.SUCCESS")
    private String resource_type;

    @Schema(description = "通知的资源数据类型，支付成功通知为encrypt-resource")
    private String event_type;

    @Schema(description = "通知简要说明")
    private String summary;

    @Schema(description = "通知资源数据")
    private WxPayCallbackResource resource;

    @Data
    public static class WxPayCallbackResource {
        /**
         * 加密算法
         */
        private String algorithm;
        private String associated_data;
        private String ciphertext;
        private String nonce;
        private String original_type;
    }

    public Date getCreate_time() {
        return Optional.of(create_time)
                .flatMap( time -> DateUtils.toDate(time
                                .replace("T", " ")
                                .replace("+08:00", ""),
                        "yyyy-MM-dd HH:mm:ss"))
                .getValue();
    }
}
