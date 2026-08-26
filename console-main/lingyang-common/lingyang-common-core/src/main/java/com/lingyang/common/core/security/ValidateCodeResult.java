package com.lingyang.common.core.security;

import cn.hutool.core.codec.Base64;
import com.lingyang.common.core.exception.MethodExecutionException;
import com.lingyang.common.core.utils.IdUtils;
import com.lingyang.common.core.utils.RandomUtils;
import com.lingyang.common.core.utils.StringUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.util.FastByteArrayOutputStream;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2023/8/14 19:13
 */
@Data
public class ValidateCodeResult {

    @Schema(description = "唯一密钥")
    private String uid;

    @Schema(description = "图片base64编码")
    private String img;

    @Schema(description = "验证码")
    private String code;

    @Schema(description = "到期时间，单位秒")
    private Long expireTime;

    public ValidateCodeResult() {
        this(null);
    }

    public ValidateCodeResult(String code) {
        this(code, null);
    }

    public ValidateCodeResult(String code, BufferedImage img) {
        this(null, code, img);
    }

    public ValidateCodeResult(String uid, String code, BufferedImage img) {
        this.uid = StringUtils.isEmpty(uid) ? IdUtils.simpleUUID() : uid;
        this.code = StringUtils.isEmpty(code) ? RandomUtils.getNumberRandom(6) : code;
        if (img != null) {
            try (FastByteArrayOutputStream os = new FastByteArrayOutputStream()) {
                ImageIO.write(img, "jpg", os);
                this.img = Base64.encode(os.toByteArray());
            } catch (IOException e) {
                throw new MethodExecutionException("图片验证码生成失败", e);
            }
        }
    }
}