package com.lingyang.cloud.model.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/3/29 14:04
 */
@Data
public class SysUserRoleVO {

    @Schema(description = "角色id")
    private Long roleId;

    @Schema(description = "角色名称")
    private String roleName;

}
