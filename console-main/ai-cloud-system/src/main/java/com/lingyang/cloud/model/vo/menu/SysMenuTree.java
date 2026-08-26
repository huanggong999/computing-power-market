package com.lingyang.cloud.model.vo.menu;

import com.lingyang.common.core.model.Tree;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2023/8/11 18:10
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysMenuTree extends Tree<SysMenuTree> {
    /**
     * 菜单名称
     */
    @Schema(description = "菜单名称")
    private String menuName;

    /**
     * 路由地址
     */
    @Schema(description = "路由地址")
    private String path;

    /**
     * 组件路径
     */
    @Schema(description = "组件路径")
    private String component;

    /**
     * 路由参数
     */
    @Schema(description = "路由参数")
    private String query;

    /**
     * 是否为外链（0是 1否）
     */
    @Schema(description = "是否为外链（0是 1否）")
    private Integer isFrame;

    @Schema(description = "显示状态（0显示 1隐藏）")
    private Integer visible;
    /**
     * 是否缓存（0缓存 1不缓存）
     */
    @Schema(description = "是否缓存（0缓存 1不缓存）")
    private Integer isCache;

    /**
     * 菜单类型（M目录 C菜单 F按钮）
     */
    @Schema(description = "菜单类型（M目录 C菜单 F按钮）")
    private String menuType;

    /**
     * 权限标识
     */
    @Schema(description = "权限标识")
    private String perms;

    /**
     * 菜单图标
     */
    @Schema(description = "菜单图标")
    private String icon;
}
