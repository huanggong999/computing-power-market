package com.lingyang.cloud.model.query.home;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SysMessageQuery {


    @Schema(description = "状态（1 未读， 2 已读）")
    private Integer status;


}
