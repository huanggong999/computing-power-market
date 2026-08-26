package com.lingyang.cloud.model.vo.customer;

import com.lingyang.cloud.model.dto.SysCustomerEcsWorkEipDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/5/23 14:34
 */
@Data
public class SysCustomerEcsWorkVO {

    @Schema(description = "工单id")
    private Long id;

    @Schema(description = "公网信息")
    private List<SysCustomerEcsWorkEipDTO> publicInfos;

}
