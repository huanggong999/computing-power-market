package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.lingyang.common.datasource.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * ai对话
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_ai_dialogue")
public class SysAiDialogue extends BaseEntity {

   @Schema(description = "用户id")
   private Long userId;

   @Schema(description = "临时用户")
   private String userLingshi;

   @Schema(description = "对话名称")
   private String name;

}
