package com.zjcql.bmobbean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/** 
* 商品表 ,之后的字段可以再加入
* @author phlofy
* @date 2015年9月10日 下午6:35:13 
*/
public class Goods extends BmobObject{
		/**
		 * 发布者
		 */
	User user;
		/**
		 * 标题
		 */
	String title;
		/**
		 * 内容
		 */
	String content;
		/**
		 * 三张图片
		 */
	String[] files;
		/**
		 * 三张图片在服务器的惟一标识名称
		 */
	String[] filesName;
		
		/**
		 * 价格
		 */
	Double price;
		/**
		 * 成色
		 */
	String state;
		/**
		 * 分类
		 */
	String classify;
		/**
		 * 是否可买：false为出售，true为求购
		 */
	Boolean isBuy;
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
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getClassify() {
		return classify;
	}
	public void setClassify(String classify) {
		this.classify = classify;
	}
	public Boolean getIsBuy() {
		return isBuy;
	}
	public void setIsBuy(Boolean isBuy) {
		this.isBuy = isBuy;
	}
}
