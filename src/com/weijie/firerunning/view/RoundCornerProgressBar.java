package com.weijie.firerunning.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.weijie.firerunning.R;

public class RoundCornerProgressBar extends RelativeLayout {

	private ProgressBar progress;
	private View point;
	private TextView maxT,kilemeter;
	private double max,k;
	private int width;
	private RelativeLayout.LayoutParams params;
	
	public RoundCornerProgressBar(Context context) {
		super(context);
		initView(context);
	}
	public RoundCornerProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}
	public RoundCornerProgressBar(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}
	
	private void initView(Context context) {
		params = new LayoutParams((int)dp2px(20),(int)dp2px(20));
		inflate(context,R.layout.view_round_progress, this);
		WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		width = wm.getDefaultDisplay().getWidth()-(int)dp2px(40);
		point = findViewById(R.id.point);
		progress = (ProgressBar) findViewById(R.id.pro);
		maxT = (TextView) findViewById(R.id.max);
		kilemeter = (TextView) findViewById(R.id.kilemeter);
	}
	
	public void setProgress(double k) {
		int p = (int) (k*100/max);
		if(p>=1) {
			this.k = k;
			progress.setProgress(p);
			int left = p*width/100+(int)dp2px(10);
			if(left<width/4) {
				params.setMargins(left+(int)dp2px(10), 0, 0, 0);
			} else {
				params.setMargins(left, 0, 0, 0);
			}
			point.setLayoutParams(params);
			point.setVisibility(View.VISIBLE);
			kilemeter.setText(k+"公里");
		} else {
			point.setVisibility(View.GONE);
		}
		invalidate();
	}
	
	public double getProgress() {
		return k;
	}
	
	public double getMax() {
		return max;
	}
	
	public void setMax(double max,double k) {
		this.max = max;
		maxT.setText(max+"公里");
		progress.setMax(100);
		this.k = k;
		setProgress(k);
	}
	
	private float dp2px(int dp) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}
	
}
