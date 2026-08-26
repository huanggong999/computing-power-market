package com.lingyang.cloud.model.vo.feilian;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/6/11 14:38
 */
@Data
public class AddVpnVO {

    @Schema(description = "操作的id列表")
    private String[] identityIds;

    @Schema(description = "操作类型1 为部门；2为用户；3为角色")
    private Integer identityType;

    @Schema(description = "使用时长（单位天），默认0，表示永久有效")
    private Integer days;
}
