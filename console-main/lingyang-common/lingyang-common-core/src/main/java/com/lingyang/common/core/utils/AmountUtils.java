package com.lingyang.common.core.utils;

import org.apache.commons.lang3.ObjectUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2023/8/29 11:16
 */
public class AmountUtils {
    public static final String MILLION_UNIT = "万";
    public static final String BILLION_UNIT = "亿";
    public static final BigDecimal ONE_HUNDRED_THOUSAND = new BigDecimal(100000);
    public static final BigDecimal ONE_HUNDRED_MILLION = new BigDecimal(100000000);
    public static final BigDecimal TEN_THOUSAND = new BigDecimal(10000);
    public static final BigDecimal DIVIDE = new BigDecimal(100);


    public static BigDecimal yuanToDivide(BigDecimal amount) {
        return amount.multiply(DIVIDE);
    }
    public static BigDecimal amountConversion(BigDecimal amount, int scale) {
        return amountConversion(amount, null, scale);
    }

    public static BigDecimal amountConversion(BigDecimal amount, BigDecimal unit, int scale) {
        if (ObjectUtils.isEmpty(amount)) {
            return new BigDecimal("0");
        }
        if (ObjectUtils.isEmpty(unit)) {
            unit = ONE_HUNDRED_MILLION;
        }
        return amount.divide(unit, scale, RoundingMode.UP);
    }

    public static BigDecimal billionUnitAmountConversionToYuan(BigDecimal amount) {
        return amount.multiply(ONE_HUNDRED_MILLION);
    }
}
