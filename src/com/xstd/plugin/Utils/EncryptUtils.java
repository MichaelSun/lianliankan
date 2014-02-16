/**
 * EncryptUtils.java
 */
package com.xstd.plugin.Utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.zip.Deflater;

/**
 * 
 * 加密解密类
 * 
 * @author Jie Yun 2013-2-20下午8:18:18
 * 
 */

public class EncryptUtils {

	public static final String SECRET_KEY = "6E09C97EB8798EEB";

	public static final String CHARSET = "UTF-8";

	public static String getMD5Data(byte[] src) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(src);
			return EncryptUtils.byte2hex(md5.digest()).toLowerCase();
		} catch (Exception e) {
		}
		return null;
	}

	public static byte[] getMD5DataBytes(byte[] src) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(src);
			return md5.digest();
		} catch (Exception e) {
		}
		return null;
	}

	public static byte[] zip(byte[] data) {
		return compress(data);
	}

	public static byte[] compress(byte[] data) {
		byte[] output = new byte[0];

		Deflater compresser = new Deflater();

		compresser.reset();
		compresser.setInput(data);
		compresser.finish();
		ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);
		try {
			byte[] buf = new byte[1024];
			while (!compresser.finished()) {
				int i = compresser.deflate(buf);
				bos.write(buf, 0, i);
			}
			output = bos.toByteArray();
		} catch (Exception e) {
			output = data;
			e.printStackTrace();
		} finally {
			try {
				bos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		compresser.end();

		return output;
	}

	public static byte[] Encrypt2Bytes(byte[] sSrc, String sKey)
			throws Exception {
		byte[] raw = sKey.getBytes(CHARSET);
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

		byte[] encrypted = cipher.doFinal(sSrc);
		return encrypted;
	}

	public static String Encrypt(String sSrc, byte[] key) throws Exception {
		SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

		byte[] encrypted = cipher.doFinal(sSrc.getBytes());
		return byte2hex(encrypted).toLowerCase();
	}

	public static String Encrypt(String sSrc, String sKey) throws Exception {
		byte[] raw = sKey.getBytes(CHARSET);
		return Encrypt(sSrc, raw);
	}

	public static String Decrypt(String sSrc, byte[] key) throws Exception {
		try {
			SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
			byte[] encrypted1 = hex2byte(sSrc.getBytes());
			try {
				byte[] original = cipher.doFinal(encrypted1);
				String originalString = new String(original);
				originalString = new String(originalString.getBytes(), "UTF-8");
				return originalString;
			} catch (Exception e) {
				return null;
			}
		} catch (Exception ex) {
			return null;
		}
	}

	public static String Decrypt(String sSrc, String sKey) throws Exception {
		byte[] raw = sKey.getBytes(CHARSET);
		return Decrypt(sSrc, raw);
	}

	public static String Decrypt(byte[] sSrc, String sKey) throws Exception {
		try {
			byte[] raw = sKey.getBytes("UTF-8");
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
			byte[] encrypted1 = hex2byte(sSrc);
			try {
				byte[] original = cipher.doFinal(encrypted1);
				String originalString = new String(original);
				originalString = new String(originalString.getBytes(), "UTF-8");
				return originalString;
			} catch (Exception e) {
				return null;
			}
		} catch (Exception ex) {
			return null;
		}
	}

	public static String byte2hex(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {

			stmp = (Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1) {
				hs = hs + "0" + stmp;
			} else {
				hs = hs + stmp;
			}
		}
		return hs.toUpperCase();
	}

	public static byte[] HexStringToHexByte(String src) {
		byte[] ret = new byte[src.length() / 2];
		// LOGD("[[StringToByte]] src length = " + src.length() / 2);
		int retIndex = 0;
		for (int index = 0; index < src.length();) {
			char cH = src.charAt(index);
			char cL = src.charAt(index + 1);
			ret[retIndex] = (byte) ((charToByte(cH) << 4) | (charToByte(cL) & 0x000f));
			// LOGD("[[StringToByte]] current cH = " + cH + " cL = " + cL +
			// " data = " + byte2hex(ret));
			retIndex++;
			index = index + 2;
		}

		return ret;
	}

	private static byte charToByte(char c) {
		return (byte) "0123456789abcdef".indexOf(c);
	}

	public static byte[] hex2byte(byte[] b) {
		if ((b.length % 2) != 0)
			throw new IllegalArgumentException("length is not even");
		byte[] b2 = new byte[b.length / 2];
		for (int n = 0; n < b.length; n += 2) {
			String item = new String(b, n, 2);
			b2[n / 2] = (byte) Integer.parseInt(item, 16);
		}
		return b2;
	}

}
