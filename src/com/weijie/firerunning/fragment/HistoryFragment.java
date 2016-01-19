package com.weijie.firerunning.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.weijie.firerunning.R;
import com.weijie.firerunning.activity.RecordDescActivity;
import com.weijie.firerunning.activity.ShareActivity;
import com.weijie.firerunning.adapter.RunHistoryAdapter;
import com.weijie.firerunning.bean.RunRecord;
import com.weijie.firerunning.db.DataBaseUtil;

/**
 * 历史
 * @author weijie
 *
 */
public class HistoryFragment extends Fragment implements OnRefreshListener, OnItemClickListener {

	//private ListView listView;
	private ListView listView;
	private List<RunRecord> records;
	private LayoutInflater inflater;
	private Handler mHandler = new Handler();
	private SwipeRefreshLayout mSwipeRefreshWidget;
	
	private final Runnable mRefreshDone = new Runnable() {
		@Override
		public void run() {
			getRunRecord();
			mSwipeRefreshWidget.setRefreshing(false);
		}
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_history, container,false);
		this.inflater = inflater;
		
		mSwipeRefreshWidget = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_widget);
		mSwipeRefreshWidget.setColorSchemeResources(R.color.color1, R.color.color2, R.color.color3,
				R.color.color4);
		
		listView = (ListView) view.findViewById(R.id.listview);
		listView.setOnItemClickListener(this);
		mSwipeRefreshWidget.setOnRefreshListener(this);
		if(mSwipeRefreshWidget.hasWindowFocus()){
			mSwipeRefreshWidget.setRefreshing(true);
			refresh();
		}else{
			mSwipeRefreshWidget.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
				@Override
				public boolean onPreDraw() {
					if(mSwipeRefreshWidget.hasWindowFocus()){
						mSwipeRefreshWidget.getViewTreeObserver().removeOnPreDrawListener(this);	
						mSwipeRefreshWidget.setRefreshing(true);
						refresh();
					}
					return false;
				}
			});
		}
		
		return view;
	}
	
	private void getRunRecord() {
		List<RunRecord> datas = DataBaseUtil.getInstance().getRunRecords();
		if(datas!=null && datas.size()>0) {
			HashMap<String, String> map = new HashMap<String, String>();
			records = new ArrayList<RunRecord>();
			for(RunRecord record:datas) {
				String key = record.startTime.substring(0, 7);
				if(map.get(key)==null) {
					map.put(key, key);
					records.add(new RunRecord(key.substring(0, 4)+"年"+key.substring(5)+"月"));
				}
				records.add(record);
			}
			listView.setAdapter(new RunHistoryAdapter(records,inflater));
		}
	}
	
	@Override
	public void onRefresh() {
		refresh();
	}

	private void refresh() {
		mHandler.removeCallbacks(mRefreshDone);
		mHandler.postDelayed(mRefreshDone, 1000);
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		if(records!=null && records.size()>1 && position>0) {
			RunRecord record = records.get(position);
			Intent intent = new Intent(getActivity(),RecordDescActivity.class);
			intent.putExtra("record", record);
			startActivity(intent);
		}
	}

	public void shareHistory() {
		Intent intent = new Intent(this.getActivity(),ShareActivity.class);
		if(records!=null) {
			intent.putExtra("runCount", records.size());
			double distance = 0.0;
			double maxDistance = 0.0;
			for(RunRecord record:records) {
				if(record.distance>maxDistance) {
					maxDistance = record.distance;
				}
				distance += record.distance;
			}
			intent.putExtra("distance", Double.parseDouble(String .format("%.2f",distance/1000)));
			if(records.size()>0) {
				intent.putExtra("averate", Double.parseDouble(String .format("%.2f",distance/(1000*records.size()))));
			} else {
				intent.putExtra("averate", 0.0);
			}
			intent.putExtra("maxDistance", Double.parseDouble(String .format("%.2f",maxDistance/1000)));
		} else {
			intent.putExtra("runCount", 0);
			intent.putExtra("distance", 0.0);
			intent.putExtra("averate", 0.0);
			intent.putExtra("maxDistance", 0.0);
		}
		getActivity().startActivity(intent);
	}
	
}
