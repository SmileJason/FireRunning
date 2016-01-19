package com.weijie.firerunning.adapter;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.weijie.firerunning.R;
import com.weijie.firerunning.bean.Plan;
import com.weijie.firerunning.util.DateUtil;
import com.weijie.firerunning.view.RoundCornerProgressBar;

public class PlanAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private List<Plan> plans;
	private Calendar cal;
	private OnClickListener listener;

	public PlanAdapter(Context context, List<Plan> plans, OnClickListener listener) {
		inflater = LayoutInflater.from(context);
		this.plans = plans;
		this.listener = listener;
		cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
	}

	@Override
	public int getCount() {
		return plans.size();
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
	public View getView(int position, View view, ViewGroup group) {
		if(getItemViewType(position)==0) {
			if(view == null) {
				view = inflater.inflate(R.layout.item_plan_title, group, false);
			}
		} else {
			Plan plan = plans.get(position);
			if(view == null) {
				view = inflater.inflate(R.layout.item_plan, group, false);
				TextView name = (TextView) view.findViewById(R.id.name);
				TextView title = (TextView)view.findViewById(R.id.title);
				TextView logo = (TextView) view.findViewById(R.id.logo);
				View setting = view.findViewById(R.id.setting);
				setting.setTag(plan.pId);
				setting.setOnClickListener(listener);
				name.setText(plan.title);
				
				String start = plan.startDate;
				long startDate = DateUtil.stringToDate(DateUtil.FORMAT_YMD_HM, start).getTime();
				long nowDate = new Date().getTime();
				if(nowDate<startDate) {
					title.setText("第"+" /"+plan.weeks+" 周");
				} else {
					int days = (int)(nowDate-startDate)/(1000*24*3600)+1;
					int indexWeek;
					if(days%7==0) {
						indexWeek = days/7;
						title.setText("第"+indexWeek+" /"+plan.weeks+" 周");
					} else {
						indexWeek = days/7 + 1;
						title.setText("第"+indexWeek+" /"+plan.weeks+" 周");
						/*if(days>1) {
							indexWeek = days/7 + 1;
							title.setText("第"+indexWeek+" /"+plan.weeks+" 周");
						} else {
							title.setText("第"+" /"+plan.weeks+" 周");
						}*/
					}
				}
				switch (plan.type) {
				case 0:
					//10公里
					logo.setBackgroundResource(R.drawable.coach_shield_10k_completed);
					logo.setText("10K");
					break;
				case 1:
					//半程
					logo.setBackgroundResource(R.drawable.coach_shield_half_marathon_completed);
					logo.setText("半程");
					break;
				case 2:
					//全程
					logo.setBackgroundResource(R.drawable.coach_shield_marathon_completed);
					logo.setText("全程");
					break;
				}
				RoundCornerProgressBar progress = (RoundCornerProgressBar) view.findViewById(R.id.progress);
				//progress.setMax(plan.maxDistance,plan.distance);
				progress.setMax(Double.parseDouble(String.format("%.1f",plan.maxDistance)),Double.parseDouble(String.format("%.1f",plan.distance)));
				//progress.setMax(Double.parseDouble(String.format("%.1f",plan.maxDistance)),300);
			}
		}

		return view;
	}

	@Override
	public int getItemViewType(int position) {
		if(position==0) {
			return 0;
		} else {
			return 1;
		}
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

}
