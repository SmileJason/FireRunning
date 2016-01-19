package com.weijie.firerunning.fragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import com.weijie.firerunning.R;
import com.weijie.firerunning.activity.MainActivity;
import com.weijie.firerunning.adapter.KileMeterSpeedAdapter;
import com.weijie.firerunning.bean.KileMeterSpeed;
import com.weijie.firerunning.bean.LatLngPoint;
import com.weijie.firerunning.bean.Plan;
import com.weijie.firerunning.bean.PlanDay;
import com.weijie.firerunning.bean.PlanWeek;
import com.weijie.firerunning.bean.RunRecord;
import com.weijie.firerunning.db.DataBaseUtil;
import com.weijie.firerunning.gps.LBSService;
import com.weijie.firerunning.util.DateUtil;
import com.weijie.firerunning.util.JSONUtil;
import com.weijie.firerunning.util.ViewUtil;
import com.weijie.firerunning.view.RoundProgressBar;

/**
 * 跑步
 * @author weijie
 *
 */
public class RunFragment extends Fragment implements OnClickListener {

	public static final int CMD_STOP_SERVICE = 0;
	public LocationManager lManager;
	
	public static final String UPDATE_LIST = "com.weijie.firerunning.update";

	private String act = "529844698+";

	private LayoutInflater inflater;
	private AlertDialog dialog;
	private RoundProgressBar progressBar;
	private TextView map_schedule;
	private Fragment[] fs;
	private FragmentManager manager;
	private ListView listView;
	private TextView start;
	//private List<LatLng> latLngs;
	//private int index = 0;
	private List<KileMeterSpeed> speeds;
	private KileMeterSpeedAdapter adapter;
	private UpdateListReceiver receiver;
	private Date startDate;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.inflater = inflater;
		View view = inflater.inflate(R.layout.fragment_run, container,false);
		start = (TextView) view.findViewById(R.id.start);
		start.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean canStart = ((MapFragment)fs[1]).getCanStart();
				if(canStart) {
					Intent intent = new Intent();
					intent.setAction(MapFragment.CONTROL);
					if(start.getText().equals("开始")) {
						startDate = new Date();
						//}
						start.setText("暂停");
						intent.putExtra("start", true);

						PlanDay day = ((MainActivity)getActivity()).getPlanDay();
						if(day!=null) {
							((ScheduleFragment)fs[0]).initData(day.maxDistance);
							((MapFragment)fs[1]).initData();
							if(adapter!=null && speeds!=null) {
								speeds.clear();
								adapter.notifyDataSetChanged();
							}
						} else {
							((ScheduleFragment)fs[0]).initData(0);
							((MapFragment)fs[1]).initData();
							if(adapter!=null && speeds!=null) {
								speeds.clear();
								adapter.notifyDataSetChanged();
							}
						}
					} else {
						/*start.setText("开始");
						intent.putExtra("start", false);*/
						intent.putExtra("start", false);
						showDialog();
					}
					getActivity().sendBroadcast(intent);
				} else {
					//Toast.makeText(getActivity(), "无法定位，请稍后再开始", Toast.LENGTH_SHORT).show();
					ViewUtil.getInstance().showToast("无法定位，请稍后再开始");
				}
			}
		});
		listView = (ListView) view.findViewById(R.id.listview);
		manager = ((FragmentActivity)getActivity()).getSupportFragmentManager();
		map_schedule = (TextView) view.findViewById(R.id.map_schedule);
		map_schedule.setOnClickListener(this);
		fs = new Fragment[2];
		fs[1] = new MapFragment();
		fs[0] = new ScheduleFragment();
		manager.beginTransaction().add(R.id.runContent,fs[1]).hide(fs[1]).add(R.id.runContent,fs[0]).commit();

		receiver = new UpdateListReceiver();
		IntentFilter filter = new IntentFilter();  
		filter.addAction(UPDATE_LIST);  
		getActivity().registerReceiver(receiver, filter); 

		lManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
		initGPS();
		startService();
		
		return view;
	}
	
	private void initGPS() {
		if (!lManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			//Toast.makeText(this.getActivity(), "请开启GPS导航...", Toast.LENGTH_SHORT).show();
			ViewUtil.getInstance().showToast("请开启GPS导航...");
			// 返回开启GPS导航设置界面
			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivityForResult(intent, 0);
		}
	}
	
	private void startService() {
		if (!lManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			//Toast.makeText(this.getActivity(), "请开启GPS导航...", Toast.LENGTH_SHORT).show();
			ViewUtil.getInstance().showToast("请开启GPS导航...");
			// 返回开启GPS导航设置界面
			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivityForResult(intent, 0);
			return ;
		} else {
			Intent i = new Intent(this.getActivity(), LBSService.class);
			this.getActivity().startService(i);
		}
	}
	
	public void initView() {
		if(start.getText().equals("暂停")) {
			start.setText("开始");
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.map_schedule:
			FragmentTransaction ft = manager.beginTransaction();
			if(map_schedule.getText().toString().equals("地图")) {
				if(fs[0]!=null) {
					ft.hide(fs[0]);
				}
				if(fs[1]==null) {
					fs[1] = new MapFragment();
					ft.add(R.id.runContent, fs[1]);
				} else {
					ft.show(fs[1]);
				}
				map_schedule.setText("进度");
			} else {
				if(fs[1]!=null) {
					ft.hide(fs[1]);
				}
				if(fs[0]==null) {
					fs[0] = new ScheduleFragment();
					ft.add(R.id.runContent, fs[0]);
				} else {
					ft.show(fs[0]);
				}
				map_schedule.setText("地图");
			}
			ft.commit();
			break;
		}
	}

	private void showDialog() {
		dialog = new AlertDialog.Builder(this.getActivity()).create();
		Window window = dialog.getWindow();  
		window.setGravity(Gravity.BOTTOM);  //此处可以设置dialog显示的位置  
		window.setWindowAnimations(R.style.PopupAnimation);  //添加动画  
		dialog.setCanceledOnTouchOutside(true);
		dialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				Intent intent = new Intent();
				intent.setAction(MapFragment.CONTROL);
				intent.putExtra("start", true);
				getActivity().sendBroadcast(intent);
			}
		});
		dialog.show();
		View dialogView = LayoutInflater.from(this.getActivity()).inflate(R.layout.popupwindow_running, null);

		dialogView.findViewById(R.id.keepon).setOnClickListener(dialogListener);
		dialogView.findViewById(R.id.save).setOnTouchListener(dialogTouch);
		progressBar = (RoundProgressBar) dialogView.findViewById(R.id.saveProgress);
		progressBar.setMax(20);
		progressBar.setProgress(0);

		dialog.getWindow().setContentView(dialogView);
	}

	OnClickListener dialogListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.keepon:
				Intent intent = new Intent();
				intent.setAction(MapFragment.CONTROL);
				intent.putExtra("start", true);
				getActivity().sendBroadcast(intent);
				break;
			}
			dialog.dismiss();
		}
	};

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 0) {
				//handler.sendEmptyMessageDelayed(0, 200);  
				progressBar.setProgress(progressBar.getProgress()+1);
			} else if(msg.what == 1) {
				//Toast.makeText(getActivity(), "完成", Toast.LENGTH_SHORT).show();
				//保存数据，并初始化跑步界面
				saveData();
			} else if(msg.what == 2) {
				progressBar.setProgress(0);
				//Toast.makeText(getActivity(), "取消", Toast.LENGTH_SHORT).show();
				ViewUtil.getInstance().showToast("取消");
				//不保存数据，继续记录
			}
			super.handleMessage(msg);
		}
	};

	private void saveData() {
		ArrayList<ArrayList<LatLngPoint>> pointss = ((MapFragment)fs[1]).getPoints();
		if(pointss!=null && pointss.size()>0 && pointss.get(0).size()>1) {
			RunRecord record = new RunRecord();
			long saveTime = startDate.getTime();
			record.id = act+saveTime;
			record.points = JSONUtil.toJson(pointss);
			record.kTime = ((MapFragment)fs[1]).getKTime();
			record.distance = ((MapFragment)fs[1]).getDistance();
			record.startTime = DateUtil.dateToString(DateUtil.FORMAT_YMD_HM, startDate);
			record.saveTime = saveTime;
			record.endTime = DateUtil.dateToString(DateUtil.FORMAT_YMD_HM, new Date());
			record.timeLength = ((MapFragment)fs[1]).getTimeLength()+"";
			boolean flag = DataBaseUtil.getInstance().insertRunRecord(record);
			if(!flag) {
				//Toast.makeText(getActivity(), "该记录已经存在，不得重复保存！", Toast.LENGTH_SHORT).show();
				ViewUtil.getInstance().showToast("该记录已经存在，不得重复保存！");
			} else {
				Plan plan = ((MainActivity)getActivity()).getPlan();
				PlanWeek planWeek = ((MainActivity)getActivity()).getPlanWeek();
				PlanDay planDay = ((MainActivity)getActivity()).getPlanDay();
				if(plan!=null && planWeek!=null && planDay!=null) {
					plan.distance = Double.parseDouble(plan.distance+Double.parseDouble(String.format("%.1f",record.distance/1000))+"");
					planWeek.distance = Double.parseDouble(planWeek.distance+Double.parseDouble(String.format("%.1f",record.distance/1000))+"");
					planDay.distance = Double.parseDouble(planDay.distance+Double.parseDouble(String.format("%.1f",record.distance/1000))+"");
					DataBaseUtil.getInstance().updatePlan(plan, planWeek, planDay);
					
					((MainActivity)getActivity()).updatePlan();
				}
				//Toast.makeText(getActivity(), "保存成功！", Toast.LENGTH_SHORT).show();
				ViewUtil.getInstance().showToast("保存成功！");
			}
			finishRecord();
			start.setText("开始");
		} else {
			//Toast.makeText(getActivity(), "您本次记录的距离太短，无法保存！", Toast.LENGTH_SHORT).show();
			ViewUtil.getInstance().showToast("您本次记录的距离太短，无法保存！");
			finishRecord();
			start.setText("开始");
		}
	}

	private void finishRecord() {
		if(dialog!=null) {
			dialog.dismiss();
		}
	}

	private TouchThread thread;
	private class TouchThread extends Thread {
		public boolean cancle;
		@Override
		public void run() {
			int p = progressBar.getProgress();
			while(!cancle && p<20) {
				handler.sendEmptyMessage(0);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				p = progressBar.getProgress();
			}
			if(progressBar.getProgress()==20) {
				handler.sendEmptyMessage(1);
			} else {
				handler.sendEmptyMessage(2);
			}
		}
	}

	OnTouchListener dialogTouch = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				thread = new TouchThread();
				thread.cancle = false;
				thread.start();
				break;
			case MotionEvent.ACTION_UP:
				thread.cancle = true;
				break;
			}
			return false;
		}
	};

	private class UpdateListReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(speeds==null) {
				speeds = new ArrayList<KileMeterSpeed>();
			}
			if(speeds.size()==0) {
				speeds.add(new KileMeterSpeed());
			}
			speeds.add(new KileMeterSpeed(intent.getIntExtra("kilemeter", 1),intent.getStringExtra("time")));
			if(adapter==null) {
				adapter = new KileMeterSpeedAdapter(speeds,inflater);
				listView.setAdapter(adapter);
			} else {
				adapter.notifyDataSetChanged();
			}
		}
	}
	
	/**
	 * 方法必须重写
	 */
	@Override
	public void onDestroy() {
		getActivity().unregisterReceiver(receiver);
		super.onDestroy();
	}

}
