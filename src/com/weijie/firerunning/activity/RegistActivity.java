package com.weijie.firerunning.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.Keyframe;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.RequestSMSCodeListener;
import cn.bmob.v3.listener.SaveListener;

import com.weijie.firerunning.R;
import com.weijie.firerunning.UserManager;
import com.weijie.firerunning.bean.User;
import com.weijie.firerunning.util.ViewUtil;
import com.weijie.firerunning.view.InputView;

public class RegistActivity extends Activity implements OnClickListener {

	private ActionBar actionBar;
	private View byPhone,byEmail,lineGuide,phoneIndex,emailIndex,register;

	private RelativeLayout content;
	private LayoutTransition mTransitioner;
	private InputView username1,username2,password1,againpassword1,password2,againpassword2,phonenumber,email,verifiedCode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_regist);

		actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setLogo(R.drawable.logo);
		actionBar.setTitle("用户注册");
		actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.titlebar_background));
		actionBar.setDisplayHomeAsUpEnabled(true);

		content = (RelativeLayout) findViewById(R.id.content);
		resetTransition();    

		username1 = (InputView) findViewById(R.id.username1);
		username2 = (InputView) findViewById(R.id.username2);
		password1 = (InputView) findViewById(R.id.password1);
		password2 = (InputView) findViewById(R.id.password2);
		againpassword1 = (InputView) findViewById(R.id.againpassword1);
		againpassword2 = (InputView) findViewById(R.id.againpassword2);
		email = (InputView) findViewById(R.id.email);
		phonenumber = (InputView) findViewById(R.id.phonenumber);
		verifiedCode = (InputView) findViewById(R.id.verifiedCode);

		register = findViewById(R.id.register);
		byPhone = findViewById(R.id.byPhone);
		byEmail = findViewById(R.id.byEmail);
		lineGuide = findViewById(R.id.lineGuide);
		phoneIndex = findViewById(R.id.phoneIndex);
		emailIndex = findViewById(R.id.emailIndex);
		byPhone.setOnClickListener(this);
		byEmail.setOnClickListener(this);
		register.setOnClickListener(this);
		findViewById(R.id.getVerifiedCode).setOnClickListener(this);
		byPhone.setSelected(true);
		WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width/2, (int)dp2px(2));
		lineGuide.setLayoutParams(params);
	}

	private void resetTransition() {  
		mTransitioner = new LayoutTransition();  
		mTransitioner.setDuration(400); 
		mTransitioner.setStagger(LayoutTransition.CHANGE_APPEARING, 30);
		mTransitioner.setStagger(LayoutTransition.CHANGE_DISAPPEARING, 30);
		setupCustomAnimations();
		content.setLayoutTransition(mTransitioner); 
	} 

	// 生成自定义动画
	private void setupCustomAnimations() {
		// 动画：CHANGE_APPEARING
		// Changing while Adding
		PropertyValuesHolder pvhLeft = PropertyValuesHolder.ofInt("left", 0, 1);
		PropertyValuesHolder pvhTop = PropertyValuesHolder.ofInt("top", 0, 1);
		PropertyValuesHolder pvhRight = PropertyValuesHolder.ofInt("right", 0,
				1);
		PropertyValuesHolder pvhBottom = PropertyValuesHolder.ofInt("bottom",
				0, 1);
		PropertyValuesHolder pvhScaleX = PropertyValuesHolder.ofFloat("scaleX",
				1f, 0f, 1f);
		PropertyValuesHolder pvhScaleY = PropertyValuesHolder.ofFloat("scaleY",
				1f, 0f, 1f);

		final ObjectAnimator changeIn = ObjectAnimator.ofPropertyValuesHolder(
				this, pvhLeft, pvhTop, pvhRight, pvhBottom, pvhScaleX,
				pvhScaleY).setDuration(
						mTransitioner.getDuration(LayoutTransition.CHANGE_APPEARING));
		mTransitioner.setAnimator(LayoutTransition.CHANGE_APPEARING, changeIn);
		changeIn.addListener(new AnimatorListenerAdapter() {
			public void onAnimationEnd(Animator anim) {
				View view = (View) ((ObjectAnimator) anim).getTarget();
				view.setScaleX(1f);
				view.setScaleY(1f);
			}
		});

		// 动画：CHANGE_DISAPPEARING
		// Changing while Removing
		Keyframe kf0 = Keyframe.ofFloat(0f, 0f);
		Keyframe kf1 = Keyframe.ofFloat(.9999f, 360f);
		Keyframe kf2 = Keyframe.ofFloat(1f, 0f);
		PropertyValuesHolder pvhRotation = PropertyValuesHolder.ofKeyframe(
				"rotation", kf0, kf1, kf2);
		final ObjectAnimator changeOut = ObjectAnimator
				.ofPropertyValuesHolder(this, pvhLeft, pvhTop, pvhRight,
						pvhBottom, pvhRotation)
						.setDuration(
								mTransitioner
								.getDuration(LayoutTransition.CHANGE_DISAPPEARING));
		mTransitioner.setAnimator(LayoutTransition.CHANGE_DISAPPEARING,
				changeOut);
		changeOut.addListener(new AnimatorListenerAdapter() {
			public void onAnimationEnd(Animator anim) {
				View view = (View) ((ObjectAnimator) anim).getTarget();
				view.setRotation(0f);
			}
		});

		// 动画：APPEARING
		// Adding
		ObjectAnimator animIn = ObjectAnimator.ofFloat(null, "rotationY", 90f,
				0f).setDuration(
						mTransitioner.getDuration(LayoutTransition.APPEARING));
		mTransitioner.setAnimator(LayoutTransition.APPEARING, animIn);
		animIn.addListener(new AnimatorListenerAdapter() {
			public void onAnimationEnd(Animator anim) {
				View view = (View) ((ObjectAnimator) anim).getTarget();
				view.setRotationY(0f);
			}
		});

		// 动画：DISAPPEARING
		// Removing
		ObjectAnimator animOut = ObjectAnimator.ofFloat(null, "rotationX", 0f,
				90f).setDuration(
						mTransitioner.getDuration(LayoutTransition.DISAPPEARING));
		mTransitioner.setAnimator(LayoutTransition.DISAPPEARING, animOut);
		animOut.addListener(new AnimatorListenerAdapter() {
			public void onAnimationEnd(Animator anim) {
				View view = (View) ((ObjectAnimator) anim).getTarget();
				view.setRotationX(0f);
			}
		});

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.byPhone:
			if(!byPhone.isSelected()) {
				phoneIndex.setVisibility(View.VISIBLE);
				byEmail.setSelected(false);
				byPhone.setSelected(true);

				//phoneIndex.setVisibility(View.VISIBLE);
				ObjectAnimator anim1 = ObjectAnimator.ofFloat(emailIndex, "alpha",  
						1f, 0f);  
				ObjectAnimator anim2 = ObjectAnimator.ofFloat(phoneIndex, "alpha",  
						0f, 1f);  
				ObjectAnimator anim3 = ObjectAnimator.ofFloat(lineGuide, "translationX", lineGuide.getTranslationX(), 0);

				ObjectAnimator anim4 = ObjectAnimator.ofFloat(register, "translationY", register.getTranslationY(), register.getTranslationY());
				/** 
				 * anim1，anim2,anim3同时执行 
				 * anim4接着执行 
				 */  
				AnimatorSet animSet = new AnimatorSet();  
				animSet.play(anim1).with(anim2);  
				animSet.play(anim2).with(anim3);  
				animSet.play(anim3).with(anim4); 
				animSet.setDuration(300);  

				animSet.start();  
				emailIndex.setVisibility(View.GONE);
			}
			break;
		case R.id.byEmail:
			if(!byEmail.isSelected()) {
				emailIndex.setVisibility(View.VISIBLE);
				byPhone.setSelected(false);
				byEmail.setSelected(true);

				//emailIndex.setVisibility(View.VISIBLE);
				ObjectAnimator anim1 = ObjectAnimator.ofFloat(phoneIndex, "alpha",  
						1f, 0f); 
				ObjectAnimator anim2 = ObjectAnimator.ofFloat(emailIndex, "alpha",  
						0f, 1f);  
				ObjectAnimator anim3 = ObjectAnimator.ofFloat(lineGuide, "translationX", lineGuide.getTranslationX(), lineGuide.getWidth());
				ObjectAnimator anim4 = ObjectAnimator.ofFloat(register, "translationY", register.getTranslationY(), register.getTranslationY());
				/** 
				 * anim1，anim2,anim3同时执行 
				 * anim4接着执行 
				 */  
				AnimatorSet animSet = new AnimatorSet();  
				animSet.play(anim1).with(anim2);  
				animSet.play(anim2).with(anim3);  
				animSet.play(anim3).with(anim4); 
				animSet.setDuration(300);  
				animSet.start();  
				phoneIndex.setVisibility(View.GONE);
			}
			break;
		case R.id.register:
			if(byPhone.isSelected()) {
				byPhoneRegist();
			} else {
				byEmailRegist();
			}
			break;
		case R.id.getVerifiedCode:
			if(phonenumber.getText().toString().trim().equals("")) {
				phonenumber.setError("请输入手机号码");
				return;
			} else {
				BmobSMS.requestSMSCode(this, phonenumber.getText().toString().trim(),"FireCode", new RequestSMSCodeListener() {

					@Override
					public void done(Integer smsId, BmobException ex) {
						if(ex==null){//验证码发送成功
							ViewUtil.getInstance().showToast("短信id："+smsId);
						} else {
							ViewUtil.getInstance().showToast("验证码发送失败"+ex.getMessage());
						}
					}
				});
			}
			break;
		}
	}

	private void byPhoneRegist() {
		if(username1.getText().toString().trim().equals("")) {
			username1.setError("请输入用户昵称");
			return;
		} else if(password1.getText().toString().trim().equals("")) {
			password1.setError("请输入密码");
			return;
		} else if(againpassword1.getText().toString().trim().equals("")) {
			againpassword1.setError("请再次输入密码");
			return;
		} else if(!password1.getText().toString().trim().equals(againpassword1.getText().toString().trim())) {
			againpassword1.setError("两次输入的密码不一致，请重新输入");
			return;
		} else if(phonenumber.getText().toString().trim().equals("")) {
			phonenumber.setError("请输入手机号码");
			return;
		} else if(verifiedCode.getText().toString().trim().equals("")) {
			verifiedCode.setError("请输入验证码");
			return;
		}
		User user = new User();
		user.setUsername(username1.getText().toString().trim());
		user.setPassword(password1.getText().toString().trim());
		user.setMobilePhoneNumber(phonenumber.getText().toString().trim());

		user.signOrLogin(this, verifiedCode.getText().toString().trim(), new SaveListener() {
			@Override
			public void onSuccess() {
				//toast("注册或登录成功");
				//Log.i("smile", ""+user.getUsername()+"-"+user.getAge()+"-"+user.getObjectId());
				ViewUtil.getInstance().showToast("注册成功");
				User user = BmobUser.getCurrentUser(RegistActivity.this,User.class);
				UserManager.getInstance().setUser(user);
			}
			@Override
			public void onFailure(int code, String msg) {
				ViewUtil.getInstance().showToast("注册失败："+msg);
				//toast("错误码："+code+",错误原因："+msg);
			}
		});
	}

	private void byEmailRegist() {
		if(username2.getText().toString().trim().equals("")) {
			username2.setError("请输入用户昵称");
			return;
		} else if(password2.getText().toString().trim().equals("")) {
			password2.setError("请输入密码");
			return;
		} else if(againpassword2.getText().toString().trim().equals("")) {
			againpassword2.setError("请再次输入密码");
			return;
		} else if(!password2.getText().toString().trim().equals(againpassword2.getText().toString().trim())) {
			againpassword2.setError("两次输入的密码不一致，请重新输入");
			return;
		} else if(email.getText().toString().trim().equals("")) {
			email.setError("请输入邮箱");
			return;
		}
		User user = new User();
		user.setUsername(username2.getText().toString().trim());
		user.setPassword(password2.getText().toString().trim());
		user.setEmail(email.getText().toString().trim());

		user.signUp(this, new SaveListener() {
			@Override
			public void onSuccess() {
				ViewUtil.getInstance().showToast("注册成功!");
				User user = BmobUser.getCurrentUser(RegistActivity.this,User.class);
				UserManager.getInstance().setUser(user);
			}
			@Override
			public void onFailure(int code, String msg) {
				ViewUtil.getInstance().showToast("注册失败:"+msg);
			}
		});

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

	private float dp2px(int dp) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}

}
