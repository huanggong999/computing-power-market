package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lingyang.cloud.enums.carousel.CarouselSkipTypeEnum;
import com.lingyang.cloud.enums.carousel.CarouselTypeEnum;
import com.lingyang.cloud.enums.system.StatusEnum;
import com.lingyang.common.datasource.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 轮播图
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_carousel")
public class SysCarouselEntity extends BaseEntity {


	/**
	 * 1 pc 2 小程序
	 */
	@Schema(description = "1 pc 2 小程序")
	@TableField("`equipment_type`")
	private Integer equipmentType;


	/**
	 * 位置 1 首页 2 AGI-C 3 合作中心
	 */
	@Schema(description = "位置 1 首页 2 AGI-C 3 合作中心")
	@NotNull(message = "位置不能为空")
	@TableField("`type`")
	private Integer type;

	/**
	 * 跳转位置
	 */
	@NotNull(message = "跳转位置不能为空")
	@Schema(description = "跳转位置")
	@TableField("skip_type")
	private CarouselSkipTypeEnum skipType;

	/**
	 * 轮播图名称
	 */
	@Schema(description = "轮播图名称")
	@NotNull(message = "轮播图名称不能为空")
	@TableField("name")
	private String name;

	/**
	 * 轮播图图片
	 */
	@Schema(description = "轮播图图片")
	@NotNull(message = "轮播图图片不能为空")
	@TableField("image")
	private String image;

	/**
	 * 序号
	 */
	@NotNull(message = "序号不能为空")
	@Schema(description = "序号")
	@TableField("sort")
	private Integer sort;

	/**
	 * 状态
	 */
	@NotNull(message = "状态不能为空")
	@Schema(description = "状态")
	@TableField("status")
	private StatusEnum status;

}