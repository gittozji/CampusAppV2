package com.zjcql.bmobbean;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

/** 
* 用户表,之后的字段可以再加入
* @author phlofy
* @date 2015年8月26日 下午9:43:25 
*/
public class User extends BmobChatUser{
		/**
		 * 性别，true为男，false为女
		 */
	Boolean sex;
		/**
		 * 头像文件在bmob服务器中的唯一标识名称，可通过它来删除文件和生成缩略图
		 */
	String fileName;
		/**
		 * 所在校区
		 */
	String college;
		/**
		 * 年级
		 */
	String grade;
		/**
		 * 签名
		 */
	String signature;
		 /**
		  * 爱好
		  */
	String curious;
		/**
		 * 邮箱，该字段与email无关
		 */
	String mail;
		/**
		 * 手机，该字段与mobilePhoneNunber无关。
		 */
	String tel;
	
	BmobRelation relateGoods;
	
	public Boolean getSex() {
		return sex;
	}
	public void setSex(Boolean sex) {
		this.sex = sex;
	}
	public String getCollege() {
		return college;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public void setCollege(String college) {
		this.college = college;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	public String getCurious() {
		return curious;
	}
	public void setCurious(String curious) {
		this.curious = curious;
	}
	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail = mail;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public BmobRelation getRelateGoods() {
		return relateGoods;
	}
	public void setRelateGoods(BmobRelation relateGoods) {
		this.relateGoods = relateGoods;
	}
}
