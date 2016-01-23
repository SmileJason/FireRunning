package com.weijie.firerunning.bean;

import cn.bmob.v3.BmobObject;

public class Comment extends BmobObject {

	
	private String content;//评论内容  

    private User user;//评论的用户，Pointer类型，一对一关系

    //如果没有用户登录，就取当前时间
    private String hide;
    
    //评论的博客
    private Discuss discuss;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getHide() {
		return hide;
	}

	public void setHide(String hide) {
		this.hide = hide;
	}

	public Discuss getDiscuss() {
		return discuss;
	}

	public void setDiscuss(Discuss discuss) {
		this.discuss = discuss;
	}
    
}
