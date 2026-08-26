package com.lingyang.cloud.api.model.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/2/18 11:32
 */
@Data
public class PcImageOciOverviewDTO {
    @Schema(description = "命名空间")
    @TableField("namespace")
    private String namespace;

    @Schema(description = "OCI制品仓库名称")
    @TableField("oci_name")
    private String ociName;

    @Schema(description = "类型")
    private String type;



}
