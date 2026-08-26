package com.lingyang.cloud.model.vo.applets;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/5/23 14:34
 */
@Data
public class AppletsEcsWorkOperationVO {

    @Schema(description = "实例id")
    private Long instanceId;

    @Schema(description = "操作类型（1申请停机 2申请销毁 3申请重启 4申请续费）")
    private Integer operationType;

}
