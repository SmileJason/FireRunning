package com.weijie.firerunning.bean;

import cn.bmob.v3.BmobObject;

public class User extends BmobObject {

	public String username;
	//md5加密
	public String password;
	public Boolean sex;
	public String location;
	public Integer age;
	public String email;
	public String phone;
	public String desc;
	
	public User(String username,String password) {
		this.username = username;
		this.password = password;
	}

}
