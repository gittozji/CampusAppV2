package com.zjcql.bmobbean;

import cn.bmob.v3.BmobObject;

/** 
* 问答表
* @author phlofy
* @date 2016年1月25日 下午10:06:29 
*/
public class Answer extends BmobObject{
		/**
		 * 发布者
		 */
	User fromUser;
		/**
		 * 标题
		 */
	String title;
		/**
		 * 吐槽内容
		 */
	String content;
		/**
		 * 图片
		 */
	String[] files;
		/**
		 * 图片在服务器的惟一标识名称
		 */
	String[] filesName;
	
	public User getFromUser() {
		return fromUser;
	}
	public void setFromUser(User fromUser) {
		this.fromUser = fromUser;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String[] getFiles() {
		return files;
	}
	public void setFiles(String[] files) {
		this.files = files;
	}
	public String[] getFilesName() {
		return filesName;
	}
	public void setFilesName(String[] filesName) {
		this.filesName = filesName;
	}
}
