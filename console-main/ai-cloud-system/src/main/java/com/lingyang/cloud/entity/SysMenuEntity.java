package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lingyang.cloud.enums.system.StatusEnum;
import com.lingyang.common.datasource.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
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
@TableName("sys_menu")
public class SysMenuEntity extends BaseEntity {

	@Serial 
	private static final long serialVersionUID =  3547106534211759633L;

	/**
	 * 菜单名称
	 */
   	@TableField("menu_name")
	@Schema(description = "菜单名称")
	@NotEmpty(message = "菜单名称不能为空")
	private String menuName;

	/**
	 * 父菜单ID
	 */
   	@TableField("parent_id")
	@Schema(description = "父菜单ID")
	private Long parentId;

	/**
	 * 显示顺序
	 */
   	@TableField("order_num")
	@Schema(description = "显示顺序")
	private Long orderNum;

	/**
	 * 路由地址
	 */
   	@TableField("path")
	@Schema(description = "路由地址")
	private String path;

	/**
	 * 组件路径
	 */
   	@TableField("component")
	@Schema(description = "组件路径")
	private String component;

	/**
	 * 路由参数
	 */
   	@TableField("query")
	@Schema(description = "路由参数")
	private String query;

	/**
	 * 是否为外链（0是 1否）
	 */
   	@TableField("is_frame")
	@Schema(description = "是否为外链（0是 1否）")
	private Integer isFrame;

	/**
	 * 是否缓存（0缓存 1不缓存）
	 */
   	@TableField("is_cache")
	@Schema(description = "是否缓存（0缓存 1不缓存）")
	private Integer isCache;

	/**
	 * 菜单类型（M目录 C菜单 F按钮）
	 */
   	@TableField("menu_type")
	@Schema(description = "菜单类型（M目录 C菜单 F按钮）")
	private String menuType;

	/**
	 * 菜单状态
	 */
   	@TableField("visible")
	@Schema(description = "菜单状态")
	private StatusEnum visible;

	/**
	 * 菜单状态（0正常 1停用）
	 */
   	@TableField("status")
	@Schema(description = "菜单状态（0正常 1停用）")
	private StatusEnum status;

	/**
	 * 权限标识
	 */
   	@TableField("perms")
	@Schema(description = "权限标识")
	private String perms;

	/**
	 * 菜单图标
	 */
   	@TableField("icon")
	@Schema(description = "菜单图标")
	private String icon;

	/**
	 * 备注
	 */
   	@TableField("remark")
	@Schema(description = "备注")
	private String remark;
}