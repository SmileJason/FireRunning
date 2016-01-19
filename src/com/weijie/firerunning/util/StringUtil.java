package com.weijie.firerunning.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class StringUtil {

	/**
	 * 流转为文本
	 * 
	 * @param in
	 * @return
	 */
	public static final String stream2String(InputStream in) {
		String value = "";
		try {
			if (in != null) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				int b = -1;
				while ((b = in.read()) != -1) {
					baos.write(b);
				}
				value = baos.toString("UTF-8");
				in.close();
				baos.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}

}
