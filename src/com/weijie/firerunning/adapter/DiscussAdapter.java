package com.weijie.firerunning.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.Volley;
import com.bmob.BmobProFile;
import com.weijie.firerunning.R;
import com.weijie.firerunning.bean.Discuss;
import com.weijie.firerunning.bean.User;
import com.weijie.firerunning.view.RoundAngleImageView2;

public class DiscussAdapter extends BaseAdapter {

	private List<Discuss> discusses;
	private Context context;
	private LayoutInflater inflater;
	
	public DiscussAdapter(List<Discuss> discusses, Context context) {
		this.discusses = discusses;
		this.context = context;
		this.inflater = LayoutInflater.from(context);
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
			holder.layout1 = (LinearLayout) view.findViewById(R.id.layout1);
			for(int i=0;i<5;i++) {
				View image = inflater.inflate(R.layout.item_picture2, holder.layout1, false);
				holder.layout1.addView(image);
			}
			holder.layout2 = (LinearLayout) view.findViewById(R.id.layout2);
			for(int i=0;i<5;i++) {
				View image = inflater.inflate(R.layout.item_picture2, holder.layout2, false);
				holder.layout2.addView(image);
			}

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		User user = discuss.user;
		if(user!=null) {
			holder.name.setText(user.password);
		} else {
			holder.name.setText(discuss.hide);
		}
		holder.dateTime.setText(discuss.dateTime);
		holder.content.setText(discuss.content);
		String fileURL = discuss.fileURL;
		String fileName = discuss.fileName;
		if(fileURL!=null && !fileURL.equals("")) {
			String[] files = fileName.split(",");
			String[] urls = fileURL.split(",");
			if(urls.length>5) {
				holder.layout1.setVisibility(View.VISIBLE);
				for(int i=0;i<5;i++) {
					RoundAngleImageView2 img = (RoundAngleImageView2) holder.layout1.getChildAt(i).findViewById(R.id.img);
					img.setVisibility(View.VISIBLE);
					img.setImageDrawable(context.getResources().getDrawable(R.drawable.image_load_cache));
					//String url = BmobProFile.getInstance(context).signURL(files[i], urls[i], "80d9fa19187ad1a676248865c82b27ba", 100, "e8d70dfe20a2e648");
					String url = BmobProFile.getInstance(context).signURL(files[i], urls[i], "68c14de64572e77eb8039d5781202301", 0, null);
					showImageByNetworkImageView(img,url);
				}
				holder.layout2.setVisibility(View.VISIBLE);
				for(int i=5;i<10;i++) {
					RoundAngleImageView2 img = (RoundAngleImageView2) holder.layout2.getChildAt(i).findViewById(R.id.img);
					if(i<urls.length) {
						img.setVisibility(View.VISIBLE);
						img.setImageDrawable(context.getResources().getDrawable(R.drawable.image_load_cache));
						//String url = BmobProFile.getInstance(context).signURL(files[i], urls[i], "80d9fa19187ad1a676248865c82b27ba", 100, "e8d70dfe20a2e648");
						String url = BmobProFile.getInstance(context).signURL(files[i], urls[i], "68c14de64572e77eb8039d5781202301", 0, null);
						showImageByNetworkImageView(img,url);
					} else {
						img.setVisibility(View.GONE);
					}
				}
				
			} else if(urls.length>0) {
				holder.layout1.setVisibility(View.VISIBLE);
				for(int i=0;i<5;i++) {
					RoundAngleImageView2 img = (RoundAngleImageView2) holder.layout1.getChildAt(i).findViewById(R.id.img);
					if(i<urls.length) {
						img.setVisibility(View.VISIBLE);
						img.setImageDrawable(context.getResources().getDrawable(R.drawable.image_load_cache));
						//String url = BmobProFile.getInstance(context).signURL(files[i], urls[i], "80d9fa19187ad1a676248865c82b27ba", 100, "e8d70dfe20a2e648");
						String url = BmobProFile.getInstance(context).signURL(files[i], urls[i], "68c14de64572e77eb8039d5781202301", 0, null);
						showImageByNetworkImageView(img,url);
					} else {
						img.setVisibility(View.GONE);
					}
				}
				holder.layout2.setVisibility(View.GONE);
			}
		} else {
			holder.layout1.setVisibility(View.GONE);
			holder.layout2.setVisibility(View.GONE);
		}
		return view;
	}

	/**
     * 利用NetworkImageView显示网络图片
     */ 
    private void showImageByNetworkImageView(RoundAngleImageView2 img,String imageUrl){ 
        RequestQueue requestQueue = Volley.newRequestQueue(context); 
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
        img.setImageUrl(imageUrl,imageLoader); 
    } 
	
	class ViewHolder {
		TextView name;
		TextView dateTime;
		TextView content;
		LinearLayout layout1;
		LinearLayout layout2;
	}
	
}