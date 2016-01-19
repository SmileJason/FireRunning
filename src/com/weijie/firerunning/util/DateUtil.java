package com.weijie.firerunning.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

	/** SQLITE的日期格式 */
	public static final String FORMAT_SQLITE = "yyyy-MM-dd HH:mm:ss";
	public static final String FORMAT_SQLITE_DATE = "yyyy-MM-dd";
	public static final String FORMAT_yyyyMMddHHmmss = "yyyyMMddHHmmss";
	public static final String FORMAT_yyyyMMddHHmmssSSS = "yyyyMMddHHmmssSSS";
	public static final String FORMAT_yyyyMMdd = "yyyyMMdd";
	public static final String FORMAT_YMD_HM = "yyyy.MM.dd";
	
	public static String mSecond2Time(long ms) {
		int second = (int)ms/1000;
		int hour = second/3600;
		int minute = (second%3600)/60;
		second = second - hour*3600 - minute*60;
		if(hour==0) {
			return minute+"′"+second+"″";
		} else {
			return hour*60+minute+"′"+second+"″";
		}
	}
	
	public static String formatTimeString(long ms) {
		int second = (int)ms/1000;
		int hour = second/3600;
		int minute = (second%3600)/60;
		second = second - hour*3600 - minute*60;
		String hStr = "00";
		String mStr = "00";
		String sStr = "00";
		
		if(hour<10) {
			hStr = "0"+hour;
		} else {
			hStr = hour+"";
		}
		if(minute<10) {
			mStr = "0"+minute;
		} else {
			mStr = minute+"";
		}
		if(second<10) {
			sStr = "0"+second;
		} else {
			sStr = second+"";
		}
		return hStr+":"+mStr+":"+sStr;
	}
	
	/**
	 * 日期转换成字符串
	 * 
	 * @param pattern
	 *            转换格式
	 * @param date
	 *            日期
	 * @return 日期字符串, 转换失败时返回null
	 */
	public static String dateToString(String pattern, Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		String dateStr = null;
		try {
			dateStr = sdf.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateStr;
	}

	/**
	 * 字符串转换成日期
	 * 
	 * @param pattern
	 *            字符串的日期格式
	 * @param dateStr
	 *            源字符串
	 * @return Date格式的日期, 转换失败时返回null
	 */
	public static Date stringToDate(String pattern, String dateStr) {
		Date date = null;
		if (dateStr == null || dateStr.length() < 1) {
			return date;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		try {
			date = sdf.parse(dateStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}
	
}
