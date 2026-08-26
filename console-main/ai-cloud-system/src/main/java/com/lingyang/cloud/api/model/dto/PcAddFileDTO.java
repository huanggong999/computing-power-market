package com.lingyang.cloud.api.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PcAddFileDTO {


    @Schema(description = "桶id")
    private Long bucketId;


    @Schema(description = "文件夹id")
    private Long fileId;




}
