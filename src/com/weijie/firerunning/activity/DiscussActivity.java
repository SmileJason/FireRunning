package com.weijie.firerunning.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadBatchListener;
import com.weijie.firerunning.Conf;
import com.weijie.firerunning.R;
import com.weijie.firerunning.UserManager;
import com.weijie.firerunning.bean.Discuss;
import com.weijie.firerunning.bean.User;
import com.weijie.firerunning.util.ViewUtil;
import com.weijie.firerunning.view.ImageDialog;
import com.weijie.firerunning.view.RoundAngleImageView;

@SuppressLint("NewApi")
public class DiscussActivity extends FragmentActivity implements OnClickListener {

	private String imgName;

	/* 请求识别码 */
	private static final int CODE_GALLERY_REQUEST = 0xa0;
	private static final int CODE_CAMERA_REQUEST = 0xa1;

	private ActionBar actionBar;
	private LayoutInflater inflater;
	private int width,height;
	private LinearLayout lineLayout;
	private AlertDialog dialog;
	private List<String> paths;
	private List<RoundAngleImageView> imgs;
	private ImageDialog imgDialog;
	private EditText content;
	private View loading;

	private User user;
	private Discuss discuss;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_discuss);

		UserManager manager = UserManager.getInstance();
		user = manager.getUser();

		actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setLogo(R.drawable.logo);
		actionBar.setTitle("添加动态");
		actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.titlebar_background));
		actionBar.setDisplayHomeAsUpEnabled(true);

		Display dis = this.getWindowManager().getDefaultDisplay();
		int w = dis.getWidth();
		width = (w - ViewUtil.dp2px(this,30))/5;
		height = width;
		inflater = LayoutInflater.from(this);

		loading = findViewById(R.id.loading);
		content = (EditText) findViewById(R.id.content);
		lineLayout = (LinearLayout) findViewById(R.id.layout);

		findViewById(R.id.picture).setOnClickListener(this);
		findViewById(R.id.location).setOnClickListener(this);
		findViewById(R.id.submit).setOnClickListener(this);
	}

	private ProgressDialog pDialog =null;

	private void submitDiscuss() {
		//Toast.makeText(this, paths!=null?paths.toString():"[]", Toast.LENGTH_SHORT).show();
		if(!content.getText().toString().trim().equals("")) {
			if(paths!=null && paths.size()>0) {
				pDialog = new ProgressDialog(DiscussActivity.this);
				//String[] files = (String[]) paths.toArray();
				String[] files = new String[paths.size()];
				for(int i=0;i<paths.size();i++) {
					files[i] = paths.get(i);
				}
				pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);                 
				pDialog.setTitle("批量上传中...");
				pDialog.setIndeterminate(false);               
				pDialog.setCancelable(true);       
				pDialog.setCanceledOnTouchOutside(false);  
				pDialog.setMax(files.length);
				pDialog.show();

				BmobProFile.getInstance(DiscussActivity.this).uploadBatch(files, new UploadBatchListener() {
					@Override
					public void onSuccess(boolean isFinish,String[] fileNames,String[] urls,BmobFile[] files) {
						try {
							if(isFinish){
								pDialog.dismiss();
								loading.setVisibility(View.VISIBLE);
								StringBuffer sbURL = null;
								if(urls!=null && urls.length>0) {
									sbURL = new StringBuffer();
									for(int i=0;i<urls.length;i++) {
										sbURL.append(urls[i]);
										if(i<urls.length-1) {
											sbURL.append(",");
										}
									}
								}
								StringBuffer sbName = null;
								if(fileNames!=null && fileNames.length>0) {
									sbName = new StringBuffer();
									for(int i=0;i<urls.length;i++) {
										sbName.append(fileNames[i]);
										if(i<urls.length-1) {
											sbName.append(",");
										}
									}
								}
								if(sbURL==null || sbName==null) {
									saveDiscuss(content.getText().toString().trim(),null,null);
								} else {
									saveDiscuss(content.getText().toString().trim(),sbURL.toString(),sbName.toString());
								}
							}
						} catch (Exception e) {
						}
						
					}
					@Override
					public void onProgress(int curIndex, int curPercent, int total,int totalPercent) {
						pDialog.setProgress(curIndex);
					}
					@Override
					public void onError(int statuscode, String errormsg) {
						pDialog.dismiss();
					}
				});
			} else {
				loading.setVisibility(View.VISIBLE);
				saveDiscuss(content.getText().toString().trim(),null,null);
			}
		} else {
			ViewUtil.getInstance().showToast("评论内容不能为空，请输入您的评论");
		}
	}

	private void saveDiscuss(String content,String fileURL,String fileName) {
		discuss = new Discuss(content, fileURL, fileName);
		if(user!=null) {
			//discuss.user = user;
			//discuss.setValue("user", user);
			discuss.user = user;
		} else {
			if(city!=null && !city.equals("")) {
				discuss.hide = city+"用户";
			} else {
				discuss.hide = "匿名";
			}
		}
		if(latitude!=0d && longitude!=0d) {
			discuss.gpsPoints = latitude+","+longitude;
		}
		if(address!=null && !address.equals("")) {
			discuss.location = address;
		}

		discuss.save(DiscussActivity.this,new SaveListener() {
			@Override
			public void onSuccess() {
				loading.setVisibility(View.GONE);
				ViewUtil.getInstance().showToast("您的评论已经成功发送！");
				BmobPushManager bmobPush = new BmobPushManager(DiscussActivity.this);
				bmobPush.pushMessageAll("NewDiscuss");
				finish();
			}
			@Override
			public void onFailure(int code, String msg) {
				loading.setVisibility(View.GONE);
				ViewUtil.getInstance().showToast("您的评论发送失败！",code);
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case android.R.id.home:
			finish();
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.picture:
			if(lineLayout.getChildCount()<5) {
				shawDialog();
			} else {
				ViewUtil.getInstance().showToast("最多上传5张图片");
			}
			break;
		case R.id.location:
			getLocation();
			break;
		case R.id.submit:
			submitDiscuss();
			break;
		}
	}

	private String city;
	private String address;
	private double latitude;
	private double longitude;

	private void getLocation() {
		//初始化定位
		AMapLocationClient mLocationClient = new AMapLocationClient(getApplicationContext());
		//设置定位回调监听
		mLocationClient.setLocationListener(new AMapLocationListener() {
			@Override
			public void onLocationChanged(AMapLocation amapLocation) {
				if (amapLocation != null) {
					if (amapLocation.getErrorCode() == 0) {
						//定位成功回调信息，设置相关消息
						//amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
						latitude = amapLocation.getLatitude();//获取纬度
						longitude = amapLocation.getLongitude();//获取经度
						//amapLocation.getAccuracy();//获取精度信息
						//SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						//Date date = new Date(amapLocation.getTime());
						//df.format(date);//定位时间
						address = amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果
						//amapLocation.getCountry();//国家信息
						//amapLocation.getProvince();//省信息
						city = amapLocation.getCity();//城市信息
						//amapLocation.getDistrict();//城区信息
						//amapLocation.getRoad();//街道信息
						//amapLocation.getCityCode();//城市编码
						//amapLocation.getAdCode();//地区编码
						ViewUtil.getInstance().showToast("定位成功，当前的位置:"+address);
					} else {
						//显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
						Log.e("AmapError","location Error, ErrCode:"
								+ amapLocation.getErrorCode() + ", errInfo:"
								+ amapLocation.getErrorInfo());
					}
				}
			}
		});

		AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
		//设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
		mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
		//设置是否返回地址信息（默认返回地址信息）
		mLocationOption.setNeedAddress(true);
		//设置是否只定位一次,默认为false
		mLocationOption.setOnceLocation(true);
		//设置是否强制刷新WIFI，默认为强制刷新
		mLocationOption.setWifiActiveScan(true);
		//设置是否允许模拟位置,默认为false，不允许模拟位置
		mLocationOption.setMockEnable(false);
		//设置定位间隔,单位毫秒,默认为2000ms
		mLocationOption.setInterval(2000);
		//给定位客户端对象设置定位参数
		mLocationClient.setLocationOption(mLocationOption);
		//启动定位
		mLocationClient.startLocation();
	}

	private void shawDialog() {
		dialog = new AlertDialog.Builder(this).create();
		Window window = dialog.getWindow();  
		window.setGravity(Gravity.BOTTOM);  //此处可以设置dialog显示的位置  
		window.setWindowAnimations(R.style.PopupAnimation);  //添加动画  
		dialog.show();
		View dialogView = LayoutInflater.from(this).inflate(R.layout.popupwindow_add_photos, null);
		dialogView.findViewById(R.id.local).setOnClickListener(dialogListener);
		dialogView.findViewById(R.id.camera).setOnClickListener(dialogListener);
		dialogView.findViewById(R.id.cancle).setOnClickListener(dialogListener);
		//dialog.getWindow().setContentView(R.layout.popupwindow_update_icon);
		dialog.getWindow().setContentView(dialogView);

		getWindow().getAttributes().width = WindowManager.LayoutParams.MATCH_PARENT;
		getWindow().getAttributes().height = WindowManager.LayoutParams.WRAP_CONTENT;
	}

	private OnClickListener dialogListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.local:
				//Toast.makeText(UserSettingActivity.this, "local", Toast.LENGTH_SHORT).show();
				choseHeadImageFromGallery();
				break;
			case R.id.camera:
				//Toast.makeText(UserSettingActivity.this, "camera", Toast.LENGTH_SHORT).show();
				choseHeadImageFromCameraCapture();
				break;
			case R.id.cancle:
				break;
			}
			dialog.dismiss();
		}
	};

	// 从本地相册选取图片作为头像
	private void choseHeadImageFromGallery() {
		Intent intentFromGallery = new Intent();
		// 设置文件类型
		intentFromGallery.setType("image/*");
		intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intentFromGallery, CODE_GALLERY_REQUEST);
	}

	// 启动手机相机拍摄照片作为头像
	private void choseHeadImageFromCameraCapture() {
		Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// 判断存储卡是否可用，存储照片文件
		if (hasSdcard()) {
			imgName = new Date().getTime()+".png";
			intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri
					.fromFile(new File(Environment
							.getExternalStorageDirectory(), imgName)));
		}
		startActivityForResult(intentFromCapture, CODE_CAMERA_REQUEST);
	}

	/**
	 * 检查设备是否存在SDCard的工具方法
	 */
	public static boolean hasSdcard() {
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			// 有存储的SDCard
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		// 用户没有进行有效的设置操作，返回
		if (resultCode == RESULT_CANCELED) {
			return;
		}
		switch (requestCode) {
		case CODE_GALLERY_REQUEST:
			try{
				Uri uri = intent.getData();
				String path = getPath(uri);
				if(path!=null) {
					//Bitmap bm = getBitmap(path);
					addPhotos(path);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case CODE_CAMERA_REQUEST:
			if (hasSdcard()) {
				String path = Environment.getExternalStorageDirectory()+"/"+imgName;
				addPhotos(path);
			} else {
				ViewUtil.getInstance().showToast("没有SDCard!");
			}
			break;
		}

		super.onActivityResult(requestCode, resultCode, intent);
	}

	private Bitmap getBitmap(String path) {
		File file = new File(path);
		try {
			FileInputStream is = new FileInputStream(file);
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inTempStorage = new byte[1024*100];
			opts.inPreferredConfig = Bitmap.Config.RGB_565;
			opts.inPurgeable = true;
			opts.inSampleSize = (int) (file.length()/Conf.MEMORY_OUT_MAX+3);
			opts.inInputShareable = true;
			Bitmap bm = BitmapFactory.decodeStream(is, null, opts);
			return bm;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	private String getPath(Uri uri) {
		boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
		if(isKitKat && DocumentsContract.isDocumentUri(this, uri)) {
			String wholeID = DocumentsContract.getDocumentId(uri);
			String id = wholeID.split(":")[1];
			String[] column = {MediaStore.Images.Media.DATA};
			String sel = MediaStore.Images.Media._ID + " = ?";
			Cursor cursor = this.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,column, sel, new String[] {id}, null);
			int index = cursor.getColumnIndex(column[0]);
			if(cursor.moveToFirst()) {
				return cursor.getString(index);
			}
		} else {
			String[] ps = {MediaStore.Images.Media.DATA};
			Cursor cursor = managedQuery(uri, ps, null, null, null);
			int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(index);
		}
		return null;
	}

	private void addPhotos(String path) {
		if(imgs == null) {
			imgs = new ArrayList<RoundAngleImageView>();
		}
		if(paths == null) {
			paths = new ArrayList<String>();
		}
		paths.add(path);

		Bitmap bm = getBitmap(path);
		RelativeLayout layout = (RelativeLayout) (inflater.inflate(R.layout.item_picture, null));
		LayoutParams params = new LayoutParams(width, height);
		layout.setLayoutParams(params);

		RoundAngleImageView roundImg = (RoundAngleImageView)layout.findViewById(R.id.img);
		roundImg.setImageBitmap(bm);
		roundImg.setTag(path);
		roundImg.setOnClickListener(imgClick);
		imgs.add(roundImg);

		View cancle = layout.findViewById(R.id.canclePic);
		cancle.setTag(path);
		cancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				View l = (View) v.getParent();
				lineLayout.removeView(l);
				paths.remove(v.getTag().toString());
				for(RoundAngleImageView img: imgs) {
					if(img!=null && img.getTag().toString()!=null) {
						if(img.getTag().toString().equals(v.getTag().toString())) {
							imgs.remove(img);
							break;
						} 
					}
				}
			}
		});

		if(lineLayout.getChildCount()<5) {
			lineLayout.addView(layout);
		} else {
			//Toast.makeText(this, "最多上传10张图片", Toast.LENGTH_SHORT).show();
			ViewUtil.getInstance().showToast("最多上传5张图片");
		}
	}

	private OnClickListener imgClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if(imgDialog==null) {
				imgDialog = new ImageDialog(DiscussActivity.this); 
			}
			imgDialog.setData(paths, v.getTag().toString(),getSupportFragmentManager());
			imgDialog.show();
		}
	};

}
