package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lingyang.cloud.enums.system.StatusEnum;
import com.lingyang.common.core.datasource.enums.DataScopeType;
import com.lingyang.common.datasource.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024-03-27
 */

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class SysRoleEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 3166625498361734516L;

    /**
     * 角色名称
     */
    @TableField("role_name")
    private String roleName;

    /**
     * 是否为超管，1是，0否
     */
    @TableField("admin")
    private Integer admin;

    /**
     * 显示顺序
     */
    @TableField("role_sort")
    private Long roleSort;

    /**
     * 数据范围
     */
    @TableField("data_scope")
    private DataScopeType dataScope;

    /**
     * 菜单树选择项是否关联显示
     */
    @TableField("menu_check_strictly")
    private Integer menuCheckStrictly;

    /**
     * 部门树选择项是否关联显示
     */
    @TableField("dept_check_strictly")
    private Integer deptCheckStrictly;

    /**
     * 角色状态（0正常 1停用）
     */
    @TableField("status")
    private StatusEnum status;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;
}