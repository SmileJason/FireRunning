package com.weijie.firerunning.bean;
/*
 * 计划类型
 */
public class PlanType {

	//训练类型的Id 0 1 2
	public String ptId;
	//训练所需的周数 8 12 20
	public int weeks;
	//训练的总数 378.5 701.75
	public float distance;
	
	public PlanType(String ptId, int weeks, float distance) {
		this.ptId = ptId;
		this.weeks = weeks;
		this.distance = distance;
	}
}
