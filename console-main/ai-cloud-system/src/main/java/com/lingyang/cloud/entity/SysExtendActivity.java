package com.lingyang.cloud.entity;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lingyang.common.datasource.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;


@TableName(value = "sys_extend_activity", autoResultMap = true)
@Data
public class SysExtendActivity extends BaseEntity {

    @TableId(type = IdType.AUTO)
    @TableField("id")
    @Schema(description = "id")
    private Long id;

    @TableField("status")
    @Schema(description = "状态 1启用 2停用")
    private Integer status;

    @TableField("end_time")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "活动结束时间")
    private Date endTime;


    @TableField(exist = false)
    private List<SysCouponEntity> couponList;


}