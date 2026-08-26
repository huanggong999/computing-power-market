package com.lingyang.cloud.model.query.home;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SysContractQuery {

    /**
     * 合同编号
     */
    @Schema(description = "合同编号")
    private String contractNo;

    /**
     * 关联订单号
     */
    @Schema(description = "关联订单号")
    private String linkOrderNo;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    private String startTime;


    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    private String endTime;

    /**
     * 用户id
     */
    private Long userId;


}
