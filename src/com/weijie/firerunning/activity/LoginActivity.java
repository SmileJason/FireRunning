package com.weijie.firerunning.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;

import com.weijie.firerunning.R;
import com.weijie.firerunning.UserManager;
import com.weijie.firerunning.bean.User;
import com.weijie.firerunning.util.ViewUtil;
import com.weijie.firerunning.view.InputView;

public class LoginActivity extends Activity implements OnClickListener {

	private ActionBar actionBar;
	private InputView username,password;
	private View loading;
	
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
		
		findViewById(R.id.forgetpsw).setOnClickListener(this);
		findViewById(R.id.regist).setOnClickListener(this);
		findViewById(R.id.login).setOnClickListener(this);
		loading = findViewById(R.id.loading);
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
				loading.setVisibility(View.VISIBLE);
				BmobUser.loginByAccount(this, username.getText(), password.getText(), new LogInListener<User>() {
		            @Override
		            public void done(User user, BmobException e) {
		                if(user!=null){
		                	loading.setVisibility(View.GONE);
		                	ViewUtil.getInstance().showToast("登录成功");
		                	user = BmobUser.getCurrentUser(LoginActivity.this,User.class);
		    		    	UserManager.getInstance().setUser(user);
		    		    	setResult(MainActivity.LOGIN_RESULT);
		    		    	finish();
		                } else {
		                	loading.setVisibility(View.GONE);
		                	ViewUtil.getInstance().showToast("登录失败:"+e.getMessage());
		                }
		            }
		        });
			}
			break;
		case R.id.regist:
			startActivityForResult(new Intent(this,RegistActivity.class),MainActivity.LOGIN_REQUEST);
			break;
		case R.id.forgetpsw:
			startActivityForResult(new Intent(this,ResetPasswordActivity.class),MainActivity.LOGIN_REQUEST);
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

	@Override
	protected void onActivityResult(int request, int result, Intent data) {
		if(request==MainActivity.LOGIN_REQUEST && result==MainActivity.LOGIN_RESULT) {
			setResult(MainActivity.LOGIN_RESULT);
			finish();
		}
		super.onActivityResult(request, result, data);
	}

}
