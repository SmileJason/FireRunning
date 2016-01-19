package com.weijie.firerunning.util;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.util.TypedValue;
import android.widget.Toast;

/**
 * 界面控件单位换算工具
 * 创建时间：Jul 6, 2015  1:55:53 PM
 * @author weijie
 */
public class ViewUtil {

	private static ViewUtil viewUtil;
	private Context context;
	private Toast toast;

	private ViewUtil() {}
	
	public void setContext(Context context) {
		this.context = context;
	}

	public static synchronized  ViewUtil getInstance() {
		if(viewUtil==null) {
			viewUtil = new ViewUtil();
		}
		return viewUtil;
	}
	
	public void showToast(String msg) {
		if(toast==null) {
			toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
		}
		if(msg!=null) {
			toast.setText(msg);
			toast.show();
		}
	}
	
	public void showToast(String msg,int code) {
		if(toast==null) {
			toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
		}
		if(msg!=null) {
			toast.setText(msg);
			toast.show();
		}
	}

	/**
	 * dp转化为px
	 */
	public static int dp2px(Context context, int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				context.getResources().getDisplayMetrics());
	}

	/**
	 * 计算字体高度
	 */
	public static final float getTextHeight(Paint p){
		FontMetrics metrics = p.getFontMetrics();
		return (float) Math.ceil(metrics.descent - metrics.ascent);
	}

}
