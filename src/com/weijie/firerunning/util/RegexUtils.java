package com.weijie.firerunning.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则校验
 * 
 * @author weijie
 * @datetime 2014-12-11 上午10:36:57
 */
public class RegexUtils {

	/**
	 * 验证邮箱
	 * 
	 * @param 待验证的字符串
	 * @return 如果是符合的字符串,返回 <b>true </b>,否则为 <b>false </b>
	 */
	public static boolean isEmail(String str) {
		String regex = "^([\\w-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([\\w-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		return match(regex, str);
	}

	/**
	 * 验证输入密码长度 (6-18位)
	 * 
	 * @param 待验证的字符串
	 * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
	 */
	public static boolean isPassword(String str) {
		String regex = "^[\\@A-Za-z0-9\\!\\#\\$\\%\\^\\&\\*\\.\\~]{6,18}$";
		return match(regex, str);
	}
	
	/**
	 * 验证输入用户名 (字母开头，+数字4-16位)
	 * 
	 * @param 待验证的字符串
	 * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
	 */
	public static boolean isUsername(String str) {
		String regex = "^[A-Za-z]+[A-Za-z0-9]*$";
		return (match(regex, str) && str.length() >= 4 && str.length() <= 16);
	}
	
	/**
	 * 验证输入手机号码
	 * 
	 * @param 待验证的字符串
	 * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
	 */
	public static boolean isHandset(String str) {
		String regex = "^[0,1]+[3,5,7,8]+\\d{9}$";
		return match(regex, str);
	}

	/**
	 * @param regex  正则表达式字符串
	 * @param str 要匹配的字符串
	 * @return 如果str 符合 regex的正则表达式格式,返回true, 否则返回 false;
	 */
	private static boolean match(String regex, String str) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

}
