package com.lingyang.cloud.entity;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/10/23 15:34
 */
@TableName(value = "sys_config", autoResultMap = true)
@Data
public class SysConfigEntity {

    @TableId(type = IdType.AUTO)
    @TableField("id")
    @Schema(description = "id")
    private Integer id;

    @TableField("config_key")
    @Schema(description = "配置key")
    private String configKey;

    @TableField(value = "config_value", typeHandler = JacksonTypeHandler.class)
    @Schema(description = "配置值，任意类型")
    private JSONObject configValue;

    @JsonIgnore
    @Schema(hidden = true)
    public <T> T getValue(Class<T> valueClass) {
        return configValue == null ? null : configValue.to(valueClass);
    }
}