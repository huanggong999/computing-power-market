package com.lingyang.cloud.model.dto.excelDto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/1/15 10:21
 */
@Data
@HeadRowHeight(50)
@ColumnWidth(25)
public class PcInvoiceExportDto {

    @ExcelProperty("订单号")
    private String orderNo;

    @ExcelProperty("账期")
    private String bill;

    @ExcelProperty("消费时间")
    private String date;

    @ExcelProperty("账单类型")
    private String type;

    @ExcelProperty("产品")
    private String productName;

    @ExcelProperty("可开票总额")
    private String totalAmount;

    @ExcelProperty("已开票金额")
    private String alreadyAmount;

    @ExcelProperty("可开票金额")
    private String amount;
}
