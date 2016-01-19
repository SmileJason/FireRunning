package com.weijie.firerunning.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.weijie.firerunning.R;

public class LoginActivity extends Activity implements OnClickListener {

	private ActionBar actionBar;
	
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
		
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.login:
			
			break;
		case R.id.regist:
			startActivity(new Intent(this,RegistActivity.class));
			break;
		}
	}

}
