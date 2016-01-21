package com.weijie.firerunning;

import android.app.Application;

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
	}
	
	public RequestQueue getRequestQueue(){
		return requestQueue;
	}

}
