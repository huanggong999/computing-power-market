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
public class SysProductIpExportDto {

    @ExcelProperty("产品名称")
    private String productName;

    @ExcelProperty("内网IP")
    private String ip;

    @ExcelProperty("公网IP")
    private String publicIp;

    @ExcelProperty("IP地区")
    private String ipAddress;

    @ExcelProperty("状态")
    private String status;

    @ExcelProperty("客户账户")
    private String nickname;

    @ExcelProperty("客户邮箱")
    private String email;
}
