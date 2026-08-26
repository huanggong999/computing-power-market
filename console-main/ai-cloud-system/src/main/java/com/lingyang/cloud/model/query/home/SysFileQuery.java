package com.lingyang.cloud.model.query.home;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.lingyang.cloud.enums.coupon.CouponTypeEnum;
import com.lingyang.cloud.enums.system.StatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Collection;
import java.util.Date;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/10/23 15:06
 */
@Data
public class SysFileQuery {

    /**
     * 桶id
     */
    @Schema(description = "桶id")
    private Long bucketId;


    @Schema(description = "文件夹id")
    private Long fileId;

    private String name;


}
