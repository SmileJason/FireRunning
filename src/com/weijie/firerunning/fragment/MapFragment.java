package com.weijie.firerunning.fragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.GroundOverlayOptions;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.PolylineOptions;
import com.weijie.firerunning.R;
import com.weijie.firerunning.bean.LatLngPoint;
import com.weijie.firerunning.util.DateUtil;

public class MapFragment extends Fragment {

	public static final String LOCATION = "com.weijie.firerunning.location";
	public static final String CONTROL = "com.weijie.firerunning.control";

	private MapView mapView;
	private AMap aMap;
	private LocationReceiver receiver;
	private ControlReceiver cReceiver;
	/*private List<List<LatLng>> pointss;
	private List<LatLng> points;*/

	private ArrayList<ArrayList<LatLngPoint>> pointss;
	private ArrayList<LatLngPoint> points;
	private int index = 1;
	private float meter = 0;
	//记录上一个点的时间
	private long time;
	//记录总耗时
	private long timeLong = 0;
	private List<Long> kTime;
	//private StringBuffer data;
	private PolylineOptions po;
	private boolean start,canStart;
	private CameraUpdate cu;
	private TextView timeTv,kilometerTv,speedTv;

	//private List<LatLng> latLngs;
	//private int latLngIndex = 0;

	public void initData() {
		if(pointss!=null) {
			pointss.clear();
		}
		if(points!=null) {
			points.clear();
		}
		
		index = 1;
		meter = 0;
		
		if(kTime!=null) {
			kTime.clear();
		}
		timeLong = 0;
		start = false;
		
		timeTv.setText("00:00:00");
		kilometerTv.setText("00.0");
		speedTv.setText("00.0km/h");
		
		//latLngIndex = 0;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_map, container, false);
		
		timeTv = (TextView) view.findViewById(R.id.time);
		kilometerTv = (TextView) view.findViewById(R.id.kilometer);
		speedTv = (TextView) view.findViewById(R.id.speed);
		
		mapView = (MapView) view.findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);
		init();
		initReceiver();
		return view;
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

	private void initReceiver() {
		//points = new ArrayList<LatLng>();
		pointss = new ArrayList<ArrayList<LatLngPoint>>();
		kTime = new ArrayList<Long>();
		//data = new StringBuffer();
		receiver = new LocationReceiver();
		IntentFilter filter = new IntentFilter();  
		filter.addAction(LOCATION);  
		getActivity().registerReceiver(receiver, filter); 

		cReceiver = new ControlReceiver();
		IntentFilter cFilter = new IntentFilter();  
		cFilter.addAction(CONTROL);  
		getActivity().registerReceiver(cReceiver, cFilter); 
	}

	private void setUpMap() {
		UiSettings us = aMap.getUiSettings();
		us.setCompassEnabled(false);
		us.setZoomControlsEnabled(false);
		cu = CameraUpdateFactory.zoomTo(16);
		aMap.moveCamera(cu);
	}

