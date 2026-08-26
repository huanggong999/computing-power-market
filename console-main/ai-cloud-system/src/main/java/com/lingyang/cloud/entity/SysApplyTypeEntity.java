package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lingyang.cloud.enums.source.EcsTypeEnum;
import com.lingyang.cloud.enums.source.SourceRegionsEnum;
import com.lingyang.cloud.enums.system.StatusEnum;
import com.lingyang.common.datasource.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.util.List;

/**
 * 应用分类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_apply_type")
public class SysApplyTypeEntity extends BaseEntity {

	/**
	 * 分类名称
	 */
	@Schema(description = "分类名称")
	@NotNull(message = "分类名称不能为空")
	@TableField("name")
	private String name;

	/**
	 * 上级分类id(0是一级分类)
	 */
	@Schema(description = "上级分类id(0是一级分类)")
	@NotNull(message = "上级分类id不能为空")
	@TableField("parent_id")
	private Long parentId;

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
	 * 子类列表
	 */
	@Schema(description = "子类列表")
	@TableField(exist = false)
	private List<SysApplyTypeEntity> children;




}