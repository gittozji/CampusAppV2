package com.zjcql.bmobbean;

import cn.bmob.v3.BmobObject;

/** 
* 回答问题pop记录
* @author phlofy
* @date 2016年1月27日 下午5:55:44 
*/
public class AnswerPopMark extends BmobObject{
		/**
		 * 赞者
		 */
	User user;
		/**
		 * 被赞回答
		 */
	ReplyAnswer replyAnswer;
	public User getUser() {
	return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public ReplyAnswer getReplyAnswer() {
		return replyAnswer;
	}
	public void setReplyAnswer(ReplyAnswer replyAnswer) {
		this.replyAnswer = replyAnswer;
	}
}
