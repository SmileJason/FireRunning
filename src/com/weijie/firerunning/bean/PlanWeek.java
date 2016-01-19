package com.weijie.firerunning.bean;

import java.util.List;

/*
 * 计划（周）
 */
public class PlanWeek extends PlanBase {

	//pwId 等于pId+_序号
	public String pwId;
	public String pId;
	public int w_number;
	public String planType;
	public String weekDesc;
	public double maxDistance;
	public double distance;
	
	public PlanWeek(String pwId, String pId, int w_number, String planType, String weekDesc) {
		this.pwId = pwId;
		this.pId = pId;
		this.w_number = w_number;
		this.planType = planType;
		this.weekDesc = weekDesc;
	}
	
	public PlanWeek() {}
	
	public List<PlanDay> planDays;

	@Override
	public String toString() {
		return "PlanWeek [pwId=" + pwId + "]";
	}
	
}
