package com.weijie.firerunning.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.weijie.firerunning.R;

public class ShareActivity extends Activity {

	private ActionBar actionBar;
	private TextView runCount,distance,averate,mostDistance;
	
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
	
}
