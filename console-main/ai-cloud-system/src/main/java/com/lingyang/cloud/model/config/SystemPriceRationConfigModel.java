package com.lingyang.cloud.model.config;

import com.lingyang.cloud.entity.SysPriceEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/7 16:31
 */
@Data
public class SystemPriceRationConfigModel {

    private BigDecimal priceRatio;

    public SystemPriceRationConfigModel(BigDecimal priceRatio) {
        this.priceRatio = priceRatio;
    }


    /**
     * 计算溢价
     * @param originalPrice 原价
     * @return 溢价之后的价格
     */
    public BigDecimal calculatePremium(BigDecimal originalPrice){
        if (originalPrice == null || priceRatio == null ) {
            originalPrice = BigDecimal.ZERO;
        }
       return originalPrice.multiply(priceRatio);
    }


    public void calculatePremium(SysPriceEntity sysPriceEntity){
        sysPriceEntity.setHoursPrice(calculatePremium(sysPriceEntity.getHoursPrice()));
        sysPriceEntity.setMonthPrice(calculatePremium(sysPriceEntity.getMonthPrice()));
        sysPriceEntity.setOneYearPrice(calculatePremium(sysPriceEntity.getOneYearPrice()));
        sysPriceEntity.setTwoYearPrice(calculatePremium(sysPriceEntity.getTwoYearPrice()));
        sysPriceEntity.setThreeYearPrice(calculatePremium(sysPriceEntity.getThreeYearPrice()));
    }
}
