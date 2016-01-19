package com.weijie.firerunning.fragment;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.weijie.firerunning.R;

public class AboutFragment extends Fragment implements OnClickListener {

	private TextView version;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_about, container, false);
		version = (TextView) view.findViewById(R.id.version);
		version.setText(getAppVersionName(getActivity()));
		view.findViewById(R.id.shareApp).setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.shareApp:
			
			break;
		}
	}
	
	/** 
	 * 返回当前程序版本名 
	 */  
	private String getAppVersionName(Context context) {  
	    String versionName = "1.0";  
	    try {  
	        PackageManager pm = context.getPackageManager();  
	        PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);  
	        versionName = pi.versionName;  
	    } catch (Exception e) {  
	        Log.e("VersionInfo", "Exception", e);  
	    }  
	    return versionName;  
	}  

}
