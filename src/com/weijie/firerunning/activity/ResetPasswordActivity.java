package com.weijie.firerunning.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.RequestSMSCodeListener;
import cn.bmob.v3.listener.ResetPasswordByCodeListener;

import com.weijie.firerunning.R;
import com.weijie.firerunning.util.Countdown;
import com.weijie.firerunning.util.RegexUtils;
import com.weijie.firerunning.util.ViewUtil;
import com.weijie.firerunning.view.InputView;

public class ResetPasswordActivity extends Activity implements OnClickListener {

	static final String GET_VERCODE = "重发验证码 ";
	
	private View loading;
	private TextView loadMsg;
	private InputView phonenumber,password1,againpassword1,verifiedCode;
	private Button getVerifiedCode;
	private Countdown mCountdown;
	
	Handler countdownHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			getVerifiedCode.setEnabled(msg.what == 0);
			if (msg.what == 0) {
				getVerifiedCode.setEnabled(true);
				getVerifiedCode.setText(GET_VERCODE);
			} else {
				getVerifiedCode.setText(GET_VERCODE + msg.what);
			}
			return true;
		}
	});
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reset_psw);
		
		initView();
	}
	
	private void initView() {
		loading = findViewById(R.id.loading);
		loadMsg = (TextView) findViewById(R.id.loadMsg);
		phonenumber = (InputView) findViewById(R.id.phonenumber);
		verifiedCode = (InputView) findViewById(R.id.verifiedCode);
		password1 = (InputView) findViewById(R.id.password1);
		againpassword1 = (InputView) findViewById(R.id.againpassword1);
		findViewById(R.id.reset).setOnClickListener(this);
		getVerifiedCode = (Button) findViewById(R.id.getVerifiedCode);
		getVerifiedCode.setOnClickListener(this);
		
		mCountdown = new Countdown(countdownHandler,60);
		//mCountdown.start();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.reset:
			if(password1.getText().toString().trim().equals("")) {
				password1.setError("请输入密码");
				return;
			} else if(againpassword1.getText().toString().trim().equals("")) {
				againpassword1.setError("请再次输入密码");
				return;
			} else if(!password1.getText().toString().trim().equals(againpassword1.getText().toString().trim())) {
				againpassword1.setError("两次输入的密码不一致，请重新输入");
				return;
			} else if(!RegexUtils.isPassword(password1.getText().toString().trim())) {
				password1.setError("输入密码长度 (6-18位)");
				return;
			} else if(phonenumber.getText().toString().trim().equals("")) {
				phonenumber.setError("请输入手机号码");
				return;
			} else if(verifiedCode.getText().toString().trim().equals("")) {
				verifiedCode.setError("请输入验证码");
				return;
			}
			loadMsg.setText("正在重设密码！请稍等......");
			loading.setVisibility(View.VISIBLE);
			BmobUser.resetPasswordBySMSCode(this, verifiedCode.getText().toString().trim() ,password1.getText().trim().toString(), new ResetPasswordByCodeListener() {
			    @Override
			    public void done(BmobException ex) {
			    	loading.setVisibility(View.GONE);
			        if(ex==null){
			        	ViewUtil.getInstance().showToast("密码重置成功");
			        	finish();
			        }else{
			        	ViewUtil.getInstance().showToast("重置失败：code ="+ex.getErrorCode()+",msg = "+ex.getLocalizedMessage());
			        }
			    }
			});
			break;
		case R.id.getVerifiedCode:
			if(phonenumber.getText().toString().trim().equals("")) {
				phonenumber.setError("请输入手机号码");
				return;
			} else if(!RegexUtils.isHandset(phonenumber.getText().toString().trim())) {
				phonenumber.setError("请输入正确的手机号码");
				return;
			} else {
				loadMsg.setText("正在获取验证码！请稍等......");
				loading.setVisibility(View.VISIBLE);
				BmobSMS.requestSMSCode(this, phonenumber.getText().toString().trim(),"FireCode", new RequestSMSCodeListener() {
					@Override
					public void done(Integer smsId, BmobException ex) {
						loading.setVisibility(View.GONE);
						if(ex==null){//验证码发送成功
							getVerifiedCode.setEnabled(false);
							mCountdown.start();
							ViewUtil.getInstance().showToast("信息已发送成功！");
						} else {
							ViewUtil.getInstance().showToast("验证码发送失败"+ex.getMessage());
						}
					}
				});
			}
			break;
		}
	}
	
	@Override
	protected void onDestroy() {
		if(mCountdown != null){
			mCountdown.stop();
		}
		super.onDestroy();
	}
	
}
