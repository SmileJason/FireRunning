package com.weijie.firerunning.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.bmob.BmobProFile;
import com.weijie.firerunning.App;
import com.weijie.firerunning.R;
import com.weijie.firerunning.bean.Discuss;
import com.weijie.firerunning.bean.User;
import com.weijie.firerunning.net.BitmapImageCache;
import com.weijie.firerunning.view.RoundAngleImageView;
import com.weijie.firerunning.view.SingleImageDialog;

public class DiscussAdapter extends BaseAdapter {

	private List<Discuss> discusses;
	private Context context;
	private LayoutInflater inflater;
	private SingleImageDialog imgDialog;
	private ImageLoader imageLoader;
	
	public DiscussAdapter(List<Discuss> discusses, Activity context) {
		this.discusses = discusses;
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		RequestQueue queue = App.getRequestQueue(context.getApplication());
		imageLoader = new ImageLoader(queue, new BitmapImageCache(context));
	}

	@Override
	public int getCount() {
		if(discusses!=null) {
			return discusses.size();
		} else {
			return 0;
		}
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View view, ViewGroup group) {
		Discuss discuss = discusses.get(position);
		ViewHolder holder;
		if(view == null) {
			holder = new ViewHolder();
			view = inflater.inflate(R.layout.item_discuss, group, false);
			holder.name = (TextView) view.findViewById(R.id.name);
			holder.dateTime = (TextView) view.findViewById(R.id.dateTime);
			holder.content = (TextView) view.findViewById(R.id.content);
			holder.layout = (LinearLayout) view.findViewById(R.id.layout);
			for(int i=0;i<5;i++) {
				//View image = inflater.inflate(R.layout.item_picture2, holder.layout, false);
				View image = inflater.inflate(R.layout.item_picture2, holder.layout, false);
				image.setClickable(true);
				image.setOnClickListener(listener);
				holder.layout.addView(image);
			}
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		User user = discuss.user;
		if(user!=null) {
			holder.name.setText(user.getUsername());
		} else {
			holder.name.setText(discuss.hide);
		}
		holder.dateTime.setText(discuss.getCreatedAt());
		holder.content.setText(discuss.content);
		String fileURL = discuss.fileURL;
		String fileName = discuss.fileName;
		if(fileURL!=null && !fileURL.equals("")) {
			String[] files = fileName.split(",");
			String[] urls = fileURL.split(",");
			if(urls.length>0) {
				holder.layout.setVisibility(View.VISIBLE);
				//String[] us = new String[urls.length];
				for(int i=0;i<5;i++) {
					//RoundAngleImageView2 img = (RoundAngleImageView2) holder.layout.getChildAt(i).findViewById(R.id.img);
					if(i<urls.length) {
						holder.layout.getChildAt(i).setVisibility(View.VISIBLE);
						RoundAngleImageView img = (RoundAngleImageView) holder.layout.getChildAt(i).findViewById(R.id.img);
						//img.setImageDrawable(context.getResources().getDrawable(R.drawable.image_load_cache));
						//String url = BmobProFile.getInstance(context).signURL(files[i], urls[i], "80d9fa19187ad1a676248865c82b27ba", 100, "e8d70dfe20a2e648");
						String url = BmobProFile.getInstance(context).signURL(files[i], urls[i], "68c14de64572e77eb8039d5781202301", 0, null);
						showImageByNetworkImageView(img,url);
						//us[i] = url;
					} else {
						//RoundAngleImageView img = (RoundAngleImageView) holder.layout.getChildAt(i).findViewById(R.id.img);
						holder.layout.getChildAt(i).setVisibility(View.GONE);
					}
				}
				/*for(int i=0;i<us.length;i++) {
					RoundAngleImageView img = (RoundAngleImageView) holder.layout.getChildAt(i).findViewById(R.id.img);
					img.setVisibility(View.VISIBLE);
					showImageByNetworkImageView(img,us[i]);
				}*/
			}
		} else {
			holder.layout.setVisibility(View.GONE);
		}
		return view;
	}

	/**
     * 利用NetworkImageView显示网络图片
     */ 
    private void showImageByNetworkImageView(RoundAngleImageView img,String imageUrl){ 
        /*RequestQueue requestQueue = Volley.newRequestQueue(context); 
        final LruCache<String, Bitmap> lruCache = new LruCache<String, Bitmap>(20); 
        ImageCache imageCache = new ImageCache() { 
            @Override 
            public void putBitmap(String key, Bitmap value) { 
                lruCache.put(key, value); 
            } 
   
            @Override 
            public Bitmap getBitmap(String key) { 
                return lruCache.get(key); 
            } 
        }; 
        ImageLoader imageLoader = new ImageLoader(requestQueue, imageCache); 
        img.setTag("url"); 
        img.setImageUrl(imageUrl,imageLoader); */
    	
    	imageLoader.get(imageUrl
				,ImageLoader.getImageListener(img
						,R.drawable.image_load_cache
						, R.drawable.image_load_cache));
    	
    } 
	
	class ViewHolder {
		TextView name;
		TextView dateTime;
		TextView content;
		LinearLayout layout;
	}
	
	private OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if(imgDialog==null) {
				imgDialog = new SingleImageDialog(context); 
			}
			RoundAngleImageView img = (RoundAngleImageView)(v.findViewById(R.id.img));
			imgDialog.setData(img.getDrawable());
			imgDialog.show();
		}
	};
	
}
