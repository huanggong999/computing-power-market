package com.lingyang.cloud.common.login.model;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lingyang.cloud.enums.system.StatusEnum;
import com.lingyang.common.core.model.BaseRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/4/28 14:36
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysRoleInfo extends BaseRole {
    /**
     * 是否为超管
     */
    @Schema(description = "是否为超管")
    private Integer admin;

    /**
     * 显示顺序
     */
    @Schema(description = "显示顺序")
    private Long roleSort;

    /**
     * 菜单树选择项是否关联显示
     */
    @Schema(description = "菜单树选择项是否关联显示")
    private Integer menuCheckStrictly;

    /**
     * 部门树选择项是否关联显示
     */
    @Schema(description = "部门树选择项是否关联显示")
    private Integer deptCheckStrictly;

    /**
     * 角色状态
     */
    @Schema(description = "角色状态")
    private StatusEnum status;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;

    @Override
    @JSONField(serialize = false)
    @JsonIgnore
    public boolean isAdmin() {
        return admin.equals(1);
    }
}
