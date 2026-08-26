package com.lingyang.cloud.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/1/13 10:57
 */
@Data
public class PcInvoiceBillDTO {

    /**
     * 账期
     */
    @Schema(description = "账期")
    private String bill;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startDate;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endDate;

    /**
     * 可开发票金额
     */
    @Schema(description = "可开发票金额")
    private BigDecimal amount;

    /**
     * 可开发票总额
     */
    @Schema(description = "可开发票总额")
    private BigDecimal totalAmount;

    /**
     * 产品名称
     */
    @Schema(description = "产品名称")
    private String productName;

    @Schema(description = "账单id集合")
    private String billIds;

    @Schema(description = "标签(0未开票 1已开票)")
    private Integer tag;

}
