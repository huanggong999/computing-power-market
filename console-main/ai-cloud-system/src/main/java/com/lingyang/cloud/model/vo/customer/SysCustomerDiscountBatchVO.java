package com.lingyang.cloud.model.vo.customer;

import com.lingyang.cloud.entity.SysCustomerNetwork;
import com.lingyang.cloud.enums.source.SourceTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/2/24 15:32
 */
@Data
public class SysCustomerDiscountBatchVO {

    /**
     * 客户id
     */
    @Schema(description = "客户id")
    @NotNull(message = "客户id不能为空")
    private Long customerId;



    private List<VV> list;

    @Schema(description = "AGIC折扣列表")
    private List<SysCustomerNetwork> customerNetworkList;


    @Data
    public static class VV {



        /**
         * 资源类型
         */
        @Schema(description = "资源类型")
        @NotNull(message = "资源类型不能为空")
        private SourceTypeEnum sourceType;

        /**
         * 折扣比列
         */
        @Schema(description = "折扣比列")
        @NotNull(message = "折扣比列不能为空")
        private BigDecimal discountRation;
    }


}
