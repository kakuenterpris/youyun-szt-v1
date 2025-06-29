package com.ustack.file.utils;

import cn.hutool.core.codec.Base64Decoder;
import cn.hutool.core.codec.Base64Encoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * 产生用户公钥私钥以及签名方法
 *
 *
 */
@Slf4j
public class RSASignature {

	private static final char[] bcdLookup = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	private static final String ENCODING_UTF_8="UTF-8";

	private static final String pri = null;
	private static final String pub = null;


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

			rsaKeyGen.initialize(1024, random);

			rsaKeyPair = rsaKeyGen.genKeyPair();
			PublicKey rsaPublic = rsaKeyPair.getPublic();
			PrivateKey rsaPrivate = rsaKeyPair.getPrivate();

			String publicKeyString = new String(Base64Encoder.encode(rsaPublic.getEncoded()));
			// 得到私钥字符串
			String privateKeyString = new String(Base64Encoder.encode((rsaPrivate.getEncoded())));
			System.out.println("pubKey:" + publicKeyString);
			System.out.println("priKey:" + privateKeyString);

			System.out.println("1024-bit RSA key GENERATED.");
		} catch (Exception e) {
			System.out.println("Exception genRSAKeyPair:" + e);
		}


	}


	private static String bytesToHexStr(byte[] bcd) {
		StringBuffer s = new StringBuffer(bcd.length * 2);
		for (int i = 0; i < bcd.length; i++) {
			s.append(bcdLookup[(bcd[i] >>> 4) & 0x0f]);
			s.append(bcdLookup[bcd[i] & 0x0f]);
		}
		return s.toString();
	}

	private static byte[] hexStrToBytes(String s) {
		byte[] bytes;
		bytes = new byte[s.length() / 2];
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte) Integer.parseInt(s.substring(2 * i, 2 * i + 2),
					16);
		}
		return bytes;
	}

	/**
	 * 签名方法
	 *
	 * @param priKey 用户的私钥
	 * @param src 要进行签名的字符串
	 * @return 签名
	 */
	public static String generateSigature(String priKey, String src) {
		try {
			Signature sigEng = Signature.getInstance("SHA1withRSA");
			byte[] pribyte = hexStrToBytes(priKey.trim());
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pribyte);
			KeyFactory fac = KeyFactory.getInstance("RSA");
			RSAPrivateKey privateKey = (RSAPrivateKey) fac
					.generatePrivate(keySpec);
			sigEng.initSign(privateKey);
			sigEng.update(src.getBytes(StandardCharsets.UTF_8));
			byte[] signature = sigEng.sign();
			return bytesToHexStr(signature);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	/**
	 * RSA公钥加密
	 *
	 * @param str
	 *            加密字符串
	 * @param publicKey
	 *            公钥
	 * @return 密文
	 * @throws Exception
	 *             加密过程中的异常信息
	 */
	public static String encrypt( String str, String publicKey ) throws Exception{
		//base64编码的公钥
		byte[] decoded = Base64.decodeBase64(publicKey);
		RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
		//RSA加密
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, pubKey);
		String outStr = Base64.encodeBase64String(cipher.doFinal(str.getBytes(StandardCharsets.UTF_8)));
		return outStr;
	}

	/**
	 * RSA私钥解密
	 *
	 * @param str
	 *            加密字符串
	 * @param privateKey
	 *            私钥
	 * @return 铭文
	 * @throws Exception
	 *             解密过程中的异常信息
	 */
	public static String decrypt(String str, String privateKey) throws Exception{
		//64位解码加密后的字符串
		byte[] inputByte = Base64Decoder.decode(str.getBytes(StandardCharsets.UTF_8));
		//base64编码的私钥
		byte[] decoded = Base64Decoder.decode(privateKey);
		RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
		//RSA解密
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, priKey);
		String outStr = new String(cipher.doFinal(inputByte));
		return outStr;
	}

}