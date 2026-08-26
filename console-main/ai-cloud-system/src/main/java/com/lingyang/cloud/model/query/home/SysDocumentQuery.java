package com.lingyang.cloud.model.query.home;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SysDocumentQuery {

    /**
     * 文档名称
     */
    @Schema(description = "文档名称")
    private String name;

    /**
     * 状态1上架 2 下架
     */
    @Schema(description = "状态1上架 2 下架")
    private Integer status;


}
