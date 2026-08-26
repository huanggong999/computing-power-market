package com.lingyang.cloud.model.vo.feilian;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/6/11 11:42
 */
@Data
public class UpdateUserVO extends CreateUserVO{

    @Schema(description = "用户id")
    private String id;
}
