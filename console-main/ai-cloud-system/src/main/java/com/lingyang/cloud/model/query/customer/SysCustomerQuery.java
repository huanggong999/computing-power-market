package com.lingyang.cloud.model.query.customer;

import com.lingyang.cloud.enums.system.StatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/10/30 11:14
 */
@Data
public class SysCustomerQuery {

    @Schema(description = "用户名手机号搜索key")
    private String namePhoneSearchKey;

    /**
     * 状态
     */
    @Schema(description = "状态")
    private StatusEnum status;
}
