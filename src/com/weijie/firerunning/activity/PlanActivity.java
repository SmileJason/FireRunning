package com.weijie.firerunning.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.weijie.firerunning.R;
import com.weijie.firerunning.adapter.PlanDayAdapter;
import com.weijie.firerunning.bean.PlanBase;
import com.weijie.firerunning.bean.PlanDay;
import com.weijie.firerunning.bean.PlanWeek;
import com.weijie.firerunning.db.DataBaseUtil;
import com.weijie.firerunning.view.PinnedSectionListView;

public class PlanActivity extends Activity implements OnItemClickListener {

	private ActionBar actionBar;
	private PinnedSectionListView listview;
	private String pId;
	private int weeks;
	private List<PlanBase> planBases;
	private int type = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_plan);
		
		pId = getIntent().getStringExtra("pId");
		weeks = getIntent().getIntExtra("weeks", 8);
		type = getIntent().getIntExtra("type", 0);
		
		actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setLogo(R.drawable.logo);
		actionBar.setTitle("教练");
		actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.titlebar_background));
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		listview = (PinnedSectionListView) findViewById(R.id.listview);
		listview.setFastScrollEnabled(false);
		/*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			listview.setFastScrollAlwaysVisible(true);
		}*/
		//listview.setAdapter(new PlanAdapter(this, getData()));
		listview.setAdapter(new PlanDayAdapter(getPlans(),weeks,this));
		listview.setOnItemClickListener(this);
	}
	
	private List<PlanBase> getPlans() {
		List<PlanWeek> planWeeks = DataBaseUtil.getInstance().getPlanWeeks(pId);
		planBases = new ArrayList<PlanBase>();
		for(PlanWeek w:planWeeks) {
			planBases.add(w);
			planBases.addAll(w.planDays);
		}
		return planBases;
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

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		PlanBase planBase = planBases.get(position);
		if(planBase.type==PlanBase.ITEM) {
			PlanDay day = (PlanDay)planBase;
			Intent intent = new Intent(this,PlanDescActivity.class);
			intent.putExtra("plan", day);
			intent.putExtra("type", type);
			for(int i=position;i>position-7 && i>=0;i--) {
				PlanBase plan = planBases.get(i);
				if(plan.type == PlanBase.SECTION) {
					PlanWeek planWeek = (PlanWeek)plan;
					intent.putExtra("week", planWeek.w_number+1);
					startActivityForResult(intent, MainActivity.PLAN_REQUEST);
					break;
				}
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode==MainActivity.PLAN_RESULT) {
			PlanDay day = (PlanDay) data.getSerializableExtra("day");
			PlanWeek week = null;
			for(PlanBase planBase:planBases) {
				if(planBase.type==PlanBase.SECTION) {
					week = (PlanWeek)planBase;
					if(week.pwId.equals(day.p_w_Id)) {
						break;
					} else {
						week = null;
					}
				}
			}
			if(week!=null) {
				data.putExtra("week", week);
				setResult(MainActivity.PLAN_RESULT,data);
				finish();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
}
