package com.lingyang.cloud.model.query.home;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SysCreditContractQuery {


    /**
     * 用户id
     */
    private Long userId;


    /**
     * 合同编号
     */
    @Schema(description = "合同编号")
    private String contractNo;


}
