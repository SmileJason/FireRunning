package com.weijie.firerunning.bean;

import cn.bmob.v3.BmobUser;

public class User extends BmobUser {

	//true 男  false 女
	private Boolean sex;
	//地址 例如 “深圳”
	private String location;
	//年龄
	private Integer age;
	//简介
	private String desc;

	private String iconURL;

	public Boolean getSex() {
		return sex;
	}

	public void setSex(Boolean sex) {
		this.sex = sex;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getIconURL() {
		return iconURL;
	}

	public void setIconURL(String iconURL) {
		this.iconURL = iconURL;
	}
}
