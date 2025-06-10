package com.ustack.global.common.utils;


import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSAUtil {

    private static final int KEY_SIZE = 2048;
    private static final String ALGORITHM = "RSA";

    // 初始化密钥对
    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance(ALGORITHM);
        generator.initialize(KEY_SIZE);
        return generator.generateKeyPair();
    }

    /**
     * 公钥加密
     * @param userName loginId
     * @param rsaPublicKey Base64编码的RSA公钥
     * @return rsa加密的loginId
     * @throws Exception
     */
    public static String encrypt(String userName, String rsaPublicKey) throws Exception {
        byte[] decodedKey = Base64.getDecoder().decode(rsaPublicKey);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decodedKey);
        PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(spec);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(userName.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // 私钥解密
    public static String decrypt(String encryptedText, String rsaPrivateKey) throws Exception {
        byte[] decodedKey = Base64.getDecoder().decode(rsaPrivateKey);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decodedKey);
        PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(spec);
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes);
    }
}
