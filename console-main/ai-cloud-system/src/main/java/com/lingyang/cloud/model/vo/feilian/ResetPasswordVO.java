package com.lingyang.cloud.model.vo.feilian;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/6/11 14:54
 */
@Data
public class ResetPasswordVO {

    @Schema(description = "用户ID，格式为：ou_xxx")
    private String id;

    @Schema(description = "自定义初始密码，默认随机生成10 位长度密码，最小长度与密码策略一致，最大长度为20 字符。")
    private String customPassword;
}
