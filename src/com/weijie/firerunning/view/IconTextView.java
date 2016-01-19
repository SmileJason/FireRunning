package com.weijie.firerunning.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.weijie.firerunning.R;

public class IconTextView extends LinearLayout {

	public IconTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FireRunning);
		String text = ta.getString(R.styleable.FireRunning_text);
		Drawable icon = ta.getDrawable(R.styleable.FireRunning_icon);
		ta.recycle();
		
		inflate(context, R.layout.view_icon_text, this);
		TextView tv = (TextView) findViewById(R.id.txt);
		tv.setText(text);
		ImageView ic = (ImageView) findViewById(R.id.icon);
		ic.setImageDrawable(icon);
	}

}
