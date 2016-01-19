package com.weijie.firerunning.fragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.weijie.firerunning.R;
import com.weijie.firerunning.util.StringUtil;

/**
 * BUG报告
 * @author weijie
 *
 */

public class ErrorFragment extends Fragment implements OnClickListener {

	private Button clear,getError;
	private TextView error;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_error, container, false);
		clear = (Button) view.findViewById(R.id.clearError);
		getError = (Button) view.findViewById(R.id.getError);
		clear.setOnClickListener(this);
		getError.setOnClickListener(this);
		
		error = (TextView) view.findViewById(R.id.error);
		getErrorMsg();
		return view;
	}

	private void getErrorMsg() {
		try{
			String fileName = "crashError.txt";  
			File file = new File(getActivity().getFilesDir(),fileName);  
			if (!file.exists()) {  
				file.createNewFile();
			} 
			FileInputStream fis = new FileInputStream(file);
			String err = StringUtil.stream2String(fis);
			error.setText(err);
		} catch(Exception e) {
		}
	}
	
	private void clearError() {
		String fileName = "crashError.txt";  
		File file = new File(getActivity().getFilesDir(),fileName);  
		if (file.exists()) {  
			try {
				FileOutputStream fos = new FileOutputStream(file);
				fos.write("".getBytes());
				fos.close();
				error.setText("");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.getError:
			getErrorMsg();
			break;
		case R.id.clearError:
			clearError();
			break;
		}
	}
	
}
