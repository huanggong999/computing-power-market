package com.lingyang.cloud.model.edit.role;

import com.lingyang.cloud.enums.system.StatusEnum;
import com.lingyang.common.core.datasource.enums.DataScopeType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/3/27 17:18
 */
@Data
public class SysRoleEditDTO {

    @Schema(description = "角色id")
    private Long id;
    /**
     * 角色名称
     */
    @Schema(description = "角色名称")
    @NotEmpty(message = "角色名称为空")
    private String roleName;

    /**
     * 显示顺序
     */
    @Schema(description = "显示顺序")
    @NotNull(message = "显示顺序为空")
    private Long roleSort;

    /**
     * 数据范围
     */
    @Schema(description = "数据范围")
    @NotNull(message = "数据范围为空")
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
    @NotNull(message = "菜单数组为空")
    private List<Long> menuIds;
}
