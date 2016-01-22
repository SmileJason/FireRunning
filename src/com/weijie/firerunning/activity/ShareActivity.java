package com.weijie.firerunning.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
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

import com.mob.tools.utils.UIHandler;
import com.weijie.firerunning.R;
import com.weijie.firerunning.util.ViewUtil;

public class ShareActivity extends Activity implements PlatformActionListener,Callback {

	private static final int WHAT_COMPLETE = 200;
	private static final int WHAT_ERROR = 201;
	private static final int WHAT_CANCEL = 202;
	private static final String SINA_WEIBO = "com.sina.weibo";
	
	private String path;
	
	private ActionBar actionBar;
	private TextView runCount,distance,averate,mostDistance;
	private AlertDialog dialog;
	private Platform platform;
	private Bitmap screen;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share);

		actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setLogo(R.drawable.logo);
		actionBar.setTitle("分享");
		actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.titlebar_background));
		actionBar.setDisplayHomeAsUpEnabled(true);

		runCount = (TextView) findViewById(R.id.runCount);
		distance = (TextView) findViewById(R.id.distance);
		averate = (TextView) findViewById(R.id.averate);
		mostDistance = (TextView) findViewById(R.id.mostDistance);

		initData();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.share_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case android.R.id.home:
			finish();
		case R.id.share:
			if(screen==null) {
				path = getFilesDir().getAbsolutePath()+"/screen.jpg";
				screen = captureScreen();
				saveFile(screen);
			}
			shawDialog();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private void initData() {
		Intent intent = getIntent();
		runCount.setText("跑步记录"+intent.getIntExtra("runCount", 0)+"次");
		distance.setText(intent.getDoubleExtra("distance", 0.0)+"");
		averate.setText("平均每次"+intent.getDoubleExtra("averate", 0.0)+"公里");
		mostDistance.setText("最长跑步记录一次"+intent.getDoubleExtra("maxDistance", 0.0)+"公里");
	}

	private void shawDialog() {
		dialog = new AlertDialog.Builder(this).create();
		Window window = dialog.getWindow();  
		window.setGravity(Gravity.BOTTOM);  //此处可以设置dialog显示的位置  
		window.setWindowAnimations(R.style.PopupAnimation);  //添加动画  
		dialog.show();
		View dialogView = LayoutInflater.from(this).inflate(R.layout.view_share_dialog, null);
		
		dialogView.findViewById(R.id.qq).setOnClickListener(shareListener);
		dialogView.findViewById(R.id.sina).setOnClickListener(shareListener);
		dialogView.findViewById(R.id.QZone).setOnClickListener(shareListener);
		dialogView.findViewById(R.id.tencent).setOnClickListener(shareListener);
		dialogView.findViewById(R.id.wx).setOnClickListener(shareListener);
		dialogView.findViewById(R.id.wx_timeline).setOnClickListener(shareListener);
		
		dialog.getWindow().setContentView(dialogView);

		getWindow().getAttributes().width = WindowManager.LayoutParams.MATCH_PARENT;
		getWindow().getAttributes().height = WindowManager.LayoutParams.WRAP_CONTENT;
	}
	
	private OnClickListener shareListener = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
			ShareParams shareParams;
			switch (view.getId()) {
			case R.id.qq:
				shareParams = new QQ.ShareParams();
				shareParams.setShareType(Platform.SHARE_IMAGE);
				shareParams.setImagePath(path);
				doShare(shareParams, QQ.NAME);
				break;
			case R.id.sina:
				shareParams = new SinaWeibo.ShareParams();
				//shareParams.setShareType(Platform.SHARE_TEXT);
				shareParams.setText("FireRunning分享纪录！");
				shareParams.setImagePath(path);
				doShare(shareParams, SinaWeibo.NAME);
				break;
			case R.id.QZone:
				shareParams = new QZone.ShareParams();
				shareParams.setShareType(Platform.SHARE_IMAGE);
				//shareParams.setText("药膳居最新版本下载地址："+path);
				shareParams.setText("FireRunning分享纪录！");
				shareParams.setImagePath(path);
				doShare(shareParams, QZone.NAME);
				break;
			case R.id.tencent:
				shareParams = new TencentWeibo.ShareParams();
				//shareParams.setShareType(Platform.SHARE_TEXT);
				shareParams.setText("FireRunning分享纪录！");
				shareParams.setImagePath(path);
				doShare(shareParams, TencentWeibo.NAME);
				break;
			case R.id.wx:
				shareParams = new Wechat.ShareParams();
				shareParams.setShareType(Platform.SHARE_IMAGE);
				shareParams.setText("FireRunning分享纪录！");
				shareParams.setImagePath(path);
				doShare(shareParams, Wechat.NAME);
				break;
			case R.id.wx_timeline:
				shareParams = new WechatMoments.ShareParams();
				//shareParams.setShareType(Platform.SHARE_TEXT);
				shareParams.setText("FireRunning分享纪录！");
				shareParams.setImagePath(path);
				doShare(shareParams, WechatMoments.NAME);
				break;
			default:
				break;
			}
			dialog.dismiss();
		}
	};
	
	private void doShare(ShareParams shareParams,String platformName){
		platform = ShareSDK.getPlatform(this, platformName);
		platform.setPlatformActionListener(this);
		platform.share(shareParams);
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
			if((msg.obj instanceof SinaWeibo && isAppInstalled(this,SINA_WEIBO))
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
	
	private Bitmap captureScreen() {
		getWindow().getDecorView().setDrawingCacheEnabled(true);
		Bitmap bmp= getWindow().getDecorView().getDrawingCache();
		return bmp;
	}

	private void saveFile(Bitmap bitmap) {
		File file = new File(path);
		if(file.exists()) {
			file.delete();
		}
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(path);
			if (fileOutputStream != null) {
				bitmap.compress(Bitmap.CompressFormat.PNG, 80, fileOutputStream);
				fileOutputStream.flush();
				fileOutputStream.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
