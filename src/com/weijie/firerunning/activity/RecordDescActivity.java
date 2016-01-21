package com.weijie.firerunning.activity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.GroundOverlayOptions;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.PolylineOptions;
import com.google.gson.reflect.TypeToken;
import com.weijie.firerunning.R;
import com.weijie.firerunning.bean.LatLngPoint;
import com.weijie.firerunning.bean.RunRecord;
import com.weijie.firerunning.util.DateUtil;
import com.weijie.firerunning.util.JSONUtil;
import com.weijie.firerunning.util.ViewUtil;

public class RecordDescActivity extends Activity {

	private ActionBar actionBar;
	private MapView mapView;
	private AMap aMap;
	private CameraUpdate cu;
	private PolylineOptions po;
	private ArrayList<ArrayList<LatLngPoint>> pointss;
	
	private TextView time,speed,minSpeed,maxSpeed,distance;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record_desc);
		
		actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setLogo(R.drawable.logo);
		actionBar.setTitle("历史详情");
		actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.titlebar_background));
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);
		
		initView();
		initMap();
		initData();
		
	}
	
	private void initView() {
		time = (TextView) findViewById(R.id.time);
		speed = (TextView) findViewById(R.id.speed);
		minSpeed = (TextView) findViewById(R.id.minSpeed);
		maxSpeed = (TextView) findViewById(R.id.maxSpeed);
		distance = (TextView) findViewById(R.id.distance);
	}
	
	private void initData() {
		Object obj = getIntent().getSerializableExtra("record");
		if(obj!=null) {
			RunRecord record = (RunRecord)obj;
			//Log.e("record", record.toString());
			String str = record.points;
			try {
				JSONArray ja = new JSONArray(str);
				pointss = new ArrayList<ArrayList<LatLngPoint>>();
				for(int i=0;i<ja.length();i++) {
					JSONArray jap = ja.getJSONArray(i);
					TypeToken<ArrayList<LatLngPoint>> typeToken = new TypeToken<ArrayList<LatLngPoint>>() {};
					ArrayList<LatLngPoint> points = JSONUtil.toList(jap, typeToken);
					pointss.add(points);
				}
				po = new PolylineOptions();
				
				LatLng targetPos = new LatLng(pointss.get(0).get(0).lat,pointss.get(0).get(0).lng);
				aMap.clear();	
				cu = CameraUpdateFactory.changeLatLng(targetPos);
				aMap.moveCamera(cu);
				GroundOverlayOptions options = new GroundOverlayOptions().image(BitmapDescriptorFactory
						.fromResource(R.drawable.start)).position(targetPos, 60);
				aMap.addGroundOverlay(options);
				
				for(ArrayList<LatLngPoint> points:pointss) {
					for(int i=0;i<points.size()-1;i++) {
						LatLngPoint lp1 = points.get(i);
						LatLng l1 = new LatLng(lp1.lat, lp1.lng);
						LatLngPoint lp2 = points.get(i+1);
						LatLng l2 = new LatLng(lp2.lat,lp2.lng);
						aMap.addPolyline(po.add(l1,l2).geodesic(true).width(10).color(Color.BLUE));
					}
				}
				time.setText(DateUtil.formatTimeString(Long.parseLong(record.timeLength)));
				distance.setText(String .format("%.2f",record.distance/(1000))+"公里");
				long ms = (long) (Long.parseLong(record.timeLength)*1000/record.distance);
				speed.setText(DateUtil.mSecond2Time(ms));
				
				String[] mss = record.kTime.split(";");
				long max = 0;
				long min = 0;
				for(String s:mss) {
					if(s.equals("")) {
						return;
					}
					long m = Long.parseLong(s);
					if(m>max) {
						max = m;
					}
					if(m<=min) {
						min = m;	
					}
				}
				maxSpeed.setText(DateUtil.mSecond2Time(min));
				minSpeed.setText(DateUtil.mSecond2Time(max));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			//Toast.makeText(this, "数据异常！", Toast.LENGTH_SHORT).show();
			ViewUtil.getInstance().showToast("数据异常！");
			finish();
		}
		
	}
	
	/**
	 * 初始化AMap对象
	 */
	private void initMap() {
		if (aMap == null) {
			aMap = mapView.getMap();
		}
		setUpMap();
	}
	
	private void setUpMap() {
		UiSettings us = aMap.getUiSettings();
		us.setCompassEnabled(false);
		us.setZoomControlsEnabled(false);
		cu = CameraUpdateFactory.zoomTo(16);
		aMap.moveCamera(cu);
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
	
	/**
	 * 方法必须重写
	 */
	@Override
	public void onResume() {
		super.onResume();
		mapView.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	public void onPause() {
		super.onPause();
		mapView.onPause();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}
	
}
