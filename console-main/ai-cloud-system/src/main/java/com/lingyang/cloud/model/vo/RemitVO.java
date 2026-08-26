package com.lingyang.cloud.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RemitVO {

    private China china;

    private NoChina noChina;


    @Data
    public static class China {

        @Schema(description = "银行类型（1 国内， 2 国外）")
        private Integer bankType = 1;

        @Schema(description = "银行名称")
            private String bankName = "招商银行振华支行";

        @Schema(description = "开户名称")
        private String account = "逸云数智科技（深圳）有限公司";

        @Schema(description = "银行卡号")
        private String bankNo = "755975336210000";

    }

    @Data
    public static class NoChina {

        @Schema(description = "银行类型（1 国内， 2 国外）")
        private Integer bankType = 1;

        @Schema(description = "银行名称")
        private String bankName = "恒生银行有限公司";

        @Schema(description = "开户名称")
        private String account = "Eyun Technology (HK) Limited";

        @Schema(description = "银行卡号")
        private String bankNo = "203-612288-883";

        @Schema(description = "SWIFT CODE")
        private String swiftCode = "HASEHKHH（HASEHKHHXXX）";

    }



}
