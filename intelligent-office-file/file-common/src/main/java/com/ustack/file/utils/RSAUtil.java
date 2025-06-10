package com.ustack.file.utils;

import cn.hutool.core.codec.Base64Decoder;
import cn.hutool.core.codec.Base64Encoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Cipher;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @Description : 非对称加密工具类
 * @Author : LinXin
 * @ClassName : RSAUtil
 * @Date: 2021-03-23 09:12
 */
@Slf4j
public class RSAUtil {

    /**
     * 默认加密公钥 公钥加密
     */
    public static final String DEFUALT_PUB_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAuqLzCuU01U9eX25nZe/Xmy98FFgMxDWiXnM+do9HGg3olWdLcbGaQkTfGa2bKM9yG1wJdtRNQlSSXE4eNfGs1rzpNwO/SLtxSh5OfzapptUr5nlbbS5hjo1oFO5TnxINBKREZJ7hYgPJnGMBrZXifHE+HKPpRKTv95R09My+j1G0PINUXslBDdD4820kGrTAxziqcdInplO7wanXxudmdIHdHMIvPKEGdoPe6XCFE4jQ3TEngQXl/X79b6OPhc9tjleyX8Eh/+rgdTgHxSDfLMpCenGQQio89yqR/aS4zMFobV6bShccfDg4gaKwvHhDZXHmFL3vWssn1BRE2+xw+QIDAQAB";

    /**
     * nginx 私钥解密
     */
    public static final String priKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC6ovMK5TTVT15fbmdl79ebL3wUWAzENaJecz52j0caDeiVZ0txsZpCRN8ZrZsoz3IbXAl21E1CVJJcTh418azWvOk3A79Iu3FKHk5/Nqmm1SvmeVttLmGOjWgU7lOfEg0EpERknuFiA8mcYwGtleJ8cT4co+lEpO/3lHT0zL6PUbQ8g1ReyUEN0PjzbSQatMDHOKpx0iemU7vBqdfG52Z0gd0cwi88oQZ2g97pcIUTiNDdMSeBBeX9fv1vo4+Fz22OV7JfwSH/6uB1OAfFIN8sykJ6cZBCKjz3KpH9pLjMwWhtXptKFxx8ODiBorC8eENlceYUve9ayyfUFETb7HD5AgMBAAECggEAE2S/UJCEru2yZsQ4JlWH1+5VPF/x46mXF61t/i/hOYnccznqkwPbfatea3KovtLfjow/x4NEC/JxyvmrFfvo899pdHgFNm+T1BnWGk35zwYzq51MrMm3BXMYL6ScN9lOVP8kxDfmP6MZHhEDqZ/7iubRh3RY1vM0pEte49kqrAWp/YKvjYf8LLlWTNCT2JscU1W5lMK4D2XtkUrYKYdeTjN2WAnXCm37/ZyydHJ7gxNiJaBOWWi4dVrfcIoBLaF+4kGyvmArFNZ3/TmRBpT94R3Hy9hOB+ji30haO4j2Ri3y26QH0Ve6CblqjKOe4foclZU/OdQcRsy9Vw8Dd8W5yQKBgQDgbPHhgRvMADYYujvTlRKJHByKKb3jbU1Abf++wwn8WsuydtPl/MeG7pwrhbiJsD4iZpUyxYd42xFxiIZQR32ZQF1QBqPs46jBwT6k9WpIbjDZFEb2ougBPPBZ/o8RiBFs2FPfWLwH8+mJ9sUyjw7LhepoVCjF5KPsEoKjBu6PjwKBgQDU5PhVwwE5B68PzxNd3DWKzf4+aXMKxzKnQ2jIoT3hlVJz5U26infk5LsdR7cU0QarawOPUgvZ/1ejYdWu1FxV7iweOqFmixiDKOa7JX88hSv8PyRwkFaA49VD9HBU+X7ZUF2ulWxlTNuQxIZMRUhxCv3f+gw/roCyVANddeoy9wKBgHxgxJkGtcIUp+aCaB+18YTdeI+13JBLhlHb5K3zZ/hc1aF9q6g9tNL6MMEKJiDg9T6Zfbyarp7zQjKayF17kYc6nbx5QaZK8b8X+0Du5k1oDFhynXWZCV2OSWKG6wF7WLTrG2ifw+3gZyDYC6geksOG30ecxNAQDC0MgSwa7mHPAoGBANOKMLgG7FAesnSi7jt1dD5YMSkS3pz3Ef3hnVy92x8roKJIVtPkgv2Nyd9uQx0MFfR+gSsH+37BmfNeY1U07gr0fiILYBTkYZbyLVIIEEWyjeI3W539zfaEbIT5fiTxRHnqwSuYjfyaQhG3NTrUHQc2RM0pSMJ+QqLvl+hmIjBBAoGBAKKffTEKvJcYxitHG0dU40GdwN+GjN/dohrX36LI3e190ruom+61HZtewspkoh3HPpYQqXl8HU7evOItaqzXcf0Cy4ujl+YGEM41YJV7hIatLjyTDLJUWNsA6EFHITgbpx8HKenIsRT2KTa9Sq/gZquUekBeKgp2bX9oiDJFSjyD";

