package com.weijie.firerunning;

import android.app.Application;
import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.weijie.firerunning.db.DataBaseUtil;
import com.weijie.firerunning.net.HttpsStack;
import com.weijie.firerunning.util.ViewUtil;

public class App extends Application {

	private RequestQueue requestQueue;
	
	public static final App getInstance(Application application){
		return (App) application;
	}
	
	public static final RequestQueue getRequestQueue(Application application){
		return getInstance(application).getRequestQueue();
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		CrashHandler crashHandler = CrashHandler.getInstance();  
		crashHandler.init(getApplicationContext());  
		DataBaseUtil.getInstance().setContext(this);
		ViewUtil.getInstance().setContext(this);
		requestQueue = Volley.newRequestQueue(this,new HttpsStack(this));
		
		// 初始化BmobSDK
	    Bmob.initialize(this, Conf.ApplicationID);
	    // 使用推送服务时的初始化操作
	    BmobInstallation.getCurrentInstallation(this).save();
	    // 启动推送服务
	    BmobPush.startWork(this, Conf.ApplicationID);
	}
	
	public RequestQueue getRequestQueue(){
		return requestQueue;
	}

}
