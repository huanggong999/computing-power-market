package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lingyang.cloud.enums.source.EcsTypeEnum;
import com.lingyang.cloud.enums.source.SourceRegionsEnum;
import com.lingyang.cloud.model.vo.regions.SourceRegionZoneVO;
import com.lingyang.common.datasource.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;


@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_home_ecs")
public class SysHomeEcsEntity extends BaseEntity {


		/**
		 * ecs类型
		 */
		@TableField("ecs_type")
		@Schema(description = "ecs类型")
		private EcsTypeEnum ecsType;

	/**
	 * 产品类型（1火山云引擎 2自建服务器）
	 */
	@Schema(description = "产品类型（1火山云引擎 2自建服务器）")
	@TableField("product_type")
	private Integer productType;

	/**
	 * 服务器名称
	 */
	@TableField("name")
	@Schema(description = "服务器名称")
	private String name;

	/**
	 * 服务器描述
	 */
	@TableField("remark")
	@Schema(description = "服务器描述")
	private String remark;


	/**
	 * 服务器描述
	 */
	@TableField("pay_price_text")
	@Schema(description = "价格")
	private String payPriceText;


	/**
	 * 价格颜色
	 */
	@TableField("pay_price_color")
	@Schema(description = "价格颜色")
	private String payPriceColor;


	/**
	 * ecsId
	 */
	@TableField("ecs_id")
	@Schema(description = "ecsId")
	private Long ecsId;

	/**
	 * 状态1上架 2 下架
	 */
	@TableField("status")
	@Schema(description = "状态1上架 2 下架")
	private Integer status;

	/**
	 * 服务器规格
	 */
	@Schema(description = "服务器规格")
	@TableField(exist = false)
	private String ecsScale;

	/**
	 * cpu数量
	 */
	@Schema(description = "cpu数量")
	@TableField(exist = false)
	private Integer cpuNumber;

	/**
	 * 内存大小
	 */
	@Schema(description = "内存大小")
	@TableField(exist = false)
	private Integer memorySize;

	/**
	 * gpu型号
	 */
	@Schema(description = "gpu型号")
	@TableField(exist = false)
	private String gpuModel;



	/**
	 * 按量计费
	 */
	@Schema(description = "按量计费价格")
	@TableField(exist = false)
	private BigDecimal hoursPrice;

	/**
	 * 包年包月
	 */
	@Schema(description = "包年包月价格")
	@TableField(exist = false)
	private BigDecimal monthPrice;

	/**
	 * 1年
	 */
	@Schema(description = "1年价格")
	@TableField(exist = false)
	private BigDecimal oneYearPrice;

	/**
	 * 2年
	 */
	@Schema(description = "2年价格")
	@TableField(exist = false)
	private BigDecimal twoYearPrice;

	/**
	 * 3年
	 */
	@Schema(description = "3年价格")
	@TableField(exist = false)
	private BigDecimal threeYearPrice;

	/**
	 * 可用区
	 */
	@Schema(description = "可用区")
	@TableField(exist = false)
	private SourceRegionsEnum regionsZones;


	@Schema(description = "可下单可以用区列表")
	@TableField(exist = false)
	private List<SourceRegionZoneVO> zoneList;

	@Schema(description = "是否有资源")
	@TableField(exist = false)
	private Boolean hasResource;
}