package com.weijie.firerunning.view;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.weijie.firerunning.Conf;
import com.weijie.firerunning.R;

@SuppressLint("ValidFragment")
public class ImageDialog extends Dialog implements android.view.View.OnClickListener {

	private View back;
	private TextView imgNum;
	private ViewPager imgPage;
	private int size;
	
	public ImageDialog(Context context) {
		super(context,R.style.full_screen_dialog);
		setContentView(R.layout.view_image_page);
		initView();
	}
	
	private void initView() {
		back = findViewById(R.id.back);
		back.setOnClickListener(this);
		imgNum = (TextView) findViewById(R.id.imgNum);
		imgPage = (ViewPager) findViewById(R.id.imgPage);
		imgPage.setOnPageChangeListener(pageListener);
	}
	
	private OnPageChangeListener pageListener = new OnPageChangeListener() {
		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}
		@Override
		public void onPageSelected(int position) {
			imgNum.setText(position+1+"/"+size);
		}
	};
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			dismiss();
			break;
		}
	}
	
	public void setData(List<String> paths,String path,FragmentManager fragmentManager) {
		int index = 0;
		size = paths.size();
		for(int i=0;i<paths.size();i++) {
			if(paths.get(i).equals(path)) {
				index = i;
				break;
			}
		}
		imgNum.setText(index+1+"/"+paths.size());
		imgPage.setAdapter(new ImagePagerAdapter(paths,fragmentManager));
		imgPage.setCurrentItem(index);
	}
	
	private class ImagePagerAdapter extends PagerAdapter{
		
		private FragmentManager fragmentManager;
		private List<Fragment> fragments;
		
		public ImagePagerAdapter(List<String> paths,FragmentManager fragmentManager) {
			this.fragmentManager = fragmentManager;
			fragments = new ArrayList<Fragment>();
			for(String path:paths) {
				ImageFragment f = new ImageFragment();
				f.setData(path);
				fragments.add(f);
			}
		}

		@Override
		public int getCount() {
			//return Integer.MAX_VALUE;
			return fragments.size();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(fragments.get(position).getView()); // 移出viewpager两边之外的page布局
		}

		
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			Fragment fragment = fragments.get(position);
	        if(!fragment.isAdded()){ // 如果fragment还没有added
	            FragmentTransaction ft = fragmentManager.beginTransaction();
	            ft.add(fragment, fragment.getClass().getSimpleName());
	            ft.commit();
	            /**
	             * 在用FragmentTransaction.commit()方法提交FragmentTransaction对象后
	             * 会在进程的主线程中，用异步的方式来执行。
	             * 如果想要立即执行这个等待中的操作，就要调用这个方法（只能在主线程中调用）。
	             * 要注意的是，所有的回调和相关的行为都会在这个调用中被执行完成，因此要仔细确认这个方法的调用位置。
	             */
	            fragmentManager.executePendingTransactions();
	        }

	        if(fragment.getView().getParent() == null){
	            container.addView(fragment.getView()); // 为viewpager增加布局
	        }
	        //imgNum.setText(position+1+"/"+fragments.size());
	        ((ImageFragment)fragment).setImage();
	        return fragment.getView();
		}

		@Override
		public boolean isViewFromObject(View view, Object o) {
			return view == o;
		}
		
	}
	
	private Bitmap getBitmap(String path) {
		File file = new File(path);
		try {
			//Log.e("LENGTH", file.length()+"");
			FileInputStream is = new FileInputStream(file);
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inTempStorage = new byte[1024*100];
			opts.inPreferredConfig = Bitmap.Config.RGB_565;
			opts.inPurgeable = true;
			
			opts.inSampleSize = (int) ((file.length()/Conf.MEMORY_OUT_MAX)+1);
			opts.inInputShareable = true;
			Bitmap bm = BitmapFactory.decodeStream(is, null, opts);
			return bm;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private class ImageFragment extends Fragment {

		private ImageView img;
		private String path;
		
		@Override
		public View onCreateView(LayoutInflater inflater,
				ViewGroup container,
				Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.item_image, null, false);
			img = (ImageView) view.findViewById(R.id.img);
			return view;
		}
		
		public void setData(String path) {
			//Bitmap bm = getBitmap(path);
			//img.setImageBitmap(bm);
			this.path = path;
		}
		
		public void setImage() {
			Bitmap bm = getBitmap(path);
			img.setImageBitmap(bm);
		}
		
	}
	

}
