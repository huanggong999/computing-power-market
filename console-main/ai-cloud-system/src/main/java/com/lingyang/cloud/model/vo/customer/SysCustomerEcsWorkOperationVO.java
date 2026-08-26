package com.lingyang.cloud.model.vo.customer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/5/23 14:34
 */
@Data
public class SysCustomerEcsWorkOperationVO {

    /**
     * 实例id
     */
    @Schema(description = "实例id")
    private String instanceId;

    @Schema(description = "操作类型（1停机 2销毁 3重启 4续费）")
    private Integer operationType;

}
