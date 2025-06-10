package com.ustack.global.common.utils;

import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.symmetric.AES;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * 加密解密工具
 * @author linxin
 * @date 2024/10/11 9:50
 */
public class AESUtil {
    private static final String DEFAULT_KEY = "aa0cm91m923d69la";
    private static final String IV_KEY = "0000000000000000";
    private static final String ALGO = "AES";

    public AESUtil() {
    }

    public static String encryptAuthInfo(String authInfo) {
        return encrypt(authInfo, Mode.CBC, Padding.ZeroPadding);
    }

    public static String decryptAuthInfo(String encryptAuthInfo) {
        return decrypt(encryptAuthInfo, Mode.CBC, Padding.ZeroPadding);
    }

    public static String encrypt(String data, Mode mode, Padding padding) {
        AES aes;
        if (Mode.CBC == mode) {
            aes = new AES(mode, padding, new SecretKeySpec(DEFAULT_KEY.getBytes(), ALGO), new IvParameterSpec(IV_KEY.getBytes()));
        } else {
            aes = new AES(mode, padding, new SecretKeySpec(DEFAULT_KEY.getBytes(), ALGO));
        }

        return aes.encryptHex(data, StandardCharsets.UTF_8);
    }

    public static String decrypt(String data, Mode mode, Padding padding) {
        AES aes;
        if (Mode.CBC == mode) {
            aes = new AES(mode, padding, new SecretKeySpec(DEFAULT_KEY.getBytes(), ALGO), new IvParameterSpec(IV_KEY.getBytes()));
        } else {
            aes = new AES(mode, padding, new SecretKeySpec(DEFAULT_KEY.getBytes(), ALGO));
        }

        byte[] decryptDataBase64 = aes.decrypt(data);
        return new String(decryptDataBase64, StandardCharsets.UTF_8);
    }
}
