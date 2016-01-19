package com.weijie.firerunning.bean;

/*
 * 计划（每天）
 */
public class PlanDay extends PlanBase {

	public int d_number;
	public String p_desc;
	public String p_desc_list;
	public String p_w_Id;
	public double maxDistance;
	public double distance;
	public String date;
	
	public PlanDay(int d_number, double maxDistance, String p_desc,
			String p_desc_list, String p_w_Id) {
		this.d_number = d_number;
		this.p_desc = p_desc;
		this.p_desc_list = p_desc_list;
		this.p_w_Id = p_w_Id;
		this.maxDistance = maxDistance;
	}
	
	public PlanDay() {
	}

	@Override
	public String toString() {
		return "PlanDay [p_w_Id=" + p_w_Id + "]";
	}
	
}
