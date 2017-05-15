package com.zjcql.bmobbean;

import cn.bmob.v3.BmobObject;

/** 
* 做赞或贬操作的记录
* @author phlofy
* @date 2015年10月10日 上午10:54:38 
*/
public class PopMark extends BmobObject{
		/**
		 * 赞者
		 */
	User initiative;
		/**
		 * 被赞者
		 */
	User possivity;
		/**
		 * 类型。1为赞用户，2为赞帖子
		 */
	Integer type;
	public User getInitiative() {
		return initiative;
	}
	public void setInitiative(User initiative) {
		this.initiative = initiative;
	}
	public User getPossivity() {
		return possivity;
	}
	public void setPossivity(User possivity) {
		this.possivity = possivity;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
}
