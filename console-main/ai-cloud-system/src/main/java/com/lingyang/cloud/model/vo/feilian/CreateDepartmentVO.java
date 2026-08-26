package com.lingyang.cloud.model.vo.feilian;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/6/11 11:42
 */
@Data
public class CreateDepartmentVO {

    @Schema(description = "部门名称")
    private String name;

    @Schema(description = "父部门的id，格式为：od_xxx")
    private String parentId;

    @Schema(description = "部门类型：1:子公司2:部门。默认2")
    private Integer type;
}
