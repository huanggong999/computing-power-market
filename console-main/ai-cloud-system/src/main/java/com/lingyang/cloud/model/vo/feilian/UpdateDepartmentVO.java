package com.lingyang.cloud.model.vo.feilian;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/7/1 11:49
 */
@Data
public class UpdateDepartmentVO {

    @Schema(description = "部门的id，格式为：od_xxx")
    private String id;

    @Schema(description = "部门名称")
    private String name;

    @Schema(description = "父部门的id，格式为：od_xxx")
    private String parentId;
}
