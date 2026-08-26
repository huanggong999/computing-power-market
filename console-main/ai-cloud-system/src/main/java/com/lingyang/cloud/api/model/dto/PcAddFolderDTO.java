package com.lingyang.cloud.api.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PcAddFolderDTO {

    /**
     * 桶id
     */
    @Schema(description = "桶id")
    private Long bucketId;


    @Schema(description = "上级文件夹id")
    private Long fileId;


    @Schema(description = "文件夹名称")
    private String name;




}