    /**
     * 产生私钥公钥对
     */
    public static void createKeyPair() throws Exception {
        KeyPairGenerator rsaKeyGen = null;
        KeyPair rsaKeyPair = null;
        try {
            System.out.println("Generating a pair of RSA key ... ");
            rsaKeyGen = KeyPairGenerator.getInstance("RSA");
            SecureRandom random = new SecureRandom();
            random.setSeed(System.currentTimeMillis());

            rsaKeyGen.initialize(2048, random);

            rsaKeyPair = rsaKeyGen.genKeyPair();
            PublicKey rsaPublic = rsaKeyPair.getPublic();
            PrivateKey rsaPrivate = rsaKeyPair.getPrivate();

            String publicKeyString = new String(Base64Encoder.encode(rsaPublic.getEncoded()));
            // 得到私钥字符串
            String privateKeyString = new String(Base64Encoder.encode((rsaPrivate.getEncoded())));
            System.out.println("1024-bit RSA key GENERATED.");
        } catch (Exception e) {
            System.out.println("Exception genRSAKeyPair:" + e);
        }
    }

    /**
     * RSA公钥加密
     *
     * @param str
     *            加密字符串
     * @param privateKey
     *            私钥
     * @return 密文
     * @throws Exception
     *             加密过程中的异常信息
     */
    public static String encrypt( String str, String privateKey ) throws Exception{
        //base64编码的公钥
        byte[] decoded = Base64.decodeBase64(privateKey);
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
        //RSA加密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, priKey);
        String outStr = Base64.encodeBase64String(cipher.doFinal(str.getBytes(StandardCharsets.UTF_8)));
        return outStr;
    }

    /**
     * RSA私钥解密
     *
     * @param str
     *            加密字符串
     * @param publicKey
     *            公钥
     * @return 铭文
     * @throws Exception
     *             解密过程中的异常信息
     */
    public static String decrypt(String str, String publicKey) throws Exception{
        //64位解码加密后的字符串
        byte[] inputByte = Base64Decoder.decode(str.getBytes(StandardCharsets.UTF_8));
        //base64编码的私钥
        byte[] decoded = Base64.decodeBase64(publicKey);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
        //RSA解密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, pubKey);
        String outStr = new String(cipher.doFinal(inputByte));
        return outStr;
    }

    /**
     * 公钥加密方法
     * @param s 待价密字符串
     * @param publicKey 公钥
     * @author linxin
     * @return java.lang.String
     * @date 2021/4/13 15:58
     */
    public static String encryptByPublicKey(String s, String publicKey) throws Exception{
        //base64编码的公钥
        byte[] decoded = Base64.decodeBase64(publicKey);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
        //RSA加密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        String outStr = Base64.encodeBase64String(cipher.doFinal(s.getBytes(StandardCharsets.UTF_8)));
        return outStr;
    }

    /**
     * 默认公钥加密方法
     * @param s 待加密字符串
     * @author linxin
     * @return java.lang.String
     * @date 2021/4/13 15:58
     */
    public static String encryptByPublicKey(String s) throws Exception {
        String result = "";
        //base64编码的公钥
        byte[] decoded = Base64.decodeBase64(DEFUALT_PUB_KEY);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
        //RSA加密
        Cipher cipher = Cipher.getInstance("RSA");
        // 判断如果加密串过长，需要分段加密
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        String outStr = Base64.encodeBase64String(cipher.doFinal(s.getBytes(StandardCharsets.UTF_8)));
        return outStr;
    }

    public static String decryptByPrivateKey(String encryptStr, String privateKey) throws Exception{
        //64位解码加密后的字符串
        byte[] inputByte = Base64Decoder.decode(encryptStr.getBytes(StandardCharsets.UTF_8));
        //base64编码的私钥
        byte[] decoded = Base64Decoder.decode(privateKey);
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
        //RSA解密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, rsaPrivateKey);
        String outStr = new String(cipher.doFinal(inputByte));
        return outStr;
    }

    public static String urlEncoded(String s, String encodeCharset) throws UnsupportedEncodingException {
        // 默认UTF-8
        encodeCharset = StringUtils.isBlank(encodeCharset) ? "UTF-8" : encodeCharset;
        return URLEncoder.encode(s, encodeCharset);
    }

    public static String urlDecode(String s, String encodeCharset) throws UnsupportedEncodingException {
        // 默认UTF-8
        encodeCharset = StringUtils.isBlank(encodeCharset) ? "UTF-8" : encodeCharset;
        return URLDecoder.decode(s, encodeCharset);
    }


}
