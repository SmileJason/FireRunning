package com.weijie.firerunning.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.weijie.firerunning.R;
import com.weijie.firerunning.activity.MainActivity;
import com.weijie.firerunning.util.DateUtil;
import com.weijie.firerunning.view.RoundProgressBar;

public class ScheduleFragment extends Fragment {
	
	public final static String UPDATE_UI = "com.weijie.firerunning.update_ui";
	
	private UIReceiver receiver;
	private RoundProgressBar progress;
	private TextView kilemiter,speed,progressTxt,timeLength;
	private double maxDistance;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_schedule, container,false);
		
		kilemiter = (TextView) view.findViewById(R.id.kilemiter);
		speed = (TextView) view.findViewById(R.id.speed);
		progressTxt = (TextView) view.findViewById(R.id.progressTxt);
		timeLength = (TextView) view.findViewById(R.id.timeLength);
		
		progress = (RoundProgressBar) view.findViewById(R.id.progress);
		progress.setMax(1000);
		progress.setProgress(0);
		initReceiver();
		return view;
	}
	
	private void initReceiver() {
		receiver = new UIReceiver();
		IntentFilter filter = new IntentFilter();  
		filter.addAction(UPDATE_UI);  
		getActivity().registerReceiver(receiver, filter); 
	} 

	public void initData(double maxDistance) {
		this.maxDistance = maxDistance;
		if(maxDistance>0) {
			progressTxt.setText("目标完成"+String.format("%.1f",maxDistance)+"公里-完成00.0%");
			progress.setMax((int)maxDistance*1000);
		} else {
			progressTxt.setText("目标完成 --.- 公里-完成 --.- %");
			progress.setMax(1000);
		}
		progress.setProgress(0);
		kilemiter.setText("--.- km");
		speed.setText("--.- km/h -  --- 千卡");
		timeLength.setText("00:00:00");
	}
	
	private class UIReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(((MainActivity)getActivity()).getPlan()!=null) {
				float distance = intent.getFloatExtra("distance", 0);
				long timeLong = intent.getLongExtra("timeLong", 0);
				if(distance>0) {
					progress.setProgress((int)distance);
					progressTxt.setText("目标完成"+String.format("%.1f",maxDistance)+"公里-完成"+String.format("%.1f",distance/(maxDistance*10))+"%");
					kilemiter.setText(String.format("%.1f",distance/1000f)+" km");
					speed.setText(String.format("%.1f",distance*3600f/timeLong)+" km/h -  --- 千卡");
					timeLength.setText(DateUtil.formatTimeString(timeLong));
				}
			} else {
				float distance = intent.getFloatExtra("distance", 0);
				long timeLong = intent.getLongExtra("timeLong", 0);
				if(distance>0) {
					kilemiter.setText(String.format("%.1f",distance/1000f)+" km");
					speed.setText(String.format("%.1f",distance*3600f/timeLong)+" km/h -  --- 千卡");
					timeLength.setText(DateUtil.formatTimeString(timeLong));
				}
			}
		}
	}
	
	/**
	 * 方法必须重写
	 */
	@Override
	public void onDestroy() {
		if(receiver!=null) {
			getActivity().unregisterReceiver(receiver);
		}
		super.onDestroy();
	}
	
}
