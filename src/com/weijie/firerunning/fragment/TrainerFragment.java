package com.weijie.firerunning.fragment;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.weijie.firerunning.R;
import com.weijie.firerunning.activity.MainActivity;
import com.weijie.firerunning.activity.PlanActivity;
import com.weijie.firerunning.adapter.PlanAdapter;
import com.weijie.firerunning.bean.Plan;
import com.weijie.firerunning.bean.PlanDay;
import com.weijie.firerunning.bean.PlanWeek;
import com.weijie.firerunning.db.DataBaseUtil;
import com.weijie.firerunning.util.DateUtil;
import com.weijie.firerunning.util.ViewUtil;
import com.weijie.firerunning.view.PullToZoomListView;

/**
 * 教练
 * @author weijie
 *
 */
public class TrainerFragment extends Fragment implements OnClickListener, OnItemClickListener {

	private View createV,editV,view,cancle,submit,_10k,half,full;
	private PopupWindow createWindow,editWindow;
	private DatePicker datePicker;
	private EditText planName,updatePlan;
	private LayoutInflater inflater;
	private PullToZoomListView listView;
	private List<Plan> plans;
	private PlanAdapter adapter;

	private AlertDialog dialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_trainer, container, false);
		this.inflater = inflater;

		listView = (PullToZoomListView)view.findViewById(R.id.listview);

		getPlanDatas();
		listView.getHeaderView().setImageResource(R.drawable.title_pic);
		listView.getHeaderView().setScaleType(ImageView.ScaleType.CENTER_CROP);
		listView.setOnItemClickListener(this);
		return view;
	}

	public void getPlanDatas() {
		plans = DataBaseUtil.getInstance().getPlans();
		plans.add(0, new Plan());
		adapter = new PlanAdapter(getActivity(),plans,listener); 
		listView.setAdapter(adapter);
	}

	public void showCreatePlan() {
		if(createV==null) {
			createV = inflater.inflate(R.layout.popupwindow_create_plan, null);
			_10k = createV.findViewById(R.id._10k);
			half = createV.findViewById(R.id.half);
			half.setSelected(true);
			full = createV.findViewById(R.id.full);

			_10k.setOnClickListener(selectListener);
			half.setOnClickListener(selectListener);
			full.setOnClickListener(selectListener);

			submit = createV.findViewById(R.id.submit);
			cancle = createV.findViewById(R.id.cancle);
			submit.setOnClickListener(this);
			cancle.setOnClickListener(this);
			datePicker = (DatePicker) createV.findViewById(R.id.datePicker);
			planName = (EditText) createV.findViewById(R.id.plan_name);
		}
		if(createWindow==null || !createWindow.isShowing()) {
			createWindow = new PopupWindow(createV,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,true);
			createWindow.setFocusable(true);
			createWindow.setOutsideTouchable(true);
			createWindow.setAnimationStyle(R.style.PopupAnimation);
			createWindow.update();
			createWindow.setBackgroundDrawable(getResources().getDrawable(R.color.pop_bg));
			createWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.submit:
			if(planName.getText().toString().trim().equals("")) {
				//Toast.makeText(getActivity(), "请输入训练名称", Toast.LENGTH_SHORT).show();
				ViewUtil.getInstance().showToast("请输入训练名称");
			} else {
				//Date date = new Date();
				Date date = new Date(datePicker.getYear()-1900, datePicker.getMonth(), datePicker.getDayOfMonth());
				Date mDate = new Date();
				//Date mDate = new Date(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth()-1);
				long ms = date.getTime() - mDate.getTime();
				int days = (int) (ms/(1000*24*3600))+1;
				if(days>0) {
					Plan plan = new Plan();
					if(_10k.isSelected()) {
						plan.type = 0;
						if(days==56) {
							plan.weeks = 8;
							plan.maxDistance = 370;
							plan.startDate = DateUtil.dateToString(DateUtil.FORMAT_YMD_HM, new Date(Long.parseLong(dateAdd(mDate.getTime(),1))));
						} else if(days>56) {
							plan.weeks = 8;
							plan.maxDistance = 370;
							//Date start = new Date(mDate.getTime()+(days-56)*(1000*24*3600));
							Date start = new Date(Long.parseLong(dateAdd(mDate.getTime(),days-55)));
							plan.startDate = DateUtil.dateToString(DateUtil.FORMAT_YMD_HM, start);
						} else {
							//Toast.makeText(getActivity(), "剩余的时间不足够训练", Toast.LENGTH_SHORT).show();
							ViewUtil.getInstance().showToast("剩余的时间不足够训练");
							return;
						}
					} else if(half.isSelected()) {
						plan.type = 1;
						if(days==84) {
							plan.weeks = 12;
							plan.maxDistance = 700;
							plan.startDate = DateUtil.dateToString(DateUtil.FORMAT_YMD_HM, new Date(Long.parseLong(dateAdd(mDate.getTime(),1))));
						} else if(days>84) {
							plan.weeks = 12;
							plan.maxDistance = 700;
							//Date start = new Date(mDate.getTime()+(days-84)*(1000*24*3600));
							Date start = new Date(Long.parseLong(dateAdd(mDate.getTime(),days-83)));
							plan.startDate = DateUtil.dateToString(DateUtil.FORMAT_YMD_HM, start);
						} else {
							//Toast.makeText(getActivity(), "剩余的时间不足够训练", Toast.LENGTH_SHORT).show();
							ViewUtil.getInstance().showToast("剩余的时间不足够训练");
							return;
						}
					} else {
						plan.type = 2;
						//Toast.makeText(getActivity(), "暂时不支持全马训练计划！", Toast.LENGTH_SHORT).show();
						ViewUtil.getInstance().showToast("暂时不支持全马训练计划！");
						return;
					}

					plan.pId = date.getTime()+"";
					plan.title = planName.getText().toString().trim();
					plan.matchDate = DateUtil.dateToString(DateUtil.FORMAT_YMD_HM, date);
					//plan.weeks = days/7;
					//plan.maxDistance = 720;
					plan.distance = 0;
					plan.state = 0;
					DataBaseUtil.getInstance().insertPlan(plan);
					createWindow.dismiss();
					getPlanDatas();

				} else {
					//Toast.makeText(getActivity(), "剩余的时间不足够训练", Toast.LENGTH_SHORT).show();
					ViewUtil.getInstance().showToast("剩余的时间不足够训练");
				}
			}
			break;
		case R.id.cancle:
			createWindow.dismiss();
			break;
		}
	}

	private String dateAdd(Long time, int index) {
		Date date = new Date(time);
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(calendar.DATE, index);
		return calendar.getTimeInMillis()+"";
	}

	private OnClickListener selectListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if(v==_10k) {
				_10k.setSelected(true);
				half.setSelected(false);
				full.setSelected(false);
			} else if(v==half) {
				_10k.setSelected(false);
				half.setSelected(true);
				full.setSelected(false);
			} else if(v==full) {
				_10k.setSelected(false);
				half.setSelected(false);
				full.setSelected(true);
			}
		}
	};

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		if(position>=2) {
			Intent intent = new Intent(this.getActivity(),PlanActivity.class);
			intent.putExtra("pId", plans.get(position-1).pId);
			intent.putExtra("weeks", plans.get(position-1).weeks);
			intent.putExtra("type", plans.get(position-1).type);
			startActivityForResult(intent, MainActivity.PLAN_REQUEST);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode==MainActivity.PLAN_RESULT) {
			if(data!=null) {
				Object planDay = data.getSerializableExtra("day");
				Object planWeek = data.getSerializableExtra("week");
				if(planDay!=null && planWeek!=null) {
					Plan plan = null;
					for(Plan p:plans) {
						if(p.pId!=null && p.pId.equals(((PlanWeek)planWeek).pId)) {
							plan = p;
							break;
						}
					}
					if(plan!=null) {
						((MainActivity)getActivity()).setPlan(plan, (PlanWeek)planWeek, (PlanDay)planDay);
						((MainActivity)getActivity()).initRunView();
						((MainActivity)getActivity()).setItemClick(1);
						((MainActivity)getActivity()).invalidateMenu();
					}
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			String pId = (String) v.getTag();
			shawDialog(pId);
		}
	};

	private void shawDialog(String pId) {
		dialog = new AlertDialog.Builder(getActivity()).create();
		Window window = dialog.getWindow();  
		window.setGravity(Gravity.BOTTOM);  //此处可以设置dialog显示的位置  
		window.setWindowAnimations(R.style.PopupAnimation);  //添加动画  
		dialog.show();
		View dialogView = LayoutInflater.from(this.getActivity()).inflate(R.layout.popupwindow_edit_plan, null);
		dialogView.findViewById(R.id.cancle).setOnClickListener(dialogListener);

		View deletePlan = dialogView.findViewById(R.id.deletePlan);
		deletePlan.setTag(pId);
		deletePlan.setOnClickListener(dialogListener);

		View editPlan = dialogView.findViewById(R.id.editPlan);
		editPlan.setTag(pId);
		editPlan.setOnClickListener(dialogListener);
		//dialog.getWindow().setContentView(R.layout.popupwindow_update_icon);
		dialog.getWindow().setContentView(dialogView);

		getActivity().getWindow().getAttributes().width = WindowManager.LayoutParams.MATCH_PARENT;
		getActivity().getWindow().getAttributes().height = WindowManager.LayoutParams.WRAP_CONTENT;
	}


	private void showEditPlan(String pId) {
		if(editV==null) {
			editV = inflater.inflate(R.layout.popupwindow_update_plan, null);
			updatePlan = (EditText) editV.findViewById(R.id.updatePlan);
			submit = editV.findViewById(R.id.submit);
			cancle = editV.findViewById(R.id.cancle);
			submit.setOnClickListener(editDialogListener);
			cancle.setOnClickListener(editDialogListener);
		}
		if(editWindow==null || !editWindow.isShowing()) {
			if(plans!=null) {
				for(Plan plan:plans) {
					if(plan.pId!=null && plan.pId.equals(pId)) {
						updatePlan.setTag(pId);
						updatePlan.setText(plan.title);
						editWindow = new PopupWindow(editV,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,true);
						editWindow.setFocusable(true);
						editWindow.setOutsideTouchable(true);
						editWindow.setAnimationStyle(R.style.PopupAnimation2);
						editWindow.update();
						editWindow.setBackgroundDrawable(getResources().getDrawable(R.color.pop_bg));
						editWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
						break;
					}
				}
			}
			
		}

	}


	private OnClickListener dialogListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.deletePlan:
				v.setEnabled(false);
				String pId = (String) v.getTag();
				if(plans!=null) {
					for(Plan plan:plans) {
						if(plan.pId!=null && plan.pId.equals(pId)) {
							boolean delete = DataBaseUtil.getInstance().deletePlan(pId);
							if(delete) {
								getPlanDatas();
								break;
							}
						}
					}
				}
				v.setEnabled(true);
				dialog.dismiss();
				break;
			case R.id.editPlan:
				v.setEnabled(false);
				String pId2 = (String) v.getTag();
				v.setEnabled(true);
				dialog.dismiss();
				showEditPlan(pId2);
				break;
			case R.id.cancle:
				dialog.dismiss();
				break;
			}
		}
	};

	private OnClickListener editDialogListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.submit:
				String pId = (String) updatePlan.getTag();
				String title = updatePlan.getText().toString();
				if(!title.trim().equals("")) {
					boolean update = DataBaseUtil.getInstance().editPlan(pId,title);
					if(update) {
						editWindow.dismiss();
						getPlanDatas();
						break;
					}
				} else {
					//Toast.makeText(getActivity(), "请输入训练名称", Toast.LENGTH_SHORT).show();
					ViewUtil.getInstance().showToast("请输入训练名称");
				}
				break;
			case R.id.cancle:
				editWindow.dismiss();
				break;
			}
		}
	};

}
