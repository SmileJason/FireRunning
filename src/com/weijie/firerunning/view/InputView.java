package com.weijie.firerunning.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.weijie.firerunning.R;

/** 
 * 输入控件
 * @author weijie 
 * @version 创建时间：2015年3月13日 下午5:47:31 
 */
public class InputView extends LinearLayout {
	
	private EditText edit;
	private ImageView lIcon,rIcon;
	
	public InputView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.FireRunning, 0, 0);
		String txt = ta.getString(R.styleable.FireRunning_input_txt);
		String hint = ta.getString(R.styleable.FireRunning_input_hint);
		Drawable left = ta.getDrawable(R.styleable.FireRunning_input_left);
		Drawable right = ta.getDrawable(R.styleable.FireRunning_input_right);
		int inputType = ta.getInteger(R.styleable.FireRunning_input_type, 0);
		ta.recycle();
		
		inflate(context, R.layout.view_input_text, this);
		edit = (EditText) findViewById(R.id.input_txt);
		lIcon = (ImageView) findViewById(R.id.input_left);
		rIcon = (ImageView) findViewById(R.id.input_right);
		if(hint!=null) {
			edit.setHint(hint);
		}
		
		if(left!=null) {
			lIcon.setImageDrawable(left);
		}
		if(right!=null) {
			rIcon.setImageDrawable(right);
		}
		edit.setSingleLine(true);
		switch (inputType) {
		case 1:
			edit.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
			break;
		case 2:
			edit.setInputType(EditorInfo.TYPE_CLASS_NUMBER
					| EditorInfo.TYPE_NUMBER_FLAG_SIGNED);
			break;
		case 3:
			edit.setInputType(EditorInfo.TYPE_CLASS_NUMBER
					| EditorInfo.TYPE_NUMBER_FLAG_DECIMAL);
			break;
		case 4:
			edit.setInputType(EditorInfo.TYPE_CLASS_TEXT
					| EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
			break;
		case 5:
			edit.setInputType(EditorInfo.TYPE_CLASS_NUMBER
					| EditorInfo.TYPE_NUMBER_VARIATION_PASSWORD);
			break;
		case 6:
			edit.setInputType(EditorInfo.TYPE_CLASS_PHONE);
			break;
		default:
			edit.setInputType(EditorInfo.TYPE_CLASS_TEXT);
			break;
		}
		
	}
	
}
