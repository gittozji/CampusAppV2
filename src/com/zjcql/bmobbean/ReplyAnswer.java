package com.zjcql.bmobbean;

import cn.bmob.v3.BmobObject;

/** 
* 回答问题表
* @author phlofy
* @date 2016年1月27日 下午1:23:43 
*/
public class ReplyAnswer extends BmobObject{

	User user;
	Answer answer;
	String content;
	Integer pop;
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Answer getAnswer() {
		return answer;
	}
	public void setAnswer(Answer answer) {
		this.answer = answer;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Integer getPop() {
		return pop;
	}
	public void setPop(Integer pop) {
		this.pop = pop;
	}
}
