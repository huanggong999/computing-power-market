package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lingyang.cloud.enums.system.SexEnum;
import com.lingyang.cloud.enums.system.StatusEnum;
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
@TableName("sys_user")
public class SysUserEntity extends BaseEntity {

	@Serial
	private static final long serialVersionUID =  4006732601089586359L;

	/**
	 * 用户账号
	 */
   	@TableField("user_name")
	private String userName;

	/**
	 * 用户昵称
	 */
   	@TableField("nick_name")
	private String nickName;

	/**
	 * 手机号码
	 */
   	@TableField("phone")
	private String phone;

	/**
	 * 用户性别（0男 1女 2未知）
	 */
   	@TableField("sex")
	private SexEnum sex;

	/**
	 * 头像地址
	 */
   	@TableField("avatar")
	private String avatar;

	/**
	 * 密码
	 */
   	@TableField("password")
	private String password;

	/**
	 * 帐号状态（0正常 1停用,2离职）
	 */
   	@TableField("status")
	private StatusEnum status;

	/**
	 * 角色ID
	 */
	@TableField("role_id")
	private Long roleId;
	/**
	 * 备注
	 */
   	@TableField("remark")
	private String remark;
}