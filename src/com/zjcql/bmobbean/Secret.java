package com.zjcql.bmobbean;

import cn.bmob.v3.BmobObject;

/** 
* 秘档表
* @author phlofy
* @date 2016年1月27日 下午10:04:41 
*/
public class Secret extends BmobObject{
	User user;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
