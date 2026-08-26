package com.lingyang.cloud.model.query.user;

import com.lingyang.cloud.enums.system.StatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/3/29 10:27
 */
@Data
public class SysUserQueryDTO {

    /**
     * 帐号状态（0正常 1停用,2离职）
     */
    @Schema(description = "状态")
    private StatusEnum status;
}
