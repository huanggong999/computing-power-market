package com.lingyang.cloud.model.edit.ecs;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description: ecs创建信息
 * @Author: 王小龙
 * @Date: 2024/11/8 10:49
 */
@Data
public class EcsCreateDTO {

    /**
     * 地域id
     */
    @Schema(description = "地域id")
    private String regionId;

    /**
     * 可用区id
     */
    @Schema(description = "可用区id")
    private String zoneId;

    @Schema(description = """
            实例到期后是否自动续费，取值：
            true：自动续费。
            false（默认）：不自动续费。""")
    private Boolean autoRenew = false;

    @Schema(description = "每次自动续费的时长")
    private Integer autoRenewPeriod;

    @Schema(description = "实例描述")
    private String description;

    @Schema(description = """
            主机名称:
            Linux实例：
            允许使用字母、数字、点号“.”或中划线“-”。
            不能以中划线、点号开头或结尾，且不能连续使用中划线和点号。
            Linux系统长度限制在2～63个字符之间。
            Windows实例：
            允许使用字母、数字或中划线“-”，不能完全是数字。
            不能以中划线开头或结尾，且不能连续使用中划线。
            Windows系统长度限制在2～15个字符之间。""")
    private String hostname;


    @Schema(description = "镜像id")
    private String imageId;


    @Schema(description = """
            实例和云盘的计费类型，取值：
            PostPaid：按量计费。
            PrePaid：包年包月。请确认您的账号支持余额支付或者信控支付，否则将返回InvalidInstanceChargeType的错误提示。""")
    private String instanceChargeType;

    @Schema(description = """
            实例的名称。
            以字母或中文开头。
            只能包含中文、字母、数字、下划线“_”、中划线“-”和点号“.”。
            长度限制为1～128个字符。
            说明""")
    private String instanceName;


    @Schema(description = "实例规格" )
    private String instanceType;


    @Schema(description = "实例登陆密码")
    private String password;

    @Schema(description = "系统盘大小")
    private Integer systemVolumeSize;
}
