package com.zjcql.bmobbean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/** 
* 通知表
* @author phlofy
* @date 2015年10月17日 下午2:44:09 
*/
public class Notices extends BmobObject{
		/**
		 * 发布者
		 */
	User user;
		/**
		 * 标题
		 */
	String title;
		/**
		 * 颜色索引
		 */
	int index;
		/**
		 * 内容
		 */
	String content;
		/**
		 * 图片
		 */
	String file;
		/**
		 * 图片在服务器的惟一标识名称
		 */
	String fileName;
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
