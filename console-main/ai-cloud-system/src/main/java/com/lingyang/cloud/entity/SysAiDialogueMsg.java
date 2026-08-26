package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lingyang.common.datasource.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * ai对话消息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_ai_dialogue_msg")
public class SysAiDialogueMsg extends BaseEntity {

   @Schema(description = "对话id")
   private Long dialogueId;

   @Schema(description = "用户发的")
   private String userValue;

   @Schema(description = "ai回的")
   private String aiValue;

   @Schema(description = "状态 1 输出中， 2 已结束， 3 已终止")
   private Integer status;

}
