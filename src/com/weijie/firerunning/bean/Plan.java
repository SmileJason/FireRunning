package com.weijie.firerunning.bean;


public class Plan {

	public String pId;
	//type 0:10K 1:半马  2:全马 
	public int type;
	public String title;
	public String matchDate;
	public String startDate;
	public int weeks;
	public double maxDistance;
	public double distance;
	
	//state 0:进行中   1:已完成 
	public int state;

	@Override
	public String toString() {
		return "Plan [pId=" + pId + ", distance=" + distance + "]";
	}
	
}
