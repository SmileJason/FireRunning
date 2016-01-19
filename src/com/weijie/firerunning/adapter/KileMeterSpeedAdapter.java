package com.weijie.firerunning.adapter;

import java.util.Date;
import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.weijie.firerunning.R;
import com.weijie.firerunning.bean.KileMeterSpeed;
import com.weijie.firerunning.util.DateUtil;

public class KileMeterSpeedAdapter extends BaseAdapter{

	private LayoutInflater inflater;
	private List<KileMeterSpeed> speeds;

	public KileMeterSpeedAdapter(List<KileMeterSpeed> speeds, LayoutInflater inflater) {
		this.speeds = speeds;
		this.inflater = inflater;
	}

	@Override
	public int getCount() {
		return speeds.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(position==0) {
			convertView = inflater.inflate(R.layout.view_run_speed_head, null,false);
			TextView date = (TextView) convertView.findViewById(R.id.date);
			date.setText(DateUtil.dateToString(DateUtil.FORMAT_YMD_HM, new Date()));
		} else {
			KileMeterSpeed speed = speeds.get(position);
			convertView = inflater.inflate(R.layout.item_run_speed, parent, false);
			((TextView)convertView.findViewById(R.id.kilemeter)).setText(speed.kilometer+"公里");
			((TextView)convertView.findViewById(R.id.time)).setText(speed.time);
		}
		return convertView;
	}

}
