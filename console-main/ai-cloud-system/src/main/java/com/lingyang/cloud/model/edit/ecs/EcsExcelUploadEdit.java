package com.lingyang.cloud.model.edit.ecs;

import com.alibaba.excel.annotation.ExcelProperty;
import com.lingyang.cloud.entity.SysPriceEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/18 16:58
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EcsExcelUploadEdit extends SysPriceEntity {

    @Serial
    private static final long serialVersionUID = -3564783227627181622L;
    /**
     * 服务器规格
     */
    @Schema(description = "服务器规格")
    @ExcelProperty("实例规格")
    private String ecsScale;

    /**
     * cpu数量
     */
    @Schema(description = "cpu数量")
    @ExcelProperty("vCPU")
    private Integer cpuNumber;

    /**
     * 内存大小
     */
    @Schema(description = "内存大小")
    @ExcelProperty("内存 (GiB)")
    private Integer memorySize;

    /**
     * cpu型号
     */
    @Schema(description = "cpu型号")
    @ExcelProperty("处理器型号")
    private String cpuModel;

    /**
     * gpu型号
     */
    @Schema(description = "gpu型号")
    @ExcelProperty("GPU型号")
    private String gpuModel;

    /**
     * gpu内存
     */
    @Schema(description = "gpu内存")
    @ExcelProperty("GPU显存(GB)")
    private String gpuMemory;


    /**
     * 可用区
     */
    @ExcelProperty("地域")
    private String regions;
}
