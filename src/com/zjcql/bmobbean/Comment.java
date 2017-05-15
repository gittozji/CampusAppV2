package com.zjcql.bmobbean;

import cn.bmob.v3.BmobObject;

/** 
* 吐槽墙评论表
* @author phlofy
* @date 2016年1月24日 下午1:23:50 
*/
public class Comment extends BmobObject{
	User user;
	Says say;
	String content;
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Says getSay() {
		return say;
	}
	public void setSay(Says say) {
		this.say = say;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
}
