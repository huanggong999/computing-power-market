package com.lingyang.cloud.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SysNewsQuery {


    /**
     * 标题
     */
    @Schema(description = "标题")
    private String name;


}
