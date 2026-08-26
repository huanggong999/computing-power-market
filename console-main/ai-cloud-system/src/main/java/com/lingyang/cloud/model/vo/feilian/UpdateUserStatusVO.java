package com.lingyang.cloud.model.vo.feilian;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/7/1 11:54
 */
@Data
public class UpdateUserStatusVO {

    @Schema(description = "用户的id，格式为：ou_xxx")
    private String id;

    @Schema(description = "更新的状态，enable为启用，disable为禁用，offline为离职")
    private String status;
}
