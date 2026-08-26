package com.lingyang.cloud.model.vo.menu;

import com.lingyang.cloud.enums.system.StatusEnum;
import com.lingyang.common.web.model.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/3/27 17:33
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysMenuListVO extends BaseVO {
    @Serial
    private static final long serialVersionUID = 5249540674782251606L;
    /**
     * 菜单名称
     */
    @Schema(description = "菜单名称")
    @NotEmpty(message = "菜单名称为空")
    private String menuName;

    /**
     * 父菜单ID
     */
    @Schema(description = "父菜单ID")
    private Long parentId;

    /**
     * 显示顺序
     */
    @Schema(description = "显示顺序")
    private Long orderNum;

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

    /**
     * 是否缓存（0缓存 1不缓存）
     */
    @Schema(description = " 是否缓存（0缓存 1不缓存）")
    private Integer isCache;

    /**
     * 菜单类型（M目录 C菜单 F按钮）
     */
    @Schema(description = "菜单类型（M目录 C菜单 F按钮）")
    @NotEmpty(message = "菜单类型为空")
    private String menuType;

    /**
     * 菜单状态
     */
    @Schema(description = "菜单状态")
    private StatusEnum visible;

    /**
     * 菜单状态
     */
    @Schema(description = "菜单状态")
    private StatusEnum status;

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

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;
}
