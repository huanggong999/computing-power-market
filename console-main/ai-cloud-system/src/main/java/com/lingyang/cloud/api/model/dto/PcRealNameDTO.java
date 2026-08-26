package com.lingyang.cloud.api.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/5/15 17:33
 */
@Data
public class PcRealNameDTO {

    @Schema(description = "实名认证记录id")
    private Long logId;

    @Schema(description = "是否成功")
    private Boolean success;

    @Schema(description = "后台-人脸识别二维码")
    private String qrCode;

    @Schema(description = "小程序人脸识别token")
    private String eidToken;

    @Schema(description = "失败原因")
    private String failReason;
}
