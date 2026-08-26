package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lingyang.cloud.enums.system.StatusEnum;
import com.lingyang.common.datasource.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 新闻
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_news")
public class SysNews extends BaseEntity {



	/**
	 * 标题
	 */
	@Schema(description = "标题")
	@TableField("name")
	private String name;

	/**
	 * 官网图片
	 */
	@Schema(description = "官网图片")
	@TableField("image")
	private String image;

	/**
	 * 小程序图片
	 */
	@Schema(description = "小程序图片")
	@TableField("image_two")
	private String imageTwo;


	/**
	 * 简介
	 */
	@Schema(description = "简介")
	@TableField("memo")
	private String memo;

	/**
	 * 内容
	 */
	@Schema(description = "内容")
	@TableField("content")
	private String content;

	/**
	 * 序号
	 */
	@Schema(description = "序号")
	@TableField("sort")
	private Integer sort;

	/**
	 * 状态
	 */
	@Schema(description = "状态")
	@TableField("status")
	private Integer status;

}