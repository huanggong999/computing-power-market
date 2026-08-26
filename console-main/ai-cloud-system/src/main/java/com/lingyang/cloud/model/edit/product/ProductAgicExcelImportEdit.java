package com.lingyang.cloud.model.edit.product;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/7/11 14:23
 */
@Data
public class ProductAgicExcelImportEdit implements Serializable {

    @Serial
    private static final long serialVersionUID = -3564783227627181622L;

    /**
     * 用户id
     */
    @ExcelProperty("用户ID")
    private String customerId;

    @ExcelProperty("产品ID")
    private String productId;

    @ExcelProperty("产品名称")
    private String productName;

    @ExcelProperty("计费类型")
    private Integer chargeType;

    @ExcelProperty("时长")
    private Integer duration;

    /**
     * 时长单位， 按量计费 小时/GB， 包年 年， 包月 月
     */
    @ExcelProperty("时长单位")
    private Integer durationUnit;

    @ExcelProperty("购买价格")
    private Long payAmount;

    @ExcelProperty("账户数")
    private Integer networkCount;

    @ExcelProperty("IP数量")
    private Integer ipCount;

    @ExcelProperty("带宽数量")
    private Integer bandwidth;

    @ExcelProperty("客户名称")
    private String agiCustomerName;

    @ExcelProperty("部门ID")
    private String departmentId;

    @ExcelProperty("联系电话")
    private String mobile;

    @ExcelProperty("购买时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date actualAgiOpenTime;

    @ExcelProperty("到期时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date actualAgiExpireTime;

    @ExcelProperty("状态")
    private Integer actualStatus;
}
