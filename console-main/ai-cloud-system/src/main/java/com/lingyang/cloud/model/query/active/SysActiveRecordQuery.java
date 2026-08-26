package com.lingyang.cloud.model.query.active;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/1/17 15:39
 */
@Data
public class SysActiveRecordQuery {

    @Schema(description = "活动id")
    private Long activeId;

    /**
     * 姓名或手机号
     */
    @Schema(description = "姓名或手机号")
    private String nameOrPhone;

    @Schema(description = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDay;

    @Schema(description = "结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDay;
}
