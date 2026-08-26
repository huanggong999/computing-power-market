package com.lingyang.cloud.api.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.lingyang.cloud.enums.source.ContainerStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/2/18 11:28
 */
@Data
public class PcImageRepositoryVO {
    @Schema(description = "实例名称")
    private String instanceName;

    @Schema(description = "状态")
    @TableField("status")
    private ContainerStatusEnum status;

    @Schema(description = "password")
    private String password;

    @Schema(description = "imageVersions")
    private List<String> imageVersions;

    @Schema(description = "客户id")
    private Long customerId;
}
