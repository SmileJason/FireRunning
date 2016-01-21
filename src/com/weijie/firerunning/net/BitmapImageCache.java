package com.weijie.firerunning.net;

import java.io.File;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Environment;
import android.text.TextUtils;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.jakewharton.disklrucache.DiskLruCache;
import com.jakewharton.disklrucache.DiskLruCache.Snapshot;
import com.weijie.firerunning.util.EncryptUtil;
import com.weijie.firerunning.util.Utility;

/**
 * 图片缓存
 * @author weijie
 * @datetime 2014-12-16 下午7:55:04
 */
public class BitmapImageCache extends LruCache<String,Bitmap> implements ImageCache {
	
	public BitmapImageCache(Context context){
		super(getLruCacheMaxsize());
		File directory = getDiskCacheDir(context, uniqueName);
		int appVersion = Utility.getAppVersion(context);
		try {
			diskLruCache = DiskLruCache.open(directory, appVersion, 1, diskMaxSize);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static final String uniqueName = "images";
	private static final int diskMaxSize = 15 * 1024 * 1024;

	private DiskLruCache diskLruCache;
	
	 /** 
     * 根据传入的uniqueName获取硬盘缓存的路径地址。 
     */  
    private File getDiskCacheDir(Context context, String uniqueName) {  
        String cachePath;  
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())  
                || !Environment.isExternalStorageRemovable()) {  
            cachePath = context.getExternalCacheDir().getPath();  
        } else {  
            cachePath = context.getCacheDir().getPath();  
        }  
        return new File(cachePath + File.separator + uniqueName);  
    }  
    
    private static int getLruCacheMaxsize(){
    	return (int) (Runtime.getRuntime().maxMemory() / 1024 / 8);
    }
    
    @SuppressLint("NewApi") @Override
    protected int sizeOf(String key, Bitmap value) {
    	if(VERSION.SDK_INT >= VERSION_CODES.KITKAT){
    		return value.getAllocationByteCount() / 1024;
    	}else{
    		return value.getByteCount() / 1024;
    	}
    }
    
	@Override
	public Bitmap getBitmap(String url) {
		try {
			if(!TextUtils.isEmpty(url)){
				url = EncryptUtil.MD5(url);
				Bitmap bitmap = get(url);
				if(bitmap == null && diskLruCache != null){
					Snapshot snapshot = diskLruCache.get(url);
					if(snapshot != null){
						bitmap = BitmapFactory.decodeStream(snapshot.getInputStream(0));
						snapshot.close();
						if(bitmap != null){
							put(url, bitmap);
						}
					}
				}
				return bitmap;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		if(bitmap != null){
			url = EncryptUtil.MD5(url);
			put(url, bitmap);
			if(diskLruCache != null){
				try {
					Snapshot snapshot = diskLruCache.get(url);
					if(snapshot != null){
						bitmap.compress(CompressFormat.JPEG,80,snapshot.edit().newOutputStream(0));
						snapshot.close();
						diskLruCache.flush();
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
