package com.weijie.firerunning.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import cn.bmob.v3.Bmob;

import com.weijie.firerunning.Conf;
import com.weijie.firerunning.R;
import com.weijie.firerunning.UserManager;
import com.weijie.firerunning.adapter.MenuAdapter;
import com.weijie.firerunning.bean.Plan;
import com.weijie.firerunning.bean.PlanDay;
import com.weijie.firerunning.bean.PlanWeek;
import com.weijie.firerunning.bean.User;
import com.weijie.firerunning.fragment.AboutFragment;
import com.weijie.firerunning.fragment.DiscussFragment;
import com.weijie.firerunning.fragment.ErrorFragment;
import com.weijie.firerunning.fragment.HistoryFragment;
import com.weijie.firerunning.fragment.RunFragment;
import com.weijie.firerunning.fragment.TrainerFragment;

public class MainActivity extends FragmentActivity implements OnItemClickListener {

	public static final int PLAN_REQUEST = 10000;
	public static final int PLAN_RESULT = 10001;
	
	private User user;

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	//private CharSequence mTitle;
	private Fragment[] fs;
	private MenuAdapter adapter;
	private FragmentManager manager;
	private int position = 1;
	private ActionBar actionBar;
	
	private PlanDay planDay;
	private PlanWeek planWeek;
	private Plan plan;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 初始化 Bmob SDK
        // 使用时请将第二个参数Application ID替换成你在Bmob服务器端创建的Application ID
		Bmob.initialize(this, Conf.ApplicationID);
		
		setContentView(R.layout.activity_main);
		manager = getSupportFragmentManager();
		//mTitle = mDrawerTitle = getTitle();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setLogo(R.drawable.logo);
		actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.titlebar_background));
		actionBar.setDisplayHomeAsUpEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(
				this,                  
				mDrawerLayout,         
				R.drawable.drawer,  
				R.string.drawer_open,  
				R.string.drawer_close  
				) {
			public void onDrawerClosed(View view) {
				//getActionBar().setTitle(mTitle);
				invalidateOptionsMenu();
			}
			public void onDrawerOpened(View drawerView) {
				//getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		user = UserManager.getInstance().getUser();
		View head = getLayoutInflater().inflate(R.layout.view_left_menu_head,null);
		head.findViewById(R.id.regist_login).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(user==null) {
					//跳到登录或者注册界面
					startActivity(new Intent(MainActivity.this,LoginActivity.class));
				}
			}
		});
		TextView username = (TextView) head.findViewById(R.id.username);
		if(user==null) {
			username.setText("注册/登录");
		} else {
			username.setText(user.username);
		}
		mDrawerList.addHeaderView(head);

		List<com.weijie.firerunning.bean.MenuItem> menus = new ArrayList<com.weijie.firerunning.bean.MenuItem>();
		menus.add(new com.weijie.firerunning.bean.MenuItem("跑步", R.drawable.ic_running));
		menus.add(new com.weijie.firerunning.bean.MenuItem("历史", R.drawable.ic_history));
		menus.add(new com.weijie.firerunning.bean.MenuItem("教练", R.drawable.ic_trainer));
		menus.add(new com.weijie.firerunning.bean.MenuItem("讨论", R.drawable.ic_discuss));
		menus.add(new com.weijie.firerunning.bean.MenuItem("关于", R.drawable.ic_about));
		menus.add(new com.weijie.firerunning.bean.MenuItem("BUG报告", R.drawable.ic_error_msg));
		adapter = new MenuAdapter(menus,this);
		mDrawerList.setAdapter(adapter);
		mDrawerList.setOnItemClickListener(this);
		fs = new Fragment[6];
		fs[0] = new RunFragment();
		manager.beginTransaction().add(R.id.content,fs[0]).commit();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		switch (position) {
		case 1:
			inflater.inflate(R.menu.share_menu, menu);
			menu.findItem(R.id.share).setVisible(!drawerOpen);
			break;
		case 2:
			inflater.inflate(R.menu.account_menu, menu);
			menu.findItem(R.id.account).setVisible(!drawerOpen);
			break;
		case 3:
			inflater.inflate(R.menu.add_menu, menu);
			menu.findItem(R.id.add).setVisible(!drawerOpen);
			break;
		case 4:
			inflater.inflate(R.menu.discuss_menu, menu);
			menu.findItem(R.id.discuss).setVisible(!drawerOpen);
			break;
		case 5:
			break;
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		switch(item.getItemId()) {
		case R.id.account:
			if(position==2 && fs[1]!=null) {
				//startActivity(new Intent(this,ShareActivity.class));
				((HistoryFragment)fs[1]).shareHistory();
			} else if(position==1 && fs[0]!=null) {
				
			}
			return true;
		case R.id.add:
			//startActivity(new Intent(this,PlanActivity.class));
			((TrainerFragment)fs[2]).showCreatePlan();
			return true;
		case R.id.share:
			return true;
		case R.id.discuss:
			//startActivity(new Intent(this,DiscussActivity.class));
			startActivityForResult(new Intent(this,DiscussActivity.class), DiscussFragment.DISCUSS_REQUEST);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	public void initRunView() {
		if(fs[0]!=null) {
			((RunFragment)fs[0]).initView();
		}
	}

	public void setItemClick(int position) {
		if(position>=1) {
			mDrawerLayout.closeDrawers();
			if(this.position!=position) {
				switch (position) {
				case 1:
					actionBar.setTitle("FiireRunning");
					break;
				case 2:
					actionBar.setTitle("历史");
					break;
				case 3:
					actionBar.setTitle("教练");
					break;
				case 4:
					actionBar.setTitle("讨论");
					break;
				case 5:
					actionBar.setTitle("关于");
					break;
				case 6:
					actionBar.setTitle("BUG报告");
					break;
				}
				showFragment(position-1);
				this.position = position;
			}
		}
	}
	
	public void invalidateMenu() {
		invalidateOptionsMenu();
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		setItemClick(position);
	}

	private void showFragment(int index) {
		FragmentTransaction ft = manager.beginTransaction();
		for(int i=0;i<fs.length;i++) {
			if(i==index) {
				if(fs[i]!=null) {
					ft.show(fs[i]);
				} else {
					fs[i] = createFragment(i);
					ft.add(R.id.content,fs[i]);
				}
			} else {
				if(fs[i]!=null) {
					ft.hide(fs[i]);
				}
			}
		}
		ft.commit();
	}

	private Fragment createFragment(int index) {
		switch (index) {
		case 0:
			return new RunFragment();
		case 1:
			return new HistoryFragment();
		case 2:
			return new TrainerFragment();
		case 3:
			return new DiscussFragment();
		case 4:
			return new AboutFragment();
		case 5:
			return new ErrorFragment();
		default:
			return new RunFragment();
		}
	}
	
	public void setPlan(Plan plan,PlanWeek week,PlanDay planDay) {
		this.plan = plan;
		this.planWeek = week;
		this.planDay = planDay;
		
	}
	
	public PlanDay getPlanDay() {
		return planDay;
	}
	
	public PlanWeek getPlanWeek() {
		return planWeek;
	}
	
	public Plan getPlan() {
		return plan;
	}
	
	public void updatePlan() {
		((TrainerFragment)fs[2]).getPlanDatas();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
