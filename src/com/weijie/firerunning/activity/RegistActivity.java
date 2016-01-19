package com.weijie.firerunning.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.Keyframe;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.weijie.firerunning.R;

public class RegistActivity extends Activity implements OnClickListener {

	private View byPhone,byEmail,lineGuide,phoneIndex,emailIndex,register;

	private RelativeLayout content;
	private LayoutTransition mTransitioner;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_regist);
		content = (RelativeLayout) findViewById(R.id.content);
		resetTransition();    

		register = findViewById(R.id.register);
		byPhone = findViewById(R.id.byPhone);
		byEmail = findViewById(R.id.byEmail);
		lineGuide = findViewById(R.id.lineGuide);
		phoneIndex = findViewById(R.id.phoneIndex);
		emailIndex = findViewById(R.id.emailIndex);
		byPhone.setOnClickListener(this);
		byEmail.setOnClickListener(this);
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
				byEmail.setSelected(false);
				byPhone.setSelected(true);
				
				//phoneIndex.setVisibility(View.VISIBLE);
				ObjectAnimator anim1 = ObjectAnimator.ofFloat(emailIndex, "alpha",  
						1f, 0f);  
				ObjectAnimator anim2 = ObjectAnimator.ofFloat(phoneIndex, "alpha",  
						0f, 1f);  
				ObjectAnimator anim3 = ObjectAnimator.ofFloat(lineGuide, "translationX", lineGuide.getTranslationX(), 0);
				
				ObjectAnimator anim4 = ObjectAnimator.ofFloat(register, "translationY", register.getTranslationY(), register.getTranslationY()+dp2px(45));
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

			}
			break;
		case R.id.byEmail:
			if(!byEmail.isSelected()) {
				byPhone.setSelected(false);
				byEmail.setSelected(true);

				//emailIndex.setVisibility(View.VISIBLE);
				ObjectAnimator anim1 = ObjectAnimator.ofFloat(phoneIndex, "alpha",  
						1f, 0f); 
				ObjectAnimator anim2 = ObjectAnimator.ofFloat(emailIndex, "alpha",  
						0f, 1f);  
				ObjectAnimator anim3 = ObjectAnimator.ofFloat(lineGuide, "translationX", lineGuide.getTranslationX(), lineGuide.getWidth());
				ObjectAnimator anim4 = ObjectAnimator.ofFloat(register, "translationY", register.getTranslationY(), register.getTranslationY()-dp2px(45));
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


			}
			break;

		default:
			break;
		}
	}


	private float dp2px(int dp) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}
	
}
