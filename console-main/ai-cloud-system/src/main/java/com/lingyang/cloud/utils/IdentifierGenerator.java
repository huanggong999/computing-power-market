package com.lingyang.cloud.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/1/11 15:13
 */
public class IdentifierGenerator {
    private static final String PREFIX = "FP";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");
    private static int sequence = 1;

    public static synchronized String generateIdentifier() {
        String timestamp = DATE_FORMAT.format(new Date());
        String seqStr = String.format("%03d", sequence++);
        return PREFIX + timestamp + seqStr;
    }

    public static void main(String[] args) {
        // 测试生成FP标识符
        System.out.println(generateIdentifier());
    }
}
