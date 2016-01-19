package com.weijie.firerunning.activity;

import java.util.Date;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.weijie.firerunning.R;
import com.weijie.firerunning.bean.PlanDay;
import com.weijie.firerunning.db.DataBaseUtil;
import com.weijie.firerunning.util.DateUtil;

public class PlanDescActivity extends Activity {

	private ActionBar actionBar;
	private ListView listview;
	private TextView planDesc,logo,date,distance;
	private Button start;
	private PlanDay day;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_plan_desc);
		
		day = (PlanDay) getIntent().getSerializableExtra("plan");
		
		actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setLogo(R.drawable.logo);
		actionBar.setTitle("训练详情");
		actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.titlebar_background));
		actionBar.setDisplayHomeAsUpEnabled(true);

		listview = (ListView) findViewById(R.id.listview);
		planDesc = (TextView) findViewById(R.id.planDesc);
		logo = (TextView) findViewById(R.id.logo);
		date = (TextView) findViewById(R.id.date);
		distance = (TextView) findViewById(R.id.distance);
		start = (Button) findViewById(R.id.start);
		// && day.date.equals(DateUtil.dateToString(DateUtil.FORMAT_YMD_HM, new Date()))
		Date startDate = new Date(Long.parseLong(day.date));
		if(day.maxDistance>0 && day.distance<day.maxDistance && DateUtil.dateToString(DateUtil.FORMAT_YMD_HM,startDate).equals(DateUtil.dateToString(DateUtil.FORMAT_YMD_HM, new Date()))) {
			start.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.putExtra("day", day);
					setResult(MainActivity.PLAN_RESULT,intent);
					finish();
				}
			});
		} else {
			start.setVisibility(View.GONE);
		}
		int type = getIntent().getIntExtra("type", 0);
		switch (type) {
		case 0:
			logo.setBackgroundResource(R.drawable.coach_shield_10k);
			logo.setText("10K");
			break;
		case 1:
			logo.setBackgroundResource(R.drawable.coach_shield_half_marathon);
			logo.setText("21.1K");
			break;
		case 2:
			logo.setBackgroundResource(R.drawable.coach_shield_marathon);
			logo.setText("42.195K");
			break;
		}
		
		int wNum = getIntent().getIntExtra("week", 1);
		date.setText("第"+wNum+"周/第"+(day.d_number+1)+"天");
		
		if(day.maxDistance>0) {
			distance.setText(String.format("%.1f",day.maxDistance)+"公里");
		} else if(day.maxDistance==0d) {
			distance.setText("休息");
		} else if(day.maxDistance<0) {
			distance.setText("交叉训练");
		}
		
		planDesc.setText(day.p_desc);
		listview.setAdapter(new PlanDescListAdapter(day.p_desc_list.split(DataBaseUtil.INTERVAL)));
	}

	
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        case android.R.id.home:
        	finish();
        default:
            return super.onOptionsItemSelected(item);
        }
    }
	
	private class PlanDescListAdapter extends BaseAdapter {

		private String[] pDescs;
		private LayoutInflater inflater;
		
		public PlanDescListAdapter(String[] pDescs) {
			this.pDescs = pDescs;
			inflater = LayoutInflater.from(PlanDescActivity.this);
		}

		@Override
		public int getCount() {
			return pDescs.length;
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
			if(convertView==null) {
				convertView = inflater.inflate(R.layout.item_plan_desc, parent,false);
			}
			TextView pDesc = (TextView) convertView.findViewById(R.id.pDesc);
			pDesc.setText(pDescs[position]);
			return convertView;
		}
	}
}
