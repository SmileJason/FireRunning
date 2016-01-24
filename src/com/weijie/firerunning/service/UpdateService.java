package com.weijie.firerunning.service;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.IBinder;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindCallback;

import com.google.gson.reflect.TypeToken;
import com.weijie.firerunning.bean.AppInfo;
import com.weijie.firerunning.fragment.AboutFragment;
import com.weijie.firerunning.util.JSONUtil;

/**
 * 更新服务
 * @author Administrator
 */
public class UpdateService extends Service {
	
	public static final String UPDATE_ACTION = "com.weijie.firerunning.update";
	public static final String EXTRA_INFO = "info";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		getData();
		return super.onStartCommand(intent, flags, startId);
	}
	
	 //获取应用信息
	 private static final AppInfo getAppInfo(Context context){
		 AppInfo info = new AppInfo();
		 try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(),0);
			info.setVersionCode(packageInfo.versionCode);
			info.setVersionName(packageInfo.versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		 return info;
	 }
	
	private void getData() {
		BmobQuery<AppInfo> query = new BmobQuery<AppInfo>("AppInfo");
		query.findObjects(this.getApplicationContext(), new FindCallback() {
			@Override	
			public void onSuccess(JSONArray jsonArray) {
				if(jsonArray!=null) {
					TypeToken<ArrayList<AppInfo>> typeToken = new TypeToken<ArrayList<AppInfo>>() {
					};
					List<AppInfo> apps = JSONUtil.toList(jsonArray,typeToken);
					if(apps!=null && apps.size()>0) {
						AppInfo info = apps.get(0);
						AppInfo app = getAppInfo(UpdateService.this);
						if(info !=null && info.isValid()) {
							AboutFragment.path = info.getUrl();
						}
						if(info !=null && info.isValid() && app.getVersionCode()>0 && info.getVersionCode() > app.getVersionCode()){
							Intent intent = new Intent(UpdateService.UPDATE_ACTION);
							intent.putExtra(UpdateService.EXTRA_INFO,info);
							sendBroadcast(intent);
						}
					}
				}
				stopSelf();
			}

			@Override
			public void onFailure(int i, String s) {
				stopSelf();
			}
		});
	}
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}
