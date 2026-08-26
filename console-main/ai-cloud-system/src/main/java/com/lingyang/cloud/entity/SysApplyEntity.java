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
 * 应用分类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_apply")
public class SysApplyEntity extends BaseEntity {

	/**
	 * 应用名称
	 */
	@Schema(description = "应用名称")
	@NotNull(message = "应用名称不能为空")
	@TableField("name")
	private String name;

	/**
	 * 图片
	 */
	@Schema(description = "图片")
	@TableField("img")
	private String img;

	/**
	 * 应用简介
	 */
	@Schema(description = "应用简介")
	@NotNull(message = "应用简介不能为空")
	@TableField("intro")
	private String intro;

	/**
	 * 分类id
	 */
	@Schema(description = "分类id")
	@NotNull(message = "分类id不能为空")
	@TableField("type_id")
	private Long typeId;

	/**
	 * 应用标签
	 */
	@Schema(description = "应用标签")
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
	 * 应用版本
	 */
	@Schema(description = "应用版本")
	@TableField("version")
	private String version;

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


	/**
	 * 应用介绍
	 */
	@Schema(description = "应用介绍")
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
	 * 分类名称
	 */
	@Schema(description = "分类名称")
	@TableField(exist = false)
	private String typeName;

	/**
	 * 分类列表
	 */
	@Schema(description = "分类列表")
	@TableField(exist = false)
	private List<Long> typeTypes;



}