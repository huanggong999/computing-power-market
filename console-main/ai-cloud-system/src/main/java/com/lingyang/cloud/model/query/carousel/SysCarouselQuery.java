package com.lingyang.cloud.model.query.carousel;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SysCarouselQuery {


    /**
     * 轮播图名称
     */
    @Schema(description = "轮播图名称")
    private String name;

    /**
     * 1 pc 2 小程序
     */
    @Schema(description = "1 pc 2 小程序")
    private Integer equipmentType;


}
