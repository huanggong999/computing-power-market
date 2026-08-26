package com.lingyang.common.core.utils;

import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import com.lingyang.common.core.exception.http.HttpServiceException;
import com.lingyang.common.core.model.result.Result;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2023/9/19 10:59
 */
@Slf4j
public class QrCodeUtils {

    private static final QrConfig DEFAULT_QR_CONFIG;

    static {
        DEFAULT_QR_CONFIG = new QrConfig();
        DEFAULT_QR_CONFIG.setBackColor(Color.white);
        DEFAULT_QR_CONFIG.setForeColor(Color.black);
    }

    public static void generateStream(String content, HttpServletResponse response) throws IOException {
        generateStream(content, null, response);
    }

    public static void generateStream(String content, QrConfig qrConfig, HttpServletResponse response) throws IOException {
        qrConfig = qrConfig == null ? DEFAULT_QR_CONFIG : qrConfig;
        QrCodeUtil.generate(content, qrConfig, "png", response.getOutputStream());
    }

    public static BufferedImage generateImg(String content, int width, int height) {
        return QrCodeUtil.generate(content, width, height);
    }

    public static void responseImage(String content, Integer width, Integer height, HttpServletResponse response) {
        try {
            width = width == null ? 200 : width;
            height = height == null ? 200 : height;
            BufferedImage image = QrCodeUtils.generateImg(content, width, height);
            response.setContentType("image/png");
            ServletOutputStream os = response.getOutputStream();
            ImageIO.write(image, "jpg", os);
        }catch (Exception e) {
            log.error("二维码生成失败", e);
            Result.throwsError(HttpServiceException.class,"生成二维码失败");
        }
    }
}