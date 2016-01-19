package com.weijie.firerunning.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.weijie.firerunning.R;

public class TitleBar extends LinearLayout implements OnClickListener {

	public static final int WHICH_BUTTON_LEFT = 1;
	public static final int WHICH_BUTTON_RIGHT = 2;

	public ImageView leftImageView, rightImageView;
	TextView titleTextView;

	private OnClickButtonListener mListener;

	public TitleBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		inflate(context, R.layout.view_title_bar, this);

		leftImageView = (ImageView) findViewById(R.id.left_imageview);
		rightImageView = (ImageView) findViewById(R.id.right_imageview);
		titleTextView = (TextView) findViewById(R.id.title_textview);
		leftImageView.setOnClickListener(this);
		rightImageView.setOnClickListener(this);

		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FireRunning);
		Drawable leftDrawable = ta
				.getDrawable(R.styleable.FireRunning_leftButtonDrawable);
		Drawable rightDrawable = ta
				.getDrawable(R.styleable.FireRunning_rightButtonDrawable);
		String title = ta.getString(R.styleable.FireRunning_label);

		if (leftDrawable != null) {
			leftImageView.setImageDrawable(leftDrawable);
			leftImageView.setVisibility(View.VISIBLE);
		}
		if (rightDrawable != null) {
			rightImageView.setImageDrawable(rightDrawable);
			rightImageView.setVisibility(View.VISIBLE);
		}

		if (!TextUtils.isEmpty(title)) {
			titleTextView.setText(title);
		}

		ta.recycle();

	}
	
	public ImageView getImageView() {
		return leftImageView;
	}

	/**
	 * 设置标题文字
	 * 
	 * @param text
	 */
	public void setLabel(CharSequence text) {
		if (TextUtils.isEmpty(text))
			text = "";
		titleTextView.setText(text);
	}

	public String getLabel() {
		return titleTextView.getText().toString();
	}

	/**
	 * 设置左边按钮图片
	 * 
	 * @param resId
	 */
	public void setLeftButtonDrawable(int resId) {
		leftImageView.setImageResource(resId);
		leftImageView.setVisibility(View.VISIBLE);
	}

	/**
	 * 设置右边按钮图片
	 * 
	 * @param resId
	 */
	public void setRightButtonDrawable(int resId) {
		rightImageView.setImageResource(resId);
		rightImageView.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		if (mListener == null || !v.isShown())
			return;

		switch (v.getId()) {
		case R.id.left_imageview:
			mListener.onClickButton(WHICH_BUTTON_LEFT);
			break;
		case R.id.right_imageview:
			mListener.onClickButton(WHICH_BUTTON_RIGHT);
			break;
		default:
			break;
		}
	}

	public void setOnClickButtonListener(OnClickButtonListener listener) {
		this.mListener = listener;
	}

	public interface OnClickButtonListener {
		/**
		 * 标题栏按钮被点击
		 * 
		 * @param which
		 *            ，哪一个按钮被点击，TitleBar有常量
		 */
		public void onClickButton(int which);
	}

}