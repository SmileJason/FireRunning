package com.weijie.firerunning.util;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;


public class Utility {

	public static final int getAppVersion(Context context){
		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 1;
	}
	
	
	public static final String getAppVersionName(Context context){
		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "1.0";
	}
	
}
