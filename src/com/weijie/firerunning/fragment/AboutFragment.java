package com.weijie.firerunning.fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindCallback;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qq.QQClientNotExistException;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.tencent.weibo.TencentWeibo;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import cn.sharesdk.wechat.utils.WechatClientNotExistException;
import cn.sharesdk.wechat.utils.WechatTimelineNotSupportedException;

import com.google.gson.reflect.TypeToken;
import com.mob.tools.utils.UIHandler;
import com.weijie.firerunning.R;
import com.weijie.firerunning.bean.AppInfo;
import com.weijie.firerunning.util.BitmapUtil;
import com.weijie.firerunning.util.JSONUtil;
import com.weijie.firerunning.util.ViewUtil;

public class AboutFragment extends Fragment implements OnClickListener, PlatformActionListener, Callback {

	private static final int WHAT_COMPLETE = 200;
	private static final int WHAT_ERROR = 201;
	private static final int WHAT_CANCEL = 202;
	private static final String SINA_WEIBO = "com.sina.weibo";
	
	private TextView version;
	private AlertDialog dialog;
	private Platform platform;
	private File qrcodeFile;
	private Bitmap qrcodeBm;
	public static String path = "";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_about, container, false);
		version = (TextView) view.findViewById(R.id.version);
		version.setText(getAppVersionName(getActivity()));
		view.findViewById(R.id.shareApp).setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.shareApp:
			shawDialog();
			break;
		}
	}
	
	private void shawDialog() {
		dialog = new AlertDialog.Builder(getActivity()).create();
		Window window = dialog.getWindow();  
		window.setGravity(Gravity.BOTTOM);  //此处可以设置dialog显示的位置  
		window.setWindowAnimations(R.style.PopupAnimation);  //添加动画  
		dialog.show();
		View dialogView = LayoutInflater.from(this.getActivity()).inflate(R.layout.view_share_dialog, null);
		
		dialogView.findViewById(R.id.qq).setOnClickListener(shareListener);
		dialogView.findViewById(R.id.sina).setOnClickListener(shareListener);
		dialogView.findViewById(R.id.QZone).setOnClickListener(shareListener);
		dialogView.findViewById(R.id.tencent).setOnClickListener(shareListener);
		dialogView.findViewById(R.id.wx).setOnClickListener(shareListener);
		dialogView.findViewById(R.id.wx_timeline).setOnClickListener(shareListener);
		
		dialog.getWindow().setContentView(dialogView);

		getActivity().getWindow().getAttributes().width = WindowManager.LayoutParams.MATCH_PARENT;
		getActivity().getWindow().getAttributes().height = WindowManager.LayoutParams.WRAP_CONTENT;
	}
	
	private OnClickListener shareListener = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
			ShareParams shareParams;
			if(path.equals("")) {
				getData();
			}
			if(!path.equals("")) {
				showQRCode();
			}
			switch (view.getId()) {
			case R.id.qq:
				shareParams = new QQ.ShareParams();
				//shareParams.setShareType(Platform.SHARE_TEXT);
				shareParams.setShareType(Platform.SHARE_IMAGE);
				shareParams.setText("FireRunning最新版本下载地址："+path);
				if(qrcodeFile != null && qrcodeFile.exists()){
					shareParams.setImagePath(qrcodeFile.getAbsolutePath());
				}
				doShare(shareParams, QQ.NAME);
				break;
			case R.id.sina:
				shareParams = new SinaWeibo.ShareParams();
				//shareParams.setShareType(Platform.SHARE_TEXT);
				shareParams.setText("FireRunning最新版本下载地址："+path);
				if(qrcodeFile != null && qrcodeFile.exists()){
					shareParams.setImagePath(qrcodeFile.getAbsolutePath());
				}
				doShare(shareParams, SinaWeibo.NAME);
				break;
			case R.id.QZone:
				shareParams = new QZone.ShareParams();
				//shareParams.setShareType(Platform.SHARE_IMAGE);
				//shareParams.setText("药膳居最新版本下载地址："+path);
				/*if(qrcodeFile != null && qrcodeFile.exists()){
					shareParams.setImagePath(qrcodeFile.getAbsolutePath());
				}*/
				shareParams.setTitle("FireRunning");
				shareParams.setTitleUrl("http://sharesdk.cn");
				shareParams.setText("FireRunning最新版本下载地址："+path);
				shareParams.setSite("FireRunning");
				shareParams.setSiteUrl("http://sharesdk.cn");
				doShare(shareParams, QZone.NAME);
				break;
			case R.id.tencent:
				shareParams = new TencentWeibo.ShareParams();
				//shareParams.setShareType(Platform.SHARE_TEXT);
				shareParams.setText("FireRunning最新版本下载地址："+path);
				if(qrcodeFile != null && qrcodeFile.exists()){
					shareParams.setImagePath(qrcodeFile.getAbsolutePath());
				}
				doShare(shareParams, TencentWeibo.NAME);
				break;
			case R.id.wx:
				shareParams = new Wechat.ShareParams();
				shareParams.setShareType(Platform.SHARE_TEXT);
				shareParams.setText("FireRunning最新版本下载地址："+path);
				doShare(shareParams, Wechat.NAME);
				break;
			case R.id.wx_timeline:
				shareParams = new WechatMoments.ShareParams();
				//shareParams.setShareType(Platform.SHARE_TEXT);
				shareParams.setText("FireRunning最新版本下载地址："+path);
				if(qrcodeBm != null){
					shareParams.setImageData(qrcodeBm);
				}
				doShare(shareParams, WechatMoments.NAME);
				break;
			default:
				break;
			}
			dialog.dismiss();
		}
	};
	
	private void doShare(ShareParams shareParams,String platformName){
		platform = ShareSDK.getPlatform(this.getActivity(), platformName);
		platform.setPlatformActionListener(this);
		platform.share(shareParams);
	}
	
	private void getData() {
		BmobQuery<AppInfo> query = new BmobQuery<AppInfo>("AppInfo");
		query.findObjects(this.getActivity(), new FindCallback() {
			@Override	
			public void onSuccess(JSONArray jsonArray) {
				if(jsonArray!=null) {
					TypeToken<ArrayList<AppInfo>> typeToken = new TypeToken<ArrayList<AppInfo>>() {
					};
					List<AppInfo> apps = JSONUtil.toList(jsonArray,typeToken);
					if(apps!=null && apps.size()>0) {
						AppInfo info = apps.get(0);
						path = info.getUrl();
					}
				}
			}

			@Override
			public void onFailure(int i, String s) {
			}
		});
	}
	
	/** 
	 * 返回当前程序版本名 
	 */  
	private String getAppVersionName(Context context) {  
	    String versionName = "1.0";  
	    try {  
	        PackageManager pm = context.getPackageManager();  
	        PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);  
	        versionName = pi.versionName;  
	    } catch (Exception e) {  
	        Log.e("VersionInfo", "Exception", e);  
	    }  
	    return versionName;  
	}

	@Override
	public void onComplete(Platform platform, int action,HashMap<String, Object> res) {
		Message message = new Message();
		message.what = WHAT_COMPLETE;
		message.obj = platform;
		UIHandler.sendMessage(message, this);
	}

	@Override
	public void onError(Platform platform, int action, Throwable t) {
		t.printStackTrace();
		if(t instanceof ActivityNotFoundException){
			if(platform instanceof QQ){
				t = new QQClientNotExistException();
			}
		}

		Message message = new Message();
		message.what = WHAT_ERROR;
		message.obj = t;
		UIHandler.sendMessage(message, this);
	}

	@Override
	public void onCancel(Platform platform, int action) {
		UIHandler.sendEmptyMessage(WHAT_CANCEL, this);
	}

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case WHAT_COMPLETE:
			if((msg.obj instanceof SinaWeibo && isAppInstalled(this.getActivity(),SINA_WEIBO))
					|| msg.obj instanceof Wechat 
					|| msg.obj instanceof WechatMoments){
				return true;
			}
			ViewUtil.getInstance().showToast("分享成功");
			break;
		case WHAT_CANCEL:
			break;
		case WHAT_ERROR:
			if(msg.obj instanceof WechatClientNotExistException){
				//没有安装微信客户端
				ViewUtil.getInstance().showToast("没有安装微信客户端");
			}else if(msg.obj instanceof WechatTimelineNotSupportedException){
				//客户端版本太低，不支持分享好友圈
				ViewUtil.getInstance().showToast("您的微信客户端版本太低，不支持分享好友圈");
			}else if(msg.obj instanceof QQClientNotExistException){
				//QQ客户端没有安装或被停用
				ViewUtil.getInstance().showToast("您没有安装QQ客户端");
			}else{
				ViewUtil.getInstance().showToast("分享失败");
			}
			break;
		default:
			break;
		}
		return true;
	}
	
	private boolean isAppInstalled(Context context, String packageName) {  
		PackageManager pm = context.getPackageManager();  
		boolean installed = false;  
		try {  
			pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);  
			installed = true;  
		} catch (PackageManager.NameNotFoundException e) {  
			installed = false;  
		}  
		return installed;  
	}
	
	/**
	 * 显示用户二维码对话框
	 */
	void showQRCode() {
		if (qrcodeBm == null) {
			qrcodeBm = BitmapUtil.createQRCodeBitmap(path, 200);
			saveBitmap();
		}
	}
	//http://file.bmob.cn/M02/58/48/oYYBAFahZ-eAHCVfAE_RWUSZJHQ021.apk
	private void saveBitmap() {
		if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
			if(qrcodeFile == null){
				File dir = new File(Environment.getExternalStorageDirectory(),"firerunning");
				if(!dir.exists()){
					dir.mkdir();
				}
				qrcodeFile = new File(dir,"qrcode");
			}
			if (qrcodeFile.exists()) {
				qrcodeFile.delete();
			}
			try {
				FileOutputStream out = new FileOutputStream(qrcodeFile);
				qrcodeBm.compress(Bitmap.CompressFormat.JPEG, 100, out);
				out.flush();
				out.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			ViewUtil.getInstance().showToast("SD卡不可用，无法分享图片");
		}
	}

}
