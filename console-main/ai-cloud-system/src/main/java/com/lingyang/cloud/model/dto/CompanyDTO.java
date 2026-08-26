package com.lingyang.cloud.model.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class CompanyDTO {


    @Schema(description = "企业名称")
    private String companyName;

    @Schema(description = "企业统一社会信用代码")
    private String companyCode;

    @Schema(description = "企业注册地址")
    private String companyAddress;


    @Schema(description = "企业营业执照")
    private String companyImg;

    @Schema(description = "企业联系人名称")
    private String companyContactName;

    @Schema(description = "企业联系人电话")
    private String companyContactPhone;

}
