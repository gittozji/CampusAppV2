package com.zjcql.bmobbean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobRelation;

/** 
*
* @author phlofy
* @date 2015年8月26日 下午9:44:44 
*/
public class Post extends BmobObject{
	User author;
	BmobRelation like;
	public User getAuthor() {
		return author;
	}
	public void setAuthor(User author) {
		this.author = author;
	}
	public BmobRelation getLike() {
		return like;
	}
	public void setLike(BmobRelation like) {
		this.like = like;
	}
}
