package com.lingyang.cloud.api.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/2/18 11:28
 */
@Data
public class PcImageRepositoryDeleteVO {
    @Schema(description = "实例名称")
    private String instanceName;

    @Schema(description = "命名空间")
    @TableField("namespace")
    private String namespace;

    @Schema(description = "OCI制品仓库名称")
    @TableField("oci_name")
    private String ociName;


    @Schema(description = "imageVersions")
    private List<String> imageVersions;
}
