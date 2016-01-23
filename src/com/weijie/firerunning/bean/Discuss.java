package com.weijie.firerunning.bean;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;

public class Discuss extends BmobObject implements Serializable {

	public String content;
	//坐标。可有可无
	public String gpsPoints;
	//具体地址名称。可有可无
	public String location;
	//图片的地址 多个地址用 “,”隔开
	public String fileURL;
	public String fileName;
	//用户（不登录的情况下为空）
	public User user;
	//在没有登录的情况下是随机字符串（时间或者手机号码）
	public String hide;
	
	public Discuss(String content, String fileURL, String fileName) {
		this.content = content;
		this.fileURL = fileURL;
		this.fileName = fileName;
	}
	
}
