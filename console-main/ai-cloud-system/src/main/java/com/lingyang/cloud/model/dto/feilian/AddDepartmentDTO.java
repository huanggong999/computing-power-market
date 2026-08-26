package com.lingyang.cloud.model.dto.feilian;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/6/11 17:08
 */
@Data
public class AddDepartmentDTO {

    @Schema(description = "部门id")
    private String id;

    @Schema(description = "部门名称")
    private String name;

    @Schema(description = "部门类型：1:子公司2:部门。默认2")
    private Integer type;

    @Schema(description = "上级部门ID")
    private String parentId;

    @Schema(description = "排序序号")
    private Integer seq;

    @Schema(description = "提示信息")
    private String message;
}
