package com.weijie.firerunning.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.bmob.BmobProFile;
import com.weijie.firerunning.App;
import com.weijie.firerunning.R;
import com.weijie.firerunning.UserManager;
import com.weijie.firerunning.bean.Comment;
import com.weijie.firerunning.bean.Discuss;
import com.weijie.firerunning.bean.User;
import com.weijie.firerunning.net.BitmapImageCache;
import com.weijie.firerunning.util.ViewUtil;
import com.weijie.firerunning.view.RoundAngleImageView;
import com.weijie.firerunning.view.SingleImageDialog;

public class CommentActivity extends Activity implements OnClickListener, OnRefreshListener {

	private Discuss discuss;
	private List<Comment> comments;
	private final int LIMIT = 5;
	private Handler mHandler = new Handler();

	private ActionBar actionBar;
	private LayoutInflater inflater;
	private SingleImageDialog imgDialog;
	private ImageLoader imageLoader;
	private LinearLayout comment;
	private EditText commentContent;
	private ProgressBar loadProgress;
	private TextView loadData;
	private SwipeRefreshLayout mSwipeRefreshWidget;
	private View loading;

	private final Runnable mRefreshDone = new Runnable() {
		@Override
		public void run() {
			//index = 1;
			getComments(true,false);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment);
		discuss = (Discuss) getIntent().getSerializableExtra("discuss");
		comments = new ArrayList<Comment>();
		initView();
	}

