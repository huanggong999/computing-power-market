package com.lingyang.cloud.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 定时关机状态
 */
@Data
@Schema(description = "定时关机状态")
public class ShutdownScheduleVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "是否有定时关机任务")
    private Boolean hasSchedule;

    @Schema(description = "关机时间")
    private String shutdownTime;

    @Schema(description = "剩余时间（秒）")
    private Long remainingTime;
}
