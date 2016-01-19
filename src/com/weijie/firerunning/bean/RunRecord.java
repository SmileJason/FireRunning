package com.weijie.firerunning.bean;

import java.io.Serializable;

/**
 * 跑步记录
 * @author weijie
 *
 */
public class RunRecord implements Serializable {

	//id 账号+时间
	public String id;
	//坐标点的集合,用json封装
	public String points;
	//每公里的时间，用“,”分割，时间是毫秒
	public String kTime;
	//距离
	public double distance;
	//开始时间
	public String startTime;
	//保存时间
	public long saveTime;
	//结束时间
	public String endTime;
	//跑步所用的时间 小时
	public String timeLength;
	//最快配速 00:00每公里
	public String highSpeed;
	//最慢配速 00:00每公里
	public String lowSpeed;
	
	public String ym;
	
	@Override
	public String toString() {
		return "RunRecord [id=" + id + ", points=" + points + ", kTime="
				+ kTime + ", distance=" + distance + ", startTime=" + startTime
				+ ", endTime=" + endTime + ", timeLength=" + timeLength
				+ ", highSpeed=" + highSpeed + ", lowSpeed=" + lowSpeed + "]";
	}

	public RunRecord() {
	}
	
	public RunRecord(String ym) {
		this.ym = ym;
	}
	
}
