package com.weijie.firerunning.activity;

import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

import com.weijie.firerunning.R;
import com.weijie.firerunning.bean.User;
import com.weijie.firerunning.view.InputView;

public class LoginActivity extends Activity implements OnClickListener {

	private ActionBar actionBar;
	private InputView username,password;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setLogo(R.drawable.logo);
		actionBar.setTitle("用户登录");
		actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.titlebar_background));
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		findViewById(R.id.regist).setOnClickListener(this);
		findViewById(R.id.login).setOnClickListener(this);
		username = (InputView) findViewById(R.id.username);
		password = (InputView) findViewById(R.id.password);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.login:
			if(username.getText().equals("")) {
				username.setError("用户名不能为空");
			} else if(password.getText().equals("")) {
				password.setError("密码不能为空");
			} else {
				BmobQuery<User> query = new BmobQuery<User>();
				query.addWhereEqualTo("username", username.getText());
				query.addWhereEqualTo("password", password.getText());
				query.findObjects(this, new FindListener<User>() {
					@Override
					public void onSuccess(List<User> users) {
					}
					@Override
					public void onError(int code, String msg) {
					}
				});
			}
			break;
		case R.id.regist:
			startActivity(new Intent(this,RegistActivity.class));
			break;
		}
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

}
