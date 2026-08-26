package com.lingyang.cloud.model.vo.product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/7/1 15:38
 */
@Data
public class SysResetPasswordVO {

    @Schema(description = "邮箱")
    private String email;
}
