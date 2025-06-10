package com.ustack.file.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.symmetric.SM4;
import cn.hutool.crypto.symmetric.SymmetricCrypto;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * @author linxin
 * @Description : sm4加密算法
 * @ClassName : Sm4Util
 * @Date: 2022-08-29 19:02
 */
public class Sm4Util {

    /** 密钥 */
    private static final String key = "yyszserver_00000";
    /** 偏移量 加盐 */
    private static final String iv_key = "8868886888688868";

    public static String encrypt(String plainTxt){
        String cipherTxt = "";
        SymmetricCrypto sm4 = new SM4(Mode.CBC, Padding.ZeroPadding, key.getBytes(CharsetUtil.CHARSET_UTF_8), iv_key.getBytes(CharsetUtil.CHARSET_UTF_8));
        byte[] encrypHex = sm4.encrypt(plainTxt);
        cipherTxt = Base64.encode(encrypHex);
        try {
            return URLEncoder.encode(cipherTxt, CharsetUtil.CHARSET_UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String decrypt(String cipherTxt){
        String plainTxt = "";
        SymmetricCrypto sm4 = new SM4(Mode.CBC, Padding.ZeroPadding, key.getBytes(CharsetUtil.CHARSET_UTF_8), iv_key.getBytes(CharsetUtil.CHARSET_UTF_8));
        byte[] cipherHex = new byte[0];
        try {
            cipherHex = Base64.decode(URLDecoder.decode(cipherTxt, CharsetUtil.CHARSET_UTF_8.name()));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        plainTxt = sm4.decryptStr(cipherHex, CharsetUtil.CHARSET_UTF_8);
        return plainTxt;
    }

    public static void main(String[] args) {
//        String encrypt = encrypt("/2022/08/11/b9df76845e141a188f6a2d4febbebc1/1.png");
//        System.out.println(encrypt);
//        String decrypt = decrypt(encrypt);
//        System.out.println(decrypt);

        for (int i = 0; i < 20; i++) {
            String s = RandomUtil.randomString(16);
            System.out.println(s);
        }

    }
}
