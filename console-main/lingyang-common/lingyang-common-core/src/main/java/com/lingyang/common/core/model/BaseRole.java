package com.lingyang.common.core.model;

import com.lingyang.common.core.datasource.enums.DataScopeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Collection;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2023/8/11 16:57
 */
@Data
public abstract class BaseRole {

    /**
     * 角色id
     */
    @Schema(description = "角色id")
    private Long id;

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "数据权限类型")
    private DataScopeType dataScope;

    /**
     * 菜单id列表
     */
    @Schema(description = "当前角色关联的菜单Id列表", hidden = true)
    private Collection<Long> menuIdList;

    @Schema(description = "权限列表", hidden = true)
    private Collection<String> permissions;

    /**
     * 是否为超管
     * @return true
     */
    public abstract boolean isAdmin();
}
