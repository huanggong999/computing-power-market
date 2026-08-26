package com.lingyang.cloud.model.query.home;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SysAIMsgQuery {

    @Schema(description = "对话id")
    private Long dialogueId;

}
