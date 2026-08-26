package com.lingyang.cloud.model.config;

import com.alibaba.fastjson2.JSONObject;
import com.lingyang.cloud.entity.SysOrderSourceEntity;
import com.lingyang.cloud.enums.source.SourceChargeTypeEnum;
import com.lingyang.cloud.enums.source.SourceChargeUnitEnum;
import com.lingyang.cloud.enums.source.SourceTypeEnum;
import com.lingyang.cloud.model.edit.order.SysOrderCreateDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/7 14:43
 */
@Data
public class EipAddressConfigModel {

    @Schema(description = "公网ip上限")
    private Integer bandwidthMbps = 200;

    @Schema(description = """
            计费方式:
            PayByBandwidth（默认）：按量计费-按带宽上限计费。
            PayByTraffic：按量计费-按实际流量计费。
            PrePaid：包年包月。""")
    private String chargeType = "PayByTraffic";

    @Schema(description = """
            公网IP的线路类型，默认为BGP。取值：
            BGP：BGP（多线）。
            若您的账号已申请并开通了静态单线权限，则可传入如下取值：
            ChinaMobile：中国移动静态单线。
            ChinaTelecom：中国电信静态单线。
            ChinaUnicom：中国联通静态单线。
            若您的账号已申请并开通了BGP单线权限，则可传入SingleLine_BGP。
            若您的账号已申请并开通了静态BGP权限，则可传入Static_BGP。""")
    private String  isp = "BGP";

    @Schema(description = """
            公网IP是否随实例删除，仅按量计费公网IP生效。取值：
            true：公网IP随实例删除。当实例被系统自动回收（退订24小时后、到期回收等）或被调用DeleteInstance、DeleteInstances接口删除时，公网IP随实例一同释放。
            false（默认）：公网IP不随实例删除。""")
    private Boolean releaseWithInstance = true;

    @Schema(description = "按量计费（元/GB）")
    private BigDecimal trafficPrice = BigDecimal.valueOf(0.8);

    public static SysOrderSourceEntity buildIpOrderSource(SysOrderCreateDTO createDTO,SysOrderSourceEntity ecsOrderSource, EipAddressConfigModel configModel) {
        if (ecsOrderSource.getProductType() ==2 ){
            configModel.setBandwidthMbps(createDTO.getBandwidth());
        }
        SysOrderSourceEntity orderSourceEntity = new SysOrderSourceEntity();
        orderSourceEntity.setRegionsId(ecsOrderSource.getRegionsId());
        orderSourceEntity.setSourceType(SourceTypeEnum.CLOUD_NETWORK);
        orderSourceEntity.setSourceName(configModel.getIsp());
        orderSourceEntity.setProductName("公网IP");
        orderSourceEntity.setConfigDetail(JSONObject.parseObject(JSONObject.toJSONString(configModel)));
        orderSourceEntity.setChargeType(SourceChargeTypeEnum.POSTPAID_BY_HOUR);
        orderSourceEntity.setDuration(1);
        orderSourceEntity.setDurationUnit(SourceChargeUnitEnum.CAPACITY);
        orderSourceEntity.setUnitPrice(configModel.getTrafficPrice());
        return orderSourceEntity;
    }
}