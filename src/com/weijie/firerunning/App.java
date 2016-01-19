package com.weijie.firerunning;

import android.app.Application;

import com.weijie.firerunning.db.DataBaseUtil;
import com.weijie.firerunning.util.ViewUtil;

public class App extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		CrashHandler crashHandler = CrashHandler.getInstance();  
		crashHandler.init(getApplicationContext());  
		DataBaseUtil.getInstance().setContext(this);
		ViewUtil.getInstance().setContext(this);
	}

}
