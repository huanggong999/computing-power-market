package com.lingyang.cloud.model.query.home;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SysHomeEcsQuery {

    /**
     * 名称
     */
    @Schema(description = "名称")
    private String name;

    /**
     * 状态1上架 2 下架
     */
    @Schema(description = "状态1上架 2 下架")
    private Integer status;

    @Schema(description = "表单类型（1 咨询表单 2 购买表单）")
    private Integer formType;

    @Schema(description = "产品类型（1火山云引擎 2自建服务器）")
    private Integer productType;

}
