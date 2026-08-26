package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Description: 
 * @Author: 王小龙
 * @Date: 2024-03-27 
 */

@Data
@TableName("sys_role_menu")
public class SysRoleMenuEntity implements Serializable {

	@Serial 
	private static final long serialVersionUID =  2610604408923994911L;

	/**
	 * 角色ID
	 */
   	@TableField("role_id")
	private Long roleId;

	/**
	 * 菜单ID
	 */
   	@TableField("menu_id")
	private Long menuId;

}
