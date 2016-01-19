package com.weijie.firerunning.bean;

import cn.bmob.v3.BmobObject;

public class Suggest extends BmobObject {

	public String suggestContent;

	public Suggest(String suggestContent) {
		this.suggestContent = suggestContent;
	}
	
}
