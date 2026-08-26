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
 * 文档
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_document")
public class SysDocument extends BaseEntity {

	/**
	 * 文档名称
	 */
	@Schema(description = "文档名称")
	@NotNull(message = "文档名称不能为空")
	@TableField("name")
	private String name;

	/**
	 * 文档简介
	 */
	@Schema(description = "文档简介")
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
	 * 文档介绍
	 */
	@Schema(description = "文档介绍")
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