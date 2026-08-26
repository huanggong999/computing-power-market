package com.lingyang.cloud.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/4/9 10:49
 */
@Data
public class FileUploadVO implements Serializable {

    @Serial
    private static final long serialVersionUID = -4137615466183411073L;

    @Schema(description = "文件名称")
    private String name;

    @Schema(description = "文件地址")
    private String url;


    private Object ocrzs;

}
