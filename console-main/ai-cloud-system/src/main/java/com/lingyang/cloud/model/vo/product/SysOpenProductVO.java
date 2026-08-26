package com.lingyang.cloud.model.vo.product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/6/5 16:10
 */
@Data
public class SysOpenProductVO {

    @Schema(description = "id")
    private Long id;

    @Schema(description = "账密集合")
    private List<SysOpenProductUserPwdVO> userPwdList;

    @Schema(description = "IP地区")
    private String ipAddress;

}
