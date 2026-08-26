package com.lingyang.cloud.utils;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2023/4/17 下午5:19
 */
public class AesUtil {
    public static final int KEY_LENGTH_BYTE = 32;
    public static final int TAG_LENGTH_BIT = 128;
    private final byte[] aesKey;

    public AesUtil(byte[] key) {
        if (key.length != KEY_LENGTH_BYTE) {
            throw new IllegalArgumentException("无效的ApiV3Key，长度必须为32个字节");
        }
        this.aesKey = key;
    }
    /**
     * 解密
     * @param associatedData 附加数据
     * @param nonce 随机串
     * @param ciphertext 数据密文
     * @return 结果
     * @throws GeneralSecurityException 解密异常
     */
    public String decryptToString(byte[] associatedData, byte[] nonce, String ciphertext) throws GeneralSecurityException{
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

            SecretKeySpec key = new SecretKeySpec(aesKey, "AES");
            GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, nonce);
            cipher.init(Cipher.DECRYPT_MODE, key, spec);
            cipher.updateAAD(associatedData);
            return new String(cipher.doFinal(Base64.getDecoder().decode(ciphertext)), StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new IllegalStateException(e);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