	private void initView() {
		inflater = LayoutInflater.from(this);
		actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setLogo(R.drawable.logo);
		actionBar.setTitle("添加动态");
		actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.titlebar_background));
		actionBar.setDisplayHomeAsUpEnabled(true);

		TextView name = (TextView) findViewById(R.id.name);
		TextView dateTime = (TextView) findViewById(R.id.dateTime);
		TextView content = (TextView) findViewById(R.id.content);

		User user = discuss.user;
		if(user!=null) {
			name.setText(user.getUsername());
		} else {
			name.setText(discuss.hide);
		}
		dateTime.setText(discuss.getCreatedAt());
		content.setText(discuss.content);
		String fileURL = discuss.fileURL;
		String fileName = discuss.fileName;
		if(fileURL!=null && !fileURL.equals("")) {
			RequestQueue queue = App.getRequestQueue(this.getApplication());
			imageLoader = new ImageLoader(queue, new BitmapImageCache(this));
			LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
			layout.setVisibility(View.VISIBLE);
			String[] files = fileName.split(",");
			String[] urls = fileURL.split(",");
			for(int i=0;i<urls.length;i++) {
				if(!urls[i].trim().equals("") && !files[i].trim().equals("")) {
					View image = inflater.inflate(R.layout.item_picture2, layout, false);
					image.setClickable(true);
					image.setOnClickListener(listener);
					layout.addView(image);
					RoundAngleImageView img = (RoundAngleImageView) image.findViewById(R.id.img);
					String url = BmobProFile.getInstance(this).signURL(files[i], urls[i], "68c14de64572e77eb8039d5781202301", 0, null);
					showImageByNetworkImageView(img,url);
				}
			}
		}

		loading = findViewById(R.id.loading);
		commentContent = (EditText) findViewById(R.id.commentContent);
		comment = (LinearLayout) findViewById(R.id.comment);
		loadProgress = (ProgressBar) findViewById(R.id.loadProgress);
		loadData = (TextView) findViewById(R.id.loadData);
		findViewById(R.id.submit).setOnClickListener(this);
		findViewById(R.id.load).setOnClickListener(this);

		mSwipeRefreshWidget = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_widget);
		mSwipeRefreshWidget.setColorSchemeResources(R.color.color1, R.color.color2, R.color.color3,
				R.color.color4);
		mSwipeRefreshWidget.setOnRefreshListener(this);
		if(mSwipeRefreshWidget.hasWindowFocus()){
			mSwipeRefreshWidget.setRefreshing(true);
			refresh();
		}else{
			mSwipeRefreshWidget.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
				@Override
				public boolean onPreDraw() {
					if(mSwipeRefreshWidget.hasWindowFocus()){
						mSwipeRefreshWidget.getViewTreeObserver().removeOnPreDrawListener(this);	
						mSwipeRefreshWidget.setRefreshing(true);
						refresh();
					}
					return false;
				}
			});
		}
	}

	/**
	 * 利用NetworkImageView显示网络图片
	 */ 
	private void showImageByNetworkImageView(RoundAngleImageView img,String imageUrl){ 
		imageLoader.get(imageUrl
				,ImageLoader.getImageListener(img
						,R.drawable.image_load_cache
						, R.drawable.image_load_cache));

	}

	private OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if(imgDialog==null) {
				imgDialog = new SingleImageDialog(CommentActivity.this); 
			}
			RoundAngleImageView img = (RoundAngleImageView)(v.findViewById(R.id.img));
			imgDialog.setData(img.getDrawable());
			imgDialog.show();
		}
	};

	private void getComments(final boolean refresh,final boolean load) {
		BmobQuery<Comment> query = new BmobQuery<Comment>();
		if(comments!=null && comments.size()>0) {
			String sDate;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
			Date date  = null;
			if(refresh) {
				sDate = comments.get(0).getCreatedAt();
				try {
					date = sdf.parse(sDate);
					query.addWhereGreaterThan("createdAt",new BmobDate(date));
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if(load) {
				sDate = comments.get(comments.size()-1).getCreatedAt();
				try {
					date = sdf.parse(sDate);
					query.addWhereLessThan("createdAt",new BmobDate(date));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		query.addWhereEqualTo("discuss",new BmobPointer(discuss));
		query.order("-createdAt");
		query.setLimit(LIMIT);
		query.include("user");
		query.findObjects(this, new FindListener<Comment>() {
			@Override
			public void onSuccess(List<Comment> object) {
				if(object!=null && object.size()>0) {
					if(refresh) {
						if(comments.size()==0) {
							comments.addAll(0, object);
						} else {
							object.remove(object.size()-1);
							if(object.size()>0) {
								comments.addAll(0, object);
							}
						}

						comment.removeAllViews();
						for(Comment c:comments) {
							View view = inflater.inflate(R.layout.item_comment, comment, false);
							TextView commentName = (TextView) view.findViewById(R.id.commentName);
							TextView commentTime = (TextView) view.findViewById(R.id.commentTime);
							TextView commentMsg = (TextView) view.findViewById(R.id.commentMsg);
							User user = c.getUser();
							if(user!=null && user.getUsername()!=null && !user.getUsername().equals("")) {
								commentName.setText(user.getUsername()+"：");
							} else {
								commentName.setText(c.getHide()+"：");
							}
							commentMsg.setText(c.getContent());
							commentTime.setText(c.getCreatedAt());
							comment.addView(view);
						}
					} else {
						for(Comment c:object) {
							View view = inflater.inflate(R.layout.item_comment, comment, false);
							TextView commentName = (TextView) view.findViewById(R.id.commentName);
							TextView commentTime = (TextView) view.findViewById(R.id.commentTime);
							TextView commentMsg = (TextView) view.findViewById(R.id.commentMsg);
							User user = c.getUser();
							if(user!=null && user.getUsername()!=null && !user.getUsername().equals("")) {
								commentName.setText(user.getUsername()+"：");
							} else {
								commentName.setText(c.getHide()+"：");
							}
							commentMsg.setText(c.getContent());
							commentTime.setText(c.getCreatedAt());
							comment.addView(view);
						}
						comments.addAll(object);
					}
				}

				if(refresh) {
					mSwipeRefreshWidget.setRefreshing(false);
				} else if(load) {
					loadData.setText("点击加载更多数据");
					loadProgress.setVisibility(View.GONE);
				}
			}
			@Override
			public void onError(int code, String msg) {
				ViewUtil.getInstance().showToast(msg);
				if(refresh) {
					mSwipeRefreshWidget.setRefreshing(false);
				} else if(load) {
					loadData.setText("点击加载更多数据");
					loadProgress.setVisibility(View.GONE);
				}
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
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.submit:
			String msg = commentContent.getText().toString().trim();
			if(msg.equals("")) {
				ViewUtil.getInstance().showToast("请输入评论内容再提交！");
			} else {
				loading.setVisibility(View.VISIBLE);
				UserManager manager = UserManager.getInstance();
				User user = manager.getUser();
				final Comment c = new Comment();
				if(user!=null) {
					c.setUser(user);
				} else {
					c.setHide("匿名用户");
				}
				c.setContent(msg);
				c.setDiscuss(discuss);
				c.save(CommentActivity.this,new SaveListener() {
					@Override
					public void onSuccess() {
						loading.setVisibility(View.GONE);
						ViewUtil.getInstance().showToast("您的评论已经成功发送！");
						comments.add(0, c);
						View view = inflater.inflate(R.layout.item_comment, comment, false);
						TextView commentName = (TextView) view.findViewById(R.id.commentName);
						TextView commentTime = (TextView) view.findViewById(R.id.commentTime);
						TextView commentMsg = (TextView) view.findViewById(R.id.commentMsg);
						User user = c.getUser();
						if(user!=null && user.getUsername()!=null && !user.getUsername().equals("")) {
							commentName.setText(user.getUsername()+"：");
						} else {
							commentName.setText(c.getHide()+"：");
						}
						commentMsg.setText(c.getContent());
						commentTime.setText(c.getCreatedAt());
						comment.addView(view, 0);
					}
					@Override
					public void onFailure(int code, String msg) {
						loading.setVisibility(View.GONE);
						ViewUtil.getInstance().showToast("您的评论发送失败！",code);
					}
				});
			}
			break;
		case R.id.load:
			if(comments.size()>0) {
				loadData.setText("正在加载，请稍等");
				loadProgress.setVisibility(View.VISIBLE);
				getComments(false,true);
			}
			break;
		}
	}

	@Override
	public void onRefresh() {
		refresh();
	}

	private void refresh() {
		mHandler.removeCallbacks(mRefreshDone);
		mHandler.post(mRefreshDone);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
