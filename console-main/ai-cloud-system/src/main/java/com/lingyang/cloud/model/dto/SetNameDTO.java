package com.lingyang.cloud.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 设置实例名称请求
 */
@Data
@Schema(description = "设置实例名称请求")
public class SetNameDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "名称不能为空")
    @Schema(description = "实例名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
}
