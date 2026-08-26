package com.lingyang.cloud.model.vo.role;

import com.lingyang.cloud.enums.system.StatusEnum;
import com.lingyang.common.core.datasource.enums.DataScopeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/3/28 17:09
 */
@Data
public class SysRoleDetailVO {

    @Schema(description = "角色id")
    private Long id;
    /**
     * 角色名称
     */
    @Schema(description = "角色名称")
    private String roleName;

    /**
     * 显示顺序
     */
    @Schema(description = "显示顺序")
    private Long roleSort;

    /**
     * 数据范围
     */
    @Schema(description = "数据范围")
    private DataScopeType dataScope;
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

    @Schema(description = "角色关联的菜单id数组")
    private List<Long> menuIds;

}