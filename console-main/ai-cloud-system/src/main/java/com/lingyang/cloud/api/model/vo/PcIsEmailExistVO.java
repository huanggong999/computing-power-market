package com.lingyang.cloud.api.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/6/20 11:26
 */
@Data
public class PcIsEmailExistVO {

    @Schema(description = "邮箱集合")
    private List<String> emails;
}
