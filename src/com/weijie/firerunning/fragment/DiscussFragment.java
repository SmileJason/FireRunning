package com.weijie.firerunning.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.listener.FindListener;

import com.weijie.firerunning.R;
import com.weijie.firerunning.activity.CommentActivity;
import com.weijie.firerunning.adapter.DiscussAdapter;
import com.weijie.firerunning.bean.Discuss;
import com.weijie.firerunning.util.ViewUtil;

/**
 * 讨论
 * @author weijie
 *
 */
public class DiscussFragment extends Fragment implements OnItemClickListener, OnRefreshListener {

	public final static int DISCUSS_REQUEST = 10002;
	public final static int DISCUSS_RESULT = 10003;
	//private TypeToken<ArrayList<Discuss>> typeToken = new TypeToken<ArrayList<Discuss>>() {};
	private final int LIMIT = 5;
	private List<Discuss> discusses;
	private DiscussAdapter adapter;
	//private AsyncCustomEndpoints ace;
	private Handler mHandler = new Handler();
	private ListView listView;
	private SwipeRefreshLayout mSwipeRefreshWidget;
	private ProgressBar loadProgress;
	private TextView loadData;

	private final Runnable mRefreshDone = new Runnable() {
		@Override
		public void run() {
			//index = 1;
			getData(true,false);
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_discuss, container,false);
		mSwipeRefreshWidget = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_widget);
		mSwipeRefreshWidget.setColorSchemeResources(R.color.color1, R.color.color2, R.color.color3,
				R.color.color4);

		listView = (ListView) view.findViewById(R.id.listview);

		View footer = inflater.inflate(R.layout.view_footer, listView, false);
		loadProgress = (ProgressBar) footer.findViewById(R.id.loadProgress);
		loadData = (TextView) footer.findViewById(R.id.loadData);
		footer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if(discusses!=null && discusses.size()>0) {
					loadData.setText("正在加载，请稍等");
					loadProgress.setVisibility(View.VISIBLE);
					//Discuss discuss = discusses.get(discusses.size()-1);
					//getData(false,true,discuss.dateTime);
					getData(false,true);
				}
			}
		});
		listView.addFooterView(footer);

		listView.setOnItemClickListener(this);
		discusses = new ArrayList<Discuss>();
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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == DISCUSS_REQUEST && resultCode == DISCUSS_RESULT) {

		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void refresh() {
		mHandler.removeCallbacks(mRefreshDone);
		mHandler.post(mRefreshDone);
	}

	private void getData(final boolean refresh,final boolean load,String...dateTime){

		BmobQuery<Discuss> query = new BmobQuery<Discuss>();
		if(discusses!=null && discusses.size()>0) {
			String sDate;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
			Date date  = null;
			if(refresh) {
				sDate = discusses.get(0).getCreatedAt();
				try {
					date = sdf.parse(sDate);
					query.addWhereGreaterThan("createdAt",new BmobDate(date));
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if(load) {
				sDate = discusses.get(discusses.size()-1).getCreatedAt();
				try {
					date = sdf.parse(sDate);
					query.addWhereLessThan("createdAt",new BmobDate(date));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		query.order("-createdAt");
		query.setLimit(LIMIT);
		query.include("user");
		query.findObjects(this.getActivity(), new FindListener<Discuss>() {
			@Override
			public void onSuccess(List<Discuss> object) {
				if(object!=null && object.size()>0) {
					if(refresh) {
						if(discusses.size()==0) {
							discusses.addAll(0, object);
						} else {
							object.remove(object.size()-1);
							if(object.size()>0) {
								discusses.addAll(0, object);
							}
						}
					} else {
						discusses.addAll(object);
					}
					if(adapter==null) {
						adapter = new DiscussAdapter(discusses, getActivity());
						listView.setAdapter(adapter);
					} else {
						adapter.notifyDataSetChanged();
					}
				}

				if(refresh) {
					mSwipeRefreshWidget.setRefreshing(false);
				} else if(load) {
					loadData.setText("点击加载更多数据");
					loadProgress.setVisibility(View.GONE);
				}
			}
			@Override
			public void onError(int code, String msg) {
				ViewUtil.getInstance().showToast(msg);
				if(refresh) {
					mSwipeRefreshWidget.setRefreshing(false);
				} else if(load) {
					loadData.setText("点击加载更多数据");
					loadProgress.setVisibility(View.GONE);
				}
			}
		});
	}

	//刷新消息，每次刷新之后重置10条记录
	@Override
	public void onRefresh() {
		refresh();
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		Discuss discuss = discusses.get(position);
		Intent intent = new Intent(this.getActivity(),CommentActivity.class);
		intent.putExtra("discuss", discuss);
		startActivity(intent);
	}

}
