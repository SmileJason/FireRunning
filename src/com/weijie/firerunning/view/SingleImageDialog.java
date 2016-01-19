package com.weijie.firerunning.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.weijie.firerunning.R;

@SuppressLint("ValidFragment")
public class SingleImageDialog extends Dialog implements android.view.View.OnClickListener {

	private View back;
	private ImageView img;
	
	public SingleImageDialog(Context context) {
		super(context,R.style.full_screen_dialog);
		setContentView(R.layout.view_single_image);
		initView();
	}
	
	private void initView() {
		back = findViewById(R.id.back);
		back.setOnClickListener(this);
		img = (ImageView) findViewById(R.id.img);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			dismiss();
			break;
		}
	}
	
	public void setData(Drawable drawable) {
		img.setImageDrawable(drawable);
	}

}
