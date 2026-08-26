package com.lingyang.common.core.utils;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2023/8/15 15:17
 */
public class RandomUtils {

    public static String getRandom(int len) {
        return RandomStringUtils.randomAlphanumeric(len);
    }
    public static String getNumberRandom(int len) {
        return RandomStringUtils.randomNumeric(len);
    }
}
