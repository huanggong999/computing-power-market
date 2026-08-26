package com.lingyang.cloud.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AiMsgDTO {

    @Schema(description = "消息")
    private String message;

    @Schema(description = "对话id")
    private Long dialogueId;

}
