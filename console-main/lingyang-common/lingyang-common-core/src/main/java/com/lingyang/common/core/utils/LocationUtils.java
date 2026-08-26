package com.lingyang.common.core.utils;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2023/8/21 17:43
 */
public class LocationUtils {

    /**
     * 计算中心经纬度与目标经纬度的距离（米）
     *
     * @param centerLon
     *            中心精度
     * @param centerLat
     *            中心纬度
     * @param targetLon
     *            需要计算的精度
     * @param targetLat
     *            需要计算的纬度
     * @return 米
     */
    private static double distance(double centerLon, double centerLat, double targetLon, double targetLat) {
        // 每经度单位米;
        double jd = 102834.74258026089786013677476285;
        // 每纬度单位米;
        double wd = 111712.69150641055729984301412873;
        double b = Math.abs((centerLat - targetLat) * jd);
        double a = Math.abs((centerLon - targetLon) * wd);
        return Math.sqrt((a * a + b * b));
    }
}
