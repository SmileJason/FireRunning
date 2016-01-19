package com.weijie.firerunning.adapter;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.weijie.firerunning.R;
import com.weijie.firerunning.bean.PlanBase;
import com.weijie.firerunning.bean.PlanDay;
import com.weijie.firerunning.bean.PlanWeek;
import com.weijie.firerunning.util.DateUtil;
import com.weijie.firerunning.view.RoundCornerProgressBar;
import com.weijie.firerunning.view.PinnedSectionListView.PinnedSectionListAdapter;

public class PlanDayAdapter extends BaseAdapter implements PinnedSectionListAdapter,SectionIndexer {

	private List<PlanBase> planBases;
	private int weeks;
	private LayoutInflater inflater;

	public PlanDayAdapter(List<PlanBase> planBases, int weeks, Context context) {
		this.planBases = planBases;
		this.weeks = weeks;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		if(planBases!=null) {
			return planBases.size();
		}
		return 0;
	}

	@Override
	public PlanBase getItem(int position) {
		return planBases.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		RoundCornerProgressBar progress = null;
		PlanBase item = getItem(position);
		if (item instanceof PlanWeek) {
			view = inflater.inflate(R.layout.item_plan_week, parent, false);

			PlanWeek pWeek = ((PlanWeek) item);

			TextView title = (TextView) view.findViewById(R.id.title);
			title.setText(pWeek.weekDesc);
			TextView week = (TextView) view.findViewById(R.id.week);
			week.setText("第 "+(pWeek.w_number+1)+"/"+weeks+" 周");
			progress = (RoundCornerProgressBar) view.findViewById(R.id.progress);
			double maxDistance = 0;
			double distance = 0;
			for(PlanDay day:pWeek.planDays) {
				maxDistance += day.maxDistance;
				distance += day.distance;
			}
			progress.setMax(Double.parseDouble(String.format("%.1f",maxDistance)),Double.parseDouble(String.format("%.1f",distance)));
			//progress.setMax(300,100);
		} else {
			view = inflater.inflate(R.layout.item_plan_day, parent, false);
			TextView distance = (TextView) view.findViewById(R.id.distance);
			TextView date = (TextView) view.findViewById(R.id.date);
			TextView day = (TextView) view.findViewById(R.id.day);
			double dt = ((PlanDay)item).maxDistance;
			if(dt>0) {
				distance.setText("去跑吧 "+String.format("%.1f",dt)+" 公里");
			} else if(dt==0f) {
				distance.setText("休息");
			} else if(dt<0f) {
				distance.setText("交叉训练");
			}
			Date d = new Date(Long.parseLong(((PlanDay)item).date));
			date.setText(DateUtil.dateToString("yyyy/MM/dd", d));
			day.setText("周"+getWeek(d.getDay()));
		}
		return view;
	}

	private String getWeek(int week) {
		switch (week) {
		case 0:
			return "日";
		case 1:
			return "一";
		case 2:
			return "二";
		case 3:
			return "三";
		case 4:
			return "四";
		case 5:
			return "五";
		case 6:
			return "六";
		default:
			return null;
		}
	}

	@Override
	public Object[] getSections() {
		return null;
	}

	@Override
	public int getPositionForSection(int section) {
		if (section >= planBases.size()) {
			section = planBases.size() - 1;
		}
		return planBases.get(section).listPosition;
	}

	@Override
	public int getSectionForPosition(int position) {
		if (position >= getCount()) {
			position = getCount() - 1;
		}
		return getItem(position).sectionPosition;
	}

	@Override
	public boolean isItemViewTypePinned(int viewType) {
		return viewType == PlanBase.SECTION;
	}

	@Override 
	public int getViewTypeCount() {
		return 2;
	}

	@Override 
	public int getItemViewType(int position) {
		return getItem(position).type;
	}
}
