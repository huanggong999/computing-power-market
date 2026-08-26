package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lingyang.cloud.enums.system.StatusEnum;
import com.lingyang.common.datasource.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 模型和数据
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_module")
public class SysModuleEntity extends BaseEntity {

	/**
	 * 类型（1 模型， 2 数据）
	 */
	@Schema(description = "类型（1 模型， 2 数据）")
	@TableField("type")
	private Integer type;

	/**
	 * 封面
	 */
	@Schema(description = "封面")
	@TableField("cover")
	private String cover;

	/**
	 * 名称
	 */
	@Schema(description = "名称")
	@NotNull(message = "名称不能为空")
	@TableField("name")
	private String name;

	/**
	 * 简介
	 */
	@Schema(description = "简介")
	@NotNull(message = "简介不能为空")
	@TableField("intro")
	private String intro;


	/**
	 * 标签
	 */
	@Schema(description = "标签")
	@TableField("tags")
	private String tags;

	/**
	 * 发布者名称
	 */
	@Schema(description = "发布者名称")
	@TableField("publish_user_name")
	private String publishUserName;

	/**
	 * 发布者头像
	 */
	@Schema(description = "发布者头像")
	@TableField("publish_user_avatar")
	private String publishUserAvatar;

	/**
	 * 介绍
	 */
	@Schema(description = "介绍")
	@TableField("introduce")
	private String introduce;

	/**
	 * 序号
	 */
	@NotNull(message = "序号不能为空")
	@Schema(description = "序号")
	private Integer sort;

	/**
	 * 状态
	 */
	@NotNull(message = "状态不能为空")
	@Schema(description = "状态")
	private StatusEnum status;

	/**
	 * 镜像大小
	 */
	@Schema(description = "镜像大小")
	@TableField("mirror_size")
	private String mirrorSize;


	/**
	 * 镜像类型
	 */
	@Schema(description = "镜像类型")
	@TableField("mirror_type")
	private String mirrorType;


	/**
	 * 镜像版本
	 */
	@Schema(description = "镜像版本")
	@TableField("mirror_version")
	private String mirrorVersion;

	/**
	 * 镜像ID
	 */
	@Schema(description = "镜像ID")
	@TableField("mirror_id")
	private String mirrorId;




}