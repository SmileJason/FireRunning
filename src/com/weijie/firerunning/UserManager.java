package com.weijie.firerunning;

import com.weijie.firerunning.bean.User;


public class UserManager {

	private User user;
	
	private static UserManager uManager;

	private UserManager() {}
	
	public static UserManager getInstance() {
		if(uManager==null) {
			uManager = new UserManager();
		}
		return uManager;
	}
	
	public User getUser() {
		return user;
	}  
	
	public void setUser(User user) {
		this.user = user;
	}
	
}
