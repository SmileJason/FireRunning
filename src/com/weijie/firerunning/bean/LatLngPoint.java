package com.weijie.firerunning.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * 点坐标
 * @author weijie
 *
 */
public class LatLngPoint implements Serializable {

	public double lat;
	public double lng;
	
	public LatLngPoint(double lat, double lng) {
		super();
		this.lat = lat;
		this.lng = lng;
	}
	
	//标注第几公里
	public int index;
	
	//这公里的耗时
	public long time;
	//记录时间
	public Date dateTime;

	public LatLngPoint(double lat, double lng, long time, Date dateTime) {
		super();
		this.lat = lat;
		this.lng = lng;
		this.time = time;
		this.dateTime = dateTime;
	}
	
}
