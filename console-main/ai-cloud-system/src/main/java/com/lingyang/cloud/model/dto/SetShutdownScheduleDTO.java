package com.lingyang.cloud.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 设置定时关机请求
 */
@Data
@Schema(description = "设置定时关机请求")
public class SetShutdownScheduleDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "关机时间（ISO 8601格式，如：2026-05-27T22:00:00），null表示取消")
    private String shutdownTime;
}
