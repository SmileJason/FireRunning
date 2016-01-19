package com.weijie.firerunning.bean;

public class Record {

	public String month;
	public String date;
	public String distence;
	
	public Record(String date, String distence,String month) {
		this.date = date;
		this.distence = distence;
		this.month = month;
	}
	public Record(String month) {
		this.month = month;
	}
}
