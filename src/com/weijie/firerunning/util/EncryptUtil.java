package com.weijie.firerunning.util;

import java.security.MessageDigest;

/**
 * 加密工具类
 * @author weijie
 * @datetime 2014年7月6日 下午2:59:47
 */
public class EncryptUtil {
	
	public enum Type{
		MD5,SHA1
	}
	
	public static final String MD5(String value){
		return encrypt(Type.MD5, value);
	}
	
	public static final String SHA1(String value){
		return encrypt(Type.SHA1, value);
	}
	
	
	/**
	 * 加密文本
	 * @param type 加密类型的枚举
	 * @param value 需要加密的文本
	 * @return
	 */
	public static final String encrypt(Type type,String value){
		MessageDigest md = null;
		try {
			switch (type) {
			case SHA1:
				md = MessageDigest.getInstance("SHA-1");
				break;
			default: //默认md5加密
				md = MessageDigest.getInstance("MD5");
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		char[] charArray = value.toCharArray();
		byte[] byteArray = new byte[charArray.length];

		for (int i = 0; i < charArray.length; i++)
			byteArray[i] = (byte) charArray[i];

		byte[] md5Bytes = md.digest(byteArray);

		StringBuffer hexValue = new StringBuffer();

		for (int i = 0; i < md5Bytes.length; i++) {
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16) {
				hexValue.append("0");
			}
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();
	}
}
