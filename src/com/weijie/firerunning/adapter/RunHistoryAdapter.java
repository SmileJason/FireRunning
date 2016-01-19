package com.weijie.firerunning.adapter;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.weijie.firerunning.R;
import com.weijie.firerunning.bean.RunRecord;

public class RunHistoryAdapter extends BaseAdapter{

	private LayoutInflater inflater;
	private List<RunRecord> records;
	
	public RunHistoryAdapter(List<RunRecord> records, LayoutInflater inflater) {
		this.records = records;
		this.inflater = inflater;
	}

	@Override
	public int getCount() {
		return records.size();
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
    public int getItemViewType(int position) {
		RunRecord record = records.get(position);
        int type = 2;
        if(position==0) {
        	type = 0;
        } else if(record.id==null){
        	type = 1;
        }
        return type;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		RunRecord record = records.get(position);
		int type = getItemViewType(position);
		if(type == 0) {
			convertView = inflater.inflate(R.layout.view_history_list_head, parent, false);
			((TextView)convertView.findViewById(R.id.month)).setText(record.ym+"跑步记录");
		} else if(type == 1) {
			convertView = inflater.inflate(R.layout.view_history_list_center, parent, false);
			((TextView)convertView.findViewById(R.id.month)).setText(record.ym+"跑步记录");
		} else {
			convertView = inflater.inflate(R.layout.item_run_record_history, parent, false);
			((TextView)convertView.findViewById(R.id.distence)).setText(String .format("%.2f",record.distance/1000)+"公里");
			((TextView)convertView.findViewById(R.id.date)).setText(record.startTime);
		}
		return convertView;
	}
	
}
