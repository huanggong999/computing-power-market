package com.lingyang.cloud.model.query.active;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/1/16 15:46
 */
@Data
public class SysActiveQuery {

    /**
     * 活动名称
     */
    @Schema(description = "活动名称")
    private String name;

    @Schema(description = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDay;

    @Schema(description = "结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDay;

    /**
     * 活动状态
     */
    @Schema(description = "活动状态")
    private Integer status;
}
