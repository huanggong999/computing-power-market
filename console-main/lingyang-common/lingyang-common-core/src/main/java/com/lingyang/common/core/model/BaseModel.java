package com.lingyang.common.core.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2023/8/15 11:46
 */
@Data
public abstract class BaseModel implements Serializable {
    @Serial
    private static final long serialVersionUID = -1997851569198661268L;
    /**
     * 主键
     */
    @Schema(description ="主键")
    private Long id;
    /**
     * 创建者
     */
    @Schema(description ="创建者")
    private String createBy;
    /**
     * 创建者ID
     */
    @Schema(description ="创建者ID")
    private Long createById;
    /**
     * 创建时间
     */
    @Schema(description ="创建时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /**
     * 更新者
     */
    @Schema(description ="更新者")
    private String updateBy;
    /**
     * 更新者Id
     */
    @Schema(description ="更新者Id")
    private Long updateById;
    /**
     * 更新时间
     */
    @Schema(description ="更新时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 备注
     */
    @Schema(description ="备注")
    private String remark;
}
