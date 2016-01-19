package com.weijie.firerunning.bean;

import cn.bmob.v3.BmobObject;

public class ErrorTable extends BmobObject {

	//错误信息
	public String errorMsg;

	public ErrorTable(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
}
