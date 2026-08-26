package com.lingyang.cloud.api.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class PcMoveOrCopyFileDTO {


    @Schema(description = "需要的目标文件夹id列表")
    private List<Long> fileIdList;


    @Schema(description = "目标文件夹id")
    private Long fileId;




}
