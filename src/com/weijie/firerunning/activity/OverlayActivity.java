package com.weijie.firerunning.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CircleOptions;
import com.amap.api.maps2d.model.GroundOverlayOptions;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Polyline;
import com.amap.api.maps2d.model.PolylineOptions;
import com.weijie.firerunning.R;

public class OverlayActivity extends Activity implements OnClickListener {

	private MapView mapView;
	private AMap aMap;
	private Polyline polyline;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_overlay);
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);
		findViewById(R.id.addLine).setOnClickListener(this);
		findViewById(R.id.clear).setOnClickListener(this);
		init();
	}
	
	/**
	 * 初始化AMap对象
	 */
	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
		}
		setUpMap();
	}
	
	private void setUpMap() {
		CameraUpdate cu = CameraUpdateFactory.zoomTo(16);
		aMap.moveCamera(cu);
		LatLng targetPos = new LatLng(22.678909, 114.118041);
		cu = CameraUpdateFactory.changeLatLng(targetPos);
		aMap.moveCamera(cu);
		GroundOverlayOptions options = new GroundOverlayOptions().image(BitmapDescriptorFactory
				.fromResource(R.drawable.man)).position(targetPos, 64);
		aMap.addGroundOverlay(options);
		CircleOptions cOptions = new CircleOptions().center(targetPos).fillColor(0x80ffff00)
				.radius(25).strokeWidth(1).strokeColor(0xff000000);
		aMap.addCircle(cOptions);
		aMap.addPolyline(new PolylineOptions().add(new LatLng(22.678909, 114.118041),new LatLng(22.679019, 114.118051),new LatLng(22.679229, 114.118061),new LatLng(22.679339, 114.118071),new LatLng(22.679549, 114.118081)).geodesic(true).width(5).color(Color.BLUE));
	}
	
	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.addLine:
			//aMap.addPolyline(new PolylineOptions().add(new LatLng(22.678949, 114.118081),new LatLng(18.678949, 112.118081)).geodesic(true).width(5).color(Color.RED));
			break;
		case R.id.clear:
			aMap.clear();
			break;
		}
	}
	
}
