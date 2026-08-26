package com.lingyang.common.core.model.page;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2023/8/4 18:13
 */
@Data
public abstract class BasePage implements Serializable {
    @Serial
    private static final long serialVersionUID = 423428825950816514L;
    @Schema(description = "当前页")
    private int pageNo;

    @Schema(description = "当前页大小")
    private int pageSize;
}
