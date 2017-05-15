package com.zjcql.bmobbean;

import cn.bmob.v3.BmobObject;

/** 
* 用户的Pop值
* @author phlofy
* @date 2015年10月1日 下午2:55:30 
*/
public class Pops extends BmobObject{
	User user;
	Integer pop;
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Integer getPop() {
		return pop;
	}
	public void setPop(Integer pop) {
		this.pop = pop;
	}
}
