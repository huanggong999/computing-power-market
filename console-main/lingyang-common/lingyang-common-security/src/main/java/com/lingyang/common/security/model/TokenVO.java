package com.lingyang.common.security.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2023/8/7 19:18
 */
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class TokenVO implements Serializable {
    @Serial
    private static final long serialVersionUID = -9011077860764098376L;

    @Schema(defaultValue = "令牌")
    private String token;

    @Schema(defaultValue = "到期时间，单位秒")
    private Long expTime;
}