package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lingyang.common.datasource.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;


@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_network_form")
public class SysNetworkFormEntity extends BaseEntity {

	@Schema(description = "表单类型（1 咨询表单 2 购买表单）")
	private Integer formType;

	/**
	 * 表单名称
	 */
	@TableField("name")
	@Schema(description = "表单名称")
	private String name;

	/**
	 * json
	 */
	@TableField("json")
	@Schema(description = "json")
	private String json;

	/**
	 * 状态 1启用 2关闭
	 */
	@TableField("status")
	@Schema(description = "状态 1启用 2关闭")
	private Integer status;


	/**
     * 用户希望产品开通时间
     */
    @TableField(exist = false)
    @Schema(description = "用户希望产品开通时间")
    private Date userAgiOpenTime;



}