	class LocationReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(start) {
				//double[] latlng = getLatLng();
				//if(latlng!=null) {
				double[] latlng = new double[2];
				latlng[0] = intent.getDoubleExtra("Latitude", 0.0d);
				latlng[1] = intent.getDoubleExtra("Longitude", 0.0d);
				//if(latlng!=null) {
				if(latlng[0]!=0.0d && latlng[1]!=0.0d) {
					double latitude = latlng[0];
					double longitude = latlng[1];
					if(latitude!=0 && longitude!=0) {
						int pSize = points.size();
						if(pSize==0) {
							LatLng targetPos = new LatLng(latitude,longitude);
							if(pointss.size()==0) {
								aMap.clear();	
								cu = CameraUpdateFactory.changeLatLng(targetPos);
								aMap.moveCamera(cu);
								GroundOverlayOptions options = new GroundOverlayOptions().image(BitmapDescriptorFactory
										.fromResource(R.drawable.start)).position(targetPos, 60);
								aMap.addGroundOverlay(options);
							}
							points.add(new LatLngPoint(latitude,longitude));
							po = new PolylineOptions();
						} else {
							LatLngPoint lp1 = points.get(pSize-1);
							LatLng l1 = new LatLng(lp1.lat, lp1.lng);
							LatLngPoint lp2 = new LatLngPoint(latitude,longitude);
							LatLng l2 = new LatLng(latitude,longitude);
							float distance = AMapUtils.calculateLineDistance(l1, l2);
							if(distance>5) {
								long newTime = new Date().getTime();
								timeLong = timeLong + newTime-time;
								time = newTime;
								meter = meter + distance;
								if((int)meter/1000 == index) {
									int tSize = kTime.size();
									if(tSize==0) {
										kTime.add(timeLong);
									} else {
										kTime.add(timeLong-kTime.get(tSize-1));
									}
									lp2.index = index;
									lp2.dateTime = new Date();
									lp2.time = kTime.get(tSize);
									//data.append("#"+index+":"+kTime.get(tSize));

									Intent uIntent = new Intent();
									uIntent.setAction(RunFragment.UPDATE_LIST);
									uIntent.putExtra("kilemeter", index);
									uIntent.putExtra("time", DateUtil.mSecond2Time(kTime.get(tSize)));
									getActivity().sendBroadcast(uIntent);

									index++;
								}
								
								timeTv.setText(DateUtil.formatTimeString(timeLong));
								kilometerTv.setText(String.format("%.1f",meter/1000f));
								speedTv.setText(String.format("%.1f",meter*3600f/timeLong)+"km/h");
								
								Intent uIntent = new Intent();
								uIntent.setAction(ScheduleFragment.UPDATE_UI);
								uIntent.putExtra("distance", meter);
								uIntent.putExtra("timeLong", timeLong);
								getActivity().sendBroadcast(uIntent);
								
								points.add(lp2);
								aMap.addPolyline(po.add(l1,l2).geodesic(true).width(10).color(Color.BLUE));
							}
						}
					}
				}
			} else {
				if(pointss.size()==0) {
					/*double latitude = intent.getDoubleExtra("Latitude", 0);
					double longitude = intent.getDoubleExtra("Longitude", 0);*/
					//double[] latlng = getLatLng();
					double[] latlng = new double[2];
					latlng[0] = intent.getDoubleExtra("Latitude", 0.0d);
					latlng[1] = intent.getDoubleExtra("Longitude", 0.0d);
					//if(latlng!=null) {
					if(latlng[0]!=0.0d && latlng[1]!=0.0d) {
						double latitude = latlng[0];
						double longitude = latlng[1];
						if(latitude!=0 && longitude!=0) {
							canStart = true;
							aMap.clear();						
							LatLng targetPos = new LatLng(latitude,longitude);
							cu = CameraUpdateFactory.changeLatLng(targetPos);
							aMap.moveCamera(cu);
							GroundOverlayOptions options = new GroundOverlayOptions().image(BitmapDescriptorFactory
									.fromResource(R.drawable.start)).position(targetPos, 60);
							aMap.addGroundOverlay(options);
						}
					}
				}
			}
		}
	}

	private class ControlReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			start = intent.getBooleanExtra("start", false);
			if(start) {
				time = new Date().getTime();
				points = new ArrayList<LatLngPoint>();
			} else {
				if(points!=null && points.size()>=2) {
					pointss.add(points);
				}
				points = null;
			}
		}
	}

	public boolean getCanStart() {
		return canStart;
	}

	public ArrayList<ArrayList<LatLngPoint>> getPoints() {
		return pointss;
	}

	public String getKTime() {
		StringBuffer sb = new StringBuffer();
		for(Long t:kTime) {
			sb.append(t);
			sb.append(";");
		}
		return sb.toString();
	}

	public double getDistance() {
		return meter;
	}

	public long getTimeLength() {
		return timeLong;
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
		getActivity().unregisterReceiver(cReceiver);
		getActivity().unregisterReceiver(receiver);
		super.onDestroy();
		mapView.onDestroy();
	}

}
