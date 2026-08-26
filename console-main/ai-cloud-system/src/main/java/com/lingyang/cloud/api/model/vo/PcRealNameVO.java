package com.lingyang.cloud.api.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/5/15 17:10
 */
@Data
public class PcRealNameVO {

    @Schema(description = "认证类型（1个人认证 2企业认证）")
    private Integer certType;

    @Schema(description = "是否人脸识别（0否 1是）")
    private Boolean isFace;

    @Schema(description = "姓名（法人姓名或者个人姓名）")
    private String name;

    @Schema(description = "身份证号码（个人身份证号码or法人身份证号码）")
    private String idCard;

    @Schema(description = "企业名称")
    private String companyName;

    @Schema(description = "企业统一社会信用代码")
    private String companyCode;

    @Schema(description = "企业注册地址")
    private String companyAddress;

    @Schema(description = "企业营业执照")
    private String companyImg;
}
